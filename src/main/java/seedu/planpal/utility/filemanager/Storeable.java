package seedu.planpal.utility.filemanager;

import java.util.ArrayList;

public interface Storeable {
    public void setCommandDescription(String description);
    public String getCommandDescription();
    public ArrayList<String> getCategories();
}
