package com.example.emspdemo.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CreateCardCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = -454894185560797841L;
    @NotBlank(message = "Editor cannot be null or blank")
    private String editor;
}
