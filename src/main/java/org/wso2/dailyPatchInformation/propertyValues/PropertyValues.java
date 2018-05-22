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
package org.wso2.dailyPatchInformation.propertyValues;

import org.wso2.dailyPatchInformation.PatchInformationMailSender;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Stores Property file values.
 */
public class PropertyValues {

    private static PropertyValues PROPERTY_VALUES;
    private String dbUser;
    private String dbPassword;
    private String pmtConnection;
    private String JIRAAuthentication;
    private String emailUser;
    private String toList;
    private String ccList;
    private String urlToCustomerIssuesFilter;
    private String urlToInternalIssuesFilter;

    private PropertyValues() throws IOException {

        Properties prop = new Properties();
        try (InputStream propertyFile =
                     PatchInformationMailSender.class.getResourceAsStream("/config.properties")) {
            prop.load(propertyFile);
        }

        this.dbUser = prop.getProperty("dbUser");
        this.dbPassword = prop.getProperty("dbPassword");
        this.pmtConnection = prop.getProperty("pmtConnection");
        this.JIRAAuthentication = prop.getProperty("JIRABasicAuth");
        this.emailUser = prop.getProperty("emailUser");
        this.toList = prop.getProperty("toList");
        this.ccList = prop.getProperty("ccList");
        this.urlToCustomerIssuesFilter = prop.getProperty("UrlToCustomerIssuesFilter");
        this.urlToInternalIssuesFilter = prop.getProperty("UrlToInternalIssuesFilter");
    }

    public static PropertyValues getPropertyValues() throws IOException {

        if (PROPERTY_VALUES == null) {
            PROPERTY_VALUES = new PropertyValues();
        }

        return PROPERTY_VALUES;
    }

    public String getDbUser() {

        return dbUser;
    }

    public String getDbPassword() {

        return dbPassword;
    }

    public String getPmtConnection() {

        return pmtConnection;
    }

    public String getJIRAAuthentication() {

        return JIRAAuthentication;
    }

    public String getEmailUser() {

        return emailUser;
    }

    public String getToList() {

        return toList;
    }

    public String getCcList() {

        return ccList;
    }

    public String getUrlToCustomerIssuesFilter() {

        return urlToCustomerIssuesFilter;
    }

    public String getUrlToInternalIssuesFilter() {

        return urlToInternalIssuesFilter;
    }
}
