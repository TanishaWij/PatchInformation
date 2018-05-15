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

package org.wso2.OpenPatchInformation.Email;

import org.wso2.OpenPatchInformation.Exceptions.EmailExceptions.EmailException;
import org.wso2.OpenPatchInformation.Exceptions.EmailExceptions.MessageSendingException;
import org.wso2.OpenPatchInformation.Exceptions.EmailExceptions.MessageSetupException;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.wso2.OpenPatchInformation.ConfiguredProperties.getValueOf;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.EMAIL_HOST;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.EMAIL_PORT;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.EMAIL_PROTOCOL;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.EMAIL_TYPE;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.HOST;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.PORT;
import static org.wso2.OpenPatchInformation.Constants.EmailConstants.PROTOCOL;

/**
 * Sends a smtp email
 */
public class EmailSender {

    /**
     * sends the email
     *
     * @param emailBody the body of the email
     */
    public void sendEmail(String emailBody, String subject) throws EmailException {
        //set property values for email
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put(HOST, EMAIL_HOST);
        prop.put(PORT, EMAIL_PORT);
        prop.put(PROTOCOL, EMAIL_PROTOCOL);
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(getValueOf("emailUser"),
                        getValueOf("emailPassword"));
            }
        });
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(getValueOf("emailUser")));

            String[] toList = getValueOf("toList").split(",");
            for (String aToList : toList) {
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(aToList));
            }

            String[] ccList = getValueOf("ccList").split(",");
            for (String aCcList : ccList) {
                message.addRecipient(Message.RecipientType.CC,
                        new InternetAddress(aCcList));
            }

        } catch (MessagingException e) {
            throw new MessageSetupException("Check email Addresses in Properties file", e);
        }
        try {
            message.setSubject(subject);
            message.setContent(emailBody, EMAIL_TYPE);
        } catch (MessagingException e) {
            throw new MessageSetupException("Error setting up email body", e);
        }

        Transport transport;
        try {
            transport = session.getTransport(EMAIL_PROTOCOL);
        } catch (NoSuchProviderException e) {
            throw new MessageSetupException("Check email protocol value in Properties file", e);
        }
        try {
            transport.connect(EMAIL_HOST, getValueOf("emailUser"),
                    getValueOf("emailPassword"));
            Transport.send(message);
        } catch (MessagingException e) {
            throw new MessageSendingException("Error sending email", e);
        }

    }
}

