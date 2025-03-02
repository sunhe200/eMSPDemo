package com.example.emspdemo.presentation.dto;

import com.example.emspdemo.domain.enums.CardStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class CardDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7371166884648023387L;

    private Long id;
    private String cardNumber;
    private CardStatus status;
    private Long accountId;
    private Date lastUpdated;
    private String uid;
    private String editor;
}
