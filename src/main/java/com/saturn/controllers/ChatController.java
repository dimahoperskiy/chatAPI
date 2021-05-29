package com.saturn.controllers;

import com.saturn.configuration.jwt.JwtProvider;
import com.saturn.models.messageModels.ChatMessage;
import com.saturn.models.messageModels.ChatNotification;
import com.saturn.models.messageModels.DeleteMessage;
import com.saturn.models.messageModels.EditMessage;
import com.saturn.services.ChatMessageService;
import com.saturn.services.ChatRoomService;
import com.saturn.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
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
    @Autowired private JwtProvider jwtProvider;

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
        Optional<ChatMessage> messageOpt = chatMessageService.findById(payload.getId());
        if (messageOpt.isPresent()) {
            ChatMessage message = messageOpt.orElseThrow();

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
        } else {
            Long recipientId = userService.findByLogin(payload.getRecipient()).getId();
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(recipientId), "queue/messages",
                    new ChatNotification(
                            null,
                            null,
                            null,
                            false,
                            false
                    )
            );
            System.out.println("No such message");
        }
    }

    @MessageMapping("/chat/edit")
    public void editMessage(@Payload EditMessage payload) {

        Optional<ChatMessage> messageOpt = chatMessageService.findById(payload.getId());

        if (messageOpt.isPresent()) {
            ChatMessage message = messageOpt.orElseThrow();

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

        } else {
            Long recipientId = userService.findByLogin(payload.getRecipient()).getId();
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(recipientId), "queue/messages",
                    new ChatNotification(
                            null,
                            null,
                            null,
                            false,
                            false
                    )
            );
            System.out.println("No such message");
        }
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
                                                @PathVariable String recipientId,
                                                @CookieValue("auth") String token) {
        String user =  jwtProvider.getLoginFromToken(token);
        Long userRequestId = userService.findByLogin(user).getId();
        if (String.valueOf(userRequestId).equals(senderId) || String.valueOf(userRequestId).equals(recipientId)) {
            return ResponseEntity
                    .ok(chatMessageService.findChatMessages(senderId, recipientId));
        } else return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage ( @PathVariable Long id,
                                           @CookieValue("auth") String token) {
        Optional<ChatMessage> msgOpt = chatMessageService.findById(id);

        if (msgOpt.isPresent()) {
            ChatMessage msg = msgOpt.orElseThrow();

            String user =  jwtProvider.getLoginFromToken(token);

            if (msg.getSenderName().equals(user) || msg.getRecipientName().equals(user)) {
                return ResponseEntity
                        .ok(chatMessageService.findById(id));
            } else return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>("No such message", HttpStatus.NOT_FOUND);
        }
    }

//    @DeleteMapping("/messages/{id}")
//    public ResponseEntity<?> deleteMessage ( @PathVariable Long id) throws Exception {
//        chatMessageService.deleteMessage(id);
//        return ResponseEntity
//                .ok("Deleted");
//    }
}