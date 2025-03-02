package com.example.emspdemo.application.command;

import com.example.emspdemo.domain.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChangeCardStatusCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 1310146226859092273L;
    @NotNull(message = "Status cannot be blank")
    private CardStatus newStatus;
    @NotBlank(message = "Editor cannot be null or blank")
    private String editor;
}
