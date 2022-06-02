package main;

import component.form.Message;
import component.form.PanelCover;
import component.form.PanelLoginAndRegister;
import event.Globals;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class MainLogin extends javax.swing.JFrame {

    private final DecimalFormat df = new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));
    private MigLayout layout;
    private PanelCover cover;
    private PanelLoginAndRegister loginAndRegister;
    private boolean isLogin;
    private final double addSize = 30;
    private final double coverSize = 40;
    private final double loginSize = 60;

    public MainLogin() {
        initComponents();
        init();
    }
    
    //Here where all hapens
    private void init() {
        setIconImage(new ImageIcon(getClass().getResource("/icon/Logo.png")).getImage());
        layout = new MigLayout("fill, insets 0");
        cover = new PanelCover();
        Globals.initGlobals();
        Thread ths= new Thread(() -> {
            String msg= new String();
            while(!msg.equals("Login Successful")){
                try{   
                    msg = Globals.decrypt(Globals.dis.readUTF());
                    switch (msg) {
                        case "Login Successful":
                            Globals.setMyEmail(loginAndRegister.getUser().getEmail().toLowerCase());
                            MainClient.main(null);
                            this.dispose();
                            break;
                        case "Login Failed":
                            showMessage(Message.MessageType.ERROR, "Invalid Email or Password!");
                            break;
                        case "Email already used":
                            showMessage(Message.MessageType.ERROR, "Email already used!");
                            break;
                        case "Registration Successful":
                            showMessage(Message.MessageType.SUCCESS, "Registered Successfully!");
                            break;
                        default:
                            break;
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        ths.start();
        
        ActionListener eventRegister = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                register();
            }
        };
        ActionListener eventLogin = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                login();
            }
        };
        loginAndRegister = new PanelLoginAndRegister(eventRegister,eventLogin);
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                double fractionCover;
                double fractionLogin;
                double size = coverSize;
                if (fraction <= 0.5f) {
                    size += fraction * addSize;
                } else {
                    size += addSize - fraction * addSize;
                }
                if (isLogin) {
                    fractionCover = 1f - fraction;
                    fractionLogin = fraction;
                    if (fraction >= 0.5f) {
                        cover.registerRight(fractionCover * 100);
                    } else {
                        cover.loginRight(fractionLogin * 100);
                    }
                } else {
                    fractionCover = fraction;
                    fractionLogin = 1f - fraction;
                    if (fraction <= 0.5f) {
                        cover.registerLeft(fraction * 100);
                    } else {
                        cover.loginLeft((1f - fraction) * 100);
                    }
                }
                if (fraction >= 0.5f) {
                    loginAndRegister.showRegister(isLogin);
                }
                fractionCover = Double.valueOf(df.format(fractionCover));
                fractionLogin = Double.valueOf(df.format(fractionLogin));
                layout.setComponentConstraints(cover, "width " + size + "%, pos " + fractionCover + "al 0 n 100%");
                layout.setComponentConstraints(loginAndRegister, "width " + loginSize + "%, pos " + fractionLogin + "al 0 n 100%");
                bg.revalidate();
            }

            @Override
            public void end() {
                isLogin = !isLogin;
            }
        };
        Animator animator = new Animator(800, target);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0);  //  for smooth animation
        bg.setLayout(layout);
        bg.add(cover, "width " + coverSize + "%, pos 0al 0 n 100%");
        bg.add(loginAndRegister, "width " + loginSize + "%, pos 1al 0 n 100%"); //  1al as 100%
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) {
                    animator.start();
                }
            }
        });
        animator.start();
    }
    
    // Check format of an email with regular a expression ( NOT WORKING !!)
    public static boolean isValid(String email)
    {
        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        Matcher matcher = pat.matcher(email);
        return matcher.matches();
    }

    // Function to reacte to sign up button
    private void register() {
        ModelUser user = loginAndRegister.getUser();
        if(user.getEmail().isEmpty()||user.getUserName().isEmpty()||user.getPassword().isEmpty()||user.getPassword2().isEmpty() ){
            showMessage(Message.MessageType.ERROR, "Fill in the blank !");
        }else if(!isValid(user.getEmail())){
            showMessage(Message.MessageType.ERROR, "Invalide email format !");
        }else if (!user.getPassword().equals(user.getPassword2())){
            showMessage(Message.MessageType.ERROR, "Passwords are not matched !");
        }else{
            try{
                Globals.writeUTFEncoded("register "+user.getUserName());
                Globals.writeUTFEncoded(user.getEmail());
                Globals.writeUTFEncoded(user.getPassword());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    // Function to reacte to sign in button
    private void login() {
        ModelUser user = loginAndRegister.getUser();
        if(user.getEmail().isEmpty()||user.getPassword().isEmpty()){
            showMessage(Message.MessageType.ERROR, "Fill in the blank !");
        }else if(!isValid(user.getEmail().trim())){
            showMessage(Message.MessageType.ERROR, "Invalide email format !");
        }else{
            try{
                Globals.writeUTFEncoded("login "+user.getEmail());
                Globals.writeUTFEncoded(user.getPassword());
            }catch(Exception e){
                e.printStackTrace();
            }
            
            //this.dispose(); // close Main form after sucessful sign in
            //int socket =1; // to be replaced with socket
            //MainClient.main();
            
        }
    }
    
    //Showing success and error messages
    
    private void showMessage(Message.MessageType messageType, String message) {
        Message ms = new Message();
        ms.showMessage(messageType, message);
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void begin() {
                if (!ms.isShow()) {
                    bg.add(ms, "pos 0.5al -30", 0); //  Insert to bg fist index 0
                    ms.setVisible(true);
                    bg.repaint();
                }
            }

            @Override
            public void timingEvent(float fraction) {
                float f;
                if (ms.isShow()) {
                    f = 40 * (1f - fraction);
                } else {
                    f = 40 * fraction;
                }
                layout.setComponentConstraints(ms, "pos 0.5al " + (int) (f - 30));
                bg.repaint();
                bg.revalidate();
            }

            @Override
            public void end() {
                if (ms.isShow()) {
                    bg.remove(ms);
                    bg.repaint();
                    bg.revalidate();
                } else {
                    ms.setShow(true);
                }
            }
        };
        Animator animator = new Animator(300, target);
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    animator.start();
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
        }).start();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(950, 640));

        bg.setBackground(new java.awt.Color(44, 30, 116));
        bg.setOpaque(true);

        javax.swing.GroupLayout bgLayout = new javax.swing.GroupLayout(bg);
        bg.setLayout(bgLayout);
        bgLayout.setHorizontalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 950, Short.MAX_VALUE)
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane bg;
    // End of variables declaration//GEN-END:variables
}
