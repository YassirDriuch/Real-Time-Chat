package chat.Service;

import chat.DAO.ChatMessageDAO;
import chat.DAO.UserDAO;
import chat.DTO.ChatMessageDTO;
import chat.Model.ChatMessage;
import chat.Model.Enums.MessageType;
import chat.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    ChatMessageDAO chatMessageDAO;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public ChatMessage sendPublicMessage(ChatMessageDTO message, Long usernameId) {
        User user = userDAO.getUser(usernameId);
        ChatMessage chatMessage = new ChatMessage(
                MessageType.PUBLIC,
                message.content(),
                user
        );

        return chatMessageDAO.saveMessage(chatMessage);
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

    public void sendPrivateMessage(ChatMessageDTO message, Long usernameId) {

        User userFrom = userDAO.getUser(usernameId);
        User userTo = userDAO.getUser(message.toUserId());
        ChatMessage chatMessage = new ChatMessage(
                MessageType.PRIVATE,
                message.content(),
                userFrom,
                userTo
        );

        chatMessageDAO.saveMessage(chatMessage);

        simpMessagingTemplate.convertAndSend("/topic/private." + userFrom.getId(), chatMessage);
        simpMessagingTemplate.convertAndSend("/topic/private." + userTo.getId(), chatMessage);
    }

    public List<ChatMessage> getPrivateMessages(Long from, Long to) {
        return chatMessageDAO.getAllPrivateMessages(from, to);
    }
}
