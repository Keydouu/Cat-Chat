package component.client.secondaries;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import event.Globals;
import static event.Globals.dos;
import static event.Globals.myWriteLock;
import event.PublicEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static main.MainClient.updateConversationFile;
import net.miginfocom.swing.MigLayout;
import swing.JIMSendTextPane;
import swing.ScrollBar;

public class Chat_Bottom extends javax.swing.JPanel {
    private static boolean recording = false;
    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    private String receiver;
    //public Call_Main cm;
    public Chat_Bottom() {
        initComponents();
        init();
    }
    public void setReceiver(String receiver){
        this.receiver = receiver;
    }
    public String sendFile(String target, String messageType,int fileSize, boolean isGroup, String fileName){
        LocalDateTime now = LocalDateTime.now();
        String s = dtf.format(now);
        try {
            myWriteLock.lock();
            if (isGroup) {
                Globals.writeUTFEncoded("messageGroup@@@" + target);
                Globals.writeUTFEncoded(messageType + "@@@Group@@@" + target + "@@@" + Globals.getMyEmail() + "@@@" + s + "@@@" + fileName);
            } else {
                Globals.writeUTFEncoded("messageUser@@@" + target);
                Globals.writeUTFEncoded(messageType + "@@@" + Globals.getMyEmail() + "@@@" + s + "@@@" + fileName);
            }
        } catch (IOException e) {
            System.out.println("failed to send msg");
            e.printStackTrace();
        }
        return s;
    }
    public String sendMessage(String target, String messageType, String messageContent, boolean isGroup) {//return Time Sent
        LocalDateTime now = LocalDateTime.now();
        String s = dtf.format(now);
        try {
            myWriteLock.lock();
            if (isGroup) {
                Globals.writeUTFEncoded("messageGroup@@@" + target);
                Globals.writeUTFEncoded(messageType + "@@@Group@@@" + target + "@@@" + Globals.getMyEmail() + "@@@" + s + "@@@" + messageContent);
            } else {
                Globals.writeUTFEncoded("messageUser@@@" + target);
                Globals.writeUTFEncoded(messageType + "@@@" + Globals.getMyEmail() + "@@@" + s + "@@@" + messageContent);
            }
        } catch (IOException e) {
            System.out.println("failed to send msg");
            e.printStackTrace();
        }
        return s;
    }
    private void init(){
            setLayout(new MigLayout("fillx, filly","3%[fill,3::1%]0[fill,70%]2%[fill,30%]1%","18[fill,center]19"));   
            this.setBackground(new Color(220,220,220));
            JScrollPane scroll=new JScrollPane();
            JIMSendTextPane txt = new JIMSendTextPane();
            txt.setHintText("Write your Message Here ...");
            scroll.setBorder(null);
            txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                refresh();
            }
            });
            
            scroll.setViewportView(txt);
            ScrollBar sb = new ScrollBar();
            sb.setPreferredSize(new Dimension(3, 10));
            scroll.setVerticalScrollBar(sb);
            add(sb);
            add(scroll,"w 100%");
            JPanel panel = new JPanel();
            panel.setLayout(new MigLayout("fillx, filly", "0%[fill]2%[fill]2%[fill]2%[fill]2%[fill]0%", "0[center]0"));
            panel.setPreferredSize(new Dimension(30, 28));
            panel.setOpaque(false);
            
            
            JButton Bsend = new JButton();
            Bsend.setBorder(null);
            Bsend.setToolTipText("Send Message");
            Bsend.setContentAreaFilled(false);
            Bsend.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Bsend.setIcon(new ImageIcon(getClass().getResource("/icon/send.png")));
            panel.add(Bsend);
            
            
            
            Bsend.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!txt.getText().trim().equals("")){
                        String str[]=receiver.split(" ",2);
                        String target;
                        if(str[0].equals("Group"))
                            target = str[1];
                        else
                            target=receiver;
                        String s=sendMessage(target, "text", txt.getText().trim().replace("@@@",""), str[0].equals("Group"));
                        myWriteLock.unlock();
                        updateConversationFile(Globals.getMyEmail(), receiver, s, "text", txt.getText().trim().replace("@@@",""));
                        PublicEvent.getInstance().getEventChat().sendMessage(s ,txt.getText().trim());
                        txt.setText("");
                        txt.grabFocus();
                        refresh();
                    }else{
                        txt.grabFocus();
                    }
                }
            });
            
            Bsend.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Bsend.setContentAreaFilled(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Bsend.setContentAreaFilled(false);
                }
            });
            
            JButton Brec = new JButton();
            Brec.setBorder(null);
            Brec.setToolTipText("Record Audio");
            Brec.setContentAreaFilled(false);
            Brec.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Brec.setIcon(new ImageIcon(getClass().getResource("/icon/record.png")));
            panel.add(Brec);
            Brec.addActionListener(new ActionListener() {
                AudioFormat audioFormat;
                DataLine.Info dataInfo;
                TargetDataLine targetLine;
                Thread audiorecorder;
                String fileName;
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!recording){
                        recording=true;
                        try {
                            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100,16,2,4,44100,false);
                            dataInfo= new DataLine.Info(TargetDataLine.class,audioFormat);
                            if(!AudioSystem.isLineSupported(dataInfo)) {
                                System.out.println("Not supported format");
                            }
                            targetLine=(TargetDataLine)AudioSystem.getLine(dataInfo);
                            targetLine.open(audioFormat);
                            targetLine.start();
                            audiorecorder=new Thread() {
                                @Override public void run() {
                                    AudioInputStream record=new AudioInputStream(targetLine);
                                    LocalDateTime now = LocalDateTime.now();
                                    fileName="Audios/"+"S "+dtf.format(now)+".wav";
                                    File outputFile = new File(fileName);
                                    try {
                                        AudioSystem.write(record, AudioFileFormat.Type.WAVE, outputFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            System.out.println("Recording...");
                            audiorecorder.start();
                        }
                        catch(Exception a) {
                            a.printStackTrace();
                        }
                    }
                    else{
                        System.out.println("+stop recording...");
                        try{
                            recording=false;
                            targetLine.stop();
                            targetLine.close();
                            File f = new File(fileName);
                            byte[] data = Files.readAllBytes(f.toPath());
                            String str[]=receiver.split(" ",2);
                            String target;
                            if(str[0].equals("Group"))
                                target = str[1];
                            else
                                target=receiver;
                            LocalDateTime now = LocalDateTime.now();
                            String timeSent=sendFile(target, "audio", data.length, str[0].equals("Group"), "R "+dtf.format(now)+".wav");
                            Globals.writeEncoded(data);
                            myWriteLock.unlock();
                            updateConversationFile(Globals.getMyEmail(), receiver, timeSent, "audio", fileName);
                            PublicEvent.getInstance().getEventChat().sendAudio(timeSent, fileName);
                        }
                        catch(Exception a) {
                            a.printStackTrace();
                        }
                    }
                }
            });
            
            Brec.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Brec.setContentAreaFilled(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Brec.setContentAreaFilled(false);
                }      
            });
            
            
            JButton Bfile = new JButton();
            Bfile.setBorder(null);
            Bfile.setToolTipText("Send a File");
            Bfile.setContentAreaFilled(false);
            Bfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Bfile.setIcon(new ImageIcon(getClass().getResource("/icon/addFile.png")));
            panel.add(Bfile);
            Bfile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String str[] = receiver.split(" ", 2);
                    String target;
                    if (str[0].toLowerCase().equals("Group")) {
                        target = str[1];
                    } else {
                        target = receiver;
                    }
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setDialogTitle("Importer");
                    int result = jFileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = jFileChooser.getSelectedFile();
                        try {
                            byte[] data = Files.readAllBytes(file.toPath());
                            String timeSent=sendFile(target, "file", data.length, str[0].equals("Group"), file.getName());
                            Globals.writeEncoded(data);
                            myWriteLock.unlock();
                            String fileName = "Files/"+timeSent+" "+file.getName();
                            File outputfile = new File(fileName);
                            outputfile.createNewFile();
                            FileOutputStream fout = new FileOutputStream(fileName);
                            fout.write(data);
                            PublicEvent.getInstance().getEventChat().sendFile(timeSent, fileName);
                            updateConversationFile(Globals.getMyEmail(), target, timeSent, "file", fileName);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            Bfile.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Bfile.setContentAreaFilled(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Bfile.setContentAreaFilled(false);
                }      
            });
            
            
            JButton Bimage = new JButton();
            Bimage.setBorder(null);
            Bimage.setToolTipText("Send Image");
            Bimage.setContentAreaFilled(false);
            Bimage.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Bimage.setIcon(new ImageIcon(getClass().getResource("/icon/addImage.png")));
            panel.add(Bimage);
            
            Bimage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String str[] = receiver.split(" ", 2);
                    String target;
                    if (str[0].toLowerCase().equals("Group")) {
                        target = str[1];
                    } else {
                        target = receiver;
                    }
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setDialogTitle("Importer");
                    int result = jFileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = jFileChooser.getSelectedFile();
                        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                        if (extension.toLowerCase().equals("png") || extension.toLowerCase().equals("jpg")) {
                            try {
                                BufferedImage bfimg = ImageIO.read(new File(file.getAbsolutePath()));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ImageIO.write(bfimg, "png", baos);
                                byte bytes[] = baos.toByteArray();
                                String timeSent = sendFile(target, "image", bytes.length, str[0].equals("Group"), file.getName());
                                Globals.writeEncoded(bytes);
                                myWriteLock.unlock();
                                String imageFileName = "Images/S " + timeSent + ".png";
                                File outputfile = new File(imageFileName);
                                outputfile.createNewFile();
                                ImageIO.write(bfimg, "png", outputfile);
                                PublicEvent.getInstance().getEventChat().sendImage(timeSent,  new ImageIcon(imageFileName));
                                updateConversationFile(Globals.getMyEmail(), target, timeSent, "image", imageFileName);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            });
                    
            Bimage.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Bimage.setContentAreaFilled(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Bimage.setContentAreaFilled(false);
                }      
            });
            
            JButton Bcamera = new JButton();
            Bcamera.setBorder(null);
            Bcamera.setToolTipText("Call");
            Bcamera.setContentAreaFilled(false);
            Bcamera.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Bcamera.setIcon(new ImageIcon(getClass().getResource("/icon/camera.png")));
            panel.add(Bcamera);
            Bcamera.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    /*if(!Call_Main.canBeCalled())
                        return;
                    cm=new Call_Main(receiver, true);
                    try {
                        Globals.dos.writeUTF("call@@@"+receiver);
                    } catch (IOException ex) {
                        Logger.getLogger(Chat_Bottom.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                }
            });
            Bcamera.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Bcamera.setContentAreaFilled(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Bcamera.setContentAreaFilled(false);
                }      
            });
            
            JButton Bscreen = new JButton();
            Bscreen.setBorder(null);
            Bscreen.setToolTipText("Take a photo");
            Bscreen.setContentAreaFilled(false);
            Bscreen.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Bscreen.setIcon(new ImageIcon(getClass().getResource("/icon/screen.png")));
            panel.add(Bscreen);
            
            Bscreen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String str[] = receiver.split(" ", 2);
                    String target;
                    if (str[0].toLowerCase().equals("Group")) {
                        target = str[1];
                    } else {
                        target = receiver;
                    }
                    Webcam webcam = Webcam.getDefault();
                    if(webcam==null){
                        JOptionPane.showMessageDialog(null,"Cannot detect webcam");
                        return;
                    }
                    webcam.setViewSize(WebcamResolution.VGA.getSize());
                    webcam.open();
                    try {
                        BufferedImage bfimg = webcam.getImage();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bfimg, "png", baos);
                        byte bytes[] = baos.toByteArray();
                        LocalDateTime now = LocalDateTime.now();
                        String timeSent=sendFile(target, "image", bytes.length, str[0].equals("Group"), "R capture "+dtf.format(now)+"png");
                        Globals.writeEncoded(bytes);
                        myWriteLock.unlock();
                        String imageFileName = "Images/S capture" + timeSent + ".png";
                        File outputfile = new File(imageFileName);
                        outputfile.createNewFile();
                        ImageIO.write(bfimg, "png", outputfile);
                        PublicEvent.getInstance().getEventChat().sendImage(timeSent, new ImageIcon(imageFileName));
                        updateConversationFile(Globals.getMyEmail(), target, timeSent, "image", imageFileName);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    webcam.close();
                }
            });
            
            Bscreen.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Bscreen.setContentAreaFilled(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Bscreen.setContentAreaFilled(false);
                }      
            });
            
            add(panel);
            
    }
    
    private void refresh(){
        revalidate();
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
            .addGap(0, 40, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
