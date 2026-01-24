package chat.Component;

import lombok.RequiredArgsConstructor;
import chat.DAO.UserDAO;
import chat.Model.ChatMessage;
import chat.Model.Enums.MessageType;
import chat.Model.User;
import chat.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


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
    public void disconnect(SessionDisconnectEvent event) {
        System.out.println("Someone disconnected");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        User disconnectedUser = userDAO.getUser((Long) headerAccessor.getSessionAttributes().get("usernameId"));
        if (disconnectedUser != null) {

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(MessageType.LEAVE);
            chatMessage.setSender(disconnectedUser);

            userDAO.deleteUser(disconnectedUser.getId());

            template.convertAndSend("/topic/users", chatService.getAllActiveUsers());
            template.convertAndSend("/topic/public", chatMessage);


        }
    }

}

