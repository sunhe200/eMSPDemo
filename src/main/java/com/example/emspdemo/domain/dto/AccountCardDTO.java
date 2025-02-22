package com.example.emspdemo.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class AccountCardDTO {
    private AccountDTO account;
    private List<CardDTO> cards;
}
