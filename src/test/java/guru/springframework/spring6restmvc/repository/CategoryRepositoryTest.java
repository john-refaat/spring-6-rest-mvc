package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Category;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 05/08/2024
 */
@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
    }

    @Rollback
    @Transactional
    @Test
    void createCategory() {
        // Given
        Category category = Category.builder()
                .description("New Category")
                .build();

        // When
        Category savedCategory = categoryRepository.save(category);

        // Then
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getId());
    }
}