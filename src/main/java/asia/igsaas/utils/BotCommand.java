package asia.igsaas.utils;

public class BotCommand {
    public static final String GET_SUMMARY_PERDAY = "/get_summary";

    public static boolean isDailySummary(String command) {
        return GET_SUMMARY_PERDAY.equals(command) || command.startsWith(GET_SUMMARY_PERDAY + "@");
    }
}
