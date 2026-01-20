package chat.Controller;

import chat.Model.ChatMessage;
import chat.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        return chatService.sendMessage(message, (Long) headerAccessor.getSessionAttributes().get("usernameId"));

    }

    @MessageMapping("/chat.registerUser")
    public void registerUser(@Payload Map<String, String> usernameObj, SimpMessageHeaderAccessor headerAccessor) {
       chatService.registerUser(usernameObj.get("username"), headerAccessor);
    }


    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getAllMessages() {
        return new ResponseEntity<>(chatService.getAllMessages(), HttpStatus.OK);
    }
}
