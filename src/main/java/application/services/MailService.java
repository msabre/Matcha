package application.services;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MailService {

    private static final String PATH_TO_PROPERTIES = MailService.class.getResource("/mail.properties").getPath();

    private String userName;
    private final String password;

    private String text;
    private String subject;

    public MailService(String username, String passsword) {
        this.userName = username;
        this.password = passsword;
    }

    public void sendMail(String to) throws MessagingException {
        Properties prop = new Properties();

        FileInputStream fileInputStream = null;
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

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(userName));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);

            message.setText(text);

            Transport.send(message);

            System.out.println("Email Sent successfully....");

        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
