package com.example.emspdemo.application.command;

import com.example.emspdemo.domain.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChangeAccountStatusCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = -6397124525656621050L;
    @NotNull(message = "Status must not be null")
    private AccountStatus status;
    @NotBlank(message = "Editor cannot be null or blank")
    private String editor;
}
