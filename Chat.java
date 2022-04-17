import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Chat extends JFrame{
    static DataInputStream dis=null;
	static DataOutputStream dos=null;
    static Socket commsocket;
    static JPanel chatPanel = new JPanel();
    public static void main(String[] args) {
		InetAddress ip;
        Thread ths=new Thread();
        try{
			ip = InetAddress.getByName("localhost");
			commsocket = new Socket(ip, 5057);
			dis = new DataInputStream(commsocket.getInputStream());
			dos = new DataOutputStream(commsocket.getOutputStream());
        }catch(Exception e){
            e.printStackTrace();
        }
        JScrollPane scroll = new JScrollPane(chatPanel); 
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
        ths = new Thread(() -> {
            while(true){
                try{
                    String msg = dis.readUTF();
                    System.out.println(msg);
                    if(msg.equals("image")) {
                        BufferedImage bfimg = ImageIO.read(ImageIO.createImageInputStream(dis));
                        chatPanel.add(new JLabel(new ImageIcon(bfimg)));
                        dis.skip(dis.available()); 
                    }
                    else
                        chatPanel.add(new JLabel(msg));
                    chatPanel.setVisible(false);
                    chatPanel.setVisible(true);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        ths.start();
		
        JFrame chatWindow = new JFrame();
        chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        chatWindow.setLayout(new BorderLayout());
        
        chatWindow.add(scroll, BorderLayout.CENTER);
        chatPanel.setLayout (new BoxLayout (chatPanel, BoxLayout.Y_AXIS));  
        JPanel chatInputPanel = new JPanel();
        
        chatInputPanel.setLayout(new FlowLayout());
        JTextField receiverField = new JTextField();
        receiverField.setPreferredSize(new Dimension(400, 30));
        JPanel sendmsgPanel = new JPanel();
        sendmsgPanel.setLayout (new BoxLayout (sendmsgPanel, BoxLayout.Y_AXIS));
        sendmsgPanel.add(receiverField);
        JTextField chatArea = new JTextField();
        chatArea.setPreferredSize(new Dimension(400, 30));
        chatInputPanel.add(chatArea);
        JButton sendButton = new JButton("Send");
        chatInputPanel.add(sendButton);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMsg(receiverField.getText());
                sendMsg(chatArea.getText());
                chatArea.setText("");
                sendmsgPanel.setVisible(false);
                sendmsgPanel.setVisible(true);
            }
        });
        JButton btnNewButton_2 = new JButton("fichier");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
		        jFileChooser.setDialogTitle("Importer");
		        int result = jFileChooser.showOpenDialog(null);
		        if(result == JFileChooser.APPROVE_OPTION) {
		            File file = jFileChooser.getSelectedFile();
		            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		            if(extension.toLowerCase().equals("png") ||  extension.toLowerCase().equals("jpg")) {
				        chatPanel.add(new JLabel(new ImageIcon(file.getAbsolutePath())));
                        chatPanel.setVisible(false);
                        chatPanel.setVisible(true);
				        try {
                            BufferedImage bfimg = ImageIO.read(new File(file.getAbsolutePath()));
				        	sendMsg("ImageTo"+receiverField.getText());
							ImageIO.write(bfimg, "png", dos);
						}
						catch (IOException e1) {
							e1.printStackTrace();
						}
	                }
		        }
			}
		});
        sendmsgPanel.add(btnNewButton_2);
        sendmsgPanel.add(chatInputPanel);
        chatWindow.add(sendmsgPanel, BorderLayout.SOUTH);
        chatWindow.setVisible(true);
        chatWindow.setTitle("Chat");
        chatWindow.setSize(500, 500);
        chatWindow.setResizable(false);
        chatWindow.setLocationRelativeTo(null);    
    }
    public static String sendMsg(String msg){
        try{
            dos.writeUTF(msg);
            return msg;
        }
        catch(Exception e2){
            return "";
        }    
    }
}