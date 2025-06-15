package asia.igsaas.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static asia.igsaas.utils.BotCommand.isListExpense;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private static final ConcurrentHashMap<Integer, String> db = new ConcurrentHashMap<>();

    @Override
    public String getBotUsername() {
        return "jianhua2_finance_bot";
    }

    @Override
    public String getBotToken() {
        return "7188303916:AAEbEbMBabwoyuY-G7rcRIZd-63E2zZywDc";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            var callbackQuery = update.getCallbackQuery();
            var data = callbackQuery.getData();
            var senderId = callbackQuery.getFrom().getId();
            if ("reply_button1".equals(data)) {
                var keyboardM2 = ForceReplyKeyboard.builder()
                        .inputFieldPlaceholder("Reply")
                        .forceReply(true)
                        .build();
                var result = sendMenu(senderId, "Reply", keyboardM2);
                var msgId = result.getMessageId();
                log.info("message id is:{}", msgId);
                db.put(msgId, "EXPENSE_REASON");
            } else if ("reply_button2".equals(data)) {
                var keyboardM3 = ForceReplyKeyboard.builder()
                        .inputFieldPlaceholder("Reply")
                        .forceReply(true)
                        .build();
                var result = sendMenu(senderId, "Reply", keyboardM3);
                var msgId = result.getMessageId();
                db.put(msgId, "EXPENSE_AMOUNT");
            }
            return;
        }

        var message = update.getMessage();
        var senderId = message.getFrom().getId();
        if (message.isCommand()) {
            if (isListExpense(message.getText())) {
                var reason = InlineKeyboardButton.builder()
                        .text("Reply")
                        .callbackData("reply_button1")
                        .build();
                var reasonReply = InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(reason)).build();

                sendMenu(senderId, "What is the expense reason?", reasonReply);
            }
        } else {
            var isReply = message.isReply();
            if (isReply) {
                log.info("is reply");
                var replyToMessageId = message.getReplyToMessage().getMessageId();
                log.info("replyToMessageId: {}", replyToMessageId);
                if (Objects.equals(db.get(replyToMessageId), "EXPENSE_REASON")) {
                    var amount = InlineKeyboardButton.builder()
                            .text("Reply")
                            .callbackData("reply_button2")
                            .build();
                    var amountReply = InlineKeyboardMarkup.builder()
                            .keyboardRow(List.of(amount)).build();

                    sendMenu(senderId, "What is the expense amount?", amountReply);
                } else if (Objects.equals(db.get(replyToMessageId), "EXPENSE_AMOUNT")) {
                    sendText(senderId, "Done Thank you");
                }
            } else {
                sendText(senderId, "Please Input command");
            }
        }
    }

    private Message sendMenu(Long who, String txt, ReplyKeyboard kb) {
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .parseMode("HTML")
                .text(txt)
                .replyMarkup(kb)
                .build();

        try {
            return execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }
}
