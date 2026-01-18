package com.library.library_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Сущность книги.
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @NotBlank(message = "Номер книги обязателен")
    @Column(name = "book_number", unique = true, nullable = false)
    private String bookNumber;

    @NotBlank(message = "Название книги обязательно")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Автор обязателен")
    @Column(name = "author", nullable = false)
    private String author;

    @NotNull(message = "Год издания обязателен")
    @Min(value = 1500, message = "Год должен быть не ранее 1500")
    @Max(value = 2025, message = "Год должен быть не позже текущего")
    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;
}