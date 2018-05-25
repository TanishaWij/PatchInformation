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
import org.wso2.patchinformation.email.EmailContentCreator;
import org.wso2.patchinformation.email.EmailSender;
import org.wso2.patchinformation.exceptions.PatchInformationException;
import org.wso2.patchinformation.jira.JIRAAccessor;
import org.wso2.patchinformation.jira.JIRAIssue;
import org.wso2.patchinformation.pmt.PmtAccessor;
import org.wso2.patchinformation.properties.PropertyValues;

import java.io.IOException;
import java.util.ArrayList;

import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_SUBJECT_CUSTOMER;
import static org.wso2.patchinformation.constants.EmailConstants.EMAIL_SUBJECT_INTERNAL;
import static org.wso2.patchinformation.constants.EmailConstants.MAIN_HEADER_CUSTOMER;
import static org.wso2.patchinformation.constants.EmailConstants.MAIN_HEADER_END;
import static org.wso2.patchinformation.constants.EmailConstants.MAIN_HEADER_INTERNAL;
/**
 * Sends 2 emails on behalf of the engineering efficiency team. One on Customer related JIRA issues,
 * and the other on Proactive JIRA issues and their corresponding patch information.
 */
public class MainEmailSender {

    private static final Logger LOGGER = Logger.getLogger(MainEmailSender.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("Executing process to send email on Internal JIRA issues.");
            executeEmailSendingProcess(false);
            LOGGER.info("Execution completed successfully.\n");
        } catch (PatchInformationException e) {
            LOGGER.error("Execution failed, process was not completed\n", e);
        }
        try {
            LOGGER.info("Executing process to send email on Customer related JIRA issues.");
            executeEmailSendingProcess(true);
            LOGGER.info("Execution completed successfully.\n");
        } catch (PatchInformationException e) {
            LOGGER.error("Execution failed, process was not completed\n", e);
        }
    }

    /**
     * The process gets internal JIRA issues or customer related JIRA issues, and sends an email
     * containing information on them and their associated patches.
     *
     * @param isMailOnCustomerReportedIssues boolean to determine if it is the internal or customer related mail
     *                                       being sent.
     * @throws PatchInformationException The process execution has halted.
     */
    private static void executeEmailSendingProcess(boolean isMailOnCustomerReportedIssues)
            throws PatchInformationException {
        PropertyValues propertyValues;
        try {
            propertyValues = PropertyValues.getPropertyValues();
        } catch (IOException e) {
            throw new PatchInformationException("Failed to read properties file", e);
        }
        //set values dependent on email type.
        String urlToJIRAFilter;
        String emailSubject;
        String emailHeaderHTML;
        if (isMailOnCustomerReportedIssues) {
            urlToJIRAFilter = propertyValues.getUrlToCustomerIssuesFilter();
            emailSubject = EMAIL_SUBJECT_CUSTOMER;
            emailHeaderHTML = MAIN_HEADER_CUSTOMER;
        } else {
            urlToJIRAFilter = propertyValues.getUrlToInternalIssuesFilter();
            emailSubject = EMAIL_SUBJECT_INTERNAL;
            emailHeaderHTML = MAIN_HEADER_INTERNAL;
        }
        //access Jira.
        ArrayList<JIRAIssue> jiraIssues;
        try {
            jiraIssues = new ArrayList<>(JIRAAccessor.getJiraAccessor().getIssues(urlToJIRAFilter,
                    propertyValues.getJiraAuthentication()));
            LOGGER.info("Successfully extracted JIRA issue information from JIRA.");
            emailHeaderHTML += jiraIssues.size() + MAIN_HEADER_END;
        } catch (PatchInformationException e) {
            String errorMessage = "Failed to extract JIRA issues from JIRA.";
            LOGGER.error(errorMessage, e);
            throw new PatchInformationException(errorMessage, e);
        }
        //access the pmt.
        String emailBodyHTML;
        String pmtConnection = propertyValues.getPmtConnection();
        String pmtUserName = propertyValues.getDbUser();
        String pmtUserPassword = propertyValues.getDbPassword();
        PmtAccessor pmtAccessor = PmtAccessor.getPmtAccessor();
        try {
            ArrayList<JIRAIssue> jiraIssuesInPmtAndJIRA = new ArrayList<>(
                    pmtAccessor.filterJIRAIssues(jiraIssues, pmtConnection, pmtUserName, pmtUserPassword));
            pmtAccessor.populatePatches(jiraIssuesInPmtAndJIRA, pmtConnection, pmtUserName, pmtUserPassword);
            LOGGER.info("Successfully extracted patch information from the pmt.");
            emailBodyHTML = EmailContentCreator.getEmailContentCreator().getEmailBody(jiraIssues,
                    emailHeaderHTML);
        } catch (PatchInformationException e) {
            String errorMessage = "Failed to extract OpenPatch information from the pmt.";
            LOGGER.error(errorMessage, e);
            throw new PatchInformationException(errorMessage, e);
        }
        //send mail
        try {
            EmailSender.getEmailSender().sendMessage(emailBodyHTML, emailSubject, propertyValues.getEmailUser(),
                    propertyValues.getToList(), propertyValues.getCcList());
            LOGGER.info("Successfully sent email with patch information.");
        } catch (PatchInformationException e) {
            String errorMessage = "Failed to send email.";
            LOGGER.error(errorMessage, e);
            throw new PatchInformationException(errorMessage, e);
        }
    }
}
