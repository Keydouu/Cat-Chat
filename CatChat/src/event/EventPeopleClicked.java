package event;

import javax.swing.Icon;

public interface EventPeopleClicked {

    public void viewChat(String receiver, boolean online);
    public void updateStatus(boolean online);
    
}
