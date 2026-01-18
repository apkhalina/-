package com.library.library_system.controller;

import com.library.library_system.entity.Reader;
import com.library.library_system.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 * Контроллер для управления читателями.
 */
@Controller
@RequestMapping("/readers")
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    /**
     * Список всех читателей.
     */
    @GetMapping
    public String listReaders(Model model) {
        List<Reader> readers = readerService.getAllReaders();
        model.addAttribute("readers", readers);
        return "readers/list";
    }

    /**
     * Форма создания нового читателя.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reader", new Reader());
        return "readers/form";
    }

    /**
     * Создание нового читателя.
     * @param reader данные читателя из формы
     */
    @PostMapping
    public String createReader(@Valid @ModelAttribute("reader") Reader reader,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "readers/form";
        }

        try {
            if (readerService.existsByTicketNumber(reader.getTicketNumber())) {
                result.rejectValue("ticketNumber", "error.reader", "Номер читательского билета уже существует");
                return "readers/form";
            }

            if (readerService.existsByPhoneNumber(reader.getPhoneNumber())) {
                result.rejectValue("phoneNumber", "error.reader", "Номер телефона уже используется другим читателем");
                return "readers/form";
            }

            readerService.saveReader(reader);
            redirectAttributes.addFlashAttribute("successMessage", "Читатель добавлен!");
            return "redirect:/readers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
            return "redirect:/readers";
        }
    }

    /**
     * Форма редактирования читателя.
     * @param id ID читателя для редактирования
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Reader reader = readerService.getReaderById(id);
        if (reader == null) {
            return "redirect:/readers";
        }
        model.addAttribute("reader", reader);
        return "readers/form";
    }

    /**
     * Обновление информации о читателе.
     * @param id ID читателя для обновления
     * @param reader обновленные данные читателя
     */
    @PostMapping("/update/{id}")
    public String updateReader(@PathVariable Long id,
                               @Valid @ModelAttribute("reader") Reader reader,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "readers/form";
        }

        try {
            Reader existingByTicket = readerService.findByTicketNumber(reader.getTicketNumber());
            if (existingByTicket != null && !existingByTicket.getId().equals(id)) {
                result.rejectValue("ticketNumber", "error.reader", "Номер читательского билета уже используется другим читателем");
                return "readers/form";
            }

            if (readerService.isPhoneNumberUsedByOtherReader(id, reader.getPhoneNumber())) {
                result.rejectValue("phoneNumber", "error.reader", "Номер телефона уже используется другим читателем");
                return "readers/form";
            }

            reader.setId(id);
            readerService.saveReader(reader);
            redirectAttributes.addFlashAttribute("successMessage", "Читатель обновлен!");
            return "redirect:/readers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
            return "redirect:/readers";
        }
    }

    /**
     * Удаление читателя.
     * @param id ID читателя для удаления
     */
    @GetMapping("/delete/{id}")
    public String deleteReader(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            readerService.deleteReader(id);
            redirectAttributes.addFlashAttribute("successMessage", "Читатель удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/readers";
    }
}