package component.form;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import java.awt.AlphaComposite;
import java.awt.Color;

public class Message extends javax.swing.JPanel {
    private MessageType messageType = MessageType.SUCCESS;
    private boolean  show;
    
    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public Message() {
        initComponents();
        setOpaque(false);
        setVisible(false);
    }
    public void showMessage(MessageType messageType,String message){
        this.messageType = messageType;
        lbMessage.setText(message);
        if(messageType==MessageType.SUCCESS){
            lbMessage.setIcon(new ImageIcon(getClass().getResource("/icon/success.png"))); 
        }else{
            lbMessage.setIcon(new ImageIcon(getClass().getResource("/icon/error.png"))); 
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        lbMessage = new javax.swing.JLabel();

        lbMessage.setFont(new java.awt.Font("SansSerif", 1, 15)); // NOI18N
        lbMessage.setForeground(new java.awt.Color(248, 248, 248));
        lbMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbMessage.setText("Message");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lbMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );
    }// </editor-fold>                        

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        if(messageType==MessageType.SUCCESS){
            g2.setColor(new Color(2,160,100));   
        }else{
            g2.setColor(new Color(200,52,53));
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.9f));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(new Color(245,245,245));
        g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
        super.paintComponent(g);
    }

    
    public static enum MessageType{
        SUCCESS,ERROR
    }
    // Variables declaration - do not modify                     
    private javax.swing.JLabel lbMessage;
    // End of variables declaration                   
}
