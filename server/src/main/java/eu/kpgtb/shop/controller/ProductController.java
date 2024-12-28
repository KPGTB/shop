package eu.kpgtb.shop.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.TaxCode;
import com.stripe.param.*;
import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.data.dto.product.*;
import eu.kpgtb.shop.data.entity.product.*;
import eu.kpgtb.shop.data.repository.product.*;
import eu.kpgtb.shop.util.AesUtil;
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
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductFieldRepository productFieldRepository;
    @Autowired private ProductDropdownOptionRepository productDropdownOptionRepository;
    @Autowired private RconRepository rconRepository;
    @Autowired private ProductActionKeyRepository keyRepository;
    @Autowired private ApiRepository apiRepository;
    @Autowired private ProductSecretRepository productSecretRepository;

    private final Properties properties;
    private final List<TaxCode> taxes;

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

    @GetMapping("/{productId}")
    public JsonResponse<ProductDto> info(@PathVariable("productId") int id) {
        Optional<ProductEntity> result = productRepository.findById(id);

        return result
                .map(product -> new JsonResponse<>(HttpStatus.OK, "Product info", new ProductDto(
                        product, Arrays.asList("fields", "fields.options", "category", "actions"),""
                )))
                .orElseGet(() -> new JsonResponse<>(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @PutMapping
    public JsonResponse<Boolean> create(@RequestBody ProductBody body) throws StripeException {
        Optional<CategoryEntity> categoryOpt = categoryRepository.findById(body.categoryId);
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
                                .setTaxBehavior(ProductCreateParams.DefaultPriceData.TaxBehavior.valueOf(properties.getStripeTaxBehaviour()))
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
                body.displayTax,
                product.getId(),
                categoryOpt.get(),
                body.fields,
                body.actions
        );
        productRepository.save(entity);
        return new JsonResponse<>(HttpStatus.CREATED, "Created");
    }

    @PostMapping
    public JsonResponse<Boolean> edit(@RequestBody ProductBody body) throws StripeException {
        Optional<CategoryEntity> categoryOpt = categoryRepository.findById(body.categoryId);
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
        Price oldPrice = null;

        if(entity.getPrice() != body.price) {
            PriceCreateParams priceParams = PriceCreateParams.builder()
                    .setUnitAmount(Math.round(body.price*100.0))
                    .setCurrency(properties.getStripeCurrency())
                    .setTaxBehavior(PriceCreateParams.TaxBehavior.EXCLUSIVE)
                    .setProduct(entity.getStripeId())
                    .build();
            Price price = Price.create(priceParams);
            oldPrice = Price.retrieve(priceId);
            priceId = price.getId();
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
        entity.setDisplayTax(body.displayTax);
        entity.setActions(body.actions);

        product.update(productParams);
        productRepository.save(entity);

        if(oldPrice != null) {
            PriceUpdateParams updatePriceParams = PriceUpdateParams.builder()
                    .setActive(false)
                    .build();
            oldPrice.update(updatePriceParams);
        }
        return new JsonResponse<>(HttpStatus.OK, "Updated");
    }


    @DeleteMapping
    public JsonResponse<Boolean> delete(@RequestBody Integer id) throws StripeException {
        Optional<ProductEntity> productOpt = productRepository.findById(id);
        if(productOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.NOT_FOUND, "Product not found");
        }
        ProductEntity entity = productOpt.get();
        Product product = Product.retrieve(entity.getStripeId());
        ProductUpdateParams params = ProductUpdateParams.builder()
                .setActive(false)
                .build();
        product.update(params);
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

    @PutMapping("/rcon")
    public JsonResponse<Boolean> addRcon(@RequestBody RconEntity body) throws Exception {
        body.setPassword(AesUtil.encrypt(body.getPassword(), properties.getAesSecretKey()));
        rconRepository.save(body);
        return new JsonResponse<>(HttpStatus.OK, "Added");
    }

    @PostMapping("/rcon")
    public JsonResponse<Boolean> updateRcon(@RequestBody RconEntity body) throws Exception {
        body.setPassword(AesUtil.encrypt(body.getPassword(), properties.getAesSecretKey()));
        rconRepository.save(body);
        return new JsonResponse<>(HttpStatus.OK, "Updated");
    }

    @DeleteMapping("/rcon")
    public JsonResponse<Boolean> deleteRcon(@RequestBody Integer id) {
        rconRepository.deleteById(id);
        return new JsonResponse<>(HttpStatus.OK, "Deleted");
    }

    @GetMapping("/rcon")
    public JsonResponse<List<RconDto>> getRcons() {
        List<RconDto> result = new ArrayList<>();
        rconRepository.findAll().forEach(entity -> {
            result.add(new RconDto(entity));
        });
        return new JsonResponse<>(HttpStatus.OK, "List of available rcons", result);
    }

    @PutMapping("/api")
    public JsonResponse<Boolean> addApi(@RequestBody ApiEntity body) throws Exception {
        body.setToken(AesUtil.encrypt(body.getToken(), properties.getAesSecretKey()));
        apiRepository.save(body);
        return new JsonResponse<>(HttpStatus.OK, "Added");
    }

    @PostMapping("/api")
    public JsonResponse<Boolean> updateApi(@RequestBody ApiEntity body) throws Exception {
        body.setToken(AesUtil.encrypt(body.getToken(), properties.getAesSecretKey()));
        apiRepository.save(body);
        return new JsonResponse<>(HttpStatus.OK, "Updated");
    }

    @DeleteMapping("/api")
    public JsonResponse<Boolean> deleteApi(@RequestBody Integer id) {
        apiRepository.deleteById(id);
        return new JsonResponse<>(HttpStatus.OK, "Deleted");
    }

    @GetMapping("/api")
    public JsonResponse<List<ApiDto>> getApis() {
        List<ApiDto> result = new ArrayList<>();
        apiRepository.findAll().forEach(entity -> {
            result.add(new ApiDto(entity));
        });
        return new JsonResponse<>(HttpStatus.OK, "List of available apis", result);
    }

    @PutMapping("/secret")
    public JsonResponse<Boolean> addSecret(@RequestBody ProductSecretEntity body) throws Exception {
        body.setSecret(AesUtil.encrypt(body.getSecret(), properties.getAesSecretKey()));
        productSecretRepository.save(body);
        return new JsonResponse<>(HttpStatus.OK, "Added");
    }

    @PostMapping("/secret")
    public JsonResponse<Boolean> updateSecret(@RequestBody ProductSecretEntity body) throws Exception {
        body.setSecret(AesUtil.encrypt(body.getSecret(), properties.getAesSecretKey()));
        productSecretRepository.save(body);
        return new JsonResponse<>(HttpStatus.OK, "Updated");
    }

    @DeleteMapping("/secret")
    public JsonResponse<Boolean> deleteSecret(@RequestBody Integer id) {
        productSecretRepository.deleteById(id);
        return new JsonResponse<>(HttpStatus.OK, "Deleted");
    }

    @GetMapping("/secret")
    public JsonResponse<List<ProductSecretDto>> getSecrets() {
        List<ProductSecretDto> result = new ArrayList<>();
        productSecretRepository.findAll().forEach(entity -> {
            result.add(new ProductSecretDto(entity));
        });
        return new JsonResponse<>(HttpStatus.OK, "List of available secrets", result);
    }

    @PutMapping("/key")
    public JsonResponse<Boolean> addKey(@RequestBody ProductActionKeyEntity body) throws Exception {
        body.setKey(AesUtil.encrypt(body.getKey(), properties.getAesSecretKey()));
        keyRepository.save(body);
        return new JsonResponse<>(HttpStatus.OK, "Added");
    }

    @DeleteMapping("/key")
    public JsonResponse<Boolean> deleteKey(@RequestBody Integer id) {
        keyRepository.deleteById(id);
        return new JsonResponse<>(HttpStatus.OK, "Deleted");
    }

    @GetMapping("/key")
    public JsonResponse<List<ProductActionKeyDto>> getKeys() {
        List<ProductActionKeyDto> result = new ArrayList<>();
        keyRepository.findAll().forEach(entity -> {
            result.add(new ProductActionKeyDto(entity));
        });
        return new JsonResponse<>(HttpStatus.OK, "List of available keys", result);
    }

    @GetMapping("/key/names")
    public JsonResponse<List<String>> getKeyNames() {
        List<String> result = new ArrayList<>();
        keyRepository.findAll().forEach(entity -> {
            if(!result.contains(entity.getKey())) result.add(entity.getKey());
        });
        return new JsonResponse<>(HttpStatus.OK, "List of available key names", result);
    }

    public record ProductBody(int id, String name, String description, String nameInUrl, String image, double price, String taxCode, double displayTax, int categoryId, List<ProductFieldEntity> fields, List<ProductActionEntity> actions) {}
    public record TaxData(boolean hasMore, List<TaxCode> codes) {}
}
