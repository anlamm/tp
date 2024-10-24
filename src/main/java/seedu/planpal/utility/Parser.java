package seedu.planpal.utility;

import seedu.planpal.contacts.ContactManager;
import seedu.planpal.exceptions.EmptyDescriptionException;
import seedu.planpal.exceptions.IllegalCommandException;
import seedu.planpal.exceptions.PlanPalExceptions;
import seedu.planpal.utility.filemanager.FileManager;

/**
 * Parses user input and executes commands within the PlanPal application.
 */
public class Parser implements ListFunctions<String> {

    private static final String ADD_COMMAND = "add";
    private static final String DELETE_COMMAND = "delete";
    private static final String CATEGORY_COMMAND = "category";
    private static final String SEARCH_CATEGORY_COMMAND = "search";
    private static final String EDIT_COMMAND = "edit";
    private static final String FIND_COMMAND = "find";
    private static final String LIST_COMMAND = "list";
    private static final String BYE_COMMAND = "bye";
    private static final int INPUT_SEGMENTS = 2;

    Ui ui = new Ui();
    ContactManager contactManager;

    public Parser(FileManager fileManager) {
        contactManager = new ContactManager(fileManager);
    }

    /**
     * Processes user commands by splitting the input into command and description.
     *
     * @param input User input string that contains a command followed by description.
     * @throws PlanPalExceptions If an invalid command is provided or the description is empty.
     */
    public void processCommand(String input) throws PlanPalExceptions {
        try {
            String[] inputParts = input.split(" ", INPUT_SEGMENTS);
            String command = inputParts[0];
            String description;

            switch (command) {
            case ADD_COMMAND:
                description = inputParts[1].trim();
                contactManager.addContact(description);
                break;

            case DELETE_COMMAND:
                description = inputParts[1].trim();
                contactManager.deleteContact(description);
                break;

            case CATEGORY_COMMAND:
                boolean quit = false;
                while (!quit) {
                    quit = contactManager.handleCategory(Ui.getSetCategory().trim());
                }
                break;

            case SEARCH_CATEGORY_COMMAND:
                description = inputParts[1].trim();
                contactManager.searchCategory(description);
                break;

            case LIST_COMMAND:
                contactManager.viewContactList();
                break;

            case EDIT_COMMAND:
                try {
                    String query = inputParts[1].trim();
                    contactManager.editContact(query);
                } catch (NumberFormatException e) {
                    throw new PlanPalExceptions("Invalid index format. Please provide a valid number.");
                }
                break;

            case FIND_COMMAND:
                String query = inputParts[1].trim();
                contactManager.findContact(query);
                break;

            case BYE_COMMAND:
                ui.printByeMessage();
                System.exit(0);
                break;

            default:
                throw new IllegalCommandException();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new EmptyDescriptionException();
        }
    }
}
