package login;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private Properties properties = new Properties();

    public Config() {
    	
    	// resources 안의 application.properties 읽음
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUser() {
        return properties.getProperty("db.user");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }
    
    public String getEmail() {
        return properties.getProperty("mail.email");
    }
    
    public String getEmailPW() {
        return properties.getProperty("mail.mailpw");
    }
    
    public String getOtpKey() {
        return properties.getProperty("OTPkey");
    }
}
