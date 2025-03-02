package com.example.emspdemo.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CreateAccountCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 6626037670913086455L;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be a valid email address")
    private String email;
    @NotBlank(message = "Editor cannot be null or blank")
    private String editor;
}
