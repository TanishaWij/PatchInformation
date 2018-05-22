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
package org.wso2.patchinformation;

import org.apache.log4j.Logger;
import org.wso2.patchinformation.email.EmailBodyCreator;
import org.wso2.patchinformation.email.EmailSender;
import org.wso2.patchinformation.exceptions.EmailProcessException;
import org.wso2.patchinformation.jira.JIRAAccessor;
import org.wso2.patchinformation.jira.JIRAIssue;
import org.wso2.patchinformation.pmt.PmtAccessor;
import org.wso2.patchinformation.properties.PropertyValues;

import java.io.IOException;
import java.util.ArrayList;

import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_HTML_HEADER_CUSTOMER_RELATED;
import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_HTML_HEADER_INTERNAL;
import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_SUBJECT_CUSTOMER_RELATED;
import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_SUBJECT_INTERNAL;

/**
 * Sends 2 emails on behalf of the engineering efficiency team. One on Customer related JIRA issues,
 * and the other on internal JIRA issues and their corresponding patch information.
 */
public class MainEmailSender {

    private static final Logger LOGGER = Logger.getLogger(MainEmailSender.class);

    public static void main(String[] args) {

        try {
            LOGGER.info("Executing process to send email on Internal JIRA issues.");
            executeEmailSendingProcess(false);
            LOGGER.info("Execution completed successfully.\n");
        } catch (EmailProcessException e) {
            LOGGER.error("Execution failed, process was not completed\n", e);
        }
        try {
            LOGGER.info("Executing process to send email on Customer related JIRA issues.");
            executeEmailSendingProcess(true);
            LOGGER.info("Execution completed successfully.\n");
        } catch (EmailProcessException e) {
            LOGGER.error("Execution failed, process was not completed\n", e);
        }
    }

    /**
     * The process gets internal JIRA issues or customer related JIRA issues, and sends an email
     * containing information on them and their associated patches.
     *
     * @param isMailOnCustomerReportedIssues boolean to determine if it is the internal or customer related mail
     *                                       being sent.
     * @throws EmailProcessException The process execution has halted
     */
    private static void executeEmailSendingProcess(boolean isMailOnCustomerReportedIssues)
            throws EmailProcessException {

        PropertyValues propertyValues;
        try {
            propertyValues = PropertyValues.getPropertyValues();
        } catch (IOException e) {
            throw new EmailProcessException("Failed to read properties file", e);
        }
        String urlToJIRAFilter;
        String emailSubject;
        String emailHeaderHTML;
        if (isMailOnCustomerReportedIssues) {
            urlToJIRAFilter = propertyValues.getUrlToCustomerIssuesFilter();
            emailSubject = EMAIL_SUBJECT_CUSTOMER_RELATED;
            emailHeaderHTML = EMAIL_HTML_HEADER_CUSTOMER_RELATED;
        } else {
            urlToJIRAFilter = propertyValues.getUrlToInternalIssuesFilter();
            emailSubject = EMAIL_SUBJECT_INTERNAL;
            emailHeaderHTML = EMAIL_HTML_HEADER_INTERNAL;
        }
        ArrayList<JIRAIssue> jiraIssues;
        try {
            jiraIssues = new ArrayList<>(JIRAAccessor.getJiraAccessor().getIssues(urlToJIRAFilter,
                    propertyValues.getJiraAuthentication()));
            LOGGER.info("Successfully extracted JIRA issue information from JIRA.");
        } catch (EmailProcessException e) {
            String errorMessage = "Failed to extract JIRA issues from JIRA.";
            LOGGER.error(errorMessage, e);
            throw new EmailProcessException(errorMessage, e);
        }

        String emailBodyHTML;
        String pmtConnection = propertyValues.getPmtConnection();
        String pmtUserName = propertyValues.getDbUser();
        String pmtUserPassword = propertyValues.getDbPassword();
        PmtAccessor pmtAccessor = PmtAccessor.getPmtAccessor();
        try {
            ArrayList<JIRAIssue> jiraTicketsInPmtAndJIRA = new ArrayList<>(
                    pmtAccessor.filterJIRAIssues(jiraIssues, pmtConnection, pmtUserName, pmtUserPassword));
            pmtAccessor.populatePatches(jiraTicketsInPmtAndJIRA, pmtConnection, pmtUserName, pmtUserPassword);
            LOGGER.info("Successfully extracted patch information from the pmt.");
            emailBodyHTML = EmailBodyCreator.getEmailBodyCreator().getEmailBody(jiraTicketsInPmtAndJIRA,
                    emailHeaderHTML);
        } catch (EmailProcessException e) {
            String errorMessage = "Failed to extract Patch information from the pmt.";
            LOGGER.error(errorMessage, e);
            throw new EmailProcessException(errorMessage, e);
        }
        try {
            EmailSender.getEmailSender().sendMessage(emailBodyHTML, emailSubject, propertyValues.getEmailUser(),
                    propertyValues.getToList(), propertyValues.getCcList());
            LOGGER.info("Successfully sent email with patch information.");
        } catch (EmailProcessException e) {
            String errorMessage = "Failed to send email.";
            LOGGER.error(errorMessage, e);
            throw new EmailProcessException(errorMessage, e);
        }

    }

}
