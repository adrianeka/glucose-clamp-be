package com.tujuhsembilan.glucoseclamp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Service
public class SseService {

    private final Map<Integer, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter registerClient(Integer sessionId) {
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L);
        
        this.emitters.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(sessionId, emitter));
        emitter.onTimeout(() -> removeEmitter(sessionId, emitter));
        emitter.onError((e) -> removeEmitter(sessionId, emitter));

        return emitter;
    }

    private void removeEmitter(Integer sessionId, SseEmitter emitter) {
        List<SseEmitter> sessionEmitters = this.emitters.get(sessionId);
        if (sessionEmitters != null) {
            sessionEmitters.remove(emitter);
            if (sessionEmitters.isEmpty()) {
                this.emitters.remove(sessionId);
            }
        }
    }

    // Mengirim pesan ke semua client yang terhubung ke sessionId tertentu
    public void sendEvent(Long sessionId, String eventName, Object data) {
        List<SseEmitter> sessionEmitters = this.emitters.get(sessionId);
        if (sessionEmitters != null) {
            List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
            for (SseEmitter emitter : sessionEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(eventName)
                            .data(data));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            }
            sessionEmitters.removeAll(deadEmitters);
        }
    }
}