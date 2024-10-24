package seedu.planpal.contacts;

import seedu.planpal.exceptions.IllegalCommandException;
import seedu.planpal.exceptions.PlanPalExceptions;
import seedu.planpal.utility.Editable;
import seedu.planpal.utility.filemanager.Storeable;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Represents a contact in the PlanPal application.
 */
public class Contact implements Editable, Storeable {
    private static final String CATEGORY_SEPARATOR = "/";
    private static final String CATEGORY_VALUE_SEPARATOR = ":";

    private String name;
    private String phone;
    private String email;
    private String commandDescription;
    private ArrayList<String> categories = new ArrayList<>();

    /**
     * Default constructor for Contact.
     * Primarily used as an identifier for retrieving the storage path in the FileManager class.
     */
    public Contact() {
        // do nothing (used as an identifier for the fileManager class to get storagePath)
    }

    /**
     * Constructs a new Contact object by parsing the given description string. The description
     * string is split using the CATEGORY_SEPARATOR, and for each category (starting from
     * the second item in the array), it processes the category using the {@link #processEditFunction(String)} method.
     *
     * @param description A string containing the description of the contact, with different
     *                    categories separated by a CATEGORY_SEPARATOR.
     * @throws PlanPalExceptions If an error occurs while processing the categories.
     */
    public Contact(String description) throws PlanPalExceptions {
        setCommandDescription(description);
        String[] categories = description.split(CATEGORY_SEPARATOR);
        if (categories.length == 1) {
            throw new IllegalCommandException();
        }
        assert categories.length >= 2: "Illegal command executed in contact";
        for (int categoryIndex = 1; categoryIndex < categories.length; categoryIndex++) {
            processEditFunction(categories[categoryIndex]);
        }
    }

    /**
     * Checks if this contact's name contains the name of another contact.
     *
     * @param other The other contact to compare with.
     * @return True if this contact's name contains the other contact's name; false otherwise.
     */
    public boolean contains(Contact other) {
        return this.name.toLowerCase().contains(other.getName().toLowerCase());
    }

    /**
     * Updates the command description by modifying the value of the specified category.
     * The method splits the current command description into its categories and updates the
     * category with the new value, if it exists.
     *
     * @param field The field whose value needs to be updated (e.g., "name").
     * @param val The new value for the specified category.
     *
     * @throws PlanPalExceptions If the input is incomplete or improperly formatted.
     * @throws IllegalCommandException If the specified category is not recognized.
     */
    private void setCommandDescription(String field, String val) throws PlanPalExceptions, IllegalCommandException {
        boolean isField = false;
        for (String cat : ContactManager.INFORMATIONFIELDS) {
            if (field.equals(cat)) {
                isField = true;
                editField(field, val);
            }
        }
        if (!isField) {
            throw new IllegalCommandException();
        }
    }

    /**
     * Helper function to edit field of Contact
     *
     * @param field The field whose value needs to be updated (e.g., "name").
     * @param val The new value for the specified category.
     *
     * @throws PlanPalExceptions If the input is incomplete or improperly formatted.
     */
    private void editField(String field, String val) throws PlanPalExceptions {
        if (field.equals("email") && !val.contains("@")) {
            throw new PlanPalExceptions("email address is not valid");
        } 
        try {
            Field changeField = this.getClass().getDeclaredField(field);
            changeField.setAccessible(true);
            changeField.set(this, val);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new PlanPalExceptions(e.getMessage());
        }
    }

    @Override
    public void setCommandDescription(String description) {
        this.commandDescription = description;
    }

    /**
     * Processes an edit command for the contact. This method parses the input string
     * to extract the category and the new value, then applies the change to the contact.
     * Currently, only the "name" category is supported.
     *
     * @param input The edit command in the format "category:value", where "category"
     *              specifies the field to edit (e.g., "name") and "value" specifies
     *              the new value for that field.
     * @throws PlanPalExceptions If the input is incomplete or improperly formatted.
     * @throws IllegalCommandException If the specified category is not recognized.
     */
    @Override
    public void processEditFunction(String input) throws PlanPalExceptions {
        assert input != null : "Input cannot be null";
        assert !input.trim().isEmpty() : "Input cannot be empty";

        String[] inputParts = input.split(CATEGORY_VALUE_SEPARATOR);
        if (inputParts.length < 2) {
            throw new PlanPalExceptions("The command is incomplete. Please provide a value for " + inputParts[0]);
        }

        assert inputParts.length >= 2 : "Input must contain category and value";

        String category = inputParts[0].trim();
        String valueToEdit = inputParts[1].trim();

        assert category != null && !category.isEmpty() : "Category cannot be null";
        assert valueToEdit != null && !valueToEdit.isEmpty() : "Value cannot be null";

        setCommandDescription(category, valueToEdit);
    }

    // A string representation of a Contact
    @Override
    public String toString() {
        return "[Name = " + name + " Phone = "+ phone + " Email = "+ email + "]";
    }

    @Override
    public String getCommandDescription() {
        return commandDescription;
    }

    @Override
    public ArrayList<String> getCategories() {
        return categories;
    }

    public String getName() {
        return name;
    }

    public void editCategory(String category) {
        categories.add(category);
    }

    public void clearCategories() {
        if (!categories.isEmpty()) {
            categories.clear();
        }
    }

}
