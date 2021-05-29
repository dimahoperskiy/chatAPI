package com.saturn.controllers;

import com.saturn.models.messageModels.ChatMessage;
import com.saturn.models.messageModels.ChatNotification;
import com.saturn.models.messageModels.DeleteMessage;
import com.saturn.models.messageModels.EditMessage;
import com.saturn.repositories.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    public void setup() {
         webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @Test
    void processMessage() throws InterruptedException, ExecutionException, TimeoutException {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/user/1/queue/messages", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return ChatNotification.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object payload) {
                System.out.println("Received message:" + payload);
                String msg = "Received message from " + ((ChatNotification) payload).getSenderName();
                blockingQueue.add(msg);
            }
        });

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId("2");
        chatMessage.setRecipientId("1");
        chatMessage.setSenderName("dima");
        chatMessage.setRecipientName("anton");
        chatMessage.setContent("Hiiiii");

        session.send("/app/chat", chatMessage);

        assertEquals("Received message from dima", blockingQueue.poll(1, TimeUnit.SECONDS));
    }

    @Test
    void deleteMessage() throws Throwable {
        Long deleteMsgId = 193L;

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        CountDownLatch latch = new CountDownLatch(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/user/1/queue/messages", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return ChatNotification.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object payload) {
                if (((ChatNotification) payload).getId() != null) {
                    System.out.println("Deleted message:" + payload);
                    String msg = "Deleted message from " + ((ChatNotification) payload).getSenderName();
                    blockingQueue.add(msg);
                    latch.countDown();
                } else {
                    System.out.println("No such message");
                    latch.countDown();
                }

            }
        });

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setId(deleteMsgId);
        deleteMessage.setRecipient("anton");

        if (chatMessageRepository.findById(deleteMsgId).isPresent()) {
            System.out.println(chatMessageRepository.findById(deleteMsgId).orElseThrow());
        } else {
            System.out.println("Message deleted");
        }

        session.send("/app/chat/delete", deleteMessage);

        latch.await();


        if (chatMessageRepository.findById(deleteMsgId).isPresent()) {
            System.out.println(chatMessageRepository.findById(deleteMsgId).orElseThrow());
        } else {
            System.out.println("Message deleted");
        }

        assertEquals("Deleted message from dima", blockingQueue.poll(1, TimeUnit.SECONDS));
    }

    @Test
    void editMessage() throws InterruptedException, ExecutionException, TimeoutException {
        Long editMsgId = 192L;

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        CountDownLatch latch = new CountDownLatch(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/user/1/queue/messages", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return ChatNotification.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object payload) {
                if (((ChatNotification) payload).getId() != null) {
                    System.out.println("Updated message:" + payload);
                    String msg = "Updated message from " + ((ChatNotification) payload).getSenderName();
                    blockingQueue.add(msg);
                    latch.countDown();
                } else {
                    System.out.println("No such message");
                    latch.countDown();
                }

            }
        });

        EditMessage editMessage = new EditMessage();
        editMessage.setId(editMsgId);
        editMessage.setRecipient("anton");
        editMessage.setContent("New content!!");

        if (chatMessageRepository.findById(editMsgId).isPresent()) {
            System.out.println("Old content");
            System.out.println(chatMessageRepository.findById(editMsgId).orElseThrow().getContent());
        } else {
            System.out.println("Message Updated");
        }

        session.send("/app/chat/edit", editMessage);

        latch.await();


        if (chatMessageRepository.findById(editMsgId).isPresent()) {
            System.out.println("New content");
            System.out.println(chatMessageRepository.findById(editMsgId).orElseThrow().getContent());
        } else {
            System.out.println("Message Updated");
        }

        assertEquals("Updated message from dima", blockingQueue.poll(1, TimeUnit.SECONDS));
    }
}