package chat.Component;

import chat.Message.ChatMessage;
import chat.Message.ChatMessageDAO;
import chat.User.User;
import chat.User.UserDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    @Autowired
    ChatMessageDAO chatMessageDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    private SimpMessagingTemplate template;

    @EventListener
    @SendToUser
    public List<ChatMessage> handleWebSocketConnect(SessionSubscribeEvent event) {
        log.info(event.toString());
        return chatMessageDAO.getAllMessages();
    }


    @EventListener
    public void disconnect(SessionDisconnectEvent event) {
        System.out.println("Someone disconnected");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        User disconnectedUser = userDAO.getUser((Long) headerAccessor.getSessionAttributes().get("usernameId"));
        if (disconnectedUser != null) {

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType("Leave");
            chatMessage.setUsername(disconnectedUser.getUsername());
            chatMessage.setUserId(disconnectedUser.getId());

            template.convertAndSend("/topic/public", chatMessage);

        }
    }

}
