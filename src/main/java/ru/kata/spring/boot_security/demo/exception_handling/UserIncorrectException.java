package ru.kata.spring.boot_security.demo.exception_handling;

import java.util.Set;

public class UserIncorrectException extends RuntimeException{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */

    private Set<FieldValidationError> errors;

    public Set<FieldValidationError> getErrors() {
        return errors;
    }

    public void setErrors(Set<FieldValidationError> errors) {
        this.errors = errors;
    }

    public UserIncorrectException(String message) {
        super(message);
    }
}
