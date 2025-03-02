package com.example.emspdemo.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AssignCardCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = -694682721591047060L;

    @NotNull(message = "accountId cannot be null")
    private Long accountId;
    @NotBlank(message = "Editor cannot be null or blank")
    private String editor;
}
