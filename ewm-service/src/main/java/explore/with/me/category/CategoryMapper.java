package explore.with.me.category;

import explore.with.me.category.dto.CategoryDto;
import explore.with.me.category.model.Category;
import org.springframework.stereotype.Component;


@Component
public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName()
        );
    }
}
