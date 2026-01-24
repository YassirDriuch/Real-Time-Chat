package chat.Controller;

import chat.DTO.ChatMessageDTO;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    ChatService chatService;

    @MessageMapping("/chat.registerUser")
    public void registerUser(@Payload Map<String, String> usernameObj, SimpMessageHeaderAccessor headerAccessor) {
        chatService.registerUser(usernameObj.get("username"), headerAccessor);
    }

    @MessageMapping("/chat.sendPublic")
    @SendTo("/topic/public")
    public ChatMessage sendPublicMessage(@Payload ChatMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        return chatService.sendPublicMessage(message, (Long) headerAccessor.getSessionAttributes().get("usernameId"));

    }

    @MessageMapping("/chat.sendPrivate")
    public void sendPrivateMessage(@Payload ChatMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        chatService.sendPrivateMessage(message, (Long) headerAccessor.getSessionAttributes().get("usernameId"));
    }


    @GetMapping("/messages/private")
    public ResponseEntity<List<ChatMessage>> getPrivateMessages(@RequestParam("with") Long withUser,
                                                @RequestHeader("X-UserId") Long me) {
        return new ResponseEntity<>(chatService.getPrivateMessages(me, withUser), HttpStatus.OK);
    }


    @GetMapping("/messages/public")
    public ResponseEntity<List<ChatMessage>> getAllMessages() {
        return new ResponseEntity<>(chatService.getAllMessages(), HttpStatus.OK);
    }
}
