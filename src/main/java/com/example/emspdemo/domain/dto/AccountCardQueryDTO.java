package com.example.emspdemo.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountCardQueryDTO {
    private Date startDate;
    private Date endDate;
}
