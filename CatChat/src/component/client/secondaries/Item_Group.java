package component.client.secondaries;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Item_Group extends javax.swing.JPanel {

    public Item_Group(String receiver) {
        initComponents();
        username.setText(receiver);
        init();
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
            
        });
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Avatar = new swing.ImageAvatar();
        username = new javax.swing.JLabel();

        setBackground(new java.awt.Color(247, 247, 247));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        Avatar.setBorderSize(0);
        Avatar.setImage(new javax.swing.ImageIcon(getClass().getResource("/icon/profileG.png"))); // NOI18N

        username.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        username.setForeground(new java.awt.Color(60, 60, 60));
        username.setText("UserName");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Avatar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
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
                        .addGap(28, 28, 28)
                        .addComponent(username)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private swing.ImageAvatar Avatar;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}
