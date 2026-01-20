package chat.DAO;

import chat.Model.ChatMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatMessageDAOImpl implements ChatMessageDAO{

    private int id = 0;

    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    public ChatMessage getMessage(int id) {
        return messages.get(id);
    }

    @Override
    public void saveMessage(ChatMessage message) {
        message.setId(id++);
        messages.add(message);
    }

    @Override
    public void updateMessage(int id, ChatMessage message) {
        messages.set(id, message);
    }

    @Override
    public void deleteMessage(int id) {
        messages.remove(id);
    }

    public List<ChatMessage> getAllMessages() {
        return messages;
    }
}
