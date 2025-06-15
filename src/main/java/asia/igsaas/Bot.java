package asia.igsaas;

import asia.igsaas.service.IncomeService;
import asia.igsaas.utils.BotUtils;
import asia.igsaas.utils.DateUtils;
import asia.igsaas.utils.PaywayParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static asia.igsaas.data.BotCommand.isDailySummary;
import static asia.igsaas.data.BotCommand.isGetMenu;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    //    private static final String BOT_TOKEN = "7188303916:AAEbEbMBabwoyuY-G7rcRIZd-63E2zZywDc";
    private static final String BOT_TOKEN = "7681368869:AAE5QlSXTOkeQMuJ3S6znuyCeyPVvo1YWFs";

    private final IncomeService incomeService;

    @Override
    public String getBotUsername() {
//        return "jianhua2_finance_bot";
        return "payment_bk_bot";
    }

    public Bot(IncomeService incomeService) throws TelegramApiException {
        super(BOT_TOKEN);
        this.incomeService = incomeService;
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("onUpdateReceived: {}", update);

        if (update.hasCallbackQuery()) {
            var callbackQuery = update.getCallbackQuery();
            var data = callbackQuery.getData();
            var chatId = callbackQuery.getMessage().getChatId();
            if ("daily_summary".equals(data)) {
                var date = DateUtils.today();
                var buttons = new ArrayList<InlineKeyboardButton>();

                for (int i = 0; i < 3; i++) {
                    var button = InlineKeyboardButton.builder()
                            .text(DateUtils.formatToMonthAndDate(date))
                            .callbackData("summary_of_" + date)
                            .build();
                    buttons.add(button);
                    date = date.minusDays(1);
                }
                var more = InlineKeyboardButton.builder()
                        .text("ថ្ងៃ\u200Bផ្សេង\u200Bទៀត")
                        .callbackData("other_dates")
                        .build();
                buttons.add(more);
                var menuList = InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(buttons)
                        .build();
                BotUtils.sendMenu(this, chatId, "ឆែករបាយការណ៍ថ្ងៃ:", menuList);
            } else if ("weekly_summary".equals(data)) {
                var text = "សរុប\u200Bលុយប្រចាំ\u200Bសប្ដាហ៍\u200Bនេះគឺ:";
                BotUtils.sendText(this, chatId, text);
            } else if ("monthly_summary".equals(data)) {
                var text = "សរុប\u200Bលុយប្រចាំ\u200Bខែ\u200Bនេះគឺ:";
                BotUtils.sendText(this, chatId, text);
            } else if (data.startsWith("summary_of_")) {
                final var dateStr = data.replaceFirst("summary_of_", "");
                final var date = DateUtils.parseDate(dateStr);
                final var result = incomeService.getSummary(chatId, date);
                var md = BotUtils.getMD(date, result);
                BotUtils.sendMD(this, chatId, md);
            } else if (data.equals("other_dates")) {
                BotUtils.sendText(this, chatId, "ចង់ឆែកថ្ងៃណា?");
            }
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
                if (isDailySummary(message.getText())) {
                    final var date = DateUtils.today();
                    final var totalAmount = incomeService.getSummary(chatId, date);
                    var md = BotUtils.getMD(date, totalAmount);
                    BotUtils.sendMD(this, chatId, md);
                } else if (isGetMenu(message.getText())) {
                    var daily = InlineKeyboardButton.builder()
                            .text("ថ្ងៃ")
                            .callbackData("daily_summary")
                            .build();
                    var weekly = InlineKeyboardButton.builder()
                            .text("សប្ដាហ៍")
                            .callbackData("weekly_summary")
                            .build();
                    var monthly = InlineKeyboardButton.builder()
                            .text("ខែ")
                            .callbackData("monthly_summary")
                            .build();
                    var menuList = InlineKeyboardMarkup.builder()
                            .keyboardRow(List.of(daily, weekly, monthly)).build();
                    BotUtils.sendMenu(this, chatId, "ជ្រើសរើសរបាយការណ៍ប្រចាំ:", menuList);
                }
            } else if (StringUtils.hasText(message.getText())) {
                final var incoming = PaywayParser.parseAmountAndCurrency(message.getText());
                log.info("incoming amount: {}", incoming);
                if (incoming.amount().compareTo(BigDecimal.ZERO) > 0) {
                    incomeService.saveIncome(chatId, incoming);
//                    BotUtils.sendText(this, chatId, "Noted income of: %s ".formatted(incoming));
                }
            }
        } else {
            if (message.isCommand()) {
                log.info("command message: {}", message.getText());
            } else {

            }
        }
    }
}
