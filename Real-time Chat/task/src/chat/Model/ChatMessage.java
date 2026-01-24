package chat.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import chat.Model.Enums.MessageType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private int id;

    private MessageType type;
    private String content;
    private User sender;
    private User receiver;
    private LocalDateTime timestamp;

    public ChatMessage(MessageType type, String content, User sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        timestamp = LocalDateTime.now();
    }

    public ChatMessage(MessageType type, String content, User sender, User receiver) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        timestamp = LocalDateTime.now();
    }

}
