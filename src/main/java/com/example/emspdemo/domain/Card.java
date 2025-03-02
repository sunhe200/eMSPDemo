package com.example.emspdemo.domain;

import cn.hutool.core.util.IdUtil;
import com.example.emspdemo.domain.enums.CardStatus;
import com.example.emspdemo.domain.enums.TokenProp;
import com.example.emspdemo.domain.enums.TokenType;
import com.example.emspdemo.domain.event.CardAssignedEvent;
import com.example.emspdemo.domain.event.CardCreatedEvent;
import com.example.emspdemo.domain.event.CardStatusChangedEvent;
import com.example.emspdemo.domain.event.DomainEvent;
import com.example.emspdemo.domain.vo.Token;
import com.example.emspdemo.util.RFIDUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Card {
    private Long id;                  // 聚合根标识
    private Long accountId;           // 关联的 Account ID
    // 用于存储 RFID 信息的 token 列表，通过 TokenVO 表示（例如包含 uid 和 visibleNumber）
    private List<Token> tokens = new ArrayList<>();
    private CardStatus status;            // 状态，如 "CREATED"、"ACTIVATED"、"DEACTIVATED"
    private Date lastUpdated;
    private String editor;

    // 内部领域事件集合，用于记录业务操作过程中产生的事件
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 工厂方法：创建 Card 聚合根，同时初始化 RFID token 信息。
     * 参数分别表示 RFID 的 uid 和 visibleNumber。
     */
    public static Card create(String editor) {
        Card card = new Card();
        card.status = CardStatus.CREATED;
        card.lastUpdated = new Date();
        card.editor = editor;
        // 使用 TokenVO 表示 RFID 信息，tokenType 固定为 "RFID"
        Token uid = new Token(TokenType.RFID, TokenProp.UID, RFIDUtil.generateUniqueUID());
        Token visibleNumber = new Token(TokenType.RFID, TokenProp.VISIBLE_NUMBER, RFIDUtil.generateUniqueVisibleNumber());
        card.tokens.add(uid);
        card.tokens.add(visibleNumber);
        card.addDomainEvent(new CardCreatedEvent(card, IdUtil.fastSimpleUUID(), new Date()));
        return card;
    }

    /**
     * 将 Card 分配给指定的 Account。
     * 此方法同时触发 CardAssignedEvent 领域事件。
     */
    public void assignToAccount(Long accountId, String editor) {
        this.accountId = accountId;
        this.lastUpdated = new Date();
        this.editor = editor;
        addDomainEvent(new CardAssignedEvent(this, IdUtil.fastSimpleUUID(), new Date(), accountId));
    }

    /**
     * 修改 Card 状态（如 "ACTIVATED"、"DEACTIVATED" 等），并触发 CardStatusChangedEvent 领域事件。
     */
    public void changeStatus(CardStatus newStatus, String editor) {
        this.status = newStatus;
        this.lastUpdated = new Date();
        this.editor = editor;
        addDomainEvent(new CardStatusChangedEvent(this, IdUtil.fastSimpleUUID(), new Date(), newStatus));
    }

    private void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }


    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}

