package com.example.emspdemo.domain.event;

import java.util.Date;

public interface DomainEvent {
    Date getEventTime();
    String getEventId();
}
