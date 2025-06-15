package asia.igsaas.helper;

import asia.igsaas.service.IncomeService;
import asia.igsaas.service.MessageService;
import asia.igsaas.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyHandler {

    private final MessageService messageService;
    private final IncomeService incomeService;

    public void handleReply(TelegramLongPollingBot bot, Message message) {
        var chatId = message.getChatId();
        // check if it is bot message
        var isReplyingToBot = messageService.userReplyingToBotQuestion(message.getReplyToMessage().getMessageId());
        if (!isReplyingToBot) {
            return;
        }

        final var text = message.getText();
        try {
            final var date = DateUtils.parseDate(text);
            final var totalAmount = incomeService.getSummary(chatId, date);
            var md = BotUtils.getMD(date, totalAmount);
            BotUtils.sendMD(bot, chatId, md);
        } catch (Exception e) {
            var keyboardM3 = ForceReplyKeyboard.builder()
                    .inputFieldPlaceholder(DateUtils.today().toString())
                    .forceReply(true)
                    .build();
            var result = BotUtils.sendMenu(bot, chatId, "បញ្ចូល\u200Bម្ដង\u200Bទៀតមកប្រូ\u200B អត់\u200Bត្រូវទេ", keyboardM3);
            messageService.saveMessage(result.getMessageId());
        }
    }
}
