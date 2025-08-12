package com.example.myproject.util;

import com.example.myproject.dto.CreateUserRequest;
import com.example.myproject.dto.UserDto;
import com.example.myproject.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para conversão entre entidades User e DTOs.
 * 
 * Esta interface utiliza MapStruct para gerar automaticamente
 * os métodos de conversão entre as diferentes representações
 * dos dados do usuário.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    /**
     * Converte uma entidade User para UserDto.
     * 
     * @param user a entidade User
     * @return o DTO correspondente
     */
    UserDto toDto(User user);

    /**
     * Converte um UserDto para entidade User.
     * 
     * @param userDto o DTO
     * @return a entidade correspondente
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDto userDto);

    /**
     * Converte um CreateUserRequest para entidade User.
     * 
     * @param request o request de criação
     * @return a entidade correspondente
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);

    /**
     * Atualiza uma entidade User existente com dados de um CreateUserRequest.
     * 
     * @param request o request com os novos dados
     * @param user a entidade a ser atualizada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CreateUserRequest request, @MappingTarget User user);
}

