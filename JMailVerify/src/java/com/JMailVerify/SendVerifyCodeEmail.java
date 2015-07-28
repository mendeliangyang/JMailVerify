/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.JMailVerify;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Administrator
 */
public class SendVerifyCodeEmail implements Runnable {

    public EmailVerifyModel verifyModel = null;

    SendVerifyCodeEmail(EmailVerifyModel model) {
        verifyModel = model;
    }

    @Override
    public void run() {
        // Get system properties
        Properties properties = new Properties(); // System.getProperties();
        // Setup mail server
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.host", EmailCommon.systemSetModel.EmailHost);

        // Get the default Session object.
        // Session session = Session.getDefaultInstance(properties);
        Session session = Session.getInstance(properties, new Authenticator() { // 策略模式
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailCommon.systemSetModel.EmailMaster, EmailCommon.systemSetModel.EmailMasterPwd);
            }
        });
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(EmailCommon.systemSetModel.EmailMaster));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(verifyModel.Email));

            // Set Subject: header field
            message.setSubject(EmailCommon.systemSetModel.EmailTitle);

            // Now set the actual message
            message.setText(verifyModel.verifyCode);

            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            EmailCommon.logError("send email error.", mex);
        }
    }

}
