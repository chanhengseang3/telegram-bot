package asia.igsaas.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static asia.igsaas.data.BotCommand.isGetMenu;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {

    public void handleCommand(TelegramLongPollingBot bot, Message message) {
        var chatId = message.getChatId();
        if (isGetMenu(message.getText())) {
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
            BotUtils.sendMenu(bot, chatId, "ជ្រើសរើសរបាយការណ៍ប្រចាំ:", menuList);
        }
    }
}
