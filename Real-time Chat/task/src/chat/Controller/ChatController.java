package chat.Controller;

import chat.Message.ChatMessage;
import chat.Message.ChatMessageDAO;
import chat.User.User;
import chat.User.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class ChatController {

    private ArrayList<String> usernames = new ArrayList<>();

    @Autowired
    ChatMessageDAO chatMessageDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        User userMessage = userDAO.getUser((Long) headerAccessor.getSessionAttributes().get("usernameId"));
        message.setType("message");
        message.setUserId(userMessage.getId());
        message.setUsername(userMessage.getUsername());
        message.setTimestamp(LocalDateTime.now());
        chatMessageDAO.saveMessage(message);
        log.info(chatMessageDAO.getAllMessages().toString());
        return message;
    }

    @MessageMapping("/chat.registerUser")
    public void registerUser(@Payload Map<String, String> usernameObj, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("usernameId", userDAO.saveUser(new User(usernameObj.get("username"))));
        log.info(usernameObj.get("username") + "\n" + userDAO.getUser((Long) headerAccessor.getSessionAttributes().get("usernameId")));
        //return chatMessageDAO.getAllMessages();
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getAllMessages() {
        log.info("Called!");
        return new ResponseEntity<>(chatMessageDAO.getAllMessages(), HttpStatus.OK);
    }
}
