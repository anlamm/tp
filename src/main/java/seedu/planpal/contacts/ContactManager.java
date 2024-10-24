package seedu.planpal.contacts;

import seedu.planpal.exceptions.EmptyDescriptionException;
import seedu.planpal.exceptions.PlanPalExceptions;
import seedu.planpal.utility.ListFunctions;
import seedu.planpal.utility.Ui;
import seedu.planpal.utility.filemanager.FileManager;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the contact list within the PlanPal application.
 */
public class ContactManager implements ListFunctions<Contact> {
    public static final String[] INFORMATIONFIELDS = {"name", "phone", "email", "categories"};
    private static final Logger CONTACT_LOGGER = Logger.getLogger(ContactManager.class.getName());
    FileManager savedContacts;
    private ArrayList<Contact> contactList = new ArrayList<>();
    private ArrayList<ArrayList<Contact>> contactListByCategory = new ArrayList<ArrayList<Contact>>();
    private ArrayList<String> categoryList = new ArrayList<>();

    public ContactManager(FileManager fileManager) {
        categoryList.add("emergency");
        categoryList.add("friend");
        categoryList.add("family");
        contactListByCategory.add(new ArrayList<>());
        contactListByCategory.add(new ArrayList<>());
        contactListByCategory.add(new ArrayList<>());
        savedContacts = fileManager;
        CONTACT_LOGGER.setLevel(Level.SEVERE);
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    /**
     * Adds a new contact to the contact list.
     * The contact is created from the provided description.
     *
     * @param description The description of the new contact. This must not be empty.
     * @throws PlanPalExceptions If the description is empty, an {@link EmptyDescriptionException} is thrown.
     */
    public void addContact(String description) throws PlanPalExceptions {
        if (description.isEmpty()){
            throw new EmptyDescriptionException();
        }
        addToList(contactList, new Contact(description));
        CONTACT_LOGGER.info("Added contact");
        savedContacts.saveList(contactList);
    }

    /**
     * Deletes an existing contact from the contact list.
     * The contact is retrieved from its description.
     *
     * @param index The description of the contact to be deleted. This must not be empty.
     * @throws PlanPalExceptions If the description is empty, an {@link EmptyDescriptionException} os thrown.
     */
    public void deleteContact(String index) throws PlanPalExceptions {
        if (index.isEmpty()) {
            throw new EmptyDescriptionException();
        }
        assert index.length() != 0 : "Input must not be empty";

        CONTACT_LOGGER.info("Deleting contact with the index: " + index);
        try {
            deleteList(contactList, index);
            savedContacts.saveList(contactList);
            CONTACT_LOGGER.info("Deleted contact");
        } catch (PlanPalExceptions e) {
            CONTACT_LOGGER.severe("Failed to delete a contact: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Displays the entire list of contacts.
     * Each contact is printed with an index number for easy reference.
     */
    public void viewContactList(){
        viewList(contactList);
    }

    /**
     * Edits a contact in the contact list based on the provided query.
     * The query should contain the index of the contact to be edited, followed by the fields to update.
     *
     * @param query The query containing the index and new values for the contact.
     * @throws PlanPalExceptions If the index is out of bounds or other editing errors occur.
     */
    public void editContact(String query) throws PlanPalExceptions {
        CONTACT_LOGGER.info("Editing contact with query: " + query);

        try {
            editList(contactList, query);
            savedContacts.saveList(contactList);
            CONTACT_LOGGER.info("Edited contact successfully");
        } catch (PlanPalExceptions e) {
            CONTACT_LOGGER.severe("Failed to edit contact: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Finds contacts in the contact list based on the provided description.
     * The description can contain multiple names separated by spaces.
     * If matching contacts are found, they are displayed to the user.
     *
     * @param description The description of the contacts to find. This must not be empty.
     * @throws PlanPalExceptions If the description is empty, an {@link EmptyDescriptionException} is thrown.
     */
    public void findContact(String description) throws PlanPalExceptions {
        CONTACT_LOGGER.info("Searching for contacts with description: " + description);

        if (description.isEmpty()) {
            CONTACT_LOGGER.warning("Search description is empty. Throwing EmptyDescriptionException.");
            throw new EmptyDescriptionException();
        }

        assert description != null : "Description must not be null";
        assert !description.isEmpty() : "Description must not be empty";

        try {
            findInList(contactList, description);
            CONTACT_LOGGER.info("Search completed successfully.");
        } catch (PlanPalExceptions e) {
            CONTACT_LOGGER.warning("Failed to find contacts: " + e.getMessage());
            throw e;
        }
    }

    public void setCategory(String description) throws PlanPalExceptions {
        if (description.startsWith("add ")) {
            addCategory(description);
//            savedContacts.saveCategories(contactList, contactListByCategory, categoryList);
        } else if (description.startsWith("remove ")) {
            removeCategory(description);
//            savedContacts.saveCategories(contactList, contactListByCategory, categoryList);
        } else if (description.startsWith("edit ")) {
            editCategory(description);
//            savedContacts.saveCategories(contactList, contactListByCategory, categoryList);
        }else {
            CONTACT_LOGGER.warning("Failed to set category: invalid command");
            throw new PlanPalExceptions("Invalid command");
        }
    }

    private void addCategory(String description) throws EmptyDescriptionException {
        String newCategory = description.replace("add ", "").trim();
        Ui.print(description);
        if (newCategory.isEmpty()) {
            CONTACT_LOGGER.warning("Category is empty. Throwing EmptyDescriptionException.");
            throw new EmptyDescriptionException();
        } else {
            contactListByCategory.add(new ArrayList<Contact>());
            categoryList.add(newCategory);
            Ui.print("successfully added Category : '" + newCategory + "'");
        }
    }

    private void removeCategory(String description) throws PlanPalExceptions {
        String removingCategory = description.replace("remove ", "").trim();
        if (removingCategory.isEmpty()) {
            CONTACT_LOGGER.warning("Category is empty. Throwing EmptyDescriptionException.");
            throw new EmptyDescriptionException();
        } else if (!categoryList.contains(removingCategory)) {
            CONTACT_LOGGER.warning("Category is not found.");
            throw new PlanPalExceptions(removingCategory + " is not a category");
        } else {
            contactListByCategory.remove(categoryList.indexOf(removingCategory));
            categoryList.remove(removingCategory);
            Ui.print("successfully deleted Category : '" + removingCategory + "'");
        }
    }

    private void editCategory(String description) throws PlanPalExceptions {
        try {
            String descriptionToEdit = description.replace("edit ", "").trim();
            int contactId = Integer.parseInt(descriptionToEdit.substring(0, descriptionToEdit.indexOf(" ")).trim());
            String[] categories = descriptionToEdit.substring(descriptionToEdit.indexOf(" ")).trim().split("/");
            Ui.print(categories);
            Contact editingContact = validateEdit(categories, contactId);
            updateCategory(editingContact, categories);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            CONTACT_LOGGER.warning("Invalid input. Throwing EmptyDescriptionException.");
            throw new EmptyDescriptionException();
        }
    }

    private Contact validateEdit(String[] categories, int contactId) throws PlanPalExceptions {
        if (categories.length == 0) {
            CONTACT_LOGGER.warning("Invalid input. Throwing EmptyDescriptionException.");
            throw new EmptyDescriptionException();
        }
        for (String category : categories) {
            if (!categoryList.contains(category)) {
                CONTACT_LOGGER.warning("Category is not found.");
                throw new PlanPalExceptions(category + " is not a valid category");
            }
        }
        return contactList.get(contactId);
    }

    private void updateCategory(Contact editingContact, String[] categories) {
        editingContact.clearCategories();
        for (String category : categories) {
            if (!categoryList.contains(category)) {
                continue;
            } else {
                contactListByCategory.get(categoryList.indexOf(category)).add(editingContact);
                editingContact.editCategory(category);
            }
        }
    }

}
