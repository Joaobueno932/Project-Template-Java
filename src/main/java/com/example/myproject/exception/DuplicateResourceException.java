package com.example.myproject.exception;

/**
 * Exceção lançada quando se tenta criar um recurso que já existe.
 * 
 * Esta exceção é usada quando uma operação tenta criar
 * um recurso que já existe no sistema (ex: username ou email duplicado).
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Construtor com mensagem.
     * 
     * @param message a mensagem de erro
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     * 
     * @param message a mensagem de erro
     * @param cause a causa da exceção
     */
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

