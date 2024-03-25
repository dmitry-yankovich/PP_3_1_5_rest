package ru.kata.spring.boot_security.demo.exception_handling;

import java.util.Set;

public class ValidationResult {
    private Set <FieldValidationError> errors;

    public Set<FieldValidationError> getErrors() {
        return errors;
    }

    public void setErrors(Set<FieldValidationError> errors) {
        this.errors = errors;
    }

    public ValidationResult(Set<FieldValidationError> errors) {
        this.errors = errors;
    }
}
