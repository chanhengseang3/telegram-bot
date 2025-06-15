package asia.igsaas.service;

import asia.igsaas.domain.Message;
import asia.igsaas.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public boolean userReplyingToBotQuestion(final Integer messageId) {
        return messageRepository.findById(messageId)
                .filter(Message::isAwaitForReply)
                .filter(message -> !message.isReplied())
                .isPresent();
    }

    public void saveMessage(final Integer messageId) {
        final var message = new Message();
        message.setMessageId(messageId);
        messageRepository.save(message);
    }
}
