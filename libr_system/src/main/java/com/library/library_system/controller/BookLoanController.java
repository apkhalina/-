package com.library.library_system.controller;

import com.library.library_system.entity.BookLoan;
import com.library.library_system.service.BookLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Контроллер для управления выдачами книг.
 */
@Controller
@RequestMapping("/loans")
public class BookLoanController {

    @Autowired
    private BookLoanService bookLoanService;

    /**
     * Список всех выдач.
     */
    @GetMapping
    public String listLoans(Model model) {
        try {
            List<BookLoan> loans = bookLoanService.getAllLoans();
            List<BookLoan> activeLoans = bookLoanService.getActiveLoans();
            List<BookLoan> overdueLoans = bookLoanService.getOverdueLoans();

            model.addAttribute("loans", loans);
            model.addAttribute("activeLoans", activeLoans);
            model.addAttribute("overdueLoans", overdueLoans);
            model.addAttribute("today", LocalDate.now());

            return "loans/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка загрузки выдач: " + e.getMessage());
            model.addAttribute("loans", List.of());
            model.addAttribute("activeLoans", List.of());
            model.addAttribute("overdueLoans", List.of());
            model.addAttribute("today", LocalDate.now());
            return "loans/list";
        }
    }

    /**
     * Форма создания новой выдачи.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("loan", new BookLoan());
            model.addAttribute("books", bookLoanService.getAllBooks());
            model.addAttribute("readers", bookLoanService.getAllReaders());
            model.addAttribute("librarians", bookLoanService.getAllLibrarians());
            model.addAttribute("defaultDueDate", LocalDate.now().plusDays(14));

            return "loans/form";
        } catch (Exception e) {
            return "redirect:/loans";
        }
    }

    /**
     * Создание новой выдачи.
     * @param loan данные выдачи из формы
     */
    @PostMapping
    public String createLoan(@ModelAttribute("loan") BookLoan loan,
                             RedirectAttributes redirectAttributes) {
        try {
            if (loan.getLoanDate() == null) {
                loan.setLoanDate(LocalDate.now());
            }

            bookLoanService.saveLoan(loan);
            redirectAttributes.addFlashAttribute("successMessage", "Книга успешно выдана!");
            return "redirect:/loans";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при выдаче книги: " + e.getMessage());
            return "redirect:/loans";
        }
    }

    /**
     * Возврат книги.
     * @param id ID выдачи для возврата
     */
    @GetMapping("/return/{id}")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookLoanService.returnBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Книга возвращена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при возврате книги: " + e.getMessage());
        }
        return "redirect:/loans";
    }

    /**
     * Удаление выдачи.
     * @param id ID выдачи для удаления
     */
    @GetMapping("/delete/{id}")
    public String deleteLoan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookLoanService.deleteLoan(id);
            redirectAttributes.addFlashAttribute("successMessage", "Запись о выдаче удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/loans";
    }

    /**
     * Поиск выдач по ключевому слову.
     * @param keyword ключевое слово для поиска
     */
    @GetMapping("/search")
    public String searchLoans(@RequestParam(value = "keyword", required = false) String keyword,
                              Model model) {
        try {
            List<BookLoan> loans;
            List<BookLoan> activeLoans = bookLoanService.getActiveLoans();
            List<BookLoan> overdueLoans = bookLoanService.getOverdueLoans();

            if (keyword != null && !keyword.trim().isEmpty()) {
                loans = bookLoanService.searchLoans(keyword);
                model.addAttribute("searchKeyword", keyword);
                System.out.println("Поиск выдач по запросу: '" + keyword + "' - найдено: " + loans.size());
            } else {
                loans = bookLoanService.getAllLoans();
            }

            model.addAttribute("loans", loans);
            model.addAttribute("activeLoans", activeLoans);
            model.addAttribute("overdueLoans", overdueLoans);
            model.addAttribute("today", LocalDate.now());

            return "loans/list";

        } catch (Exception e) {
            System.err.println("Ошибка поиска выдач: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Ошибка поиска: " + e.getMessage());
            model.addAttribute("loans", bookLoanService.getAllLoans());
            model.addAttribute("activeLoans", bookLoanService.getActiveLoans());
            model.addAttribute("overdueLoans", bookLoanService.getOverdueLoans());
            model.addAttribute("today", LocalDate.now());
            return "loans/list";
        }
    }

    /**
     * Список активных выдач.
     */
    @GetMapping("/active")
    public String getActiveLoans(Model model) {
        try {
            List<BookLoan> activeLoans = bookLoanService.getActiveLoans();
            model.addAttribute("loans", activeLoans);
            model.addAttribute("activeLoans", activeLoans);
            model.addAttribute("overdueLoans", bookLoanService.getOverdueLoans());
            model.addAttribute("today", LocalDate.now());
            model.addAttribute("activeFilter", "active");

            return "loans/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка загрузки активных выдач: " + e.getMessage());
            model.addAttribute("loans", List.of());
            model.addAttribute("activeLoans", List.of());
            model.addAttribute("overdueLoans", List.of());
            model.addAttribute("today", LocalDate.now());
            return "loans/list";
        }
    }

    /**
     * Список просроченных выдач.
     */
    @GetMapping("/overdue")
    public String getOverdueLoans(Model model) {
        try {
            List<BookLoan> overdueLoans = bookLoanService.getOverdueLoans();
            model.addAttribute("loans", overdueLoans);
            model.addAttribute("activeLoans", bookLoanService.getActiveLoans());
            model.addAttribute("overdueLoans", overdueLoans);
            model.addAttribute("today", LocalDate.now());
            model.addAttribute("activeFilter", "overdue");

            return "loans/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка загрузки просроченных выдач: " + e.getMessage());
            model.addAttribute("loans", List.of());
            model.addAttribute("activeLoans", List.of());
            model.addAttribute("overdueLoans", List.of());
            model.addAttribute("today", LocalDate.now());
            return "loans/list";
        }
    }

    /**
     * Список возвращенных книг.
     */
    @GetMapping("/returned")
    public String getReturnedLoans(Model model) {
        try {
            List<BookLoan> allLoans = bookLoanService.getAllLoans();
            List<BookLoan> returnedLoans = new ArrayList<>();

            for (BookLoan loan : allLoans) {
                if (loan.getReturnDate() != null) {
                    returnedLoans.add(loan);
                }
            }

            int returnedCount = returnedLoans.size();

            model.addAttribute("loans", returnedLoans);
            model.addAttribute("activeLoans", bookLoanService.getActiveLoans());
            model.addAttribute("overdueLoans", bookLoanService.getOverdueLoans());
            model.addAttribute("today", LocalDate.now());
            model.addAttribute("activeFilter", "returned");
            model.addAttribute("returnedCount", returnedCount);

            return "loans/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка загрузки возвращенных выдач: " + e.getMessage());
            model.addAttribute("loans", List.of());
            model.addAttribute("activeLoans", List.of());
            model.addAttribute("overdueLoans", List.of());
            model.addAttribute("today", LocalDate.now());
            return "loans/list";
        }
    }
}