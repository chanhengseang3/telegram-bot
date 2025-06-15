package asia.igsaas.helper;

import asia.igsaas.service.IncomeService;
import asia.igsaas.service.MessageService;
import asia.igsaas.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class ButtonCallbackHandler {

    private final IncomeService incomeService;
    private final MessageService messageService;

    public void handleCallback(TelegramLongPollingBot bot, Update update) {
        var callbackQuery = update.getCallbackQuery();
        var data = callbackQuery.getData();
        var message = callbackQuery.getMessage();
        var chatId = message.getChatId();
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
            BotUtils.sendMenu(bot, chatId, "ឆែករបាយការណ៍ថ្ងៃ:", menuList);
        } else if ("weekly_summary".equals(data)) {
            var text = "សរុប\u200Bលុយប្រចាំ\u200Bសប្ដាហ៍\u200Bនេះគឺ:";
            BotUtils.sendText(bot, chatId, text);
        } else if ("monthly_summary".equals(data)) {
            var text = "សរុប\u200Bលុយប្រចាំ\u200Bខែ\u200Bនេះគឺ:";
            BotUtils.sendText(bot, chatId, text);
        } else if (data.startsWith("summary_of_")) {
            final var dateStr = data.replaceFirst("summary_of_", "");
            final var date = DateUtils.parseDate(dateStr);
            final var result = incomeService.getSummary(chatId, date);
            var md = BotUtils.getMD(date, result);
//                var senderId = update.getCallbackQuery().getFrom().getId();
            BotUtils.sendMD(bot, chatId, md);
        } else if (data.equals("other_dates")) {
//                BotUtils.sendText(bot, chatId, );
            var keyboardM3 = ForceReplyKeyboard.builder()
                    .inputFieldPlaceholder(DateUtils.today().toString())
                    .forceReply(true)
                    .build();
            var result = BotUtils.sendMenu(bot, chatId, "ចង់ឆែកថ្ងៃណា?", keyboardM3);
            messageService.saveMessage(result.getMessageId());
        }
    }
}
