package com.lachguer.accountservice.exception;

/**
 * Exception représentant une absence de jeton ou un jeton invalide.
 * Déclenchée quand l'en-tête Authorization est manquant ou invalide.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
