package com.example.emspdemo.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.emspdemo.domain.enums.AccountStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("account")
public class AccountPO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9190685025393652372L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 账户唯一标识，使用 email
    private String email;

    // 账户状态
    private AccountStatus status;

    // 最后更新时间
    private Date lastUpdated;

    // 更新人
    private String editor;
}
