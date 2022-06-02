package component.client;

import event.EventPeopleClicked;
import event.Globals;
import event.PublicEvent;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class Home extends javax.swing.JLayeredPane {
    public List list =new List();
    public Chat chat =new Chat(list);
    public Accueil accueil = new Accueil();
    
    JPanel acceuil = new JPanel();
    private String currentConversation=null;
    public Chat getChat(){
        return this.chat;
    }
    public String getCurrentConversationEmail(){
        return this.currentConversation;
    }
    public Home() {
        initComponents();
        init();
    }
    private void init(){
        setLayout(new MigLayout("fillx, filly","7[fill,30%]7[fill,70%]7","7[fill]7"));
        this.add(this.list);
        this.add(accueil);
        
        initEvent();
    }
    
//    public static ImageIcon setIconDimension(ImageIcon icon){
//        double scale = Math.min((double)500/icon.getIconWidth(), (double)500/icon.getIconHeight());
//        int width = (int)(icon.getIconWidth()*scale);
//        int height = (int)(icon.getIconHeight()*scale);
//        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
//        icon = new ImageIcon(img);
//        return icon;
//    }
    
    public void addMessage(String conversation, String type, String sender, String timeStamp, String content){
        conversation=conversation.toLowerCase();
        switch (type) {
            case "text":
                if (conversation.equals(sender.toLowerCase())) {
                    chat.CBody.addItemLeft(timeStamp, content.replace("@@@", "\n"), sender);
                } else {
                    chat.CBody.addItemRight(timeStamp, content);
                }   break;
            case "image":
                if (conversation.equals(sender.toLowerCase())) {
                    chat.CBody.addItemLeft(timeStamp, "", sender, new ImageIcon(content));
                } else {
                    chat.CBody.addItemRight(timeStamp, "", new ImageIcon(content));
                }   break;
            case "audio":
                if (conversation.equals(sender.toLowerCase())) {
                    chat.CBody.addAudioLeft(timeStamp, sender, content);
                } else {
                    chat.CBody.addAudioRight(timeStamp, content);
                }   break;
            default:
                if (conversation.equals(sender.toLowerCase())) {
                        chat.CBody.addFileLeft(timeStamp, sender, content);
                } else {
                        chat.CBody.addFileRight(timeStamp, content);
                }   break;
        }
    }
    private void initEvent(){
        Home h=this;
        PublicEvent.getInstance().addEventPeople(new EventPeopleClicked() {
            @Override
            public void viewChat(String receiver, boolean online) {
                if(currentConversation==null){
                    h.remove(h.accueil);
                    h.add(h.chat);
                }
                currentConversation=receiver;
                chat.CTitle.setUserName(receiver);
                if(online)
                    chat.CTitle.statusActive();
                else
                    chat.CTitle.statusOffline();
                chat.CBottom.setReceiver(receiver);
                chat.CBody.destroy();
                try{
                    File file = new File("./Conversations/"+Globals.getMyEmail()+" "+receiver+".txt");
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while((line = br.readLine()) != null){
                        String[] data = line.split("@@@",4);
                        addMessage(receiver.toLowerCase() ,data[0], data[1], data[2], data[3]);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    System.out.println("failed to read file");
                }
                chat.CBody.repaint();
                chat.CBody.revalidate();
                chat.CBody.scrollToBottom(); 
            }
            @Override
            public void updateStatus(boolean online){
                if(currentConversation!=null){
                    if(online)
                    chat.CTitle.statusActive();
                else
                    chat.CTitle.statusOffline();
                chat.CTitle.repaint();
                chat.CTitle.revalidate();}
            }
        });
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
