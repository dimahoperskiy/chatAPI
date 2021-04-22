package com.saturn.controllers;

import com.saturn.models.messageModels.ChatMessage;
import com.saturn.models.messageModels.ChatNotification;
import com.saturn.models.messageModels.DeleteMessage;
import com.saturn.models.messageModels.EditMessage;
import com.saturn.services.ChatMessageService;
import com.saturn.services.ChatRoomService;
import com.saturn.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
@CrossOrigin
public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatRoomService chatRoomService;
    @Autowired private UserService userService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        Optional<String> chatId = chatRoomService
                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
        chatMessage.setChatId(chatId.get());

        ChatMessage saved = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),"queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName(),
                        false,
                        false));
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderId(), "queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName(),
                        false,
                        false));
    }

    @MessageMapping("/chat/delete")
    public void deleteMessage(@Payload DeleteMessage payload) {
        ChatMessage message = chatMessageService.findById(payload.getId());
        chatMessageService.deleteMessage(payload.getId());
        Long recipientId = userService.findByLogin(payload.getRecipient()).getId();

        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipientId), "queue/messages",
                new ChatNotification(
                        message.getId(),
                        message.getSenderId(),
                        message.getSenderName(),
                        true,
                        false
                )
        );
    }

    @MessageMapping("/chat/edit")
    public void editMessage(@Payload EditMessage payload) {

        ChatMessage message = chatMessageService.findById(payload.getId());
        message.setContent(payload.getContent());
        chatMessageService.save(message);
        Long recipientId = userService.findByLogin(payload.getRecipient()).getId();

        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipientId),"queue/messages",
                new ChatNotification(
                        message.getId(),
                        message.getSenderId(),
                        message.getSenderName(),
                        false,
                        true));

    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId) {

        return ResponseEntity
                .ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages ( @PathVariable String senderId,
                                                @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage ( @PathVariable Long id) throws Exception {
        return ResponseEntity
                .ok(chatMessageService.findById(id));
    }

//    @DeleteMapping("/messages/{id}")
//    public ResponseEntity<?> deleteMessage ( @PathVariable Long id) throws Exception {
//        chatMessageService.deleteMessage(id);
//        return ResponseEntity
//                .ok("Deleted");
//    }
}