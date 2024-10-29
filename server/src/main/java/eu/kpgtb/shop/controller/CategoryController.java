package eu.kpgtb.shop.controller;

import com.stripe.model.Product;
import com.stripe.param.ProductUpdateParams;
import eu.kpgtb.shop.data.dto.product.CategoryDto;
import eu.kpgtb.shop.data.entity.product.CategoryEntity;
import eu.kpgtb.shop.data.repository.product.CategoryRepository;
import eu.kpgtb.shop.data.repository.product.ProductRepository;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;

    @GetMapping("/list/base")
    public JsonResponse<List<CategoryDto>> base() {
        List<CategoryDto> result = new ArrayList<>();
        categoryRepository.findAll().forEach(entity -> result.add(new CategoryDto(entity)));
        return new JsonResponse<>(HttpStatus.OK, "List of categories", result);
    }

    @GetMapping("/list/expanded")
    public JsonResponse<List<CategoryDto>> expanded() {
        List<CategoryDto> result = new ArrayList<>();

        categoryRepository.findAll().forEach(entity -> {
            result.add(new CategoryDto(entity,Arrays.asList("products", "products.fields", "products.fields.options"),""));
        });

        return new JsonResponse<>(HttpStatus.OK, "List of categories", result);
    }

    @GetMapping("/{categoryId}")
    public JsonResponse<CategoryDto> info(@PathVariable int categoryId) {
        Optional<CategoryEntity> result = categoryRepository.findById(categoryId);

        return result
                .map(category -> new JsonResponse<>(HttpStatus.OK, "Category info", new CategoryDto(
                        category, Arrays.asList("products", "products.fields", "products.fields.options"),""
                )))
                .orElseGet(() -> new JsonResponse<>(HttpStatus.NOT_FOUND, "Category not found"));
    }

    @PutMapping
    public JsonResponse<Boolean> create(@RequestBody CategoryDto data) {
        categoryRepository.save(new CategoryEntity(
                data.getName(),
                data.getDescription(),
                data.getNameInUrl(),
                new ArrayList<>()
        ));
        return new JsonResponse<>(HttpStatus.CREATED, "Created");
    }

    @PostMapping
    public JsonResponse<Boolean> edit(@RequestBody CategoryDto data) {
        CategoryEntity entity = this.categoryRepository.findById(data.getId()).orElse(null);
        if(entity == null) return new JsonResponse<>(HttpStatus.NOT_FOUND, "Category not found");
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setNameInUrl(data.getNameInUrl());
        this.categoryRepository.save(entity);
        return new JsonResponse<>(HttpStatus.OK, "Updated");
    }

    @DeleteMapping
    public JsonResponse<Boolean> delete(@RequestBody Integer id) {
        Optional<CategoryEntity> categoryOpt = categoryRepository.findById(id);
        if(categoryOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.NOT_FOUND, "Category not found");
        }

        CategoryEntity category = categoryOpt.get();
        category.getProducts().forEach(product -> {
            try {
                Product.retrieve(product.getStripeId()).update(
                        ProductUpdateParams.builder()
                                .setActive(false).build()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        categoryRepository.deleteById(id);
        return new JsonResponse<>(HttpStatus.OK, "Deleted");
    }
}
