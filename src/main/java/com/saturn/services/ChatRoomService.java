package com.saturn.services;

import com.saturn.models.messageModels.ChatRoom;
import com.saturn.repositories.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис чат-комнаты
 */
@Service
public class ChatRoomService {

    @Autowired private ChatRoomRepository chatRoomRepository;

    /**
     * Метод для возврата айдишника чат-комнаты по
     * айди отправителя и получателя.
     * Если поменять их местами, номер комнаты будет таким же
     * @param senderId Айди отправителя
     * @param recipientId Айди получателя
     * @param createIfNotExist Создать если отсутствует
     * @return
     */
    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist) {
        return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(!createIfNotExist) {
                        return  Optional.empty();
                    }
                    String chatId =
                            String.format("%s_%s", senderId, recipientId);

                    ChatRoom senderRecipient = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    ChatRoom recipientSender = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();
                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);
                });
    }
}