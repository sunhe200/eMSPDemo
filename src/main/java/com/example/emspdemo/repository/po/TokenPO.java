package com.example.emspdemo.repository.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("token")
public class TokenPO  implements Serializable {
    @Serial
    private static final long serialVersionUID = -3933041549364932497L;

    @TableId
    private Long id;
    // externalId 用于关联 AccountPO 的 id，不设置外键约束
    private Long externalId;
    private TokenType tokenType;
    private TokenProp propName;
    private String propValue;
}
