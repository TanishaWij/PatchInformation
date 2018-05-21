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
package org.wso2.dailyPatchInformation.constants;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.GmailScopes;

import java.util.Collections;
import java.util.List;

public final class EmailConstants {

    public static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String CREDENTIALS_FOLDER = "src/main/resources/gmailCredentials";
    public static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    public static final String CLIENT_SECRET_DIR = "/clientSecret.json";
    public static final String EMAIL_HTML_HEADER_INTERNAL = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "   <head>\n" +
            "      <title></title>\n" +
            "   </head>\n" +
            "   <body>\n" +
            "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" " +
            "style=\"max-width:100%;\">\n" +
            "      <tr>\n" +
            "         <td align=\"center\" style=\"font-family: Helvetica, Arial, sans-serif; font-size: 18px;" +
            " font-weight: 400; line-height: 15px; padding-top: 0px;\">\n" +
            "            <p style=\"font-size: 24px; font-weight: 600; line-height: 26px; color: #000000;\">Daily " +
            "Update On Internal Patches</p>\n" +
            "         </td>\n" +
            "      </tr>\n" +
            "      </table>";
    public static final String EMAIL_HTML_HEADER_CUSTOMER_RELATED = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "   <head>\n" +
            "      <title></title>\n" +
            "   </head>\n" +
            "   <body>\n" +
            "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" " +
            "style=\"max-width:100%;\">\n" +
            "      <tr>\n" +
            "         <td align=\"center\" style=\"font-family: Helvetica, Arial, sans-serif; font-size: 18px;" +
            " font-weight: 400; line-height: 15px; padding-top: 0px;\">\n" +
            "            <p style=\"font-size: 24px; font-weight: 600; line-height: 26px; color: #000000;\">Daily" +
            " Update On External Patches</p>\n" +
            "         </td>\n" +
            "      </tr>\n" +
            "      </table>";
    public static final String EMAIL_FOOTER = "<br><br><table align=\"center\" border=\"0\" cellpadding=\"0\" " +
            "cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\n" +
            "   <tr>\n" +
            "      <td align=\"center\">                           \n" +
            "         <img src=\"https://upload.wikimedia.org/wikipedia/en/5/56/WSO2_Software_Logo.png\" " +
            "width=\"90\" height=\"37\" style=\"display: block; border: 0px;\"/>                        \t  \n" +
            "      </td>\n" +
            "   </tr>\n" +
            "   <tr>\n" +
            "      <td align=\"center\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; " +
            "font-size: 14px; font-weight: 400; line-height: 24px;\">\n" +
            "         <p style=\"font-size: 14px; font-weight: 400; line-height: 20px;" +
            " color: #777777;\">Copyright (c) 2018 | WSO2 Inc.<br/>All Right Reserved.                 " +
            "                     \t\t   </p>\n" +
            "      </td>\n" +
            "   </tr>\n" +
            "</table>\n" +
            "</body></html>\n";
    public static final String TABLE_HEADER_SUMMARY = "<table align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"95%\">" +
            "<tr>" +
            " <td width=\"30%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "JIRA LINK" +
            " </td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "ASSIGNEE" +
            "</td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "NUMBER OF OPEN PATCHES" +
            "</td>" +
            " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px;" +
            " padding: 10px;\">" +
            "REPORT DATE" +
            "</td>" +
            "</tr>";
    public static final String EMAIL_SUBJECT_INTERNAL = "Information on internal JIRA tickets and patches";
    public static final String EMAIL_SUBJECT_CUSTOMER_RELATED = "Information on Customer related JIRA tickets and patches";
    public static final String IN_QUEUE_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Patches In The Patch Queue</p>";
    public static final String IN_DEVELOPMENT_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Patches In Development</p>";
    public static final String IN_SIGNING_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px;" +
            " font-weight: 600; line-height: 26px; color: #000000;\">Patches Sent For Signing</p>";
    public static final String RELEASED_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Released Patches That Are Not Live Synced</p>";
    public static final String SUMMARY_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Summary of patch related JIRA issues in development</p>";

    public static final String EMAIL_TYPE = "text/html";

    public static final String STATE_TABLE_COLUMNS_START = "<table align=\"center\" cellspacing=\"0\" " +
            "cellpadding=\"0\" border=\"0\" width=\"95%\">" +
            "<tr>" +
            " <td width=\"30%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
            " 20px; padding: 10px;\">" +
            "JIRA LINK" +
            " </td>" +
            "<td width=\"20%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "PATCH NAME" +
            "</td>" +
            "<td width=\"15%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: " +
            "20px; padding: 10px;\">" +
            "PRODUCT" +
            "</td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "ASSIGNEE" +
            "</td>" +
            " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
            " 20px; padding: 10px;\">" +
            "STATE </td>" +
            "<td width=\"15%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
            " 20px; padding: 10px;\">";
    public static final String STATE_TABLE_COLUMNS_END = "</td></tr>";
    public static final String DEV_STATE_TABLE_COLUMNS_START = "<table align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"95%\">" +
            "<tr><td width=\"20%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\"> JIRA LINK</td><td width=\"20%\" align=\"center\" color=\"#044767\"" +
            " bgcolor=\"#bebebe\" style=\"font-family: Open Sans," +
            " Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">PATCH NAME" +
            "</td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans,Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\"> PRODUCT </td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, " +
            "Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; padding: " +
            "10px;\"> ASSIGNEE </td>" +
            " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\"> " +
            "STATE </td>" + "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" " +
            "style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; " +
            "line-height: 20px; padding: 10px;\">" +
            "REPORTED DATE" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, " +
            "Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: " +
            "20px; padding: 10px;\">";

    private EmailConstants() {

    }

}
