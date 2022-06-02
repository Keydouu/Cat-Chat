package component.form;

import swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

public class PanelCover extends javax.swing.JPanel {
    private final DecimalFormat df=new DecimalFormat("##0.###", DecimalFormatSymbols.getInstance(Locale.US));
    private ActionListener event;
    private MigLayout layout;
    private JLabel title;
    private JLabel description;
    private JLabel description1;
    private ButtonOutLine button;
    private boolean  isLogin;
    
    
    public PanelCover() {
        initComponents();
        setOpaque(false);
        layout = new MigLayout("wrap, fill", "[center]", "push[]30[]25[]5[]40[]40push");
        setLayout(layout);
        init();
    }
    private void init(){
        title= new JLabel("Already a member ?");
        title.setFont(new Font("sansserif",1,30));
        title.setForeground(new Color(44,30,116));
        add(title);
        
        description= new JLabel("Log in with your CatChat");
        description.setFont(new Font("sansserif",1,18));
        description.setForeground(new Color(44,30,116));
        add(description);
        
        description1= new JLabel("account now !");
        description1.setFont(new Font("sansserif",1,18));
        description1.setForeground(new Color(44,30,116));
        add(description1);
        
        button = new ButtonOutLine();
        button.setBackground(new Color(44,30,116));
        button.setForeground(new Color(44,30,116));
        button.setText("SIGN IN");
        button.setFocusable(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.actionPerformed(ae);
            }
        });
        add(button,"w 60%, h 40");
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/CatChat.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jLabel1)
                .addContainerGap(87, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(323, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        GradientPaint gra = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(240,240,240));
        g2.setPaint(gra);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(grphcs);
    }
    public void addEvent(ActionListener event){
        this.event=event;
    }
    public void registerLeft(double v){
        v=Double.valueOf(df.format(v));
        login(false);
        layout.setComponentConstraints(title,"pad 0 -"+v+"% 0 0");
        layout.setComponentConstraints(description,"pad 0 -"+v+"% 0 0");
        layout.setComponentConstraints(description1,"pad 0 -"+v+"% 0 0");
    }
    
    public void registerRight(double v){
        v=Double.valueOf(df.format(v));
        login(false);
        layout.setComponentConstraints(title,"pad 0 -"+v+"% 0 0");
        layout.setComponentConstraints(description,"pad 0 -"+v+"% 0 0");
        layout.setComponentConstraints(description1,"pad 0 -"+v+"% 0 0");
    }
    
    public void loginLeft(double v){
        v=Double.valueOf(df.format(v));
        login(true);
        layout.setComponentConstraints(title,"pad 0 "+v+"% 0 "+v+"%");
        layout.setComponentConstraints(description,"pad 0 "+v+"% 0 "+v+"%");
        layout.setComponentConstraints(description1,"pad 0 "+v+"% 0 "+v+"%");
    }
    
    public void loginRight(double v){
        v=Double.valueOf(df.format(v));
        login(true);
        layout.setComponentConstraints(title,"pad 0 "+v+"% 0 "+v+"%");
        layout.setComponentConstraints(description,"pad 0 "+v+"% 0 "+v+"%");
        layout.setComponentConstraints(description1,"pad 0 "+v+"% 0 "+v+"%");
    }
    
    private void login (boolean login){
        if(this.isLogin != login){
            if(login){
                title.setText("Not a member ?");
                description.setText("Join the CatChat family");
                description1.setText("right now !");
                button.setText("SIGN UP");
            }else{
                title.setText("Already a member ?");
                description.setText("Log in with your CatChat");
                description1.setText("account now !");
                button.setText("SIGN IN");
            }
            this.isLogin=login;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
