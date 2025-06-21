package asia.igsaas.helper;

import asia.igsaas.domain.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Random;

import static asia.igsaas.data.BotCommand.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {

    private static final Random random = new Random();

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
        } else if (isSimulateKhr(message.getText())) {
            sendSimulation(bot, Currency.KHR);
        } else if (isSimulateUsd(message.getText())) {
            sendSimulation(bot, Currency.USD);
        }
    }

    private void sendSimulation(TelegramLongPollingBot bot, Currency currency) {
        String text = """
                %s%.2f paid by Sao Dy (*210) on Jun 15, 11:49 PM via ABA KHQR (ACLEDA Bank Plc.) at MISS 16 by T.KIM. Trx. ID: 175000618948483, APV: 603982.
                """.formatted(currency.getSymbol(), random.nextDouble(10, 1000));
        BotUtils.sendText(bot, -4752202322L, text);
    }
}
