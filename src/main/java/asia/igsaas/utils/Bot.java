package asia.igsaas.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;

import static asia.igsaas.utils.BotCommand.isDailySummary;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "7188303916:AAEbEbMBabwoyuY-G7rcRIZd-63E2zZywDc";

    @Override
    public String getBotUsername() {
        return "jianhua2_finance_bot";
    }

    public Bot() {
        super(BOT_TOKEN);
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("onUpdateReceived: {}", update);

        if (update.hasChannelPost()) {
            log.info("onUpdateReceived: {}", update.getChannelPost());
        }

        var message = update.getMessage();
        if (message == null) {
            return;
        }

        if (message.isGroupMessage()) {
            log.info("group message: {}", message.getText());

            var chatId = message.getChatId();

            // parse & save data
            if (message.isCommand()) {
                log.info("command message: {}", message);
                if (isDailySummary(message.getText())) {
                    final var totalAmount = StorageUtils.getTotalIncoming(chatId, DateUtils.today());
                    sendText(chatId, "Total incoming for %s is %s".formatted(DateUtils.today(), totalAmount));
                }
            } else if (StringUtils.hasText(message.getText())) {
                final var incoming = PaywayParser.parseAmountAndCurrency(message.getText());
                log.info("incoming amount: {}", incoming);
                if (incoming.amount().compareTo(BigDecimal.ZERO) > 0) {
                    StorageUtils.addIncoming(chatId, incoming.amount(), incoming.currency());
                    sendText(chatId, "Noted income of:%s".formatted(incoming));
                }
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

    private void sendHtml(Long who, String txt) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .parseMode(ParseMode.HTML)
                .build();
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private String getHtml(Long who) {
        return """
                <body>
                <ul>
                <li> test 1 </li>
                <li> test 2 </li>
                </body>
                """;
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
