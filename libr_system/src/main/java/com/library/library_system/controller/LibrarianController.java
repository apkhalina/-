package com.library.library_system.controller;

import com.library.library_system.entity.Librarian;
import com.library.library_system.service.LibrarianService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 * Контроллер для управления библиотекарями.
 */
@Controller
@RequestMapping("/librarians")
public class LibrarianController {

    @Autowired
    private LibrarianService librarianService;

    /**
     * Список всех библиотекарей.
     */
    @GetMapping
    public String listLibrarians(Model model) {
        List<Librarian> librarians = librarianService.getAllLibrarians();
        model.addAttribute("librarians", librarians);
        return "librarians/list";
    }

    /**
     * Форма создания нового библиотекаря.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("librarian", new Librarian());
        return "librarians/form";
    }

    /**
     * Создание нового библиотекаря.
     * @param librarian данные библиотекаря из формы
     */
    @PostMapping
    public String createLibrarian(@Valid @ModelAttribute("librarian") Librarian librarian,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "librarians/form";
        }

        if (librarianService.existsByLibrarianNumber(librarian.getLibrarianNumber())) {
            result.rejectValue("librarianNumber", "error.librarian", "Номер библиотекаря уже существует");
            return "librarians/form";
        }

        librarianService.saveLibrarian(librarian);
        redirectAttributes.addFlashAttribute("successMessage", "Библиотекарь добавлен!");
        return "redirect:/librarians";
    }

    /**
     * Форма редактирования библиотекаря.
     * @param id ID библиотекаря для редактирования
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Librarian librarian = librarianService.getLibrarianById(id);
        if (librarian == null) {
            return "redirect:/librarians";
        }
        model.addAttribute("librarian", librarian);
        return "librarians/form";
    }

    /**
     * Обновление информации о библиотекаре.
     * @param id ID библиотекаря для обновления
     * @param librarian обновленные данные библиотекаря
     */
    @PostMapping("/update/{id}")
    public String updateLibrarian(@PathVariable Long id,
                                  @Valid @ModelAttribute("librarian") Librarian librarian,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "librarians/form";
        }

        Librarian existingByNumber = librarianService.findByLibrarianNumber(librarian.getLibrarianNumber());
        if (existingByNumber != null && !existingByNumber.getId().equals(id)) {
            result.rejectValue("librarianNumber", "error.librarian", "Номер библиотекаря уже используется другим сотрудником");
            return "librarians/form";
        }

        librarian.setId(id);
        librarianService.saveLibrarian(librarian);
        redirectAttributes.addFlashAttribute("successMessage", "Библиотекарь обновлен!");
        return "redirect:/librarians";
    }

    /**
     * Удаление библиотекаря.
     * @param id ID библиотекаря для удаления
     */
    @GetMapping("/delete/{id}")
    public String deleteLibrarian(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            librarianService.deleteLibrarian(id);
            redirectAttributes.addFlashAttribute("successMessage", "Библиотекарь удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/librarians";
    }

    /**
     * Поиск библиотекарей по ключевому слову.
     * @param keyword ключевое слово для поиска
     */
    @GetMapping("/search")
    public String searchLibrarians(@RequestParam("keyword") String keyword, Model model) {
        List<Librarian> librarians = librarianService.searchLibrarians(keyword);
        model.addAttribute("librarians", librarians);
        model.addAttribute("searchKeyword", keyword);
        return "librarians/list";
    }
}