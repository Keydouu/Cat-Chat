import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.sound.sampled.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalDateTime; 
import com.github.sarxos.webcam.Webcam;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.*;
import java.util.*;
import java.nio.file.Files;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.image.*;

public class Client2 extends JFrame{
    private static DataInputStream dis=null;
	private static DataOutputStream dos=null;
    private static Socket commsocket;
    private static JPanel chatPanel = new JPanel();
    private static boolean recording = false;
    private static String myEmail;
    private static JFrame appWindow;
    private static CardLayout cLayout;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    private static Map<String, String> mapEmailToCardName;
    private static Lock myReadLock, myWriteLock;
    public static void main(String[] args) {
		InetAddress ip;
        Thread ths=new Thread();
        JTextField receiverField = new JTextField();
        try{
			ip = InetAddress.getByName("localhost");
			commsocket = new Socket(ip, 5057);
			dis = new DataInputStream(commsocket.getInputStream());
			dos = new DataOutputStream(commsocket.getOutputStream());
            myReadLock = new ReentrantLock();
            myWriteLock = new ReentrantLock();
        }catch(Exception e){
            e.printStackTrace();
        }
        CallWindow.initialiseCall();
        JScrollPane scroll = new JScrollPane(chatPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        appWindow = new JFrame();
        JPanel chatWindow=new JPanel();
        JPanel loginPanel=new JPanel();
        setConnexionPanel(loginPanel);
        appWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cLayout=new CardLayout();
        appWindow.getContentPane().setLayout(cLayout);
        appWindow.getContentPane().add(loginPanel,"login");
        appWindow.getContentPane().add(chatWindow,"chat");
        mapEmailToCardName = new HashMap<>(); 
        ths = new Thread(() -> {
            boolean itIsFine=true;
            while(itIsFine){
                try{   
                    String msg = dis.readUTF();
                    myReadLock.lock();
                    System.out.println(msg);
                    String strArray[]=msg.split("@@@");
                    if(strArray[0].equals("image")){
                        String timeSent;
                        String sender;
                        String receiver;
                        if(strArray[1].equals("Group")){
                            sender=strArray[3];
                            chatPanel.add(new JLabel(strArray[3]+" sent an image at Groupe "+strArray[2]+" :"));
                            timeSent=strArray[4].replace(' ', '-').replace(':','-');;
                            receiver="Group "+strArray[2];
                        }
                        else{
                            sender=strArray[1];
                            chatPanel.add(new JLabel(strArray[1]+" sent an image : "));
                            timeSent=strArray[2].replace(' ', '-').replace(':','-');; 
                            receiver=myEmail;
                        }
                        int len = dis.readInt();
                        byte[] image = new byte[len];
                        dis.readFully(image);
                        BufferedImage bfimg = ImageIO.read(new ByteArrayInputStream(image));
                        chatPanel.add(new JLabel(setIconDimension(new ImageIcon(bfimg))));
                        String imageFileName="Images/R "+timeSent+".png";
                        File outputfile = new File(imageFileName);
                        outputfile.createNewFile();
                        ImageIO.write(bfimg, "png", outputfile);
                        String a[]=receiverField.getText().split(" ");
                        if(a[0].equals("User"))
                            updateConversationFile(sender, receiver, timeSent, "image", imageFileName);
                        else
                            updateConversationFile(sender, receiver, timeSent, "image", imageFileName);
                    }
                    else if(strArray[0].equals("audio")){
                        String sender;
                        String timeSent;
                        String target;
                        if(strArray[1].equals("Group")){
                            chatPanel.add(new JLabel(strArray[3]+" sent Audio at Groupe "+strArray[2]+" :"));
                            sender=strArray[3];
                            timeSent=strArray[4].replace(' ', '-').replace(':','-');
                            target="Group "+strArray[2];
                        }
                        else{
                            chatPanel.add(new JLabel(strArray[1]+" sent Audio : "));
                            sender=strArray[1];
                            timeSent=strArray[2].replace(' ', '-').replace(':','-');
                            target=myEmail;
                        }
                        int len = dis.readInt();
                        byte[] audio = new byte[len];
                        dis.readFully(audio);
                        FileOutputStream fout = new FileOutputStream("Audios/"+"R "+timeSent+".wav");
                        updateConversationFile(sender, target , timeSent, "audio", "Audios/"+"R "+timeSent+".wav");
                        fout.write(audio);
                        panaud pn=new panaud("Audios/"+"R "+timeSent+".wav");
                        JPanel jpf=pn.addaud();
                        chatPanel.add(jpf);
                    }
                    else if(strArray[0].equals("video")){
                        String sender;
                        String timeSent;
                        String target;
                        if(strArray[1].equals("Group")){
                            chatPanel.add(new JLabel(strArray[3]+" sent Video at Groupe "+strArray[2]+" :"));
                            sender=strArray[3];
                            timeSent=strArray[4].replace(' ', '-').replace(':','-');
                            target="Group "+strArray[2];
                        }
                        else{
                            chatPanel.add(new JLabel(strArray[1]+" sent Video : "));
                            sender=strArray[1];
                            timeSent=strArray[2].replace(' ', '-').replace(':','-');
                            target=myEmail;
                        }
                        int len = dis.readInt();
                        byte[] video = new byte[len];
                        dis.readFully(video);
                        FileOutputStream fout = new FileOutputStream("Videos/"+"R "+timeSent+".mp4");
                        updateConversationFile(sender, target , timeSent, "video", "Videos/"+"R "+timeSent+".mp4");
                        fout.write(video);
                        JPanel panel = new JPanel();
                        JLabel lblNewLabel_1 = new JLabel("play video");
                        panel.add(lblNewLabel_1);
                        //set pannel dimensions
                        panel.setPreferredSize(new Dimension(200,30));
                        chatPanel.add(panel);
                        panel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                try {
                                    Desktop.getDesktop().open(new File("Videos/"+"R "+timeSent+".mp4"));
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                    else if(strArray[0].equals("text")){
                        if(strArray[1].equals("Group")){
                            chatPanel.add(new JLabel(strArray[3]+" at Groupe "+strArray[2]+" : "+strArray[5]));
                            updateConversationFile(strArray[3], "Group "+strArray[2], strArray[4], "text", strArray[5]);
                            
                        }
                        else{
                            chatPanel.add(new JLabel(strArray[1]+" : "+strArray[3]));
                            if(!strArray[2].equals(myEmail))
                                updateConversationFile(strArray[1], myEmail, strArray[2], "text", strArray[3]);
                        }
                    }
                    else if(strArray[0].equals("videoCall")){
                    }
                    else if(strArray[0].equals("audioCall")){
                            int len = dis.readInt();
                            byte[] audio = new byte[len];
                            dis.readFully(audio);
                            try {
                                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audio));
                                Clip clip = AudioSystem.getClip();
                                clip.open(audioStream);
                                clip.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                    else if(msg.equals("Login Successful")){
                        cLayout.show(appWindow.getContentPane(),"chat");
                        appWindow.setTitle("Chat de "+myEmail);
                        File directoryPath = new File("./Conversations");
                        String contents[] = directoryPath.list();
                        for(int i=0; i<contents.length; i++) {
                            if(contents[i].split(" ",2)[0].equals(myEmail))
                                viewHistory("./Conversations/"+contents[i], contents[i].split(" ",2)[1]);
                        }
                    }
                    myReadLock.unlock();
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            scroll.getViewport().setViewPosition(new Point(0, scroll.getHeight()));
                        }
                    });
                    chatPanel.setVisible(false);
                    chatPanel.setVisible(true);
                }
                catch(Exception e){
                    try{
                        dis.skip(dis.available());
                    }
                    catch(Exception e1){
                        e.printStackTrace();
                        chatPanel.add(new JLabel("You got Disconnect, Restart the application"));
                        itIsFine=false;
                    }
                }
            }
        });
        ths.start();
        
        chatWindow.setLayout(new BorderLayout());
        
        chatWindow.add(scroll, BorderLayout.CENTER);
        chatPanel.setLayout (new BoxLayout (chatPanel, BoxLayout.Y_AXIS));  
        JPanel chatInputPanel = new JPanel();
        
        chatInputPanel.setLayout(new FlowLayout());
        
        receiverField.setPreferredSize(new Dimension(400, 30));
        JPanel sendmsgPanel = new JPanel();
        sendmsgPanel.setLayout (new BoxLayout (sendmsgPanel, BoxLayout.Y_AXIS));
        sendmsgPanel.add(receiverField);
        JTextField chatArea = new JTextField();
        chatArea.setPreferredSize(new Dimension(400, 30));
        chatInputPanel.add(chatArea);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {//your message is added to panel here
            public void actionPerformed(ActionEvent e) {
                String receiver[]=receiverField.getText().split(" ",2);
                String s=sendMessage(receiver[1], "text", chatArea.getText().replace("@@@",""), receiver[0].equals("Group"));
                myWriteLock.unlock();
                if(receiver[0].equals("User"))
                    updateConversationFile(myEmail, receiver[1], s, "text", chatArea.getText().replace("@@@",""));
                else if(receiver[0].equals("Group"))
                    updateConversationFile(myEmail, "Group "+receiver[1], s, "text", chatArea.getText().replace("@@@",""));
                chatPanel.add(new JLabel(chatArea.getText().replace("@@@","")));//Add user msg here
                chatArea.setText("");
                sendmsgPanel.setVisible(false);
                sendmsgPanel.setVisible(true);
            }
        });
        JButton btnNewButton_2 = new JButton("Image");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
		        jFileChooser.setDialogTitle("Importer");
		        int result = jFileChooser.showOpenDialog(null);
		        if(result == JFileChooser.APPROVE_OPTION) {
		            File file = jFileChooser.getSelectedFile();
		            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		            if(extension.toLowerCase().equals("png") ||  extension.toLowerCase().equals("jpg")) {
				        chatPanel.add(new JLabel(setIconDimension(new ImageIcon(file.getAbsolutePath()))));
                        chatPanel.setVisible(false);
                        chatPanel.setVisible(true);
				        try {
                            BufferedImage bfimg = ImageIO.read(new File(file.getAbsolutePath()));
                            String receiver[]=receiverField.getText().split(" ",2);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(bfimg,"png", baos);
                            byte bytes[]=baos.toByteArray();
                            String timeSent=sendMessage(receiver[1], "image",  Integer.toString(bytes.length), receiver[0].equals("Group"));
                            dos.write(bytes);
                            myWriteLock.unlock();
                            String imageFileName="Images/S "+timeSent+".png";
                            File outputfile = new File(imageFileName);
                            outputfile.createNewFile();
                            ImageIO.write(bfimg, "png", outputfile);
                            if(receiver[0].equals("User"))
                                updateConversationFile(myEmail, receiver[1] , timeSent, "image", imageFileName);
                            else
                                updateConversationFile(myEmail, receiverField.getText(), timeSent, "image", imageFileName);
						}
						catch (IOException e1) {
							e1.printStackTrace();
						}
	                }
		        }
			}
		});
        JButton btnNewButton_1 = new JButton("Reccord");
        btnNewButton_1.addActionListener(new ActionListener() {
            AudioFormat audioFormat;
            DataLine.Info dataInfo;
            TargetDataLine targetLine;
            Thread audiorecorder;
            String fileName;
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
                        String a[]=receiverField.getText().split(" ",2);
                        String timeSent=sendMessage(a[1], "audio", Integer.toString(data.length), a[0].equals("Group"));
                        dos.write(data);
                        myWriteLock.unlock();
                        if(a[0].equals("User"))
                            updateConversationFile(myEmail, a[1] , timeSent, "audio", fileName);
                        else
                            updateConversationFile(myEmail, receiverField.getText(), timeSent, "audio", fileName);
                        panaud pn=new panaud(fileName);
                        JPanel jpf=pn.addaud();
                        chatPanel.add(jpf);
                        sendmsgPanel.setVisible(false);
                        sendmsgPanel.setVisible(true);
                    }
                    catch(Exception a) {
                        a.printStackTrace();
                    }
                }
            }
        });
        JButton btnNewButton_3 = new JButton("Send Video");
        btnNewButton_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Importer");
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.mp4", "mp4");
                fileChooser.addChoosableFileFilter(filter);
                int result = fileChooser.showSaveDialog(null);
                if(result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String fileName = selectedFile.getAbsolutePath();
                    try {
                        File f = new File(fileName);
                        byte[] data = Files.readAllBytes(f.toPath());
                        String a[]=receiverField.getText().split(" ",2);
                        String timeSent=sendMessage(a[1], "video", Integer.toString(data.length), a[0].equals("Group"));
                        dos.write(data);
                        myWriteLock.unlock();
                        if(a[0].equals("User"))
                            updateConversationFile(myEmail, a[1] , timeSent, "video", fileName);
                        else
                            updateConversationFile(myEmail, receiverField.getText(), timeSent, "video", fileName);
                        JPanel panel = new JPanel();
                        panel.add(new JLabel("you sent a video"));
                        panel.setPreferredSize(new Dimension(10,30));
                        chatPanel.add(panel);
                        panel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                try {
                                    Desktop.getDesktop().open(new File(fileName));
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                        chatPanel.setVisible(false);
                        chatPanel.setVisible(true);
                    }
                    catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        JButton buttonScreen = new JButton("Capture");
        buttonScreen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Webcam webcam = Webcam.getDefault();
				if (webcam != null) {
					System.out.println("Webcam: " + webcam.getName());
				} else {
					System.out.println("No webcam detected");
				}
				webcam.open();
				try {
					BufferedImage img = webcam.getImage();
                    chatPanel.add(new JLabel(setIconDimension(new ImageIcon(img))));
                    chatPanel.setVisible(false);
                    chatPanel.setVisible(true);
                    String receiver[]=receiverField.getText().split(" ",2);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(img,"png", baos);
                    byte bytes[] = baos.toByteArray();
                    String timeSent=sendMessage(receiver[1], "image", Integer.toString(bytes.length), receiver[0].equals("Group"));
                    dos.write(bytes);
                    myWriteLock.unlock();
                    String imageFileName="Images/S "+timeSent+".png";
                    File outputfile = new File(imageFileName);
                    outputfile.createNewFile();
                    ImageIO.write(img, "png", outputfile);
                    if(receiver[0].equals("User"))
                        updateConversationFile(myEmail, receiver[1] , timeSent, "image", imageFileName);
                    else
                        updateConversationFile(myEmail, receiverField.getText(), timeSent, "image", imageFileName);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                webcam.close();
            }
        });
        //add button videocall
        JButton btnNewButton_4 = new JButton("Call");
        btnNewButton_4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        chatInputPanel.add(sendButton);
        sendmsgPanel.add(buttonScreen);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(btnNewButton_2);
        buttonsPanel.add(btnNewButton_1);
        sendmsgPanel.add(chatInputPanel);
        buttonsPanel.add(btnNewButton_3);
        buttonsPanel.add(buttonScreen);
        buttonsPanel.add(btnNewButton_4);
        sendmsgPanel.add(buttonsPanel);
        chatWindow.add(sendmsgPanel, BorderLayout.SOUTH);
        appWindow.setVisible(true);
        appWindow.setTitle("Chat");
        appWindow.setSize(500, 500);
        appWindow.setResizable(false);
        appWindow.setLocationRelativeTo(null);
    }
    public static String sendMessage(String target, String messageType, String messageContent, boolean  isGroup){//return Time Sent
        LocalDateTime now = LocalDateTime.now();
        String s=dtf.format(now);;
        try {
            myWriteLock.lock();
            if(isGroup){
                dos.writeUTF("messageGroup@@@"+target);
                if(messageType.equals("text"))
                    dos.writeUTF(messageType+"@@@Group@@@"+target+"@@@"+myEmail+"@@@"+s+"@@@"+messageContent);
                else{
                    dos.writeUTF(messageType+"@@@Group@@@"+target+"@@@"+myEmail+"@@@"+s);
                    dos.writeInt(Integer.parseInt(messageContent));
                }
            }
            else{
                dos.writeUTF("messageUser@@@"+target);
                if(messageType.equals("text"))
                    dos.writeUTF(messageType+"@@@"+myEmail+"@@@"+s+"@@@"+messageContent);
                else{
                    dos.writeUTF(messageType+"@@@"+myEmail+"@@@"+s);
                    dos.writeInt(Integer.parseInt(messageContent));
                }
            }
        } catch (IOException e) {
            System.out.println("failed to send msg");
            e.printStackTrace();
        }
        return s;
    }
    public static void viewHistory(String FileName, String conversationName){
        int idType=0;
        int idSender=1;
        int idTimeStamp=2;
        int idContent=3;
        JFrame jf=new JFrame(conversationName);
        JPanel jp=new JPanel();
        JScrollPane scroll = new JScrollPane(jp);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
        jf.setSize(500,500);
        jp.setSize(500,500);
        jf.add(scroll);
        try{
            File file = new File(FileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null){
                String[] data = line.split("@@@",4);
                if(data[idType].equals("text")){
                    jp.add(new JLabel(data[idSender]+" at "+data[idTimeStamp]+" : "+data[idContent]));
                }
                else if(data[idType].equals("image")){
                    jp.add(new JLabel(data[idSender]+" at "+data[idTimeStamp]+" : Photo"));
                    JLabel jl = new JLabel();
                    jl.setIcon(setIconDimension(new ImageIcon(data[idContent])));
                    jl.setMaximumSize(new Dimension(500,500));
                    jp.add(jl);
                }
                else if(data[idType].equals("audio")){
                    panaud pn=new panaud(data[idContent]);
                    JPanel jpf=pn.addaud();
                    jp.add(jpf);
                }
                else if(data[idType].equals("video")){
                    JPanel panel = new JPanel();
                    panel.add(new JLabel(data[idSender]+" at "+data[idTimeStamp]+" : video"));
                    panel.setPreferredSize(new Dimension(10,30));
                    jp.add(panel);
                    panel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                Desktop.getDesktop().open(new File(data[idContent]));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("failed to read file");
        }
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                scroll.getViewport().setViewPosition(new Point(0, scroll.getHeight()));
            }
        });
        jf.setVisible(true);
    }
    public static void updateConversationFile(String sender, String receiver , String clock, String type, String fileName){
        try{
            FileWriter fw;
            if(myEmail.equals(receiver)){
                File myObj = new File("Conversations/"+myEmail+" "+sender+".txt");
                myObj.createNewFile();
                fw = new FileWriter("Conversations/"+myEmail+" "+sender+".txt", true);
            }
            else{
                File myObj = new File("Conversations/"+myEmail+" "+receiver+".txt");
                myObj.createNewFile();
                fw = new FileWriter("Conversations/"+myEmail+" "+receiver+".txt", true);
            }
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(type+"@@@"+sender+"@@@"+clock+"@@@"+fileName+"\n");
            bw.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    private static void setConnexionPanel(JPanel jp){
        JLabel idLabel, pwLabel;
        JTextField idTextField;
        JPasswordField pwTextField;
        JButton b_SeConnecter;
        JPanel lilP,bigP;
        bigP=new JPanel();
        Dimension d= new Dimension(300, 30);
        idLabel=new JLabel("Identifiant :");
        idLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        pwLabel=new JLabel("Mot de passe :");
        pwLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        idTextField=new JTextField();
        pwTextField=new JPasswordField();
        idTextField.setMinimumSize(d);
        idTextField.setMaximumSize(d);
        pwTextField.setMinimumSize(d);
        pwTextField.setMaximumSize(d);
        idTextField.setBorder(new MatteBorder(0, 0, 2, 0, (Color) new Color(0, 0, 0)));
        pwTextField.setBorder(new MatteBorder(0, 0, 2, 0, (Color) new Color(0, 0, 0)));
        idLabel.setHorizontalAlignment(JLabel.LEFT);
        pwLabel.setHorizontalAlignment(JLabel.LEFT);
        idLabel.setAlignmentX(jp.LEFT_ALIGNMENT);
        pwLabel.setAlignmentX(jp.LEFT_ALIGNMENT);
        jp.setLayout(new BoxLayout(jp,  BoxLayout.Y_AXIS));
        b_SeConnecter = new JButton("Se Connecter");
        //bigP.setLayout(new GridLayout(5, 1, 100, 20));
        bigP.setLayout(null);
        bigP.setPreferredSize(new Dimension(400, 300));
        idLabel.setBounds(50, 60, 210, 44);
        idTextField.setBounds(50, 115, 350, 44);
        pwLabel.setBounds(50, 170, 210, 44);
        pwTextField.setBounds(50, 225, 350, 44);
        bigP.add(idLabel);
        bigP.add(idTextField);
        bigP.add(pwLabel);
        bigP.add(pwTextField);
        lilP=new JPanel();
        b_SeConnecter.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    dos.writeUTF("login "+idTextField.getText());
                    dos.writeUTF(pwTextField.getText());
                    pwTextField.setText("");
                    myEmail = idTextField.getText().toLowerCase();
                }
                catch(Exception dqsqsfqsff){JOptionPane.showMessageDialog(jp,"Erreur Serveur","Erreur",JOptionPane.WARNING_MESSAGE);}
        }});
        b_SeConnecter.setPreferredSize(new Dimension(150, 30));
        b_SeConnecter.setBorderPainted(false);
		b_SeConnecter.setForeground(Color.WHITE);
		b_SeConnecter.setBackground(new Color(14, 71, 108));
        lilP.add(b_SeConnecter);
        jp.add(bigP);
        jp.add(lilP);
        jp.setBackground(new Color(255,255,255));
    }
    public static ImageIcon setIconDimension(ImageIcon icon){
        double scale = Math.min((double)500/icon.getIconWidth(), (double)500/icon.getIconHeight());
        int width = (int)(icon.getIconWidth()*scale);
        int height = (int)(icon.getIconHeight()*scale);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        return icon;
    }
}
class panaud extends JPanel {
	Clip clip;
	JSlider slider;
	AudioInputStream audioStream = null;
	int t=0;
	int s;
	String f;
	public panaud(String f) {
		this.f=f;
	}
	public  JPanel addaud() {
		JButton Y = new JButton("Audio");
		File file=new File(f);
		try {
			audioStream = AudioSystem.getAudioInputStream(file);
			clip=AudioSystem.getClip();
			clip.open(audioStream);
			t=0;
			long sr= (long)clip.getMicrosecondLength()/1000;
			slider = new JSlider(0,(int)sr,0);
		} catch (UnsupportedAudioFileException | IOException e3) {
			e3.printStackTrace();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
		Y.addActionListener(new ActionListener() {
			boolean b=true;
            public void actionPerformed(ActionEvent e) {
				if(!clip.isRunning()) {
					b=false;
					clip.start();
					Thread sli=new Thread() {
						long sr= (long)clip.getMicrosecondLength()/1000;
						@Override public void run() {
                            t=slider.getValue();
                            clip.setMicrosecondPosition(t*1000);
							while( t<sr && clip.isRunning()&&!b){
								slider.setValue(t);
                                t+=100;
                                try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
                                slider.setVisible(false);
                                slider.setVisible(true);
                            }
                            if(!b){
                                b=true;
							    slider.setValue(0);
                            }
					    }
                    };
					sli.start();
				}
				else {
					clip.stop();
					b=true;
				}
			}
		});
		Y.setBounds(285, 96, 39, 38);
		this.add(Y);
		slider.setBounds(77, 96, 200, 38);
		this.add(slider);
		return this;		
	}
}
