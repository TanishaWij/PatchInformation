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

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.patchinformation.constants.Constants;
import org.wso2.patchinformation.exceptions.jira.JIRAConnectionException;
import org.wso2.patchinformation.exceptions.jira.JIRAContentException;
import org.wso2.patchinformation.exceptions.jira.JIRAException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

import static org.wso2.patchinformation.constants.Constants.OK;
import static org.wso2.patchinformation.constants.Constants.RESULTS_PER_PAGE;

/**
 * connects to JIRAIssue and extracts the data returned from the filter
 */
public class JIRAAccessor {

    private static final Logger LOGGER = Logger.getLogger(JIRAAccessor.class);
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
     * Returns an Arraylist of JIRAIssue objects containing the data returned after applying the JIRA filter
     *
     * @param jiraFilter url to JIRA filter results
     * @return Arraylist of JIRA Issues
     * @throws JIRAException JIRAs not extracted successfully
     */
    public ArrayList<JIRAIssue> getIssues(String jiraFilter, String authorizationValue) throws JIRAException {
        //gets response from JIRAIssue filter and parse it into a Json object
        try {
            String jiraResponse = sendJIRARequest(new URL(jiraFilter), authorizationValue);
            JSONParser jsonParser = new JSONParser();
            JSONObject jiraResponseInJson = (JSONObject) jsonParser.parse(jiraResponse);
            //get results from search URL and parse into Json
            String urlToFilterResults = jiraResponseInJson.get(Constants.SEARCH_URL).toString();
            String responseFromSearchUrl = sendJIRARequest(new URL(urlToFilterResults), authorizationValue);
            JSONObject responseFromSearchUrlInJson = (JSONObject) jsonParser.parse(responseFromSearchUrl);
            int totalJIRAs = Integer.parseInt(responseFromSearchUrlInJson.get(Constants.TOTAL).toString());
            return getJIRAsIssuesFromFilter(urlToFilterResults, totalJIRAs, authorizationValue);
        } catch (MalformedURLException e) {
            String errorMessage = "Url defined to access JIRA is malformed";
            LOGGER.error(errorMessage);
            throw new JIRAConnectionException(errorMessage, e);
        } catch (ParseException e) {
            String errorMessage = "Failed to parse JIRA response String to Json";
            LOGGER.error(errorMessage, e);
            throw new JIRAContentException(errorMessage, e);
        }
    }

    /**
     * Pages the JIRA response from the search Url, stores into and finally returns an arraylist of JIRAIssue objects
     *
     * @param urlToFilterResults from JIRA
     * @param totalJIRAs         total number of JIRA results returned by the filter
     * @return Araaylist of JIRAIssues
     * @throws JIRAException JIRA data not extracted successfully
     */
    private ArrayList<JIRAIssue> getJIRAsIssuesFromFilter(String urlToFilterResults, int totalJIRAs,
                                                          String authorizationValue) throws JIRAException {
        ArrayList<JIRAIssue> jiraIssues = new ArrayList<>();
        for (int i = 0; i <= totalJIRAs / RESULTS_PER_PAGE; i++) { //paging the JIRAIssue response
            try {
                String responseFromSplitSearchUrl = sendJIRARequest(new URL(urlToFilterResults +
                        "&startAt=" + (i * RESULTS_PER_PAGE) + "&maxResults=" +
                        (i + 1) * RESULTS_PER_PAGE + "&fields=key,assignee"), authorizationValue);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObjectFromSplitSearchURL = (JSONObject) jsonParser.parse(responseFromSplitSearchUrl);
                JSONArray issues = (JSONArray) jsonObjectFromSplitSearchURL.get(Constants.ISSUE);
                for (Object issue : issues) {
                    try {
                        JSONObject issueInJson = (JSONObject) issue;
                        JSONObject fieldsInJson = (JSONObject) issueInJson.get(Constants.FIELDS);
                        JSONObject assigneeInJson = (JSONObject) fieldsInJson.get(Constants.ASSIGNEE);
                        //create new JIRAIssue
                        jiraIssues.add(new JIRAIssue(issueInJson.get(Constants.JIRA_KEY).toString(),
                                assigneeInJson.get(Constants.EMAIL).toString()));
                    } catch (NullPointerException e) {
                        String errorMessage = "Failed to extract JIRA issue's field data";
                        LOGGER.error(errorMessage, e);
                        throw new JIRAContentException(errorMessage, e);
                    }
                }
            } catch (MalformedURLException e) {
                String errorMessage = "Url defined to access JIRA is malformed";
                LOGGER.error(errorMessage, e);
                throw new JIRAConnectionException(errorMessage, e);
            } catch (ParseException e) {
                String errorMessage = "Failed to parse jira response string to Json";
                LOGGER.error(errorMessage, e);
                throw new JIRAContentException(errorMessage, e);
            }
        }
        return jiraIssues;
    }

    /**
     * Returns the response returned by a http call as a String
     *
     * @param url to which the http get request is sent
     * @return http response as a String
     * @throws JIRAException Failed to connect to JIRA and return the http response as a String
     */
    private String sendJIRARequest(URL url, String authorizationValue) throws JIRAException {
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
                    String errorMessage = "Failed to read from JIRA Response Stream";
                    LOGGER.error(errorMessage, e);
                    throw new JIRAContentException(errorMessage, e);
                }
            } else {
                String errorMessage = "Failed to get expected JIRA response, response code: " +
                        connection.getResponseCode() + " returned";
                LOGGER.error(errorMessage);
                throw new JIRAException(errorMessage);
            }
        } catch (IOException e) {
            String errorMessage = "Failed to connect to Jira";
            LOGGER.error(errorMessage, e);
            throw new JIRAConnectionException(errorMessage, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}










