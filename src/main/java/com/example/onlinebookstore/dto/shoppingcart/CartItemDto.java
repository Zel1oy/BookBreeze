package com.example.onlinebookstore.dto.shoppingcart;

import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    private String bookTitle;
    private Long bookId;
    private int quantity;
}
