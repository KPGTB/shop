package eu.kpgtb.shop.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.TaxCode;
import com.stripe.param.*;
import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.data.entity.product.Category;
import eu.kpgtb.shop.data.entity.product.ProductEntity;
import eu.kpgtb.shop.data.entity.product.ProductField;
import eu.kpgtb.shop.data.repository.product.CategoryRepository;
import eu.kpgtb.shop.data.repository.product.ProductRepository;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Properties properties;

    private List<TaxCode> taxes;

    public ProductController(Properties properties) {
        taxes = new ArrayList<>();
        this.properties = properties;
        try {
            loadTaxCodes();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void loadTaxCodes() throws StripeException{
        if(Stripe.apiKey == null) {
            Stripe.apiKey = properties.getStripePrivateKey();
        }

        TaxCodeListParams params = new TaxCodeListParams.Builder()
                .setLimit(100L)
                .build();
        TaxCode.list(params).autoPagingIterable().forEach(taxes::add);
    }

    @GetMapping
    public JsonResponse<ProductEntity.ProductDisplay> info(@RequestParam(value = "id") int id) {
        Optional<ProductEntity> result = productRepository.findById(id);

        return result
                .map(product -> new JsonResponse<>(HttpStatus.OK, "Product info", product.getDisplay()))
                .orElseGet(() -> new JsonResponse<>(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @GetMapping("/taxes")
    public JsonResponse<TaxData> taxes(@RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        int from = (page-1) * 100;

        if(from >= this.taxes.size()) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Out of bound");
        }

        int to = Math.min(page * 100, this.taxes.size());
        boolean hasMore = this.taxes.size() > to;

        return new JsonResponse<>(HttpStatus.OK, "List of taxes", new TaxData(
                hasMore,
                this.taxes.subList(from,to)
        ));
    }

    @PutMapping
    public JsonResponse<Boolean> create(@RequestBody ProductCreateBody body) throws StripeException {
        Optional<Category> categoryOpt = categoryRepository.findById(body.categoryId);
        if(categoryOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.NOT_FOUND, "Category not found");
        }

        ProductCreateParams params = ProductCreateParams.builder()
                .setName(body.name)
                .setDescription(body.description)
                .addImage(body.image)
                .setTaxCode(body.taxCode)
                .setDefaultPriceData(
                        ProductCreateParams.DefaultPriceData.builder()
                                .setUnitAmount(Math.round(body.price*100.0))
                                .setCurrency(properties.getStripeCurrency())
                                .setTaxBehavior(ProductCreateParams.DefaultPriceData.TaxBehavior.EXCLUSIVE)
                                .build()
                )
                .addExpand("default_price")
                .build();
        Product product = Product.create(params);

        ProductEntity entity = new ProductEntity(
                body.name,
                body.nameInUrl,
                body.description,
                body.image,
                properties.getStripeCurrency(),
                body.price,
                body.taxCode,
                product.getId(),
                categoryOpt.get(),
                body.fields
        );
        productRepository.save(entity);
        return new JsonResponse<>(HttpStatus.CREATED, "Created");
    }

    @PostMapping
    public JsonResponse<Boolean> edit(@RequestBody ProductEditBody body) throws StripeException {
        Optional<Category> categoryOpt = categoryRepository.findById(body.categoryId);
        if(categoryOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.NOT_FOUND, "Category not found");
        }

        Optional<ProductEntity> productOpt = productRepository.findById(body.id);
        if(productOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.NOT_FOUND, "Product not found");
        }
        ProductEntity entity = productOpt.get();
        Product product = Product.retrieve(entity.getStripeId());

        String priceId = product.getDefaultPrice();

        if(entity.getPrice() != body.price) {
            PriceCreateParams priceParams = PriceCreateParams.builder()
                    .setUnitAmount(Math.round(body.price*100.0))
                    .setCurrency(properties.getStripeCurrency())
                    .setTaxBehavior(PriceCreateParams.TaxBehavior.EXCLUSIVE)
                    .setProduct(entity.getStripeId())
                    .build();
            Price price = Price.create(priceParams);
            Price oldPrice = Price.retrieve(priceId);
            priceId = price.getId();

            PriceUpdateParams updatePriceParams = PriceUpdateParams.builder()
                    .setActive(false)
                    .build();
            oldPrice.update(updatePriceParams);
        }

        ProductUpdateParams productParams = ProductUpdateParams.builder()
                .setName(body.name)
                .setDescription(body.description)
                .setTaxCode(body.taxCode)
                .setImages(Arrays.asList(body.image))
                .setDefaultPrice(priceId)
                .build();

        entity.setName(body.name);
        entity.setNameInUrl(body.nameInUrl);
        entity.setDescription(body.description);
        entity.setPrice(body.price);
        entity.setImage(body.image);
        entity.setCategory(categoryOpt.get());
        entity.setFields(body.fields);
        entity.setTaxCode(body.taxCode);

        product.update(productParams);
        productRepository.save(entity);
        return new JsonResponse<>(HttpStatus.OK, "Updated");
    }

    @DeleteMapping
    public JsonResponse<Boolean> delete(@RequestBody String idStr) throws StripeException {
        int id = Integer.parseInt(idStr);
        Optional<ProductEntity> productOpt = productRepository.findById(id);
        if(productOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.NOT_FOUND, "Product not found");
        }
        ProductEntity entity = productOpt.get();
        Product.retrieve(entity.getStripeId()).delete();
        productRepository.delete(entity);
        return new JsonResponse<>(HttpStatus.OK, "Product deleted");
    }

    @PostMapping("/updateAll")
    public JsonResponse<Boolean> updateAll() {

        productRepository.findAll().forEach(entity -> {
            try {
                Product product = Product.retrieve(entity.getStripeId());

                entity.setName(product.getName());
                entity.setDescription(product.getDescription());
                entity.setImage(product.getImages().get(0));
                entity.setPrice(product.getDefaultPriceObject().getUnitAmount() / 100.0);

                productRepository.save(entity);
            } catch (StripeException e) {
                e.printStackTrace();
            }
        });

        return new JsonResponse<>(HttpStatus.OK, "Updated");
    }

    record ProductCreateBody(String name, String nameInUrl,String description, String image, double price, String taxCode, int categoryId, List<ProductField> fields) {}
    record ProductEditBody(int id, String name, String description, String nameInUrl, String image, double price,String taxCode, int categoryId, List<ProductField> fields) {}
    record TaxData(boolean hasMore, List<TaxCode> codes) {}
}
