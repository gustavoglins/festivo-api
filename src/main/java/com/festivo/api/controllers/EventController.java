package com.festivo.api.controllers;

import com.festivo.api.request.event.NewEventRequestDTO;
import com.festivo.domain.services.interfaces.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid NewEventRequestDTO newEventRequestDTO) {
        return null;
    }
}
