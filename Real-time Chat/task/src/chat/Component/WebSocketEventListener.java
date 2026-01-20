package chat.Component;

import chat.Model.ChatMessage;
import chat.Model.User;
import chat.DAO.UserDAO;
import chat.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    @Autowired
    ChatService chatService;

    @Autowired
    UserDAO userDAO;

    @Autowired
    private SimpMessagingTemplate template;

    @EventListener
    @SendToUser
    public List<ChatMessage> handleWebSocketConnect(SessionSubscribeEvent event) {
        return chatService.getAllMessages();
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

            userDAO.deleteUser(disconnectedUser.getId());

            template.convertAndSend("/topic/users", chatService.getAllActiveUsers());
            template.convertAndSend("/topic/public", chatMessage);


        }
    }

}
