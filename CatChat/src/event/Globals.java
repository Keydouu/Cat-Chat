package event;

import java.net.InetAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.Cipher;

public class Globals {

    public static String getMyEmail() {
        return myEmail;
    }
    public static void setMyEmail(String aMyEmail) {
        myEmail = aMyEmail.toLowerCase();
    }
    
    public static Globals instance;
    public static Socket socket;
    public static InetAddress ip;
    public static DataInputStream dis;
    public static DataOutputStream dos;
    public static Lock myWriteLock;
    private static String myEmail;
    private static PrivateKey privatekey;
    private static PublicKey publickey;
    private static SecretKey key;//HADI HIYA KEY LI GHANESTA3MEL
    public static PublicKey getpublic(){ return publickey;}
    public static PrivateKey getprivate(){ return privatekey;}
    public static SecretKey getSecfretKey(){ return key;}
    public static void initGlobals(){
        try{
            ip = InetAddress.getByName("localhost");
            socket = new Socket(ip, 5059);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            myWriteLock = new ReentrantLock();
            initRsa();
            String pblkey=Base64.getEncoder().encodeToString(publickey.getEncoded());
            dos.writeUTF(pblkey);
            System.out.println("public key : "+pblkey);
            String keyS = dis.readUTF();
            String keySdec=decryptRsa(keyS);
            System.out.println("AES key : "+keySdec);
            key = new SecretKeySpec(decode(keySdec), "AES");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void writeUTFEncoded(String s) throws IOException{
        dos.writeUTF(encrypt(s));
    }
    public static byte[] readFileDecoded(int size) throws IOException{
        byte[] b=new byte[size];
        dis.readFully(b);
        b=Base64.getDecoder().decode(decrypt(new String(b)));;
        return b;
    }
    public static void writeEncoded(byte[] b) throws IOException{
        b=encrypt(new String(Base64.getEncoder().encode(b))).getBytes(); 
        writeUTFEncoded(Integer.toString(b.length));
        dos.write(b);
    }
    private static String encrypt(String plainPwd){
        byte[] outputBytes = new byte[]{};
        String returnString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            outputBytes = cipher.doFinal(plainPwd.getBytes("utf-8"));
            if (null != outputBytes)
		returnString = encode(outputBytes);
            return returnString.trim();
        } catch (Exception e) {
            System.out.println(e);
        }
        return new String(outputBytes).trim();
    }

    public static String decrypt(String encryptedPwd) {
        byte[] outputBytes = new byte[]{};
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] inputBytes = decode(encryptedPwd);
            if (null != inputBytes) {
                outputBytes = cipher.doFinal(inputBytes);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return new String(outputBytes).trim();
    }
    public static void initRsa() throws Exception {
    	KeyPairGenerator generator=KeyPairGenerator.getInstance("RSA");
    	generator.initialize(1024);
    	KeyPair pair=generator.generateKeyPair();
    	privatekey=pair.getPrivate();
    	publickey=pair.getPublic();
    }
    public static Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }
    public static String decryptRsa(String message) throws Exception {
	byte[] encryptedBytes=decode(message);
	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	cipher.init(cipher.DECRYPT_MODE, privatekey);
	byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
	return new String(decryptedBytes);
    }
    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    
    private Globals() {

    }
}
