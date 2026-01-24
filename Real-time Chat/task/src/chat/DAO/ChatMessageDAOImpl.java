package chat.DAO;

import chat.Model.ChatMessage;
import chat.Model.Enums.MessageType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatMessageDAOImpl implements ChatMessageDAO {

    private int id = 0;

    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    public ChatMessage getMessage(int id) {
        return messages.get(id);
    }

    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        message.setId(id++);
        messages.add(message);
        return message;
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
        return messages.stream().filter(chatMessage -> chatMessage.getType().equals(MessageType.PUBLIC))
                .toList();
    }

    public List<ChatMessage> getAllPrivateMessages(long userFrom, long userTo) {
        return messages.stream()
                .filter(chatMessage -> chatMessage.getType().equals(MessageType.PRIVATE) &&
                        (
                                (chatMessage.getSender().getId().equals(userFrom) &&
                                        chatMessage.getReceiver().getId().equals(userTo))
                                        ||
                                        (chatMessage.getSender().getId().equals(userTo) &&
                                                chatMessage.getReceiver().getId().equals(userFrom))
                        ))
                .toList();
    }
}
