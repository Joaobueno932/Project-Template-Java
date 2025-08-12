package com.example.myproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO para requisição de criação de usuário.
 * 
 * Esta classe é usada para receber dados de criação
 * de usuário através da API REST.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

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
     * Senha do usuário.
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String password;
}

