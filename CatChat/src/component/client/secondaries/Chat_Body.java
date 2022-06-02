package component.client.secondaries;

import java.awt.Adjustable;
import java.awt.Color;
import static java.awt.SystemColor.text;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JScrollBar;
import net.miginfocom.swing.MigLayout;
import swing.ScrollBar;


public class Chat_Body extends javax.swing.JPanel {
    private static long timeLastMsgWasSent;
    public Chat_Body() {
        initComponents();
        init();
//        addFileLeft("","","jamal","MyPDF.pdf","30 MB");
//        addFileLeft("","His File","ahmed","MyPDF.pdf","30 MB");
//        addDate("25/05/2022");
//        addFileRight("","My file \nSomthing","MyPDF.pdf","30 MB");
//        addFileRight("","","MyPDF.pdf","30 MB");
//        addAudioRight("");
//        addAudioLeft("","Ahmed");
    }
    public String setTime(String time){
        String splitTime[]=time.split("-");
        if(splitTime.length==6){
            long t=Integer.parseInt(splitTime[0])*10000+Integer.parseInt(splitTime[1])*100+Integer.parseInt(splitTime[2]);
            if(t!=timeLastMsgWasSent){
                timeLastMsgWasSent=t;
                addDate(splitTime[2]+"/"+splitTime[1]+"/"+splitTime[0]);
            }
            if(Integer.parseInt(splitTime[3])>12)
                return (splitTime[3]+":"+splitTime[4]+" PM");
            else
                return (splitTime[3]+":"+splitTime[4]+" AM");
        }
        else
            return null;
    }
    public void destroy(){
        this.removeAll();
        this.initComponents();
        this.init();
    }
    
    public void init(){
        body.setLayout(new MigLayout("fillx","", "0[]0"));
        sp.setVerticalScrollBar(new ScrollBar());
        sp.getVerticalScrollBar().setBackground(Color.WHITE);
    }
    
    public void addItemLeft(String time,String text,String user, Icon... image){
        Chat_Left item = new Chat_Left();
        item.setText(text);
        item.setImage(image);
        item.setTime(setTime(time));
        item.setUserprofile(user);
        body.add(item,"wrap, w 100::80%");
        body.repaint();
        body.revalidate();
        scrollToBottom();
    }
    
    public void addFileLeft(String time, String user,String filePath){
        Chat_Left item = new Chat_Left();
        item.setText(" ");
        File f = new File(filePath);
        String fileSize;
        if(f.length()>(1024*1024))
            fileSize = f.length() / (1024 * 1024) + " MB";
        else if(f.length()>1024)
            fileSize = f.length() / 1024 + " KB";
        else
            fileSize = f.length() + " Bytes";
        item.setFile(f.getName(), fileSize, filePath);
        item.setTime(setTime(time));
        item.setUserprofile(user);
        body.add(item,"wrap, w 100::80%");
        body.repaint();
        body.revalidate();
        scrollToBottom();
    }
    
    public void addItemRight(String time,String text, Icon... image){
        Chat_Right item = new Chat_Right();
        item.setText(text);
        item.setImage(image);
        item.setTime(setTime(time));
        body.add(item,"wrap, al right, w 100::80%");
        body.repaint();
        body.revalidate();
        scrollToBottom();
    }
    
    public void addFileRight(String time, String filePath){
        Chat_Right item = new Chat_Right();
        item.setText(" ");
        File f = new File(filePath);
        String fileSize;
        if(f.length()>(1024*1024))
            fileSize = f.length() / (1024 * 1024) + " MB";
        else if(f.length()>1024)
            fileSize = f.length() / 1024 + " KB";
        else
            fileSize = f.length() + " Bytes";
        item.setFile(f.getName(), fileSize, filePath);
        item.setTime(setTime(time));
        body.add(item,"wrap, al right, w 100::80%");
        body.repaint();
        body.revalidate();
        scrollToBottom();
    }
    
    public void addAudioRight(String time, String filePath){
        Chat_Right item = new Chat_Right();
//        item.setText(text);
//        item.setFile(fileName, fileSize,"./Conversation/Sent/FileSent.txt");
        item.setAudio(filePath);
        item.setTime(setTime(time));
        body.add(item,"wrap, al right, w 100::80%");
        body.repaint();
        body.revalidate();
        scrollToBottom();
    }
    
    public void addAudioLeft(String time,String user, String filePath){
        Chat_Left item = new Chat_Left();
//        item.setText(text);
//        item.setFile(fileName, fileSize,"./Conversation/Sent/FileSent.txt");
        
        item.setAudio(filePath);
        item.setTime(setTime(time));
        item.setUserprofile(user);
        body.add(item,"wrap, al left, w 100::80%");
        body.repaint();
        body.revalidate();
        scrollToBottom();
    }
    
    public void addDate(String date){
        Chat_Date item=new Chat_Date();
        item.setDate(date);
        body.add(item,"wrap, al center, w :: 80%");
        body.repaint();
        body.revalidate();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp = new javax.swing.JScrollPane();
        body = new javax.swing.JPanel();

        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        body.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout bodyLayout = new javax.swing.GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 807, Short.MAX_VALUE)
        );
        bodyLayout.setVerticalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 507, Short.MAX_VALUE)
        );

        sp.setViewportView(body);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    public void scrollToBottom() {
        JScrollBar verticalBar = sp.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private javax.swing.JScrollPane sp;
    // End of variables declaration//GEN-END:variables
}
