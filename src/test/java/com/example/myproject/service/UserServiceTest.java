package com.example.myproject.service;

import com.example.myproject.dto.CreateUserRequest;
import com.example.myproject.dto.UserDto;
import com.example.myproject.exception.DuplicateResourceException;
import com.example.myproject.exception.ResourceNotFoundException;
import com.example.myproject.model.User;
import com.example.myproject.repository.UserRepository;
import com.example.myproject.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserService.
 * 
 * Esta classe testa a lógica de negócio do serviço de usuários,
 * usando mocks para isolar as dependências.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .fullName("Test User")
            .password("encodedPassword")
            .active(true)
            .emailVerified(false)
            .roles(Set.of(User.Role.USER))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        testUserDto = UserDto.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .fullName("Test User")
            .active(true)
            .emailVerified(false)
            .roles(Set.of(User.Role.USER))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        createUserRequest = CreateUserRequest.builder()
            .username("testuser")
            .email("test@example.com")
            .fullName("Test User")
            .password("password123")
            .build();
    }

    @Test
    @DisplayName("Should find all users with pagination")
    void shouldFindAllUsersWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser));
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // When
        Page<UserDto> result = userService.findAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testUserDto);
        
        verify(userRepository).findAll(pageable);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Given
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // When
        UserDto result = userService.findById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testUserDto);
        
        verify(userRepository).findById(userId);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found by ID")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundById() {
        // Given
        Long userId = 999L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.findById(userId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Usuário não encontrado com ID: " + userId);
        
        verify(userRepository).findById(userId);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        String username = "testuser";
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // When
        Optional<UserDto> result = userService.findByUsername(username);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUserDto);
        
        verify(userRepository).findByUsername(username);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername(createUserRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // When
        UserDto result = userService.createUser(createUserRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testUserDto);
        
        verify(userRepository).existsByUsername(createUserRequest.getUsername());
        verify(userRepository).existsByEmail(createUserRequest.getEmail());
        verify(passwordEncoder).encode(createUserRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when username already exists")
    void shouldThrowDuplicateResourceExceptionWhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername(createUserRequest.getUsername())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createUserRequest))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage("Username já existe: " + createUserRequest.getUsername());
        
        verify(userRepository).existsByUsername(createUserRequest.getUsername());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void shouldThrowDuplicateResourceExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByUsername(createUserRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createUserRequest))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage("Email já existe: " + createUserRequest.getEmail());
        
        verify(userRepository).existsByUsername(createUserRequest.getUsername());
        verify(userRepository).existsByEmail(createUserRequest.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        Long userId = 1L;
        CreateUserRequest updateRequest = CreateUserRequest.builder()
            .username("updateduser")
            .email("updated@example.com")
            .fullName("Updated User")
            .password("newpassword")
            .build();
        
        User updatedUser = User.builder()
            .id(userId)
            .username("updateduser")
            .email("updated@example.com")
            .fullName("Updated User")
            .password("newEncodedPassword")
            .active(true)
            .emailVerified(false)
            .roles(Set.of(User.Role.USER))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        UserDto updatedUserDto = UserDto.builder()
            .id(userId)
            .username("updateduser")
            .email("updated@example.com")
            .fullName("Updated User")
            .active(true)
            .emailVerified(false)
            .roles(Set.of(User.Role.USER))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(updateRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(updatedUserDto);

        // When
        UserDto result = userService.updateUser(userId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("updateduser");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        
        verify(userRepository).findById(userId);
        verify(userRepository).existsByUsername(updateRequest.getUsername());
        verify(userRepository).existsByEmail(updateRequest.getEmail());
        verify(passwordEncoder).encode(updateRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(updatedUser);
    }

    @Test
    @DisplayName("Should activate user successfully")
    void shouldActivateUserSuccessfully() {
        // Given
        Long userId = 1L;
        testUser.setActive(false);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.activateUser(userId);

        // Then
        assertThat(testUser.getActive()).isTrue();
        
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void shouldDeactivateUserSuccessfully() {
        // Given
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.deactivateUser(userId);

        // Then
        assertThat(testUser.getActive()).isFalse();
        
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should verify email successfully")
    void shouldVerifyEmailSuccessfully() {
        // Given
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.verifyEmail(userId);

        // Then
        assertThat(testUser.getEmailVerified()).isTrue();
        
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        Long userId = 1L;
        
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent user")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentUser() {
        // Given
        Long userId = 999L;
        
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(userId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Usuário não encontrado com ID: " + userId);
        
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should find active users")
    void shouldFindActiveUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser));
        
        when(userRepository.findByActiveTrue(pageable)).thenReturn(userPage);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // When
        Page<UserDto> result = userService.findActiveUsers(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testUserDto);
        
        verify(userRepository).findByActiveTrue(pageable);
        verify(userMapper).toDto(testUser);
    }

    @Test
    @DisplayName("Should count active users")
    void shouldCountActiveUsers() {
        // Given
        long expectedCount = 5L;
        
        when(userRepository.countByActiveTrue()).thenReturn(expectedCount);

        // When
        long result = userService.countActiveUsers();

        // Then
        assertThat(result).isEqualTo(expectedCount);
        
        verify(userRepository).countByActiveTrue();
    }

    @Test
    @DisplayName("Should count verified users")
    void shouldCountVerifiedUsers() {
        // Given
        long expectedCount = 3L;
        
        when(userRepository.countByEmailVerifiedTrue()).thenReturn(expectedCount);

        // When
        long result = userService.countVerifiedUsers();

        // Then
        assertThat(result).isEqualTo(expectedCount);
        
        verify(userRepository).countByEmailVerifiedTrue();
    }
}

