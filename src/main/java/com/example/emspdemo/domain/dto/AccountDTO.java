package com.example.emspdemo.domain.dto;

import com.example.emspdemo.domain.enums.AccountStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class AccountDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4606733770778776040L;
    private Long id;
    private String email;
    private String contractId;
    private AccountStatus status;
    private Date lastUpdated;
    private String editor;
}
