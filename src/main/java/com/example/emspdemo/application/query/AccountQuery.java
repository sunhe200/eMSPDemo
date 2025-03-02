package com.example.emspdemo.application.query;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class AccountQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = 7806873817715650135L;
    private Date startDate;
    private Date endDate;

    @AssertTrue(message = "If both startDate and endDate are provided, startDate must be before endDate; also they cannot both be null")
    public boolean isDateRangeValid() {
        if (startDate == null && endDate == null) {
            return false;
        }
        if (startDate != null && endDate != null) {
            return startDate.before(endDate);
        }
        return true;
    }
}
