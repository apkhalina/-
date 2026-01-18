package com.library.library_system.service;

import com.library.library_system.entity.Librarian;
import com.library.library_system.repository.LibrarianRepository;
import com.library.library_system.repository.BookLoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления библиотекарями.
 */
@Service
public class LibrarianService {

    @Autowired
    private LibrarianRepository librarianRepository;

    @Autowired
    private BookLoanRepository bookLoanRepository;

    /**
     * Получает всех библиотекарей.
     */
    public List<Librarian> getAllLibrarians() {
        try {
            return librarianRepository.findAll();
        } catch (Exception e) {
            System.err.println("Ошибка при получении библиотекарей: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Находит библиотекаря по ID.
     * @param id ID библиотекаря
     */
    public Librarian getLibrarianById(Long id) {
        try {
            return librarianRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Ошибка при поиске библиотекаря по ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Сохраняет библиотекаря.
     * @param librarian объект библиотекаря
     */
    public Librarian saveLibrarian(Librarian librarian) {
        try {
            Librarian existingLibrarian = librarianRepository.findByLibrarianNumber(librarian.getLibrarianNumber());

            if (librarian.getId() == null) {
                if (existingLibrarian != null) {
                    throw new RuntimeException("Библиотекарь с номером '" + librarian.getLibrarianNumber() + "' уже существует");
                }
            } else {
                if (existingLibrarian != null && !existingLibrarian.getId().equals(librarian.getId())) {
                    throw new RuntimeException("Номер библиотекаря '" + librarian.getLibrarianNumber() + "' уже используется другим сотрудником");
                }
            }

            return librarianRepository.save(librarian);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении библиотекаря: " + e.getMessage());
            throw new RuntimeException("Не удалось сохранить библиотекаря: " + e.getMessage());
        }
    }

    /**
     * Удаляет библиотекаря.
     * @param id ID библиотекаря для удаления
     */
    @Transactional
    public void deleteLibrarian(Long id) {
        try {
            Librarian librarian = librarianRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Библиотекарь с ID " + id + " не найден"));

            boolean hasLoans = !bookLoanRepository.findByLibrarianId(id).isEmpty();

            if (hasLoans) {
                throw new RuntimeException("Нельзя удалить библиотекаря: у него есть записи о выдаче книг");
            }

            librarianRepository.delete(librarian);
            System.out.println("Библиотекарь с ID " + id + " удален");

        } catch (Exception e) {
            System.err.println("Ошибка при удалении библиотекаря: " + e.getMessage());
            throw new RuntimeException("Не удалось удалить библиотекаря: " + e.getMessage());
        }
    }

    /**
     * Ищет библиотекарей по ключевому слову.
     * @param keyword ключевое слово для поиска
     */
    public List<Librarian> searchLibrarians(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllLibrarians();
            }

            String searchTerm = keyword.trim().toLowerCase();
            List<Librarian> allLibrarians = getAllLibrarians();
            List<Librarian> result = new ArrayList<>();

            for (Librarian librarian : allLibrarians) {
                if (librarian.getFullName() != null && librarian.getFullName().toLowerCase().contains(searchTerm)) {
                    result.add(librarian);
                }
            }

            return result;

        } catch (Exception e) {
            System.err.println("Ошибка при поиске библиотекарей: " + e.getMessage());
            return getAllLibrarians();
        }
    }

    /**
     * Находит библиотекаря по номеру.
     * @param librarianNumber номер библиотекаря
     */
    public Librarian findByLibrarianNumber(String librarianNumber) {
        return librarianRepository.findByLibrarianNumber(librarianNumber);
    }

    /**
     * Проверяет существование библиотекаря по номеру.
     */
    public boolean existsByLibrarianNumber(String librarianNumber) {
        return librarianRepository.findByLibrarianNumber(librarianNumber) != null;
    }
}