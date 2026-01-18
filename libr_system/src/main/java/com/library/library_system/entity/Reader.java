package com.library.library_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.sql.Date;

/**
 * Сущность читателя.
 */
@Entity
@Table(name = "readers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reader_id")
    private Long id;

    @NotBlank(message = "Номер читательского билета обязателен")
    @Column(name = "ticket_number", unique = true, nullable = false)
    private String ticketNumber;

    @NotBlank(message = "ФИО обязательно")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+7\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$",
            message = "Номер телефона должен быть в формате +7(XXX)XXX-XX-XX")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "registration_date")
    private Date registrationDate;
}