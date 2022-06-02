package event;

import javax.swing.ImageIcon;

public interface EventChat {
    public void sendMessage(String time, String text);
    public void sendAudio(String time, String path);
    public void sendImage(String time, ImageIcon image);
    public void sendFile(String time, String path);
}
