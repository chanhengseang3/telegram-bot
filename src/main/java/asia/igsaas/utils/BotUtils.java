package asia.igsaas.utils;

import asia.igsaas.domain.Currency;
import asia.igsaas.domain.IncomingBalance;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.List;

public class BotUtils {

    public static Message sendMenu(TelegramLongPollingBot bot, Long who, String txt, ReplyKeyboard kb) {
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .parseMode("HTML")
                .text(txt)
                .replyMarkup(kb)
                .build();

        try {
            return bot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendMD(TelegramLongPollingBot bot, Long who, String md) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(md)
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        try {
            bot.execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public static String getMD(LocalDate date, List<IncomingBalance> results) {
        String md = "សរុបលុយថ្ងៃ: *" + date + "* : \n";
        md = md.replace("-", "\\-");
        if (results.isEmpty()) {
            md = "អត់\u200Bទាន់\u200Bមានលុយចូលទេ\u200B\uD83D\uDE0Cសម្រាប់\u200Bថ្ងៃ: " + date;
            return md.replace("-", "\\-");
        }
        for (var result : results) {
            String amount = "" + result.getAmount();
            if (result.getCurrency().equals(Currency.KHR)) {
                amount = formatAmountKHR(result.getAmount());
            }
            md = md.concat("\\- *" + result.getCurrency() + "* : " + amount + result.getCurrency().getSymbol() + "\n");
        }
        md = md.replace(".", "\\.");
        return md;
    }

    public static void sendText(TelegramLongPollingBot bot, Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            bot.execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public static String formatAmountKHR(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        return formatter.format(amount);
    }
}