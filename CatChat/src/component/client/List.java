package component.client;

import component.client.secondaries.Item_Add;
import component.client.secondaries.Item_Group;
import net.miginfocom.swing.MigLayout;
import component.client.secondaries.Item_People;
import swing.ScrollBar;
import event.Globals;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class List extends javax.swing.JPanel {
    private static Map<String, Item_People> ItemPeopleMap;
    private static ArrayList<Item_People> orderdedItemPeople;
    public List() {
        if(ItemPeopleMap==null){
            ItemPeopleMap=new HashMap<>();
            orderdedItemPeople=new ArrayList<>();
        }
        initComponents();
        init();
    }
    
    private void init(){
        sp.setVerticalScrollBar(new ScrollBar());
        menu.setLayout(new MigLayout("fillx","2%[fill,96%]2%","5[]5"));
        menuList.setLayout(new MigLayout("fillx","3%[fill,92%]3%","5[]5"));
        showPeople();
    }
    public void showPeople(){//only use this once
        menuList.removeAll();
        String myEmail=Globals.getMyEmail();
        File directoryPath = new File("./Conversations");
        String contents[] = directoryPath.list();
        for(int i = 0; i < contents.length; i++){
            String conv[]=contents[i].split(" ", 2);
            if (conv[0].toLowerCase().equals(myEmail)){
                addConversation(conv[1].substring(0, conv[1].length()-4));
            }
        }
        reprintAllConversations();
    }
    public void addConversation(String email){
        Item_People ip=new Item_People(email);
        orderdedItemPeople.add(ip);
        ItemPeopleMap.put(email, ip);
        pushConversationUp(email);
    }
    public void newMessage(String email){
        pushConversationUp(email);
        messageNotif(email);
    }
    public void pushConversationUp(String emailToPush){
        int j=-1;
        for(int i=0; i<orderdedItemPeople.size();i++){
            if(orderdedItemPeople.get(i).getEmail().equals(emailToPush))
                j=i;
        }
        if(j==-1){
            File myObj = new File("Conversations/"+Globals.getMyEmail()+" "+emailToPush+".txt");
            try {
                if(myObj.createNewFile()){
                    addConversation(emailToPush);
                    pushConversationUp(emailToPush);
                }
            } catch (IOException ex) {
                Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        Item_People tmp;
        for(int i=j;i>0;i--){
            tmp = orderdedItemPeople.get(i);
            orderdedItemPeople.set(i, orderdedItemPeople.get(i-1));
            orderdedItemPeople.set(i-1, tmp);
        }
        reprintAllConversations();
    }
    public void reprintAllConversations(){
        menuList.removeAll();
        for(int i=0;i<orderdedItemPeople.size();i++){
            menuList.add(orderdedItemPeople.get(i),"wrap");
        }
        addAddition();
        refreshMenu();
    }
    public static void connection(String key, boolean connected){
        Iterator<Map.Entry<String, Item_People>> iterator = ItemPeopleMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Item_People> entry = iterator.next();
            if(entry.getKey().equals(key))
                entry.getValue().setConnection(connected);
        }
    }
    public static void messageNotif (String key){
        Iterator<Map.Entry<String, Item_People>> iterator = ItemPeopleMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Item_People> entry = iterator.next();
            if(entry.getKey().equals(key))
                entry.getValue().addMessageNotif();
        }
    }
    private void addAddition(){
        Item_Add i=new Item_Add("Add new contact");
        menuList.add(i);
        i.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String Email=JOptionPane.showInputDialog(null,"Add a contact");
                if(Email!=null){
                    try{
                        Globals.writeUTFEncoded("checkIfExist@@@"+Email.toLowerCase());
                    }
                    catch(Exception e2){
                        System.out.println("Failed to add contact");
			e2.printStackTrace();
                    }
                }
            }
        });
        
    }
    
    private void showGroup(){
        //test showing
        menuList.removeAll();
        for(int i=1;i<=6;i++){
            menuList.add(new Item_Group("Group_"+i),"wrap");  
        }
        menuList.add(new Item_Add("Create new Group"));
        refreshMenu();
    }
    
    private void refreshMenu(){
        menuList.repaint();
        menuList.revalidate();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu = new javax.swing.JLayeredPane();
        menuMessage = new component.client.secondaries.MenuButton();
        menuGroup = new component.client.secondaries.MenuButton();
        sp = new javax.swing.JScrollPane();
        menuList = new javax.swing.JPanel();

        setBackground(new java.awt.Color(64, 50, 116));

        menu.setForeground(new java.awt.Color(255, 51, 51));
        menu.setLayout(new java.awt.GridLayout(1, 0));

        menuMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Chat.png"))); // NOI18N
        menuMessage.setSelected(true);
        menuMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMessageActionPerformed(evt);
            }
        });
        menu.add(menuMessage);

        menuGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Group.png"))); // NOI18N
        menuGroup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        menuGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGroupActionPerformed(evt);
            }
        });
        menu.add(menuGroup);

        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        menuList.setBackground(new java.awt.Color(247, 247, 247));

        javax.swing.GroupLayout menuListLayout = new javax.swing.GroupLayout(menuList);
        menuList.setLayout(menuListLayout);
        menuListLayout.setHorizontalGroup(
            menuListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        menuListLayout.setVerticalGroup(
            menuListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 315, Short.MAX_VALUE)
        );

        sp.setViewportView(menuList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                    .addComponent(sp))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void menuMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMessageActionPerformed
        if(!menuMessage.isSelected()){
            menuMessage.setSelected(true);
            menuGroup.setSelected(false);
            reprintAllConversations();
        }
            
    }//GEN-LAST:event_menuMessageActionPerformed

    private void menuGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGroupActionPerformed
        if(!menuGroup.isSelected()){
            menuMessage.setSelected(false);
            menuGroup.setSelected(true);
            showGroup();
        }
    }//GEN-LAST:event_menuGroupActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane menu;
    private component.client.secondaries.MenuButton menuGroup;
    private javax.swing.JPanel menuList;
    private component.client.secondaries.MenuButton menuMessage;
    private javax.swing.JScrollPane sp;
    // End of variables declaration//GEN-END:variables
}
