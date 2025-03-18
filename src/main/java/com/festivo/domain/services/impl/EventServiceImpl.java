package com.festivo.domain.services.impl;

import com.festivo.domain.repositories.EventRepository;
import com.festivo.domain.services.interfaces.EventService;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
}
