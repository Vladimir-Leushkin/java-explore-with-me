package explore.with.me.category;

import explore.with.me.category.dto.CategoryDto;
import explore.with.me.category.model.Category;
import explore.with.me.exception.ConflictException;
import explore.with.me.exception.NotFoundException;
import explore.with.me.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository repository;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category;
        category = CategoryMapper.toCategory(categoryDto);
        if (category.getName() == null) {
            throw new ValidationException("Имя категории не может быть пустым");
        }
        try {
            Category saveCategory = repository.save(category);
            log.info("Добавлена новая категория : {}", category);
            return CategoryMapper.toCategoryDto(saveCategory);
        } catch (DataIntegrityViolationException e) {
            log.info("Категория с таким именем уже существует {}", category.getName());
            throw new ConflictException("Категория с таким именем уже существует");
        }
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest pageRequest = pagination(from, size);
        List<Category> categories = repository.findAll(pageRequest).toList();
        log.info("Найдены категории {}, ", categories);
        List<CategoryDto> categoriesDto = categories
                .stream()
                .map(category -> CategoryMapper.toCategoryDto(category)).collect(Collectors.toList());
        return categoriesDto;
    }

    public Category getCategory(Integer categoryId) {
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Не найдена категория"));
        return category;
    }

    @Transactional
    public Category updateCategory(CategoryDto newCategoryDto) {
        if (newCategoryDto.getId() == null || newCategoryDto.getName() == null) {
            throw new ValidationException("Неверные параметры категории");
        }
        Category category = CategoryMapper.toCategory(newCategoryDto);
        Category oldCategory = getCategory(category.getId());
        List<String> names = getAllCategory()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        if (names.contains(category.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        Category saveCategory = repository.save(category);
        log.info("Обновлена категория : {}", saveCategory);
        return saveCategory;
    }

    public List<Category> getAllCategory() {
        return repository.findAll();
    }

    @Transactional
    public void deleteCategory(Integer categoryId) {
        Category category = getCategory(categoryId);
        log.info("Удалена категория : {}", category);
        repository.deleteById(categoryId);
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.unsorted());
    }
}
