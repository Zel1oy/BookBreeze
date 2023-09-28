package com.example.onlinebookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.onlinebookstore.config.SqlFilesPaths;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.cart.CartItemRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {
    private static final Book DEFAULT_BOOK = new Book();
    private static final User DEFAULT_USER = new User();
    private static final ShoppingCart VALID_SHOPPING_CART = new ShoppingCart();
    private static final CartItem VALID_CART_ITEM = new CartItem();
    private static final Long INVALID_ID = -1L;

    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeEach
    void setUp() {
        DEFAULT_BOOK.setId(1L);
        DEFAULT_BOOK.setTitle("Title 1");
        DEFAULT_BOOK.setAuthor("Author 1");
        DEFAULT_BOOK.setIsbn("978-0307743657");
        DEFAULT_BOOK.setPrice(BigDecimal.valueOf(100));

        DEFAULT_USER.setId(1L);
        DEFAULT_USER.setEmail("email@i.ua");
        DEFAULT_USER.setPassword("password");
        DEFAULT_USER.setFirstName("Denis");
        DEFAULT_USER.setLastName("Unknown");

        VALID_CART_ITEM.setId(1L);
        VALID_CART_ITEM.setBook(DEFAULT_BOOK);
        VALID_CART_ITEM.setQuantity(100);
        VALID_CART_ITEM.setShoppingCart(VALID_SHOPPING_CART);

        VALID_SHOPPING_CART.setId(1L);
        VALID_SHOPPING_CART.setUser(DEFAULT_USER);
        VALID_SHOPPING_CART.setCartItems(Collections.singleton(VALID_CART_ITEM));
    }

    @Test
    @DisplayName("Find cart items by shopping cart id")
    @Sql(scripts = SqlFilesPaths.CART_ITEM_INSERT,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SqlFilesPaths.CART_ITEM_DELETE,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findCartItemsByShoppingCartId_validShoppingCartId_returnOneItem() {
        Optional<CartItem> actual = cartItemRepository.findByIdAndShoppingcartId(1L, 1L);
        assertNotNull(actual);
        assertEquals(Optional.of(VALID_CART_ITEM), actual);
    }

    @Test
    @DisplayName("Find cart items by non-existent shopping cart id")
    void findCartItemsByShoppingCartId_nonExistentShoppingCartId_returnEmptySet() {
        Optional<CartItem> actual = cartItemRepository
                .findByIdAndShoppingcartId(INVALID_ID, INVALID_ID);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }
}
