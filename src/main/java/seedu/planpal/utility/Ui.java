package seedu.planpal.utility;

import java.util.Scanner;

/**
 * Provides user interface functionalities for the PlanPal application.
 */
public class Ui {
    protected static final String LINE_SEPARATOR = "_________________________________________________________";
    /**
     * Prints a series of messages enclosed between line separators.
     * This method is useful for clear and structured display of information in the console.
     *
     * @param messages an array of messages to be printed; each message is printed on a new line
     */
    public static void print(String... messages){
        System.out.println(LINE_SEPARATOR);
        for (String message : messages) {
            System.out.println(message);
        }
        System.out.println(LINE_SEPARATOR);
    }

    /**
     * Displays a welcome message to the user at the start of the application.
     */
    public static void printWelcomeMessage() {
        print(
                "Hello! I'm PlanPal.",
                "How can I be of service?"
        );
    }

    /**
     * Displays a goodbye message to the user upon exiting the application.
     */
    public static void printByeMessage() {
        print("Bye. Hope to see you again!");
    }

    public static void printCreateStorage(String pathToStorage) {
        print("Created a new storage at ", pathToStorage);
    }

    public static String getSetCategory() {
        print(
                "Options : ",
                "1. add Category type (e.g. add close friend)",
                "2. remove Category type (e.g. remove emergency)",
                "3. edit Category of Contact (e.g. edit 1 friend/family)"
        );
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }
}
