package com.saturn.repositories;

import com.saturn.models.messageModels.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Интерфейс для работы с БД для ChatRoom
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    /**
     * Метод для возвратачат-комнаты по айдишникам получателя и отправителя
     * @param senderId Айди отправителя
     * @param recipientId Айди получаетеля
     * @return ChatRoom
     */
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}