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

package org.wso2.patchinformation.email;

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
import org.wso2.patchinformation.MainEmailSender;
import org.wso2.patchinformation.exceptions.ConnectionException;
import org.wso2.patchinformation.exceptions.ContentException;
import org.wso2.patchinformation.exceptions.PatchInformationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.wso2.patchinformation.constants.EmailConstants.APPLICATION_NAME;
import static org.wso2.patchinformation.constants.EmailConstants.CLIENT_SECRET_DIR;
import static org.wso2.patchinformation.constants.EmailConstants.CREDENTIALS_FOLDER;
import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_TYPE;
import static org.wso2.patchinformation.constants.EmailConstants.JSON_FACTORY;
import static org.wso2.patchinformation.constants.EmailConstants.SCOPES;

/**
 * Sends an email with the JIRA issues and associated patch information.
 */
public class EmailSender {

    private static EmailSender emailSender;

    private EmailSender() {
    }

    public static EmailSender getEmailSender() {
        if (emailSender == null) {
            emailSender = new EmailSender();
        }
        return emailSender;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If there is no client_secret.
     */
    private Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {

        InputStream in = MainEmailSender.class.getResourceAsStream(CLIENT_SECRET_DIR);
        GoogleClientSecrets clientSecrets;
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in,
                Charset.defaultCharset()));
        GoogleAuthorizationCodeFlow flow;
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File((CREDENTIALS_FOLDER))))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param subject  subject of the email.
     * @param bodyText body text of the email.
     * @return the MimeMessage to be used to send email.
     * @throws ContentException email was not created.
     */
    private MimeMessage createEmail(String subject, String bodyText, String emailFrom, String emailTo, String emailCC)
            throws ContentException {

        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);
            email.setFrom("Engineering Efficiency <" + emailFrom + ">");
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
            email.setSubject(subject);
            email.setContent(bodyText, EMAIL_TYPE);
            return email;
        } catch (MessagingException e) {
            throw new ContentException("Failed to set up email", e);
        }
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent email to be set to raw of message.
     * @return a message containing a base64url encoded email.
     * @throws ContentException failed to create Message.
     */
    private Message createMessageWithEmail(MimeMessage emailContent) throws ContentException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            emailContent.writeTo(buffer);
        } catch (IOException | MessagingException e) {
            throw new ContentException("Failed to extract email content from MimeMessage object", e);
        }
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Sends an email from the user's mailbox to its recipients.
     *
     * @param emailBody body of email.
     * @param subject   subject of the email.
     * @throws PatchInformationException email was not sent.
     */
    public void sendMessage(String emailBody, String subject, String emailFrom, String emailTo, String emailCC)
            throws PatchInformationException {

        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            MimeMessage emailContent = createEmail(subject, emailBody, emailFrom, emailTo, emailCC);
            Message message = createMessageWithEmail(emailContent);
            service.users().messages().send("me", message).execute();
        } catch (GeneralSecurityException | IOException e) {
            throw new ConnectionException("Failed to send email", e);
        }
    }
}
