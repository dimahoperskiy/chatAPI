package com.saturn.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

/**
 * Сущность пользователя
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(	name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "login"),
                @UniqueConstraint(columnNames = "email")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends AuditModel {
    /**
     * Автозаполняемый айдишник
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Логин
     */
    private String login;
    /**
     * Почта
     */
    @Email
    private String email;
    /**
     * Пароль (в хэшированном виде)
     */
    private String password;
    /**
     * @deprecated
     */
    private String status;
    /**
     * @deprecated
     */
    private Boolean follow;

    /**
     * Свзяь M2O с ролью
     */
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}



