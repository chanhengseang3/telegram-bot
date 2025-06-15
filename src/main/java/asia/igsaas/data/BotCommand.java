package asia.igsaas.data;

public class BotCommand {
    public static final String GET_SUMMARY_PERDAY = "/get_summary";
    public static final String GET_MENU = "/get_menu";

    public static boolean isDailySummary(String command) {
        return GET_SUMMARY_PERDAY.equals(command) || command.startsWith(GET_SUMMARY_PERDAY + "@");
    }

    public static boolean isGetMenu(String command) {
        return GET_MENU.equals(command) || command.startsWith(GET_MENU + "@");
    }
}
