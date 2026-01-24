package chat.DAO;

import chat.Model.ChatMessage;

import java.util.List;

public interface ChatMessageDAO {

    ChatMessage getMessage(int id);
    ChatMessage saveMessage(ChatMessage message);
    void updateMessage(int id, ChatMessage message);
    void deleteMessage(int id);
    List<ChatMessage> getAllMessages();
    List<ChatMessage> getAllPrivateMessages(long userFrom, long userTo);
}
