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
package org.wso2.PatchInformation.JiraData;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.PatchInformation.ConfiguredProperties;
import org.wso2.PatchInformation.Constants.Constants;
import org.wso2.PatchInformation.Exceptions.JiraExceptions.AccessJiraException;
import org.wso2.PatchInformation.Exceptions.JiraExceptions.ExtractingFromResponseStreamException;
import org.wso2.PatchInformation.Exceptions.JiraExceptions.JiraException;
import org.wso2.PatchInformation.Exceptions.JiraExceptions.ParsingToJsonException;
import org.wso2.PatchInformation.Exceptions.JiraExceptions.ResponseCodeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

/**
 * connects to JiraIssue and extracts the data returned from the filter
 */
public class AccessJira {

    private final static Logger logger = Logger.getLogger(AccessJira.class);

    /**
     * Returns an Arraylist of JiraIssue objects containing the data returned after applying the Jira filter
     *
     * @param urlToJiraIssues of the JiraIssue filter
     * @return Arraylist of jira Issues
     * @throws Exception Jiras not extracted successfully
     */
    public static ArrayList<JiraIssue> getJirasReturnedBy(String urlToJiraIssues) throws JiraException {
        //gets response from JiraIssue filter and parse it into a Json object
        try {
            String jiraResponse = sendJiraGETRequest(new URL(urlToJiraIssues));
            JSONParser jsonParser = new JSONParser();
            JSONObject jiraResponseInJson = (JSONObject) jsonParser.parse(jiraResponse);
            //get results from search URL and parse into Json
            String urlToFilterResults = jiraResponseInJson.get(Constants.SEARCH_URL).toString();
            String responseFromSearchUrl = sendJiraGETRequest(new URL(urlToFilterResults));
            JSONObject responseFromSearchUrlInJson = (JSONObject) jsonParser.parse(responseFromSearchUrl);
            int totalJiras = Integer.parseInt(responseFromSearchUrlInJson.get(Constants.TOTAL).toString());
            return getJirasIssuesIn(urlToFilterResults, totalJiras);
        } catch (MalformedURLException e) {
            String errorMessage = "Url defined to access Jira is malformed";
            logger.error(errorMessage, e);
            throw new AccessJiraException(errorMessage, e);
        } catch (ParseException e) {
            String errorMessage = "Failed to parse Jira response String to Json";
            logger.error(errorMessage, e);
            throw new ParsingToJsonException(errorMessage, e);
        }
    }

    /**
     * Pages the jira response from the search Url, stores into and finally returns an arraylist of JiraIssue objects
     *
     * @param urlToFilterResults from Jira
     * @param totalJiras         total number of jira results returned by the filter
     * @return Araaylist of JiraIssues
     * @throws JiraException Jira data not extracted successfully
     */
    private static ArrayList<JiraIssue> getJirasIssuesIn(String urlToFilterResults, int totalJiras) throws JiraException {
        //paging the JiraIssue response
        ArrayList<JiraIssue> jiraIssues = new ArrayList<>();
        for (int i = 0; i <= totalJiras / Constants.PAGE_SIZE; i++) {
            try {
                String responseFromSplitSearchUrl = sendJiraGETRequest(new URL(urlToFilterResults +
                        "&startAt=" + (i * Constants.PAGE_SIZE) + "&maxResults=" +
                        (i + 1) * Constants.PAGE_SIZE + "&fields=key,assignee"));
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObjectFromSplitSearchURL = (JSONObject) jsonParser.parse(responseFromSplitSearchUrl);
                JSONArray issues = (JSONArray) jsonObjectFromSplitSearchURL.get(Constants.ISSUE);
                for (Object issue : issues) {
                    try {
                        JSONObject issueInJson = (JSONObject) issue;
                        JSONObject fieldsInJson = (JSONObject) issueInJson.get(Constants.FIELDS);
                        JSONObject assigneeInJson = (JSONObject) fieldsInJson.get(Constants.ASSIGNEE);
                        //create new JiraIssue
                        jiraIssues.add(new JiraIssue(issueInJson.get(Constants.JIRA_KEY).toString(),
                                assigneeInJson.get(Constants.EMAIL).toString()));
                    } catch (NullPointerException e) {
                        String errorMessage = "Failed to extract jira issue's field data";
                        logger.error(errorMessage, e);
                        throw new ExtractingFromResponseStreamException(errorMessage, e);
                    }
                }
            } catch (MalformedURLException e) {
                String errorMessage = "Url defined to access Jira is malformed";
                logger.error(errorMessage, e);
                throw new AccessJiraException(errorMessage, e);
            } catch (ParseException e) {
                String errorMessage = "Failed to parse jsonObjectFromSplitSearchURL String to Json";
                logger.error(errorMessage, e);
                throw new ParsingToJsonException(errorMessage, e);
            }

        }
        logger.info("Jira issues successfully extracted from Jira.");
        return jiraIssues;
    }

    /**
     * Returns the response returned by a http call as a String
     *
     * @param url to which the http get request is sent
     * @return http response as a String
     * @throws JiraException Failed to connect to Jira and return the http response as a String
     */
    private static String sendJiraGETRequest(URL url) throws JiraException {

        HttpURLConnection connection = null;
        BufferedReader dataInputStream = null;
        try {
            //open and set connection values
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty(Constants.AUTH, ConfiguredProperties.getValueOf("jiraBasicAuth"));
                connection.setRequestProperty(Constants.CONTENT, Constants.CONTENT_TYPE);
                connection.setRequestMethod(Constants.REQUEST_METHOD);
            } catch (IOException e) {
                String errorMessage = "Failed to open and set up Http Connection successfully";
                logger.error(errorMessage, e);
                throw new ExtractingFromResponseStreamException(errorMessage, e);
            }

            if (connection.getResponseCode() == 200) {
                try {
                    dataInputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = dataInputStream.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();
                } catch (IOException e) {
                    String errorMessage = "Failed to read from Jira Response Stream";
                    logger.error(errorMessage, e);
                    throw new ExtractingFromResponseStreamException(errorMessage, e);
                }
            } else {
                    String errorMessage = "Failed to get expected Jira response, response code: " + connection.getResponseCode();
                    logger.error(errorMessage);
                    throw new ResponseCodeException(errorMessage);
            }
        } catch (IOException e) {
            String errorMessage = "Failed to get Response code";
            logger.error(errorMessage, e);
            throw new AccessJiraException(errorMessage, e);
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    logger.warn("Buffered reader was not closed", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}











