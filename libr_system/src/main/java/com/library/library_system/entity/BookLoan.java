package com.library.library_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import java.time.LocalDate;

/**
 * Сущность выдачи книги.
 */
@Entity
@Table(name = "book_loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long id;

    @NotNull(message = "Книга обязательна")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull(message = "Читатель обязателен")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @NotNull(message = "Библиотекарь обязателен")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "librarian_id", nullable = false)
    private Librarian librarian;

    @NotNull(message = "Дата выдачи обязательна")
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate = LocalDate.now();

    @NotNull(message = "Срок возврата обязателен")
    @FutureOrPresent(message = "Срок возврата должен быть сегодня или в будущем")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;
}