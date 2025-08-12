package com.example.myproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa um usuário do sistema.
 * 
 * Esta classe define a estrutura de dados para usuários,
 * incluindo informações básicas e auditoria.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_username", columnList = "username")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Identificador único do usuário.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome de usuário único.
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    private String username;

    /**
     * Email único do usuário.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    /**
     * Nome completo do usuário.
     */
    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 100, message = "Nome completo deve ter no máximo 100 caracteres")
    private String fullName;

    /**
     * Senha criptografada do usuário.
     */
    @Column(name = "password", nullable = false)
    @NotBlank(message = "Senha é obrigatória")
    private String password;

    /**
     * Indica se o usuário está ativo.
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Indica se o email foi verificado.
     */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * Roles/perfis do usuário.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Data e hora de criação do registro.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última modificação do registro.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum que define os roles/perfis disponíveis.
     */
    public enum Role {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN"),
        MODERATOR("ROLE_MODERATOR");

        private final String authority;

        Role(String authority) {
            this.authority = authority;
        }

        public String getAuthority() {
            return authority;
        }
    }

    /**
     * Adiciona um role ao usuário.
     * 
     * @param role o role a ser adicionado
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Remove um role do usuário.
     * 
     * @param role o role a ser removido
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Verifica se o usuário tem um role específico.
     * 
     * @param role o role a ser verificado
     * @return true se o usuário tem o role, false caso contrário
     */
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    /**
     * Ativa o usuário.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Desativa o usuário.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Marca o email como verificado.
     */
    public void verifyEmail() {
        this.emailVerified = true;
    }
}

