package com.example.emspdemo.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.emspdemo.domain.enums.CardStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("card")
public class CardPO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3836128037032445565L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private CardStatus status;
    /**
     * 如果未分配，则 accountId 为 null
     */
    private Long accountId;
    // 最后更新时间
    private Date lastUpdated;
    // 更新人
    private String editor;
}
