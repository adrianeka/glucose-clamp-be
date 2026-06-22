package com.tujuhsembilan.glucoseclamp.controller.sessionmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; // Pastikan ini di-import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.tujuhsembilan.glucoseclamp.service.SseService;

@RestController
@RequestMapping("/session-stream")
public class SessionStreamController {

    @Autowired
    private SseService sseService;

    @GetMapping(path = "/{sessionId}")
    public ResponseEntity<SseEmitter> streamSession(@PathVariable Integer sessionId) {
        SseEmitter emitter = sseService.registerClient(sessionId);
        
        // Membungkus SseEmitter dengan ResponseEntity agar Spring tahu ini adalah Event Stream
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(emitter);
    }
}