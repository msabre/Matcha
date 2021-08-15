package application.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

public class MailService {

    private static String PATH_TO_PROPERTIES = "";

    static {
        try {
            PATH_TO_PROPERTIES = Paths.get(MailService.class.getResource("/mail.properties").toURI()).toFile().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private final String userName;
    private final String password;

    private String text;
    private String subject;

    public MailService(String username, String passsword) {
        this.userName = username;
        this.password = passsword;
    }

    public void sendMail(String to) throws MessagingException {
        Properties prop = new Properties();

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userName));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);

        System.out.println("Email Sent successfully....");
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
