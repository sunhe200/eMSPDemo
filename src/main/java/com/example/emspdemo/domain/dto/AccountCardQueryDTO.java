package com.example.emspdemo.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class AccountCardQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4860753923145824849L;
    private Date startDate;
    private Date endDate;
}
