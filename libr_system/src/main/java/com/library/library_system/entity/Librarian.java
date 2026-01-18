package com.library.library_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность библиотекаря.
 */
@Entity
@Table(name = "librarians")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Librarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "librarian_id")
    private Long id;

    @NotBlank(message = "Номер библиотекаря обязателен")
    @Column(name = "librarian_number", unique = true, nullable = false)
    private String librarianNumber;

    @NotBlank(message = "ФИО обязательно")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "position")
    private String position = "Библиотекарь";

    @OneToMany(mappedBy = "librarian", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookLoan> loans = new ArrayList<>();
}