package asia.igsaas;

import asia.igsaas.helper.BotUtils;
import asia.igsaas.helper.ButtonCallbackHandler;
import asia.igsaas.helper.CommandHandler;
import asia.igsaas.helper.ReplyHandler;
import asia.igsaas.service.IncomeService;
import asia.igsaas.utils.PaywayParser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final IncomeService incomeService;
    private final ButtonCallbackHandler callbackHandler;
    private final CommandHandler commandHandler;
    private final ReplyHandler replyHandler;
    private final String botName;

    @Override
    public String getBotUsername() {
        return botName;
    }

    public Bot(@Value("${bot.token}") String botToken,
               @Value("${bot.name}") String botName,
               IncomeService incomeService,
               ButtonCallbackHandler callbackHandler,
               CommandHandler commandHandler,
               ReplyHandler replyHandler) throws TelegramApiException {
        super(botToken);
        this.incomeService = incomeService;
        this.callbackHandler = callbackHandler;
        this.commandHandler = commandHandler;
        this.replyHandler = replyHandler;
        this.botName = botName;
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @PostConstruct
    private void init() {
        String text = """
                ៛114,800 paid by Sao Dy (*210) on Jun 15, 11:49 PM via ABA KHQR (ACLEDA Bank Plc.) at MISS 16 by T.KIM. Trx. ID: 175000618948483, APV: 603982.
                """;
        BotUtils.sendText(this, -4752202322L, text);
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("onUpdateReceived: {}", update);

        if (update.hasCallbackQuery()) {
            log.debug("onUpdateReceived: {}", update.getCallbackQuery());
            callbackHandler.handleCallback(this, update);
            return;
        }

        var message = update.getMessage();
        if (message == null) {
            log.debug("message is null");
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
