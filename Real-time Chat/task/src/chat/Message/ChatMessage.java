package chat.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private int id;

    private String type;
    private String content;
    private String username;
    private Long userId;
    private LocalDateTime timestamp;

    public ChatMessage(String type, String content, Long userId) {
        this.type = type;
        this.content = content;
        this.userId = userId;
    }

}
