package com.example.onlinebookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.category.CategoryResponseDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.BookMapper;
import com.example.onlinebookstore.mapper.CategoryMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.book.CategoryRepository;
import com.example.onlinebookstore.service.impl.CategoryServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private static CategoryResponseDto categoryDto;
    private static Category category;
    private static CreateCategoryRequestDto requestDto;
    private static BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds;
    private static Book book;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookMapper bookMapper;

    @BeforeAll
    static void beforeAll() {
        category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");
        category.setDeleted(false);

        requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Test Category");
        requestDto.setDescription("Test Description");
        categoryDto = new CategoryResponseDto();
        categoryDto.setId(1L);
        categoryDto.setName("Sample Category");
        categoryDto.setDescription("This is a sample category description.");

        book = new Book();
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
        book.setIsbn("978-3-16-148410-0");
        book.setPrice(new BigDecimal("19.99"));
        book.setDescription("A classic novel about the Jazz Age");
        book.setCoverImage("great_gatsby.jpg");
        Set<Category> categories = new HashSet<>();
        categories.add(new Category());
        book.setCategories(categories);

        bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setTitle("To Kill a Mockingbird");
        bookDtoWithoutCategoryIds.setAuthor("Harper Lee");
        bookDtoWithoutCategoryIds.setIsbn("978-0-446-31078-9");
        bookDtoWithoutCategoryIds.setPrice(new BigDecimal("14.99"));
        bookDtoWithoutCategoryIds.setDescription(
                "A story of racial injustice in the American South");
        bookDtoWithoutCategoryIds.setCoverImage("mockingbird.jpg");
    }

    @Test
    @DisplayName("Test findById with non valid request")
    public void findById_nonExistingId_throwsException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(1L));
        String expectedMessage = "Can't find category by id: 1";
        assertEquals(expectedMessage, actual.getMessage());

        verify(categoryRepository).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("Test getById with valid request")
    public void getById_validId_returnsCategory() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDto(any(Category.class))).thenReturn(categoryDto);

        assertEquals(categoryDto, categoryService.getById(1L));
        verify(categoryRepository).findById(1L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Test findAll with valid request")
    public void findAll_validRequest_returnsSet() {
        Pageable pageable = Pageable.ofSize(5);
        List<CategoryResponseDto> expectedCategoryDtos = Collections.singletonList(categoryDto);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toResponseDto(any(Category.class)))
                .thenReturn(expectedCategoryDtos.iterator().next());
        List<CategoryResponseDto> actual = categoryService.findAll(pageable);

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper, times(categories.size())).toResponseDto(any(Category.class));
        assertEquals(expectedCategoryDtos, actual);
    }

    @Test
    @DisplayName("Test create with valid request")
    public void createCategory_validCategory_returnsCreatedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toEntity(any(CreateCategoryRequestDto.class))).thenReturn(category);
        when(categoryMapper.toResponseDto(any(Category.class))).thenReturn(categoryDto);

        assertEquals(categoryDto, categoryService.save(requestDto));
        verify(categoryRepository).save(category);
        verify(categoryMapper).toEntity(any(CreateCategoryRequestDto.class));
        verify(categoryMapper).toResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("Test update with valid request")
    public void updateCategory_validCategory_returnsUpdatedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toEntity(any(CreateCategoryRequestDto.class))).thenReturn(category);
        when(categoryMapper.toResponseDto(any(Category.class))).thenReturn(categoryDto);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.ofNullable(category));

        assertEquals(categoryDto, categoryService.update(1L, requestDto));
        verify(categoryRepository).save(category);
        verify(categoryMapper).toEntity(any(CreateCategoryRequestDto.class));
        verify(categoryMapper).toResponseDto(any(Category.class));
        verify(categoryRepository).findById(anyLong());

    }

    @Test
    @DisplayName("Test update with non valid request")
    public void updateCategory_nonValidId_throwsException() {
        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(1L, requestDto));
        assertEquals("Can't find category by id: 1", actual.getMessage());
    }
}
