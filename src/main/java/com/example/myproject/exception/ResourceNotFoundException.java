package com.example.myproject.exception;

/**
 * Exceção lançada quando um recurso não é encontrado.
 * 
 * Esta exceção é usada quando uma operação tenta acessar
 * um recurso que não existe no sistema.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor com mensagem.
     * 
     * @param message a mensagem de erro
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     * 
     * @param message a mensagem de erro
     * @param cause a causa da exceção
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

