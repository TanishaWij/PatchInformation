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
package org.wso2.patchinformation.jira;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.patchinformation.constants.Constants;
import org.wso2.patchinformation.exceptions.ConnectionException;
import org.wso2.patchinformation.exceptions.ContentException;
import org.wso2.patchinformation.exceptions.PatchInformationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

import static org.wso2.patchinformation.constants.Constants.DATE_CREATED;
import static org.wso2.patchinformation.constants.Constants.NAME;
import static org.wso2.patchinformation.constants.Constants.OK;
import static org.wso2.patchinformation.constants.Constants.RESULTS_PER_PAGE;

/**
 * Connects to JIRAIssue and extracts the data returned from the filter.
 */
public class JIRAAccessor {

    private static JIRAAccessor jiraAccessor;

    private JIRAAccessor() {
    }

    public static JIRAAccessor getJiraAccessor() {
        if (jiraAccessor == null) {
            jiraAccessor = new JIRAAccessor();
        }
        return jiraAccessor;
    }

    /**
     * Returns an ArrayList of JIRAIssue objects after applying a JIRA filter.
     *
     * @param jiraFilter url to JIRA filter results.
     * @return ArrayList of JIRA Issues.
     * @throws PatchInformationException JIRAs not extracted successfully.
     */
    public ArrayList<JIRAIssue> getIssues(String jiraFilter, String authorizationValue) throws
            PatchInformationException {

        try {
            String jiraResponse = sendJIRARequest(new URL(jiraFilter), authorizationValue);
            JSONParser jsonParser = new JSONParser();
            JSONObject jiraResponseInJson = (JSONObject) jsonParser.parse(jiraResponse);
            //get results from search URL and parse into Json
            String urlToFilterResults = jiraResponseInJson.get(Constants.SEARCH_URL).toString();
            String responseFromSearchUrl = sendJIRARequest(new URL(urlToFilterResults), authorizationValue);
            JSONObject responseFromSearchUrlInJson = (JSONObject) jsonParser.parse(responseFromSearchUrl);
            int totalJIRAs = Integer.parseInt(responseFromSearchUrlInJson.get(Constants.TOTAL).toString());
            return getIssuesFromFilter(urlToFilterResults, totalJIRAs, authorizationValue);
        } catch (MalformedURLException e) {
            throw new ConnectionException("Url defined to access JIRA is malformed", e);
        } catch (ParseException e) {
            throw new ContentException("Failed to parse JIRA response String to Json", e);
        }
    }

    /**
     * Pages the JIRA response and returns an ArrayList of JIRAIssue objects.
     *
     * @param urlToFilterResults  url to get JIRA results.
     * @param totalJIRAs         Number of JIRA results returned by the filter.
     * @return ArrayList of JIRAIssues.
     * @throws PatchInformationException JIRA data not extracted successfully.
     */
    private ArrayList<JIRAIssue> getIssuesFromFilter(String urlToFilterResults, int totalJIRAs,
                                                     String authorizationValue) throws PatchInformationException {

        ArrayList<JIRAIssue> jiraIssues = new ArrayList<>();
        for (int i = 0; i <= totalJIRAs / RESULTS_PER_PAGE; i++) { //paging the JIRAIssue response
            try {
                String responseFromSplitSearchUrl = sendJIRARequest(new URL(urlToFilterResults +
                        "&startAt=" + (i * RESULTS_PER_PAGE) + "&maxResults=" +
                        (i + 1) * RESULTS_PER_PAGE + "&fields=key,assignee,created,status"), authorizationValue);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObjectFromSplitSearchURL = (JSONObject) jsonParser.parse(responseFromSplitSearchUrl);

                JSONArray issues = (JSONArray) jsonObjectFromSplitSearchURL.get(Constants.ISSUES);
                for (Object issue : issues) {
                    try {
                        JSONObject issueInJSON = (JSONObject) issue;
                        JSONObject fieldsInJSON = (JSONObject) issueInJSON.get(Constants.FIELDS);
                        JSONObject assigneeInJSON = (JSONObject) fieldsInJSON.get(Constants.ASSIGNEE);
                        JSONObject statusInJSON = (JSONObject) fieldsInJSON.get(Constants.STATUS);
                        //create new JIRAIssue
                        jiraIssues.add(new JIRAIssue(issueInJSON.get(Constants.JIRA_KEY).toString(),
                                assigneeInJSON.get(Constants.EMAIL).toString(),
                                fieldsInJSON.get(DATE_CREATED).toString(), statusInJSON.get(NAME).toString()));
                    } catch (NullPointerException e) {
                        throw new ContentException("Failed to extract JIRA issue's field data", e);
                    }
                }
            } catch (MalformedURLException e) {
                throw new ConnectionException("Url defined to access JIRA is malformed", e);
            } catch (ParseException e) {
                throw new ContentException("Failed to parse JIRA response string to Json", e);
            }
        }
        return jiraIssues;
    }

    /**
     * Returns the response returned by a http call as a String.
     *
     * @param url to which the http get request is sent.
     * @return http response as a String.
     * @throws PatchInformationException Failed to connect to JIRA and return the http response as a String.
     */
    private String sendJIRARequest(URL url, String authorizationValue) throws PatchInformationException {

        HttpURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty(Constants.AUTH, authorizationValue);
            connection.setRequestProperty(Constants.CONTENT, Constants.CONTENT_TYPE);
            connection.setRequestMethod(Constants.GET);
            if (connection.getResponseCode() == OK) {
                try (BufferedReader dataInputStream = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), Charset.defaultCharset()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = dataInputStream.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();
                } catch (IOException e) {
                    throw new ContentException("Failed to read from JIRA Response Stream", e);
                }
            } else {
                String errorMessage = "Failed to get expected JIRA response, response code: " +
                        connection.getResponseCode() + " returned";
                throw new ConnectionException(errorMessage);
            }
        } catch (IOException e) {
            throw new ConnectionException("Failed to connect to Jira", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}











