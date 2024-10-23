package eu.kpgtb.shop.controller;

import com.stripe.model.Product;
import eu.kpgtb.shop.data.entity.product.Category;
import eu.kpgtb.shop.data.repository.product.CategoryRepository;
import eu.kpgtb.shop.data.repository.product.ProductRepository;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/list")
    public JsonResponse<List<CategoryInfo>> list() {
        List<CategoryInfo> result = new ArrayList<>();

        categoryRepository.findAll().forEach(category -> {
            result.add(new CategoryInfo(
                    category.getId(),
                    category.getName(),
                    category.getNameInUrl()
            ));
        });

        return new JsonResponse<>(HttpStatus.OK, "List of categories", result);
    }

    @GetMapping("/list/full")
    public JsonResponse<List<Category.CategoryDisplay>> listFull() {
        List<Category.CategoryDisplay> result = new ArrayList<>();

        categoryRepository.findAll().forEach(category -> {
            result.add(category.getDisplay());
        });

        return new JsonResponse<>(HttpStatus.OK, "List of categories", result);
    }

    @GetMapping
    public JsonResponse<Category.CategoryDisplay> info(@RequestParam(name = "id") int id) {
        Optional<Category> result = categoryRepository.findById(id);

        return result
                .map(category -> new JsonResponse<>(HttpStatus.OK, "Category info", category.getDisplay()))
                .orElseGet(() -> new JsonResponse<>(HttpStatus.NOT_FOUND, "Category not found"));
    }

    @PutMapping
    public JsonResponse<Boolean> create(CategoryCreateBody data) {
        categoryRepository.save(new Category(data.name,data.description, data.nameInUrl,new ArrayList<>()));
        return new JsonResponse<>(HttpStatus.CREATED, "Created");
    }

    @PostMapping
    public JsonResponse<Boolean> edit(CategoryEditBody data) {
        Optional<Category> categoryOpt = categoryRepository.findById(data.id);
        if(categoryOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.NOT_FOUND, "Category not found");
        }

        Category category = categoryOpt.get();
        category.setName(data.name);
        category.setDescription(data.description);
        category.setNameInUrl(data.nameInUrl);
        categoryRepository.save(category);
        return new JsonResponse<>(HttpStatus.OK, "Updated");
    }

    @DeleteMapping
    public JsonResponse<Boolean> delete(@RequestBody String idStr) {
        int id = Integer.parseInt(idStr);
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if(categoryOpt.isEmpty()) {
            return new JsonResponse<>(HttpStatus.NOT_FOUND, "Category not found");
        }

        Category category = categoryOpt.get();
        category.getProducts().forEach(product -> {
            try {
                Product.retrieve(product.getStripeId()).delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            productRepository.delete(product);
        });

        categoryRepository.deleteById(id);
        return new JsonResponse<>(HttpStatus.OK, "Deleted");
    }


    record CategoryInfo(int id, String name, String nameInUrl) {}
    record CategoryCreateBody(String name, String description, String nameInUrl) {}
    record CategoryEditBody(int id, String name, String description, String nameInUrl) {}
}
