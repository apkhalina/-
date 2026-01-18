package com.library.library_system.service;

import com.library.library_system.entity.Book;
import com.library.library_system.repository.BookRepository;
import com.library.library_system.repository.BookLoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления книгами.
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookLoanRepository bookLoanRepository;

    /**
     * Получает все книги.
     */
    public List<Book> getAllBooks() {
        try {
            return bookRepository.findAll();
        } catch (Exception e) {
            System.err.println("Ошибка при получении книг: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Находит книгу по ID.
     * @param id ID книги
     */
    public Book getBookById(Long id) {
        try {
            return bookRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Ошибка при поиске книги по ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Сохраняет книгу.
     * @param book объект книги
     */
    public Book saveBook(Book book) {
        try {
            Book existingBook = bookRepository.findByBookNumber(book.getBookNumber());

            if (book.getId() == null) {
                if (existingBook != null) {
                    throw new RuntimeException("Книга с номером '" + book.getBookNumber() + "' уже существует");
                }
            } else {
                if (existingBook != null && !existingBook.getId().equals(book.getId())) {
                    throw new RuntimeException("Номер книги '" + book.getBookNumber() + "' уже используется другой книгой");
                }
            }

            return bookRepository.save(book);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении книги: " + e.getMessage());
            throw new RuntimeException("Не удалось сохранить книгу: " + e.getMessage());
        }
    }

    /**
     * Удаляет книгу.
     * @param id ID книги для удаления
     */
    @Transactional
    public void deleteBook(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Книга с ID " + id + " не найдена"));

            boolean hasActiveLoans = bookLoanRepository.existsActiveLoanByBookId(id);

            if (hasActiveLoans) {
                throw new RuntimeException("Нельзя удалить книгу: есть активные выдачи");
            }

            bookRepository.delete(book);
            System.out.println("Книга с ID " + id + " удалена");

        } catch (Exception e) {
            System.err.println("Ошибка при удалении книги: " + e.getMessage());
            throw new RuntimeException("Не удалось удалить книгу: " + e.getMessage());
        }
    }

    /**
     * Ищет книги по ключевому слову.
     * @param keyword ключевое слово для поиска
     */
    public List<Book> searchBooks(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllBooks();
            }

            String searchTerm = keyword.trim().toLowerCase();
            List<Book> allBooks = getAllBooks();
            List<Book> result = new ArrayList<>();

            for (Book book : allBooks) {
                if (book.getTitle() != null && book.getTitle().toLowerCase().contains(searchTerm)) {
                    result.add(book);
                } else if (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(searchTerm)) {
                    result.add(book);
                }
            }

            System.out.println("Поиск '" + searchTerm + "': найдено " + result.size() + " книг");
            return result;

        } catch (Exception e) {
            System.err.println("Ошибка в searchBooks: " + e.getMessage());
            e.printStackTrace();
            return getAllBooks();
        }
    }

    /**
     * Проверяет существование книги по номеру.
     */
    public boolean existsByBookNumber(String bookNumber) {
        return bookRepository.findByBookNumber(bookNumber) != null;
    }

    /**
     * Получает доступные книги.
     */
    public List<Book> getAvailableBooks() {
        try {
            List<Book> allBooks = getAllBooks();
            List<Book> availableBooks = new ArrayList<>();

            for (Book book : allBooks) {
                boolean hasActiveLoan = bookLoanRepository.existsActiveLoanByBookId(book.getId());
                if (!hasActiveLoan) {
                    availableBooks.add(book);
                }
            }

            return availableBooks;
        } catch (Exception e) {
            System.err.println("Ошибка получения доступных книг: " + e.getMessage());
            return getAllBooks();
        }
    }
}