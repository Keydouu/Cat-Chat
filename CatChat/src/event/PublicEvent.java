package event;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.locks.Lock;

public class PublicEvent {
   
    private static PublicEvent instance;
    private EventImageView eventImageView;
    private EventOpenFile eventOpenFile;
    private EventChat eventChat ;
    private EventPeopleClicked eventPeople;

    public static PublicEvent getInstance() {
        if (instance == null) {
            instance = new PublicEvent();
        }
        return instance;
    }

    private PublicEvent() {

    }

    public void addEventImageView(EventImageView event) {
        this.eventImageView = event;
    }

    public EventImageView getEventImageView() {
        return eventImageView;
    }
    
    public void addEventChat(EventChat event){
        this.eventChat = event;
    }
    
    public EventChat getEventChat() {
        return eventChat;
    }
    
    public void addEventOpenFile(EventOpenFile event) {
        this.eventOpenFile = event;
    }
    public EventOpenFile getEventOpenFile() {
        return eventOpenFile;
    }
    
    public void addEventPeople(EventPeopleClicked event) {
        this.eventPeople = event;
    }
    public EventPeopleClicked getEventPeople() {
        return eventPeople;
    }
   
}
