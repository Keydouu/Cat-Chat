package component.client.secondaries;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Item_Add extends javax.swing.JPanel {

    
    public Item_Add(String name) {
        initComponents();
        lbAdd.setText(name);
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

        lbAdd = new javax.swing.JLabel();
        pictureBox1 = new swing.PictureBox();

        setBackground(new java.awt.Color(247, 247, 247));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lbAdd.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbAdd.setForeground(new java.awt.Color(60, 60, 60));
        lbAdd.setText("jLabel1");

        pictureBox1.setImage(new javax.swing.ImageIcon(getClass().getResource("/icon/addItem.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(lbAdd))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(pictureBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbAdd;
    private swing.PictureBox pictureBox1;
    // End of variables declaration//GEN-END:variables
}
