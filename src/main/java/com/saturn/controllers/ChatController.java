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

/**
 * Котроллер для сообщений
 */
@Controller
@CrossOrigin
public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatRoomService chatRoomService;
    @Autowired private UserService userService;
    @Autowired private JwtProvider jwtProvider;

    /**
     * Сокет запрос для обработки сообщения.
     * Отправлет сообщение оправителю и получателю
     * @param chatMessage Сообщение
     */
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

    /**
     * Сокет запрос для удаления сообщения
     * @param payload Тело запроса
     */
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

    /**
     * Сокет зарпос для редактирования сообщения
     * @param payload Тело запроса
     */
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

    /**
     * @deprecated
     */
    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId) {

        return ResponseEntity
                .ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    /**
     * Получить соббщения из определенной чат-комнаты
     * @param senderId Айди отправителя
     * @param recipientId Айди получателя
     * @return Хорошо
     */
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

    /**
     * Получить определнное сообщение
     * @param id Айди сообщения
     * @return Сообщение
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage ( @PathVariable Long id,
                                           @CookieValue("auth") String token) {
        ChatMessage msg = chatMessageService.findById(id);

        String user =  jwtProvider.getLoginFromToken(token);

        if (msg.getSenderName().equals(user) || msg.getRecipientName().equals(user)) {
            return ResponseEntity
                    .ok(chatMessageService.findById(id));
        } else return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}