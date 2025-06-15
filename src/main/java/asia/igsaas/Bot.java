package asia.igsaas;

import asia.igsaas.helper.ButtonCallbackHandler;
import asia.igsaas.helper.CommandHandler;
import asia.igsaas.helper.ReplyHandler;
import asia.igsaas.service.IncomeService;
import asia.igsaas.utils.PaywayParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.math.BigDecimal;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "7188303916:AAEbEbMBabwoyuY-G7rcRIZd-63E2zZywDc";
//    private static final String BOT_TOKEN = "7681368869:AAE5QlSXTOkeQMuJ3S6znuyCeyPVvo1YWFs";

    private final IncomeService incomeService;
    private final ButtonCallbackHandler callbackHandler;
    private final CommandHandler commandHandler;
    private final ReplyHandler replyHandler;

    @Override
    public String getBotUsername() {
        return "jianhua2_finance_bot";
//        return "payment_bk_bot";
    }

    public Bot(IncomeService incomeService,
               ButtonCallbackHandler callbackHandler,
               CommandHandler commandHandler,
               ReplyHandler replyHandler) throws TelegramApiException {
        super(BOT_TOKEN);
        this.incomeService = incomeService;
        this.callbackHandler = callbackHandler;
        this.commandHandler = commandHandler;
        this.replyHandler = replyHandler;
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("onUpdateReceived: {}", update);

        if (update.hasCallbackQuery()) {
            callbackHandler.handleCallback(this, update);
            return;
        }

        var message = update.getMessage();
        if (message == null) {
            return;
        }
        if (!StringUtils.hasText(message.getText())) {
            log.warn("message is empty");
            return;
        }

        var chatId = message.getChatId();

        if (message.isGroupMessage()) {
            log.info("group message: {}", message.getText());

            // parse & save data
            if (message.isCommand()) {
                log.debug("command message: {}", message);
                commandHandler.handleCommand(this, message);
            } else if (message.isReply()) {
                replyHandler.handleReply(this, message);
            } else if (StringUtils.hasText(message.getText())) {
                final var incoming = PaywayParser.parseAmountAndCurrency(message.getText());
                log.info("incoming amount: {}", incoming);
                if (incoming.amount().compareTo(BigDecimal.ZERO) > 0) {
                    incomeService.saveIncome(chatId, incoming);
                }
            }
        } else {
            if (message.isCommand()) {
                log.info("command message: {}", message.getText());
            } else {
                log.debug("message: {}", message);
            }
        }
    }
}
