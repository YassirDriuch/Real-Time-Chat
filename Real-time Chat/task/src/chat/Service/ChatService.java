package chat.Service;

import chat.DAO.ChatMessageDAO;
import chat.DAO.UserDAO;
import chat.Model.ChatMessage;
import chat.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    ChatMessageDAO chatMessageDAO;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public ChatMessage sendMessage(ChatMessage message, Long usernameId) {
        User user = userDAO.getUser(usernameId);
        message.setType("message");
        message.setUserId(user.getId());
        message.setUsername(user.getUsername());
        message.setTimestamp(LocalDateTime.now());
        chatMessageDAO.saveMessage(message);
        return message;
    }

    public void registerUser(String username, SimpMessageHeaderAccessor headerAccessor) {
        User user = userDAO.saveUser(new User(username));
        headerAccessor.getSessionAttributes().put("usernameId", user.getId());
        simpMessagingTemplate.convertAndSend("/topic/users", getAllActiveUsers());
    }

    public List<ChatMessage> getAllMessages() {
        return chatMessageDAO.getAllMessages();
    }

    public List<User> getAllActiveUsers() {
        return userDAO.getAllUsers();
    }
}
