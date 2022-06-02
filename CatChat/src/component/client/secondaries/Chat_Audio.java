    package component.client.secondaries;

import java.awt.Cursor;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Chat_Audio extends javax.swing.JPanel {
    private int t;
    private boolean b;
    private Clip clip;
    public Chat_Audio(String fileName) {
        initComponents();
        addAudioFile(fileName);
        setOpaque(false);
        init();
    }
    private void addAudioFile(String fileName){
        File file=new File(fileName);
        AudioInputStream audioStream = null;
        try {
            audioStream = AudioSystem.getAudioInputStream(file);
            this.clip=AudioSystem.getClip();
            this.clip.open(audioStream);
            this.clip.stop();
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
            gainControl.setValue(20f * (float) Math.log10(2.0));
            this.t=0;
            this.b=false;
            long sr= (long)clip.getMicrosecondLength()/1000;
            setMaxslider((int)(sr-100));
	} catch (Exception e) {
            System.out.println("Couldn't find file "+fileName);
           // e.printStackTrace();
	}
    }
    public void init(){
        play.setBorder(null);
        play.setToolTipText("Play");
        play.setContentAreaFilled(false);
        play.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public void setMaxslider(int i){
        slider.setMaximum(i);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRoundAudio1 = new swing.PanelRoundAudio();
        slider = new slider.JsliderCustom();
        play = new javax.swing.JButton();

        setBackground(new java.awt.Color(216, 216, 216));
        setPreferredSize(new java.awt.Dimension(408, 72));

        slider.setBackground(new java.awt.Color(255, 255, 255));
        slider.setForeground(new java.awt.Color(50, 96, 215));
        slider.setValue(0);

        play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/play.png"))); // NOI18N
        play.setBorder(null);
        play.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRoundAudio1Layout = new javax.swing.GroupLayout(panelRoundAudio1);
        panelRoundAudio1.setLayout(panelRoundAudio1Layout);
        panelRoundAudio1Layout.setHorizontalGroup(
            panelRoundAudio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRoundAudio1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(play)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        panelRoundAudio1Layout.setVerticalGroup(
            panelRoundAudio1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRoundAudio1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
            .addGroup(panelRoundAudio1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(play, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(panelRoundAudio1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRoundAudio1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void playActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playActionPerformed
        if(!this.clip.isRunning()) {
            play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/pause.png")));
            play.setToolTipText("Pause");
            clip.setMicrosecondPosition(slider.getValue()*1000);
            Clip c=this.clip;
            this.b=true;
            Thread sli=new Thread() {
                int sr= (int)c.getMicrosecondLength()/1000;
                @Override public void run() {
                    while(c.isRunning()&&b){
                        slider.setValue(slider.getValue() + 100);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        slider.repaint();
                        slider.revalidate();
                    }
                    if (b) {
                        while(slider.getValue()!=slider.getMaximum()){
                            slider.setValue(java.lang.Math.min(slider.getValue() + 100,slider.getMaximum()));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        slider.setValue(0);
                        play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/play.png")));
                        play.setToolTipText("Play");
                        b=false;
                    }
                }
            };
            this.clip.start();
            sli.start();
        }
        else {
            clip.stop();
            play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/play.png")));
            play.setToolTipText("Play");
            this.b=false;
        }
    }//GEN-LAST:event_playActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private swing.PanelRoundAudio panelRoundAudio1;
    private javax.swing.JButton play;
    private slider.JsliderCustom slider;
    // End of variables declaration//GEN-END:variables
}
