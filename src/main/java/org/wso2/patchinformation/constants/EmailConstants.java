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
package org.wso2.patchinformation.constants;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.GmailScopes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Email constant values
 */
public final class EmailConstants {

    public static final String APPLICATION_NAME = "Patch Information Emailer";
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String CREDENTIALS_FOLDER = "gmailCredentials";
    public static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    public static final String CLIENT_SECRET_DIR = "/clientSecret.json";

    public static final String EMAIL_HEADER_INTERNAL =
            "<html>\n" +
                    "   <head>\n" +
                    "      <title></title>\n" +
                    "   </head>\n" +
                    "   <body>\n" +
                    "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" " +
                    "style=\"max-width:100%;\">\n" +
                    "      <tr>\n" +
                    "         <td align=\"center\" style=\"font-family: Helvetica, Arial, sans-serif; font-size:" +
                    " 18px;" +
                    " font-weight: 400; line-height: 15px; padding-top: 0px;\">\n" +
                    "            <p style=\"font-size: 30px; font-weight: 600; line-height: 26px; color: #000000;\">" +
                    "Ongoing Internal Patches as of " + LocalDate.now() + " - ";

    public static final String EMAIL_HEADER_END =  "</p>\n</td>\n" +
            "      </tr>\n" +
            "      </table>";

    public static final String EMAIL_HEADER_CUSTOMER_RELATED =
            "<html>\n" +
                    "   <head>\n" +
                    "      <title></title>\n" +
                    "   </head>\n" +
                    "   <body>\n" +
                    "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" " +
                    "style=\"max-width:100%;\">\n" +
                    "      <tr>\n" +
                    "         <td align=\"center\" style=\"font-family: Helvetica, Arial, sans-serif; font-size: " +
                    "18px;" +
                    " font-weight: 400; line-height: 15px; padding-top: 0px;\">\n" +
                    "            <p style=\"font-size: 24px; font-weight: 600; line-height: 26px; color: #000000;\">" +
                    "Ongoing Customer Related Patches as of " + LocalDate.now() + ": ";
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
    public static final String COLUMN_NAMES_SUMMARY = "<table align=\"center\" cellspacing=\"0\" cellpadding=\"0\"" +
            " border=\"0\" width=\"95%\">" +
            "<tr>" +
            " <td width=\"30%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "JIRAIssue" +
            " </td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "Assignee" +
            "</td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "NumberOfOpenPatches" +
            "</td>" +
            " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px;" +
            " padding: 10px;\">" +
            "DateReported" +
            "</td>" +
            "</tr>";

    public static final String COLUMN_NAMES_RELEASED = "<table align=\"center\" cellspacing=\"0\" cellpadding=\"0\"" +
            " border=\"0\" width=\"95%\">" +
            "<tr>" +
            " <td width=\"30%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "JIRAIssue" +
            " </td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "Assignee" +
            "</td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "NumberOfReleasedPatches" +
            "</td>" +
            " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px;" +
            " padding: 10px;\">" +
            "DateReported" +
            "</td>" +
            "</tr>";

    public static final String EMAIL_SUBJECT_INTERNAL = "[Ongoing Patches][Internal] Internal Patch Information: " +
            LocalDate.now();
    public static final String EMAIL_SUBJECT_CUSTOMER_RELATED = "[Ongoing Patches][Customer Related] Customer " +
            "Related Patch Information: " + LocalDate.now();
    public static final String IN_QUEUE_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">New Patches in Queue</p>";
    public static final String DEV_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Patches in Development</p>";
    public static final String IN_SIGNING_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px;" +
            " font-weight: 600; line-height: 26px; color: #000000;\">Patches Sent for Signing</p>";
    public static final String RELEASED_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\"> Released Patches with an Unresolved" +
            " JIRA Issue</p>";
    public static final String SUMMARY_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Summary of Patch Related JIRAs</p>";
    public static final String EMAIL_TYPE = "text/html";
    public static final String COLUMN_NAMES = "<table align=\"center\" cellspacing=\"0\" " +
            "cellpadding=\"0\" border=\"0\" width=\"95%\">" +
            "<tr>" +
            " <td width=\"30%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
            " 20px; padding: 10px;\">" +
            "JIRAIssue" +
            " </td>" +
            "<td width=\"20%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "PatchName" +
            "</td>" +
            "<td width=\"15%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: " +
            "20px; padding: 10px;\">" +
            "Product" +
            "</td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">" +
            "Assignee" +
            "</td>" +
            " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
            " 20px; padding: 10px;\">" +
            "State </td>" +
            "<td width=\"15%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
            " 20px; padding: 10px;\">";
    public static final String COLUMN_NAMES_DEV = "<table align=\"center\" cellspacing=\"0\" " +
            "cellpadding=\"0\" border=\"0\" width=\"95%\">" +
            "<tr><td width=\"20%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\"> JIRAIssue</td><td width=\"20%\" align=\"center\" color=\"#044767\"" +
            " bgcolor=\"#bebebe\" style=\"font-family: Open Sans," +
            " Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\">PatchName" +
            "</td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans,Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\"> Product </td>" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
            " Open Sans, " +
            "Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; padding: " +
            "10px;\"> Assignee </td>" +
            " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
            "padding: 10px;\"> " +
            "State </td>" + "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" " +
            "style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; " +
            "line-height: 20px; padding: 10px;\">" +
            "ReportedDate" +
            "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
            "Open Sans, " +
            "Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: " +
            "20px; padding: 10px;\">";

    private EmailConstants() {

    }
}
