package ru.kata.spring.boot_security.demo.exception_handling;

import java.util.Set;

public class UserIncorrectData {
    private String info;
    private Set<FieldValidationError> errors;

    public Set<FieldValidationError> getErrors() {
        return errors;
    }

    public void setErrors(Set<FieldValidationError> errors) {
        this.errors = errors;
    }

    public UserIncorrectData() {
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
