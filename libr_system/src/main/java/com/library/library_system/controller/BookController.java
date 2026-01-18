package com.library.library_system.controller;

import com.library.library_system.entity.Book;
import com.library.library_system.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 * Контроллер для управления книгами.
 * Обрабатывает запросы по пути /books.
 */
@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * Отображает список всех книг.
     */
    @GetMapping
    public String listBooks(Model model) {
        try {
            List<Book> books = bookService.getAllBooks();
            model.addAttribute("books", books);
            return "books/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка загрузки книг: " + e.getMessage());
            model.addAttribute("books", List.of());
            return "books/list";
        }
    }

    /**
     * Показывает форму для создания новой книги.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }

    /**
     * Создает новую книгу.
     * @param book данные книги из формы
     * @param result результаты валидации
     */
    @PostMapping
    public String createBook(@Valid @ModelAttribute("book") Book book,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "books/form";
        }

        if (bookService.existsByBookNumber(book.getBookNumber())) {
            result.rejectValue("bookNumber", "error.book", "Номер книги уже существует");
            return "books/form";
        }

        try {
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("successMessage", "Книга добавлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении: " + e.getMessage());
        }
        return "redirect:/books";
    }

    /**
     * Показывает форму для редактирования книги.
     * @param id ID книги для редактирования
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Book book = bookService.getBookById(id);
            if (book == null) {
                return "redirect:/books";
            }
            model.addAttribute("book", book);
            return "books/form";
        } catch (Exception e) {
            return "redirect:/books";
        }
    }

    /**
     * Обновляет информацию о книге.
     * @param id ID книги для обновления
     * @param book обновленные данные книги
     */
    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute("book") Book book,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "books/form";
        }

        try {
            Book existingBook = bookService.getBookById(id);

            if (existingBook != null && !existingBook.getBookNumber().equals(book.getBookNumber())) {
                if (bookService.existsByBookNumber(book.getBookNumber())) {
                    result.rejectValue("bookNumber", "error.book", "Номер книги уже существует");
                    return "books/form";
                }
            }

            book.setId(id);
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("successMessage", "Книга обновлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
        }
        return "redirect:/books";
    }

    /**
     * Удаляет книгу.
     * @param id ID книги для удаления
     */
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Книга удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/books";
    }

    /**
     * Ищет книги по ключевому слову.
     * @param keyword ключевое слово для поиска
     */
    @GetMapping("/search")
    public String searchBooks(@RequestParam(value = "keyword", required = false) String keyword,
                              Model model) {
        try {
            List<Book> books;

            if (keyword != null && !keyword.trim().isEmpty()) {
                books = bookService.searchBooks(keyword);
                model.addAttribute("searchKeyword", keyword);
                System.out.println("Поиск по: '" + keyword + "' - найдено: " + books.size());
            } else {
                books = bookService.getAllBooks();
            }

            model.addAttribute("books", books);
            model.addAttribute("pageTitle", "Результаты поиска");

        } catch (Exception e) {
            System.err.println("Ошибка поиска: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Ошибка поиска: " + e.getMessage());
            model.addAttribute("books", List.of());
        }

        return "books/list";
    }
}