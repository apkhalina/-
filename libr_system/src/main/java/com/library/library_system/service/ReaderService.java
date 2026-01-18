package com.library.library_system.service;

import com.library.library_system.entity.Reader;
import com.library.library_system.repository.ReaderRepository;
import com.library.library_system.repository.BookLoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для управления читателями.
 */
@Service
@Transactional
public class ReaderService {

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookLoanRepository bookLoanRepository;

    /**
     * Получает всех читателей.
     */
    public List<Reader> getAllReaders() {
        try {
            return readerRepository.findAll();
        } catch (Exception e) {
            System.err.println("Ошибка при получении читателей: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Находит читателя по ID.
     * @param id ID читателя
     */
    public Reader getReaderById(Long id) {
        try {
            return readerRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Ошибка при поиске читателя по ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Сохраняет читателя.
     * @param reader объект читателя
     */
    public Reader saveReader(Reader reader) {
        try {
            if (reader.getId() == null) {
                if (existsByTicketNumber(reader.getTicketNumber())) {
                    throw new RuntimeException("Читатель с номером билета '" + reader.getTicketNumber() + "' уже существует");
                }
            } else {
                Reader existingByTicket = readerRepository.findByTicketNumber(reader.getTicketNumber());
                if (existingByTicket != null && !existingByTicket.getId().equals(reader.getId())) {
                    throw new RuntimeException("Номер читательского билета '" + reader.getTicketNumber() + "' уже используется другим читателем");
                }
            }

            if (reader.getId() == null) {
                if (existsByPhoneNumber(reader.getPhoneNumber())) {
                    throw new RuntimeException("Читатель с номером телефона '" + reader.getPhoneNumber() + "' уже существует");
                }
            } else {
                List<Reader> allReaders = getAllReaders();
                for (Reader r : allReaders) {
                    if (r.getPhoneNumber() != null &&
                            r.getPhoneNumber().equals(reader.getPhoneNumber()) &&
                            !r.getId().equals(reader.getId())) {
                        throw new RuntimeException("Номер телефона '" + reader.getPhoneNumber() + "' уже используется другим читателем");
                    }
                }
            }

            return readerRepository.save(reader);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении читателя: " + e.getMessage());
            throw new RuntimeException("Не удалось сохранить читателя: " + e.getMessage());
        }
    }

    /**
     * Удаляет читателя.
     * @param id ID читателя для удаления
     */
    @Transactional
    public void deleteReader(Long id) {
        try {
            Reader reader = readerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Читатель с ID " + id + " не найден"));

            boolean hasActiveLoans = bookLoanRepository.existsActiveLoanByReaderId(id);

            if (hasActiveLoans) {
                throw new RuntimeException("Нельзя удалить читателя: у него есть активные выдачи книг");
            }

            readerRepository.delete(reader);
            System.out.println("Читатель с ID " + id + " удален");

        } catch (Exception e) {
            System.err.println("Ошибка при удалении читателя: " + e.getMessage());
            throw new RuntimeException("Не удалось удалить читателя: " + e.getMessage());
        }
    }

    /**
     * Ищет читателей по ключевому слову.
     * @param keyword ключевое слово для поиска
     */
    public List<Reader> searchReaders(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllReaders();
            }

            String searchTerm = keyword.trim().toLowerCase();
            List<Reader> allReaders = getAllReaders();
            List<Reader> result = new ArrayList<>();

            for (Reader reader : allReaders) {
                if (reader.getFullName() != null && reader.getFullName().toLowerCase().contains(searchTerm)) {
                    result.add(reader);
                }
            }

            return result;

        } catch (Exception e) {
            System.err.println("Ошибка при поиске читателей: " + e.getMessage());
            return getAllReaders();
        }
    }

    /**
     * Находит читателя по номеру билета.
     * @param ticketNumber номер читательского билета
     */
    public Reader findByTicketNumber(String ticketNumber) {
        return readerRepository.findByTicketNumber(ticketNumber);
    }

    /**
     * Проверяет существование читателя по номеру билета.
     */
    public boolean existsByTicketNumber(String ticketNumber) {
        return readerRepository.findByTicketNumber(ticketNumber) != null;
    }

    /**
     * Проверяет существование читателя по номеру телефона.
     */
    public boolean existsByPhoneNumber(String phoneNumber) {
        List<Reader> allReaders = getAllReaders();
        return allReaders.stream()
                .anyMatch(reader -> reader.getPhoneNumber() != null &&
                        reader.getPhoneNumber().equals(phoneNumber));
    }

    /**
     * Проверяет, используется ли телефон другим читателем.
     * @param readerId ID текущего читателя
     * @param phoneNumber номер телефона для проверки
     */
    public boolean isPhoneNumberUsedByOtherReader(Long readerId, String phoneNumber) {
        List<Reader> allReaders = getAllReaders();
        return allReaders.stream()
                .anyMatch(reader -> reader.getPhoneNumber() != null &&
                        reader.getPhoneNumber().equals(phoneNumber) &&
                        !reader.getId().equals(readerId));
    }
}