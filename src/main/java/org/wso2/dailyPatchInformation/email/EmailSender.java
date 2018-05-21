//
// Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
//

package org.wso2.dailyPatchInformation.email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.log4j.Logger;
import org.wso2.dailyPatchInformation.DailyPatchInfoMailSender;
import org.wso2.dailyPatchInformation.exceptions.EmailExceptions.EmailException;
import org.wso2.dailyPatchInformation.exceptions.EmailExceptions.EmailSendingException;
import org.wso2.dailyPatchInformation.exceptions.EmailExceptions.EmailSetupException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.wso2.dailyPatchInformation.constants.EmailConstants.APPLICATION_NAME;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.CLIENT_SECRET_DIR;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.CREDENTIALS_FOLDER;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.EMAIL_TYPE;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.JSON_FACTORY;
import static org.wso2.dailyPatchInformation.constants.EmailConstants.SCOPES;

/**
 * sends an email with the JIRA issues and associated patch information
 */
public class EmailSender {

    private final static Logger logger = Logger.getLogger(EmailSender.class);

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws EmailSetupException If there is no client_secret.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws EmailSetupException {
        // Load client secrets.
        InputStream in = DailyPatchInfoMailSender.class.getResourceAsStream(CLIENT_SECRET_DIR);
        GoogleClientSecrets clientSecrets;
        try {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        } catch (IOException e) {
            String errorMessage = "Failed to read clientSecret.json file in resources folder";
            logger.error(errorMessage, e);
            throw new EmailSetupException(errorMessage, e);
        }

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = null;
        try {
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                    .setAccessType("offline")
                    .build();
            //authorize
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException e) {
            String errorMessage = "Failed to set up folder to store user credentials";
            logger.error(errorMessage, e);
            throw new EmailSetupException(errorMessage, e);
        }
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param subject  subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws EmailSetupException
     */
    private static MimeMessage createEmail(String subject, String bodyText, String emailFrom, String emailTo, String emailCC) throws EmailSetupException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        try {
            email.setFrom(new InternetAddress(emailFrom));
            String[] toList = emailTo.split(",");
            for (String aToList : toList) {
                email.addRecipient(javax.mail.Message.RecipientType.TO,
                        new InternetAddress(aToList));
            }

            String[] ccList = emailCC.split(",");
            for (String aCcList : ccList) {
                email.addRecipient(javax.mail.Message.RecipientType.CC,
                        new InternetAddress(aCcList));
            }
        } catch (MessagingException e) {
            String errorMessage = "Failed to extract email Addresses in Properties file";
            logger.error(errorMessage, e);
            throw new EmailSetupException(errorMessage, e);
        }

        try {
            email.setSubject(subject);
            email.setContent(bodyText, EMAIL_TYPE);
        } catch (MessagingException e) {
            String errorMessage = "Failed to set email subject or body";
            logger.error(errorMessage, e);
            throw new EmailSetupException(errorMessage, e);
        }

        return email;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws EmailSetupException failed to create Message
     */
    private static Message createMessageWithEmail(MimeMessage emailContent) throws EmailSetupException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            emailContent.writeTo(buffer);
        } catch (IOException | MessagingException e) {
            String errorMessage = "Failed to extract email content from MimeMessage object";
            logger.error(errorMessage, e);
            throw new EmailSetupException(errorMessage, e);
        }

        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * * Send an email from the user's mailbox to its recipients.
     *
     * @param emailBody body of email
     * @param subject   subject of the email
     * @throws EmailException email was not sent
     */
    public static void sendMessage(String emailBody, String subject, String emailFrom, String emailTo, String emailCC) throws EmailException {
        // Build a new authorized API client service.
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            MimeMessage emailContent = createEmail(subject, emailBody, emailFrom, emailTo, emailCC);
            Message message = createMessageWithEmail(emailContent);
            try {
                service.users().messages().send("me", message).execute();
            } catch (IOException e) {
                String errorMessage = "Failed to execute the sending of the email";
                logger.error(errorMessage, e);
                throw new EmailSendingException(errorMessage, e);
            }

        } catch (GeneralSecurityException | IOException e) {
            String errorMessage = "Failed to set up new trusted transport";
            logger.error(errorMessage, e);
            throw new EmailSetupException(errorMessage, e);
        }

    }

}
