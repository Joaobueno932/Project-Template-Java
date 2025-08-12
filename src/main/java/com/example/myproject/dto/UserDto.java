package com.example.myproject.dto;

import com.example.myproject.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para transferência de dados do usuário.
 * 
 * Esta classe é usada para transferir dados do usuário
 * entre as camadas da aplicação e para a API REST.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * Identificador único do usuário.
     */
    private Long id;

    /**
     * Nome de usuário único.
     */
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    private String username;

    /**
     * Email único do usuário.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 100, message = "Nome completo deve ter no máximo 100 caracteres")
    private String fullName;

    /**
     * Indica se o usuário está ativo.
     */
    private Boolean active;

    /**
     * Indica se o email foi verificado.
     */
    private Boolean emailVerified;

    /**
     * Roles/perfis do usuário.
     */
    private Set<User.Role> roles;

    /**
     * Data e hora de criação do registro.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Data e hora da última modificação do registro.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}

