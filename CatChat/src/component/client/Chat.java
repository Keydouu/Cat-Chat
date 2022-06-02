package component.client;

import component.client.secondaries.Chat_Bottom;
import component.client.secondaries.Chat_Body;
import component.client.secondaries.Chat_Title;
import event.EventChat;
import event.PublicEvent;
import javax.swing.ImageIcon;
import net.miginfocom.swing.MigLayout;

public class Chat extends javax.swing.JPanel {
    public Chat_Bottom CBottom =new Chat_Bottom();
    public Chat_Body CBody =new Chat_Body();
    public Chat_Title CTitle =new Chat_Title();
    
    public Chat(List list) {
        initComponents();
        PublicEvent.getInstance().addEventChat(new EventChat() {
            @Override
            public void sendMessage(String time, String text){
                CBody.addItemRight(time, text);
                list.pushConversationUp(CTitle.getUserName());
            }
            @Override
            public void sendAudio(String time, String path){
                CBody.addAudioRight(time, path);
                list.pushConversationUp(CTitle.getUserName());
            }
            @Override
            public void sendImage(String time, ImageIcon image){
                CBody.addItemRight(time, "", image);
                list.pushConversationUp(CTitle.getUserName());
            }

            @Override
            public void sendFile(String time, String path) {
                CBody.addFileRight(time, path);
                list.pushConversationUp(CTitle.getUserName());
            }
            
        });
        init();
    }
    private void init(){
        setLayout(new MigLayout("fillx, filly","0[fill,100%]0","[fill,55::10%]0[fill,78%, bottom]0[fill,shrink 0]0"));
        this.add(this.CTitle,"wrap");
        this.add(this.CBody,"wrap");
        this.add(this.CBottom,"h ::30%");
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(950, 600));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 807, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 556, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
