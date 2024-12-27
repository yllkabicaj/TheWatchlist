package com.example.thewatchlist.auth;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    private static final String TAG = "EmailUtil";

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL = "y.bicaj@gmail.com";
    private static final String PASSWORD = "pcga nmcw bkoi gkcw";

    public static void sendEmail(String toEmail, String subject, String body) {
        new SendEmailTask().execute(toEmail, subject, body);
    }

    private static class SendEmailTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String toEmail = params[0];
            String subject = params[1];
            String body = params[2];

            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", SMTP_HOST);
                props.put("mail.smtp.port", SMTP_PORT);
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL, PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                Log.d(TAG, "Email sent successfully to " + toEmail);

            } catch (Exception e) {
                Log.e(TAG, "Error sending email", e);
            }
            return null;
        }
    }
}
