/*package component.client.secondaries;

import com.github.sarxos.webcam.Webcam;
import event.Globals;
import static event.Globals.myWriteLock;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Call_Main extends javax.swing.JFrame {
    private static boolean notInCall=true;
    private Webcam webcam;
    private boolean screenSharing;
    private static BufferedImage image;
    private Thread videoThread;
    public Call_Main(String fren, boolean isCaller) {
        notInCall = false;
        webcam = Webcam.getDefault();
        //webcam.setViewSize(WebcamResolution.VGA.getSize());
        screenSharing = false;
        try {
            image = ImageIO.read(getClass().getResource("/icon/callPic.png"));
        } catch (IOException ex) {
            System.out.println("failed to get default pic");
        }
        initComponents();
        partnerNameLabel.setText(fren);
        myNameLabel.setText(Globals.getMyEmail());
        videoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread started");
                while (!notInCall) {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(getPic(), "png", baos);
                        byte bytes[] = baos.toByteArray();
                        
                        myWriteLock.lock();
                        Globals.dos.writeUTF("messageUser@@@" + fren);
                        Globals.dos.writeUTF("videoCall@@@" + Globals.getMyEmail() + "@@@" + Integer.toString(bytes.length));
                        Globals.dos.write(bytes);
                        //Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try{
                        myWriteLock.unlock();
                        Thread.sleep(50);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if(isCaller){
            repaint();
            revalidate();
            this.setVisible(true);
        }
        else
            callStarted();
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                callEnded();
            }
        });
    }
    public static ImageIcon setIconDimension(ImageIcon icon){
        if(icon.getIconWidth()!=250){
            double scale = Math.min((double)250/icon.getIconWidth(), (double)250/icon.getIconHeight());
            int width = (int)(icon.getIconWidth()*scale);
            int height = (int)(icon.getIconHeight()*scale);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        }
        return icon;
    }
    public void callStarted(){this.videoThread.start();this.setVisible(true);};
    public static boolean canBeCalled(){return notInCall;}
    public void callEnded(){
        if(webcam.isOpen())
            webcam.close();
        screenSharing=false;
        notInCall=true;
        this.setVisible(false);
    }
    public void setFrendPic(BufferedImage frenImg){
        partnerImageLabel.setIcon(new ImageIcon(frenImg));
        this.repaint();
        this.revalidate();
    }
    private BufferedImage getPic(){
        if(screenSharing){
            try{
                Robot r = new Robot();
                Rectangle capture = new Rectangle(new Dimension(250,250));
                image = r.createScreenCapture(capture);
                return image;
            } catch (AWTException ex) {
                Logger.getLogger(Call_Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(webcam.isOpen()){
            image = webcam.getImage();
        }
        myImageLabel.setIcon(setIconDimension(new ImageIcon(image)));
        this.repaint();
        this.revalidate();
        return image;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        partnerImageLabel = new javax.swing.JLabel();
        myImageLabel = new javax.swing.JLabel();
        myNameLabel = new javax.swing.JLabel();
        partnerNameLabel = new javax.swing.JLabel();
        bMic = new javax.swing.JButton();
        bCamera = new javax.swing.JButton();
        endCall = new javax.swing.JButton();
        bScreen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        partnerImageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/callPic.png"))); // NOI18N
        partnerImageLabel.setMaximumSize(new java.awt.Dimension(250, 250));
        partnerImageLabel.setMinimumSize(new java.awt.Dimension(250, 250));

        myImageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/callPic.png"))); // NOI18N

        myNameLabel.setText(" ");

        partnerNameLabel.setText(" ");

        bMic.setLabel("Mic");
        bMic.setName(""); // NOI18N

        bCamera.setLabel("Camera");
        bCamera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCameraActionPerformed(evt);
            }
        });

        endCall.setText("EndCall");
        endCall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endCallActionPerformed(evt);
            }
        });

        bScreen.setText("Share Screen");
        bScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bScreenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(107, 107, 107)
                        .addComponent(myNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(partnerNameLabel)
                        .addGap(81, 81, 81))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(myImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(bMic)
                                .addGap(80, 80, 80)
                                .addComponent(bCamera)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(bScreen)
                                .addGap(55, 55, 55)
                                .addComponent(endCall))
                            .addComponent(partnerImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(myImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(partnerImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(myNameLabel)
                    .addComponent(partnerNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bMic)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(endCall)
                        .addComponent(bScreen)
                        .addComponent(bCamera)))
                .addGap(0, 7, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bCameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCameraActionPerformed
        if(webcam.isOpen())
            webcam.close();
        else
            webcam.open();
    }//GEN-LAST:event_bCameraActionPerformed

    private void bScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bScreenActionPerformed
        screenSharing=!screenSharing;
        if(webcam.isOpen())
            webcam.close();
    }//GEN-LAST:event_bScreenActionPerformed

    private void endCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endCallActionPerformed
        callEnded();
    }//GEN-LAST:event_endCallActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCamera;
    private javax.swing.JButton bMic;
    private javax.swing.JButton bScreen;
    private javax.swing.JButton endCall;
    private javax.swing.JLabel myImageLabel;
    private javax.swing.JLabel myNameLabel;
    private javax.swing.JLabel partnerImageLabel;
    private javax.swing.JLabel partnerNameLabel;
    // End of variables declaration//GEN-END:variables
}
*/