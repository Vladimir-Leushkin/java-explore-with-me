package explore.with.me.category;

import explore.with.me.category.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping("/admin/categories")
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto saveCategoryDto = categoryService.createCategory(categoryDto);
        return saveCategoryDto;
    }

    @GetMapping("/categories")
    public List<CategoryDto> readCategories(
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<CategoryDto> categoriesDto = categoryService.getCategories(from, size);
        return categoriesDto;
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto readCategory(@PathVariable Integer catId) {
        return categoryMapper.toCategoryDto(categoryService.getCategory(catId));
    }

    @PatchMapping("/admin/categories")
    public CategoryDto updateCategory(
            @RequestBody CategoryDto categoryDto) {
        return CategoryMapper.toCategoryDto(categoryService.updateCategory(categoryDto));
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void deleteCategory(@PathVariable Integer catId) {
        categoryService.deleteCategory(catId);
    }
}
