package asia.igsaas.utils;

public class BotCommand {
    public static final String LIST_EXPENSE = "/list_expense";

    public static boolean isListExpense(String command) {
        return LIST_EXPENSE.equals(command);
    }
}
