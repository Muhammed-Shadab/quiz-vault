package com.minProject.root.Task;

import com.minProject.root.entity.Student;
import com.minProject.root.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActivityWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private StudentRepository stnRepo;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        stnRepo.setHeatBeat("shadabtg4@gmail.com", LocalDateTime.now());
        System.out.println("Connection made");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        stnRepo.setHeatBeat("shadabtg4@gmail.com", LocalDateTime.now());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }
}