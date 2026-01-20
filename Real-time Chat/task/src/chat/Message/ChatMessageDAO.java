package chat.Message;

import org.springframework.stereotype.Repository;

import java.util.List;

public interface ChatMessageDAO {

    public ChatMessage getMessage(int id);
    public void saveMessage(ChatMessage message);
    public void updateMessage(int id, ChatMessage message);
    public void deleteMessage(int id);
    public List<ChatMessage> getAllMessages();
}
