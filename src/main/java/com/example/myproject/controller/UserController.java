package com.example.myproject.controller;

import com.example.myproject.dto.CreateUserRequest;
import com.example.myproject.dto.UserDto;
import com.example.myproject.model.User;
import com.example.myproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Controller REST para operações com usuários.
 * 
 * Esta classe expõe endpoints REST para gerenciamento de usuários,
 * incluindo operações CRUD e consultas específicas.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Users", description = "API para gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    /**
     * Lista todos os usuários com paginação.
     * 
     * @param pageable informações de paginação
     * @return página de usuários
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Listando usuários com paginação: {}", pageable);
        
        Page<UserDto> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Busca um usuário por ID.
     * 
     * @param id o ID do usuário
     * @return o usuário encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or #id == authentication.principal.id")
    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID do usuário") 
            @PathVariable @Min(1) Long id) {
        log.info("Buscando usuário por ID: {}", id);
        
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Cria um novo usuário.
     * 
     * @param request dados para criação do usuário
     * @return o usuário criado
     */
    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Username ou email já existem")
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Criando novo usuário: {}", request.getUsername());
        
        UserDto createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Atualiza um usuário existente.
     * 
     * @param id o ID do usuário
     * @param request dados para atualização
     * @return o usuário atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "409", description = "Username ou email já existem"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID do usuário") 
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody CreateUserRequest request) {
        log.info("Atualizando usuário: ID={}", id);
        
        UserDto updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deleta um usuário.
     * 
     * @param id o ID do usuário
     * @return resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário") 
            @PathVariable @Min(1) Long id) {
        log.info("Deletando usuário: ID={}", id);
        
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ativa um usuário.
     * 
     * @param id o ID do usuário
     * @return resposta sem conteúdo
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Ativar usuário", description = "Ativa um usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário ativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> activateUser(
            @Parameter(description = "ID do usuário") 
            @PathVariable @Min(1) Long id) {
        log.info("Ativando usuário: ID={}", id);
        
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Desativa um usuário.
     * 
     * @param id o ID do usuário
     * @return resposta sem conteúdo
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> deactivateUser(
            @Parameter(description = "ID do usuário") 
            @PathVariable @Min(1) Long id) {
        log.info("Desativando usuário: ID={}", id);
        
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica o email de um usuário.
     * 
     * @param id o ID do usuário
     * @return resposta sem conteúdo
     */
    @PatchMapping("/{id}/verify-email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or #id == authentication.principal.id")
    @Operation(summary = "Verificar email", description = "Marca o email de um usuário como verificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email verificado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> verifyEmail(
            @Parameter(description = "ID do usuário") 
            @PathVariable @Min(1) Long id) {
        log.info("Verificando email do usuário: ID={}", id);
        
        userService.verifyEmail(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista usuários ativos com paginação.
     * 
     * @param pageable informações de paginação
     * @return página de usuários ativos
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Listar usuários ativos", description = "Lista todos os usuários ativos com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de usuários ativos retornada com sucesso")
    public ResponseEntity<Page<UserDto>> getActiveUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Listando usuários ativos com paginação: {}", pageable);
        
        Page<UserDto> users = userService.findActiveUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Lista usuários por role.
     * 
     * @param role o role a ser buscado
     * @param pageable informações de paginação
     * @return página de usuários com o role especificado
     */
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuários por role", description = "Lista usuários que possuem um role específico")
    @ApiResponse(responseCode = "200", description = "Lista de usuários por role retornada com sucesso")
    public ResponseEntity<Page<UserDto>> getUsersByRole(
            @Parameter(description = "Role dos usuários") 
            @PathVariable User.Role role,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Listando usuários por role: {} com paginação: {}", role, pageable);
        
        Page<UserDto> users = userService.findUsersByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Lista usuários criados recentemente.
     * 
     * @param days número de dias para considerar como recente (padrão: 7)
     * @return lista de usuários criados recentemente
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Listar usuários recentes", description = "Lista usuários criados recentemente")
    @ApiResponse(responseCode = "200", description = "Lista de usuários recentes retornada com sucesso")
    public ResponseEntity<List<UserDto>> getRecentUsers(
            @Parameter(description = "Número de dias para considerar como recente") 
            @RequestParam(defaultValue = "7") @Min(1) int days) {
        log.info("Listando usuários criados nos últimos {} dias", days);
        
        List<UserDto> users = userService.findRecentUsers(days);
        return ResponseEntity.ok(users);
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
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @Operation(summary = "Buscar usuários com filtros", description = "Busca usuários aplicando filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Lista de usuários filtrados retornada com sucesso")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @Parameter(description = "Filtro por username") 
            @RequestParam(required = false) String username,
            @Parameter(description = "Filtro por email") 
            @RequestParam(required = false) String email,
            @Parameter(description = "Filtro por nome completo") 
            @RequestParam(required = false) String fullName,
            @Parameter(description = "Filtro por status ativo") 
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Buscando usuários com filtros - username: {}, email: {}, fullName: {}, active: {}", 
                username, email, fullName, active);
        
        Page<UserDto> users = userService.findUsersWithFilters(username, email, fullName, active, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Obtém estatísticas de usuários.
     * 
     * @return estatísticas dos usuários
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obter estatísticas de usuários", description = "Retorna estatísticas gerais dos usuários")
    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso")
    public ResponseEntity<UserStatsDto> getUserStats() {
        log.info("Obtendo estatísticas de usuários");
        
        long activeUsers = userService.countActiveUsers();
        long verifiedUsers = userService.countVerifiedUsers();
        
        UserStatsDto stats = UserStatsDto.builder()
            .activeUsers(activeUsers)
            .verifiedUsers(verifiedUsers)
            .build();
        
        return ResponseEntity.ok(stats);
    }

    /**
     * DTO para estatísticas de usuários.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserStatsDto {
        private long activeUsers;
        private long verifiedUsers;
    }
}

