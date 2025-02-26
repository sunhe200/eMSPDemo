package com.example.emspdemo.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class AccountCardDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3152164729784306846L;
    private AccountDTO account;
    private List<CardDTO> cards;
}
