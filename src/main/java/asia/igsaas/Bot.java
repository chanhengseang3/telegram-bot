package asia.igsaas;

import asia.igsaas.domain.IncomingBalance;
import asia.igsaas.service.IncomingService;
import asia.igsaas.utils.DateUtils;
import asia.igsaas.utils.PaywayParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.math.BigDecimal;
import java.util.List;

import static asia.igsaas.utils.BotCommand.isDailySummary;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "7188303916:AAEbEbMBabwoyuY-G7rcRIZd-63E2zZywDc";

    private final IncomingService incomingService;

    @Override
    public String getBotUsername() {
        return "jianhua2_finance_bot";
    }

    public Bot(IncomingService incomingService) throws TelegramApiException {
        super(BOT_TOKEN);
        this.incomingService = incomingService;
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
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
                    final var totalAmount = incomingService.getSummary(chatId, DateUtils.today());
                    var md = getMD(totalAmount);
                    sendMD(chatId, md);
                }
            } else if (StringUtils.hasText(message.getText())) {
                final var incoming = PaywayParser.parseAmountAndCurrency(message.getText());
                log.info("incoming amount: {}", incoming);
                if (incoming.amount().compareTo(BigDecimal.ZERO) > 0) {
                    incomingService.saveIncome(chatId, incoming);
                    sendText(chatId, "Noted income of: %s ".formatted(incoming));
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

    private void sendMD(Long who, String md) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(md)
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private String getMD(List<IncomingBalance> results) {
        String md = "Total income of: *" + DateUtils.today() + "* is: \n";
        md = md.replace("-", "\\-");
        for (var result : results) {
            md = md.concat("\\- *" + result.getCurrency() + "* : " + result.getAmount() + result.getCurrency().getSymbol() + "\n");
        }
        md = md.replace(".", "\\.");
        return md;
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
