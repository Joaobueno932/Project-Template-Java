package com.example.myproject.repository;

import com.example.myproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados da entidade User.
 * 
 * Esta interface define métodos para acesso aos dados dos usuários,
 * estendendo JpaRepository para operações CRUD básicas.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo username.
     * 
     * @param username o username do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca um usuário pelo email.
     * 
     * @param email o email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário pelo username ou email.
     * 
     * @param username o username do usuário
     * @param email o email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Verifica se existe um usuário com o username especificado.
     * 
     * @param username o username a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsByUsername(String username);

    /**
     * Verifica se existe um usuário com o email especificado.
     * 
     * @param email o email a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários ativos.
     * 
     * @param pageable informações de paginação
     * @return página de usuários ativos
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Busca usuários inativos.
     * 
     * @param pageable informações de paginação
     * @return página de usuários inativos
     */
    Page<User> findByActiveFalse(Pageable pageable);

    /**
     * Busca usuários com email verificado.
     * 
     * @param pageable informações de paginação
     * @return página de usuários com email verificado
     */
    Page<User> findByEmailVerifiedTrue(Pageable pageable);

    /**
     * Busca usuários por role.
     * 
     * @param role o role a ser buscado
     * @param pageable informações de paginação
     * @return página de usuários com o role especificado
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    Page<User> findByRole(@Param("role") User.Role role, Pageable pageable);

    /**
     * Busca usuários criados após uma data específica.
     * 
     * @param date a data de referência
     * @return lista de usuários criados após a data
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Busca usuários por nome completo contendo o texto especificado.
     * 
     * @param fullName o texto a ser buscado no nome completo
     * @param pageable informações de paginação
     * @return página de usuários encontrados
     */
    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    /**
     * Busca usuários por username contendo o texto especificado.
     * 
     * @param username o texto a ser buscado no username
     * @param pageable informações de paginação
     * @return página de usuários encontrados
     */
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    /**
     * Conta o número de usuários ativos.
     * 
     * @return número de usuários ativos
     */
    long countByActiveTrue();

    /**
     * Conta o número de usuários com email verificado.
     * 
     * @return número de usuários com email verificado
     */
    long countByEmailVerifiedTrue();

    /**
     * Busca usuários usando consulta personalizada com múltiplos critérios.
     * 
     * @param username username para busca (pode ser null)
     * @param email email para busca (pode ser null)
     * @param fullName nome completo para busca (pode ser null)
     * @param active status ativo para busca (pode ser null)
     * @param pageable informações de paginação
     * @return página de usuários encontrados
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
           "(:active IS NULL OR u.active = :active)")
    Page<User> findUsersWithFilters(@Param("username") String username,
                                   @Param("email") String email,
                                   @Param("fullName") String fullName,
                                   @Param("active") Boolean active,
                                   Pageable pageable);
}

