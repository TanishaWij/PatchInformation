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
package org.wso2.OpenPatchInformation.Constants;

public final class EmailConstants {

    public static final String EMAIL_HEADER_INTERNAL = "<!DOCTYPE html>\n" +
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
    public static final String EMAIL_HEADER_EXTERNAL = "<!DOCTYPE html>\n" +
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
    public static final String EMAIL_SUBJECT_INTERNAL = "Information on internal Jira tickets and patches";
    public static final String EMAIL_SUBJECT_CUSTOMER = "Information on customer Jira tickets and patches";
    public static final String IN_QUEUE_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Patches In The Patch Queue</p>";
    public static final String IN_DEVELOPMENT_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Patches In Development</p>";
    public static final String IN_SIGNING_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px;" +
            " font-weight: 600; line-height: 26px; color: #000000;\">Patches Sent For Signing</p>";
    public static final String RELEASED_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Released Patches That Are Not Live Synced</p>";
    public static final String SUMMARY_SECTION_HEADER = "<br><p align=\"center\"style=\"font-size: 20px; " +
            "font-weight: 600; line-height: 26px; color: #000000;\">Jiras that are not Live synced</p>";

    public static final String EMAIL_TYPE = "text/html";
    public static final String EMAIL_PROTOCOL = "smtp";
    public static final String EMAIL_HOST = "smtp.gmail.com";
    public static final String EMAIL_PORT = "587";
    public static final String PORT = "mail.smtp.port";
    public static final String HOST = "mail.smtp.host";
    public static final String PROTOCOL = "protocol";

    public static final String STATE_TABLE_COLUMNS_START = "<table align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"95%\">" +
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
