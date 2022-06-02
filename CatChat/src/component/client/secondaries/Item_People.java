package component.client.secondaries;

import event.PublicEvent;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
public class Item_People extends javax.swing.JPanel {
    private boolean connected = false;
    private String email;
    public Item_People(String receiver) {
        initComponents();
        email=receiver;
        username.setText(receiver);
        init();
    }
    
    public String getEmail(){return this.email;}
    public void setConnection(boolean b){
        connected=b;
        if(b){
            onlineIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/online.png")));
        }
        else{
            onlineIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/offline.png")));
        }
        PublicEvent.getInstance().getEventPeople().updateStatus(connected);
        this.repaint();
        this.revalidate();
        
//chat.Ctitle.getUserName();
    }
    public void addMessageNotif(){
        String s = notifLabel.getText();
        Toolkit.getDefaultToolkit().beep();
        if(s.equals(" "))
            notifLabel.setText("1 nouveau message");
        else
            notifLabel.setText((Integer.parseInt(s.split(" ",2)[0])+1)+" nouveaux messages");
    }
    private void init(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(210,210,210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(new Color(247,247,247));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                notifLabel.setText(" ");
                PublicEvent.getInstance().getEventPeople().viewChat(username.getText(),connected);
            }
            
        });
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Avatar = new swing.ImageAvatar();
        onlineIcon = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        notifLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(247, 247, 247));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        Avatar.setBorderSize(0);
        Avatar.setImage(new javax.swing.ImageIcon(getClass().getResource("/icon/profileU.png"))); // NOI18N

        onlineIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/offline.png"))); // NOI18N
        Avatar.add(onlineIcon);
        onlineIcon.setBounds(37, 37, 20, 20);

        username.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        username.setForeground(new java.awt.Color(60, 60, 60));
        username.setText("UserName");

        notifLabel.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        notifLabel.setForeground(new java.awt.Color(255, 51, 51));
        notifLabel.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Avatar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(notifLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(Avatar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(username)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(notifLabel)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        notifLabel.getAccessibleContext().setAccessibleName("notifLabel");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private swing.ImageAvatar Avatar;
    private javax.swing.JLabel notifLabel;
    private javax.swing.JLabel onlineIcon;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}
