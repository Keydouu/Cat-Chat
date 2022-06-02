package component.client.secondaries;

import java.awt.Color;

public class Chat_File extends javax.swing.JPanel {

    public Chat_File() {
        initComponents();
        setOpaque(false);
        setToolTipText("Click to open the file");
    }
    
    public void setFile(String fileName,String size){
        lbFileName.setText(fileName);
        lbFileSize.setText(size);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new swing.PanelRound();
        layer = new javax.swing.JLayeredPane();
        lbFileName = new javax.swing.JLabel();
        lbFileSize = new javax.swing.JLabel();
        pic = new swing.PictureBox();

        setBackground(new java.awt.Color(216, 216, 216));
        setPreferredSize(new java.awt.Dimension(408, 72));

        panel.setBackground(new java.awt.Color(102, 102, 102));
        panel.setForeground(new java.awt.Color(102, 102, 102));

        layer.setRequestFocusEnabled(false);
        layer.setLayout(new java.awt.GridLayout(2, 1));

        lbFileName.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lbFileName.setForeground(new java.awt.Color(33, 33, 33));
        lbFileName.setText("My File.pdf");
        layer.add(lbFileName);

        lbFileSize.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lbFileSize.setForeground(new java.awt.Color(35, 64, 146));
        lbFileSize.setText("5 MB");
        layer.add(lbFileSize);

        pic.setBackground(new java.awt.Color(153, 153, 153));
        pic.setImage(new javax.swing.ImageIcon(getClass().getResource("/icon/file.png"))); // NOI18N

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(268, Short.MAX_VALUE))
            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout.createSequentialGroup()
                    .addGap(86, 86, 86)
                    .addComponent(layer, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(55, 55, 55)))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(layer, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(10, 10, 10)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane layer;
    private javax.swing.JLabel lbFileName;
    private javax.swing.JLabel lbFileSize;
    private swing.PanelRound panel;
    private swing.PictureBox pic;
    // End of variables declaration//GEN-END:variables
}
