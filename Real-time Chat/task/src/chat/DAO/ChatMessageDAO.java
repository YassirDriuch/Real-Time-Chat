package chat.DAO;

import chat.Model.ChatMessage;

import java.util.List;

public interface ChatMessageDAO {

    public ChatMessage getMessage(int id);
    public void saveMessage(ChatMessage message);
    public void updateMessage(int id, ChatMessage message);
    public void deleteMessage(int id);
    public List<ChatMessage> getAllMessages();
}
