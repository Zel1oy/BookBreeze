package com.example.onlinebookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;

import lombok.Data;
import org.hibernate.validator.constraints.ISBN;

@Data
public class CreateBookRequestDto {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    private String title;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    private String author;
    @NotNull
    @NotBlank
    @ISBN
    private String isbn;
    @NotNull
    @Min(0)
    private BigDecimal price;
    @Size(min = 1, max = 255)
    private String description;
    @NotEmpty
    @NotNull
    private String coverImage;
    @NotEmpty
    private Set<Long> categories;
}
