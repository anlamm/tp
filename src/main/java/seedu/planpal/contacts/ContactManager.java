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
public class ContactManager implements ListFunctions {
    public static final String[] INFORMATION_CATEGORIES = {"name", "phone", "email"};
    private static final Logger CONTACT_LOGGER = Logger.getLogger(ContactManager.class.getName());
    FileManager savedContacts = new FileManager();
    private ArrayList<Contact> contactList = new ArrayList<>();
    private ArrayList<ArrayList<Contact>> contactListByCategory = new ArrayList<ArrayList<Contact>>();
    private ArrayList<String> categoryList = new ArrayList<>();

    public ContactManager() {
        CONTACT_LOGGER.setLevel(Level.SEVERE);
    }

    public ArrayList<Contact> getContactList() {
        return contactList;
    }

    protected Contact getContact(int index) {
        return contactList.get(index);
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
            savedContacts.saveList(contactList, true);
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

    /**
     * handle category commands of contacts
     *
     * @param description The description of the command
     * @return true if continue to handle category commands. Otherwise, false.
     */
    public boolean handleCategory(String description) {
        try {
            if (description.startsWith("add ")) {
                addCategory(description);
                //            savedContacts.saveCategories(contactList, contactListByCategory);
                return true;
            } else if (description.startsWith("remove ")) {
                removeCategory(description);
                //            savedContacts.saveCategories(contactList, contactListByCategory);
                return true;
            } else if (description.startsWith("edit ")) {
                editCategory(description);
                //            savedContacts.saveCategories(contactList, contactListByCategory);
                return true;
            } else if (description.equals("view")) {
                Ui.printCategoryList(categoryList);
                return true;
            } else if (description.equals("quit")) {
                return false;
            } else {
                CONTACT_LOGGER.warning("Failed to set category: invalid command");
                throw new PlanPalExceptions("Invalid command");
            }
        } catch (PlanPalExceptions e) {
            CONTACT_LOGGER.warning("Failed to set category: " + e.getMessage());
            Ui.print(e.getMessage());
            return true;
        }
    }

    /**
     * Helper function to handle adding of category
     *
     * @param description The description of the command
     * @throws EmptyDescriptionException If the description is empty, an {@link EmptyDescriptionException} is thrown.
     */
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

    /**
     * Helper function to handle removal of category
     *
     * @param description The description of the command
     * @throws PlanPalExceptions If the input is invalid
     */
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

    /**
     * Helper function to handle editing of categories of a contact
     *
     * @param description The description of the command
     * @throws PlanPalExceptions If the input is invalid
     */
    private void editCategory(String description) throws PlanPalExceptions {
        try {
            String descriptionToEdit = description.replace("edit ", "").trim();
            String[] categories;
            int contactId;
            if (descriptionToEdit.split(" ").length == 1) {
                contactId = Integer.parseInt(descriptionToEdit) - 1;
                categories = null;
            } else {
                String contactIdString = descriptionToEdit.substring(0, descriptionToEdit.indexOf(" ")).trim();
                categories = descriptionToEdit.substring(descriptionToEdit.indexOf(" ")).trim().split("/");
                contactId = Integer.parseInt(contactIdString) - 1;
            }
            Contact editingContact = validateEdit(categories, contactId);
            updateCategory(editingContact, categories);
            Ui.print("successfully assigned categories to Contact id : " + contactId);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            CONTACT_LOGGER.warning("Invalid input.");
            throw new PlanPalExceptions("Invalid input.");
        }
    }

    /**
     * Helper function to validate editing categories of a contact
     *
     * @param categories array of new categories of the contact
     * @param contactId index of Contact in contactList
     * @return contact with index equals contactId
     * @throws PlanPalExceptions If the command is invalid
     */
    private Contact validateEdit(String[] categories, int contactId) throws PlanPalExceptions {
        if (contactId >= contactList.size() || contactId < 0) {
            throw new PlanPalExceptions("Invalid contact id");
        }
        if (categories == null) {
            return contactList.get(contactId);
        }
        for (String category : categories) {
            if (!categoryList.contains(category)) {
                CONTACT_LOGGER.warning("Category is not found.");
                throw new PlanPalExceptions(category + " is not a valid category");
            }
        }
        return contactList.get(contactId);
    }

    /**
     * Helper function to update categories of a contact
     *
     * @param editingContact The contact whose categories are to be edited
     * @param categories array of new categories of the contact
     */
    private void updateCategory(Contact editingContact, String[] categories) {
        editingContact.clearCategories();
        if (categories == null) {
            return;
        }
        for (String category : categories) {
            if (!categoryList.contains(category)) {
                continue;
            } else {
                contactListByCategory.get(categoryList.indexOf(category)).add(editingContact);
                editingContact.editCategory(category);
            }
        }
    }

    /**
     * Search contacts of a category
     *
     * @param description The category to be found
     */
    public void searchCategory(String description) {
        for (String category : categoryList) {
            if (category.equals(description)) {
                Ui.printCat(category, contactListByCategory, categoryList);
                return;
            }
        }
        Ui.printCategoryNotFound();
    }

}
