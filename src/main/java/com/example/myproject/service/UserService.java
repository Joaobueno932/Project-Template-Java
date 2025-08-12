package com.example.myproject.service;

import com.example.myproject.dto.CreateUserRequest;
import com.example.myproject.dto.UserDto;
import com.example.myproject.exception.ResourceNotFoundException;
import com.example.myproject.exception.DuplicateResourceException;
import com.example.myproject.model.User;
import com.example.myproject.repository.UserRepository;
import com.example.myproject.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service para lógica de negócio relacionada aos usuários.
 * 
 * Esta classe contém toda a lógica de negócio para operações
 * com usuários, incluindo validações e transformações.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Busca todos os usuários com paginação.
     * 
     * @param pageable informações de paginação
     * @return página de usuários
     */
    public Page<UserDto> findAll(Pageable pageable) {
        log.debug("Buscando todos os usuários com paginação: {}", pageable);
        
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDto);
    }

    /**
     * Busca um usuário por ID.
     * 
     * @param id o ID do usuário
     * @return o usuário encontrado
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    public UserDto findById(Long id) {
        log.debug("Buscando usuário por ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        return userMapper.toDto(user);
    }

    /**
     * Busca um usuário por username.
     * 
     * @param username o username do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<UserDto> findByUsername(String username) {
        log.debug("Buscando usuário por username: {}", username);
        
        return userRepository.findByUsername(username)
            .map(userMapper::toDto);
    }

    /**
     * Busca um usuário por email.
     * 
     * @param email o email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<UserDto> findByEmail(String email) {
        log.debug("Buscando usuário por email: {}", email);
        
        return userRepository.findByEmail(email)
            .map(userMapper::toDto);
    }

    /**
     * Cria um novo usuário.
     * 
     * @param request dados para criação do usuário
     * @return o usuário criado
     * @throws DuplicateResourceException se username ou email já existirem
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        log.info("Criando novo usuário: {}", request.getUsername());
        
        // Validar se username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username já existe: " + request.getUsername());
        }
        
        // Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email já existe: " + request.getEmail());
        }
        
        // Criar usuário
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .fullName(request.getFullName())
            .password(passwordEncoder.encode(request.getPassword()))
            .active(true)
            .emailVerified(false)
            .build();
        
        // Adicionar role padrão
        user.addRole(User.Role.USER);
        
        User savedUser = userRepository.save(user);
        log.info("Usuário criado com sucesso: ID={}, username={}", savedUser.getId(), savedUser.getUsername());
        
        return userMapper.toDto(savedUser);
    }

    /**
     * Atualiza um usuário existente.
     * 
     * @param id o ID do usuário
     * @param request dados para atualização
     * @return o usuário atualizado
     * @throws ResourceNotFoundException se o usuário não for encontrado
     * @throws DuplicateResourceException se username ou email já existirem para outro usuário
     */
    @Transactional
    public UserDto updateUser(Long id, CreateUserRequest request) {
        log.info("Atualizando usuário: ID={}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        // Validar se username já existe para outro usuário
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username já existe: " + request.getUsername());
        }
        
        // Validar se email já existe para outro usuário
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email já existe: " + request.getEmail());
        }
        
        // Atualizar dados
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        
        // Atualizar senha se fornecida
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        log.info("Usuário atualizado com sucesso: ID={}, username={}", updatedUser.getId(), updatedUser.getUsername());
        
        return userMapper.toDto(updatedUser);
    }

    /**
     * Ativa um usuário.
     * 
     * @param id o ID do usuário
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @Transactional
    public void activateUser(Long id) {
        log.info("Ativando usuário: ID={}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        user.activate();
        userRepository.save(user);
        
        log.info("Usuário ativado com sucesso: ID={}", id);
    }

    /**
     * Desativa um usuário.
     * 
     * @param id o ID do usuário
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Desativando usuário: ID={}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        user.deactivate();
        userRepository.save(user);
        
        log.info("Usuário desativado com sucesso: ID={}", id);
    }

    /**
     * Verifica o email de um usuário.
     * 
     * @param id o ID do usuário
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @Transactional
    public void verifyEmail(Long id) {
        log.info("Verificando email do usuário: ID={}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        
        user.verifyEmail();
        userRepository.save(user);
        
        log.info("Email verificado com sucesso: ID={}", id);
    }

    /**
     * Deleta um usuário.
     * 
     * @param id o ID do usuário
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deletando usuário: ID={}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("Usuário deletado com sucesso: ID={}", id);
    }

    /**
     * Busca usuários ativos com paginação.
     * 
     * @param pageable informações de paginação
     * @return página de usuários ativos
     */
    public Page<UserDto> findActiveUsers(Pageable pageable) {
        log.debug("Buscando usuários ativos com paginação: {}", pageable);
        
        Page<User> users = userRepository.findByActiveTrue(pageable);
        return users.map(userMapper::toDto);
    }

    /**
     * Busca usuários por role.
     * 
     * @param role o role a ser buscado
     * @param pageable informações de paginação
     * @return página de usuários com o role especificado
     */
    public Page<UserDto> findUsersByRole(User.Role role, Pageable pageable) {
        log.debug("Buscando usuários por role: {} com paginação: {}", role, pageable);
        
        Page<User> users = userRepository.findByRole(role, pageable);
        return users.map(userMapper::toDto);
    }

    /**
     * Busca usuários criados recentemente.
     * 
     * @param days número de dias para considerar como recente
     * @return lista de usuários criados recentemente
     */
    public List<UserDto> findRecentUsers(int days) {
        log.debug("Buscando usuários criados nos últimos {} dias", days);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<User> users = userRepository.findByCreatedAtAfter(cutoffDate);
        
        return users.stream()
            .map(userMapper::toDto)
            .toList();
    }

    /**
     * Busca usuários com filtros.
     * 
     * @param username filtro por username (opcional)
     * @param email filtro por email (opcional)
     * @param fullName filtro por nome completo (opcional)
     * @param active filtro por status ativo (opcional)
     * @param pageable informações de paginação
     * @return página de usuários filtrados
     */
    public Page<UserDto> findUsersWithFilters(String username, String email, String fullName, 
                                             Boolean active, Pageable pageable) {
        log.debug("Buscando usuários com filtros - username: {}, email: {}, fullName: {}, active: {}", 
                 username, email, fullName, active);
        
        Page<User> users = userRepository.findUsersWithFilters(username, email, fullName, active, pageable);
        return users.map(userMapper::toDto);
    }

    /**
     * Conta o número total de usuários ativos.
     * 
     * @return número de usuários ativos
     */
    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    /**
     * Conta o número total de usuários com email verificado.
     * 
     * @return número de usuários com email verificado
     */
    public long countVerifiedUsers() {
        return userRepository.countByEmailVerifiedTrue();
    }
}

