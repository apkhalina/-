package com.library.library_system.service;

import com.library.library_system.entity.*;
import com.library.library_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления выдачами книг.
 */
@Service
@Transactional
public class BookLoanService {

    @Autowired
    private BookLoanRepository bookLoanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private LibrarianRepository librarianRepository;

    /**
     * Ищет выдачи по ключевому слову.
     * @param keyword ключевое слово для поиска
     */
    public List<BookLoan> searchLoans(String keyword) {
        try {
            System.out.println("BookLoanService: Поиск выдач по ключевому слову: " + keyword);

            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllLoans();
            }

            String searchTerm = keyword.trim().toLowerCase();
            List<BookLoan> allLoans = getAllLoans();
            List<BookLoan> result = new ArrayList<>();

            for (BookLoan loan : allLoans) {
                boolean found = false;

                if (loan.getBook() != null && loan.getBook().getTitle() != null &&
                        loan.getBook().getTitle().toLowerCase().contains(searchTerm)) {
                    result.add(loan);
                    found = true;
                }

                if (!found && loan.getBook() != null && loan.getBook().getAuthor() != null &&
                        loan.getBook().getAuthor().toLowerCase().contains(searchTerm)) {
                    result.add(loan);
                    found = true;
                }

                if (!found && loan.getReader() != null && loan.getReader().getFullName() != null &&
                        loan.getReader().getFullName().toLowerCase().contains(searchTerm)) {
                    result.add(loan);
                    found = true;
                }

                if (!found && loan.getLibrarian() != null && loan.getLibrarian().getFullName() != null &&
                        loan.getLibrarian().getFullName().toLowerCase().contains(searchTerm)) {
                    result.add(loan);
                    found = true;
                }

                if (!found && loan.getReader() != null && loan.getReader().getTicketNumber() != null &&
                        loan.getReader().getTicketNumber().toLowerCase().contains(searchTerm)) {
                    result.add(loan);
                    found = true;
                }

                if (!found && loan.getReader() != null && loan.getReader().getPhoneNumber() != null &&
                        loan.getReader().getPhoneNumber().toLowerCase().contains(searchTerm)) {
                    result.add(loan);
                    found = true;
                }

                if (!found && loan.getBook() != null && loan.getBook().getBookNumber() != null &&
                        loan.getBook().getBookNumber().toLowerCase().contains(searchTerm)) {
                    result.add(loan);
                }
            }

            System.out.println("Найдено выдач: " + result.size());
            return result;

        } catch (Exception e) {
            System.err.println("Ошибка при поиске выдач: " + e.getMessage());
            e.printStackTrace();
            return getAllLoans();
        }
    }

    /**
     * Получает все выдачи.
     */
    public List<BookLoan> getAllLoans() {
        try {
            System.out.println("BookLoanService: Получение всех выдач...");

            try {
                return bookLoanRepository.findAllWithDetails();
            } catch (Exception e) {
                System.err.println("Метод findAllWithDetails не работает, используем findAll: " + e.getMessage());
                return bookLoanRepository.findAll();
            }

        } catch (Exception e) {
            System.err.println("Ошибка получения всех выдач: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Получает активные выдачи.
     */
    public List<BookLoan> getActiveLoans() {
        try {
            System.out.println("BookLoanService: Получение активных выдач...");
            return bookLoanRepository.findActiveLoans();
        } catch (Exception e) {
            System.err.println("Ошибка получения активных выдач: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Получает просроченные выдачи.
     */
    public List<BookLoan> getOverdueLoans() {
        try {
            System.out.println("BookLoanService: Получение просроченных выдач...");
            return bookLoanRepository.findOverdueLoans(LocalDate.now());
        } catch (Exception e) {
            System.err.println("Ошибка получения просроченных выдач: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Сохраняет выдачу.
     * @param loan объект выдачи
     */
    public BookLoan saveLoan(BookLoan loan) {
        try {
            System.out.println("BookLoanService: Сохранение выдачи...");
            return bookLoanRepository.save(loan);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения выдачи: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось сохранить выдачу: " + e.getMessage());
        }
    }

    /**
     * Возвращает книгу.
     * @param loanId ID выдачи
     */
    public BookLoan returnBook(Long loanId) {
        try {
            System.out.println("BookLoanService: Возврат книги ID=" + loanId);

            BookLoan loan = bookLoanRepository.findById(loanId)
                    .orElseThrow(() -> new RuntimeException("Выдача с ID " + loanId + " не найдена"));

            if (loan.getReturnDate() == null) {
                loan.setReturnDate(LocalDate.now());
                return bookLoanRepository.save(loan);
            }

            return loan;
        } catch (Exception e) {
            System.err.println("Ошибка возврата книги: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось вернуть книгу: " + e.getMessage());
        }
    }

    /**
     * Удаляет выдачу.
     * @param id ID выдачи для удаления
     */
    public void deleteLoan(Long id) {
        try {
            System.out.println("BookLoanService: Удаление выдачи ID=" + id);

            if (bookLoanRepository.existsById(id)) {
                bookLoanRepository.deleteById(id);
            } else {
                throw new RuntimeException("Выдача с ID " + id + " не найдена");
            }
        } catch (Exception e) {
            System.err.println("Ошибка удаления выдачи: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось удалить выдачу: " + e.getMessage());
        }
    }

    /**
     * Получает все книги.
     */
    public List<Book> getAllBooks() {
        try {
            System.out.println("BookLoanService: Получение всех книг...");
            return bookRepository.findAll();
        } catch (Exception e) {
            System.err.println("Ошибка получения книг: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Получает всех читателей.
     */
    public List<Reader> getAllReaders() {
        try {
            System.out.println("BookLoanService: Получение всех читателей...");
            return readerRepository.findAll();
        } catch (Exception e) {
            System.err.println("Ошибка получения читателей: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Получает всех библиотекарей.
     */
    public List<Librarian> getAllLibrarians() {
        try {
            System.out.println("BookLoanService: Получение всех библиотекарей...");
            return librarianRepository.findAll();
        } catch (Exception e) {
            System.err.println("Ошибка получения библиотекарей: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}