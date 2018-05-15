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
package org.wso2.OpenPatchInformation.JiraData;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.wso2.OpenPatchInformation.ConfiguredProperties;
import org.wso2.OpenPatchInformation.Constants.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

/**
 * connects to JiraIssue and extracts the data returned from the filter
 */
public class AccessJira {

    final static Logger logger = Logger.getLogger(AccessJira.class);

    /**
     * Extracts the Data needed from the filter URl response
     *
     * @param url of the JiraIssue filter
     * @throws Exception
     */
    public ArrayList<JiraIssue> getJirasReturnedBy(String url) throws Exception {
        //gets response from JiraIssue filter and parse it into a Json object
        String responseFromFilter = sendJiraGETRequest(new URL(url));
        JSONParser parser = new JSONParser();
        Object responseObject = parser.parse(responseFromFilter);
        JSONObject jsonObject = (JSONObject) responseObject;
        //get results from search URL and parse into Json
        String filterSearchURL = jsonObject.get(Constants.SEARCH_URL).toString();
        String responseFromSearchUrl = sendJiraGETRequest(new URL(filterSearchURL));
        Object responseObjectFromSearchURL = parser.parse(responseFromSearchUrl);
        JSONObject jsonObjectFromSearchURL = (JSONObject) responseObjectFromSearchURL;

        int totalJiras = Integer.parseInt(jsonObjectFromSearchURL.get(Constants.TOTAL).toString());
        //TODO: create new method

        //paging the JiraIssue response
        ArrayList<JiraIssue> jiraIssues = null;
        for (int i = 0; i <= totalJiras / Constants.PAGE_SIZE; i++) {
            String responseFromSplitSearchUrl = sendJiraGETRequest(new URL(filterSearchURL +
                    "&startAt=" + (i * Constants.PAGE_SIZE) + "&maxResults=" +
                    (i + 1) * Constants.PAGE_SIZE + "&fields=key,assignee"));
            Object responseObjectFromSplitSearchURL = parser.parse(responseFromSplitSearchUrl);
            JSONObject jsonObjectFromSplitSearchURL = (JSONObject) responseObjectFromSplitSearchURL;
            JSONArray issues = (JSONArray) jsonObjectFromSplitSearchURL.get(Constants.ISSUE);
            jiraIssues = new ArrayList<>();
            for (Object issue : issues) {
                try {
                    JSONObject issueInJson = (JSONObject) issue;
                    JSONObject fields = (JSONObject) issueInJson.get(Constants.FIELDS);
                    JSONObject assignee = (JSONObject) fields.get(Constants.ASSIGNEE);
                    //create new JiraIssue
                    jiraIssues.add(new JiraIssue(issueInJson.get(Constants.JIRA_KEY).toString(), assignee.get(Constants.EMAIL).toString()));

                } catch (Exception e) {
                    //logger.error("Could not extract requested field data from JiraIssue");
                }
            }
        }
        return jiraIssues;
    }

    /**
     * sends JiraIssue get request
     *
     * @param url to send get request to
     * @return The response from the get request as a String
     * @throws Exception
     */
    private String sendJiraGETRequest(URL url) throws Exception {

        HttpURLConnection connection = null;
        BufferedReader dataInputStream = null;
        try {
            //open and set connection values
            connection = (HttpsURLConnection) url.openConnection();
            String userCredentials = ConfiguredProperties.getValueOf("jiraUser") + ":" + ConfiguredProperties.getValueOf("jiraPassword");
            String basicAuth = Constants.AUTH_TYPE +
                    Base64.encode(userCredentials.getBytes());
            connection.setRequestProperty(Constants.AUTH, basicAuth);
            connection.setRequestProperty(Constants.CONTENT, Constants.CONTENT_TYPE);
            connection.setRequestMethod(Constants.REQUEST_METHOD);
            if (connection.getResponseCode() == 200) {
                dataInputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = dataInputStream.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                        connection.getErrorStream()));
                while (bufferedReader.ready()) {
                    stringBuilder.append(bufferedReader.readLine());
                }
                return stringBuilder.toString();
            }
        } catch (IOException e) {
            //logger.error("Url connection issue", e);
            throw new Exception("Url connection issue", e);
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    //logger.error("Error closing Buffered Reader");
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}











