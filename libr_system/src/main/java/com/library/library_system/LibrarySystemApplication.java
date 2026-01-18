package com.library.library_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс Spring Boot приложения библиотечной системы.
 * Точка входа в приложение.
 */
@SpringBootApplication
public class LibrarySystemApplication {

    /**
     * Запускает Spring Boot приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(LibrarySystemApplication.class, args);
        System.out.println("http://localhost:8080");
    }
}