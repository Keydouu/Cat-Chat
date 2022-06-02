package component.form;

import java.awt.Color;
import swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import main.ModelUser;
import net.miginfocom.swing.MigLayout;

public class PanelLoginAndRegister extends javax.swing.JLayeredPane {

    //A class to return the values entered in the fields
    private ModelUser user;
    public ModelUser getUser() {
        return user;
    }
    
    public PanelLoginAndRegister(ActionListener eventRegister,ActionListener eventLogin) {
        initComponents();
        initRegister(eventRegister);
        initLogin(eventLogin);
        login.setVisible(false);
        register.setVisible(true);
    }
    
    private void initRegister(ActionListener eventRegister){
        register.setLayout(new MigLayout("wrap", "push[center]push", "push[]60[]20[]20[]20[]40[]35push"));
        JLabel label=new JLabel("Create Account");
        label.setFont(new Font("sansserif", 1, 50));
        label.setForeground(new Color(255,255,255));
        register.add(label);
        
        MyTextField txtUser=new MyTextField();
        txtUser.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/user.png")));
        txtUser.setHint(" Username");
        register.add(txtUser,"w 60%");
        
        MyTextField txtEmail=new MyTextField();
        txtEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/mail.png")));
        txtEmail.setHint(" Email");
        register.add(txtEmail,"w 60%");
        
        MyPasswordField txtPass=new MyPasswordField();
        txtPass.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/password.png")));
        txtPass.setHint(" Password");
        register.add(txtPass,"w 60%");
        
        MyPasswordField txt2Pass=new MyPasswordField();
        txt2Pass.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/password.png")));
        txt2Pass.setHint(" Re-Type password");
        register.add(txt2Pass,"w 60%");
        
        Button cmd =new Button();
        cmd.setForeground(new Color(44,30,116));
        cmd.setBackground(new Color(255,255,255));
        cmd.setEffectColor(new Color (118, 99, 222));
        cmd.addActionListener(eventRegister);
        cmd.setText("SIGN UP");
        cmd.setFocusable(false);
        register.add(cmd,"w 40%, h 40");
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName=txtUser.getText().trim();
                String email=txtEmail.getText().trim();
                String password= String.valueOf(txtPass.getPassword());
                String password2= String.valueOf(txt2Pass.getPassword());
                user = new ModelUser(userName, email, password,password2);
            }
        });
        
    }
    private void initLogin(ActionListener eventLogin){
        login.setLayout(new MigLayout("wrap", "push[center]push", "push[]90[]35[]80[]push"));
        JLabel label=new JLabel("Welcome Back !");
        label.setFont(new Font("sansserif", 1, 50));
        label.setForeground(new Color(255,255,255));
        login.add(label);
        
        MyTextField txtEmail=new MyTextField();
        txtEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/mail.png")));
        txtEmail.setHint(" Email");
        login.add(txtEmail,"w 60%");
        
        MyPasswordField txtPass=new MyPasswordField();
        txtPass.setPrefixIcon(new ImageIcon(getClass().getResource("/icon/password.png")));
        txtPass.setHint(" Password");
        login.add(txtPass,"w 60%");
        
        Button cmd =new Button();
        cmd.setForeground(new Color(44,30,116));
        cmd.setBackground(new Color(255,255,255));
        cmd.setEffectColor(new Color (118, 99, 222));
        cmd.addActionListener(eventLogin);
        cmd.setText("SIGN IN");
        cmd.setFocusable(false);
        login.add(cmd,"w 40%, h 40");
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email=txtEmail.getText().trim();
                String password= String.valueOf(txtPass.getPassword());
                user = new ModelUser(email, password);
            }
        });
        
    }
    public void showRegister(boolean show){
        if(show){
            login.setVisible(false);
            register.setVisible(true);
        }else{
            login.setVisible(true);
            register.setVisible(false);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        login = new javax.swing.JPanel();
        register = new javax.swing.JPanel();

        setLayout(new java.awt.CardLayout());

        login.setBackground(new java.awt.Color(44, 30, 116));

        javax.swing.GroupLayout loginLayout = new javax.swing.GroupLayout(login);
        login.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(login, "card3");

        register.setBackground(new java.awt.Color(44, 30, 116));

        javax.swing.GroupLayout registerLayout = new javax.swing.GroupLayout(register);
        register.setLayout(registerLayout);
        registerLayout.setHorizontalGroup(
            registerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        registerLayout.setVerticalGroup(
            registerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(register, "card2");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel login;
    private javax.swing.JPanel register;
    // End of variables declaration//GEN-END:variables
}
