package main;

public class ModelUser {

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the password2
     */
    public String getPassword2() {
        return password2;
    }

    /**
     * @param password2 the password2 to set
     */
    public void setPassword2(String password2) {
        this.password2 = password2;
    }
    
    public ModelUser(String userName, String email, String password,String password2) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.password2 = password2;
    }
    
    public ModelUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public ModelUser() {
    }

    private String userName;
    private String email;
    private String password;
    private String password2;
}
