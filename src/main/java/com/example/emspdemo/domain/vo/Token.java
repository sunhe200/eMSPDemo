package com.example.emspdemo.domain.vo;

import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token implements Serializable {
    @Serial
    private static final long serialVersionUID = 7246419686984795407L;
    private TokenType tokenType;
    private TokenProp propName;
    private String propValue;
}
