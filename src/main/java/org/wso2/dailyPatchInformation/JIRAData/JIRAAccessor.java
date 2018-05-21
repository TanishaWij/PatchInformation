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
package org.wso2.dailyPatchInformation.JIRAData;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.dailyPatchInformation.constants.Constants;
import org.wso2.dailyPatchInformation.exceptions.JIRAExceptions.AccessJIRAException;
import org.wso2.dailyPatchInformation.exceptions.JIRAExceptions.ExtractingFromResponseStreamException;
import org.wso2.dailyPatchInformation.exceptions.JIRAExceptions.JIRAException;
import org.wso2.dailyPatchInformation.exceptions.JIRAExceptions.ParsingToJsonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

/**
 * connects to JIRAIssue and extracts the data returned from the filter
 */
public class JIRAAccessor {

    private final static Logger LOGGER = Logger.getLogger(JIRAAccessor.class);
    private static JIRAAccessor JIRA_ACCESSOR;

    private JIRAAccessor() {

    }

    public static JIRAAccessor getJiraAccessor() {

        if (JIRA_ACCESSOR == null) {
            JIRA_ACCESSOR = new JIRAAccessor();
        }
        return JIRA_ACCESSOR;
    }

    /**
     * Returns an Arraylist of JIRAIssue objects containing the data returned after applying the JIRA filter
     *
     * @param JIRAFilter of the JIRAIssue filter
     * @return Arraylist of JIRA Issues
     * @throws Exception JIRAs not extracted successfully
     */
    public ArrayList<JIRAIssue> getJIRAs(String JIRAFilter, String authorizationValue) throws JIRAException {
        //gets response from JIRAIssue filter and parse it into a Json object
        try {
            String JIRAResponse = sendJIRAGETRequest(new URL(JIRAFilter), authorizationValue);
            JSONParser jsonParser = new JSONParser();
            JSONObject JIRAResponseInJson = (JSONObject) jsonParser.parse(JIRAResponse);
            //get results from search URL and parse into Json
            String urlToFilterResults = JIRAResponseInJson.get(Constants.SEARCH_URL).toString();
            String responseFromSearchUrl = sendJIRAGETRequest(new URL(urlToFilterResults), authorizationValue);
            JSONObject responseFromSearchUrlInJson = (JSONObject) jsonParser.parse(responseFromSearchUrl);
            int totalJIRAs = Integer.parseInt(responseFromSearchUrlInJson.get(Constants.TOTAL).toString());
            return getJIRAsIssuesIn(urlToFilterResults, totalJIRAs, authorizationValue);
        } catch (MalformedURLException e) {
            String errorMessage = "Url defined to access JIRA is malformed";
            LOGGER.error(errorMessage, e);
            throw new AccessJIRAException(errorMessage, e);
        } catch (ParseException e) {
            String errorMessage = "Failed to parse JIRA response String to Json";
            LOGGER.error(errorMessage, e);
            throw new ParsingToJsonException(errorMessage, e);
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
    private ArrayList<JIRAIssue> getJIRAsIssuesIn(String urlToFilterResults, int totalJIRAs, String authorizationValue) throws JIRAException {
        //paging the JIRAIssue response
        ArrayList<org.wso2.dailyPatchInformation.JIRAData.JIRAIssue> JIRAIssues = new ArrayList<>();
        for (int i = 0; i <= totalJIRAs / Constants.PAGE_SIZE; i++) {
            try {
                String responseFromSplitSearchUrl = sendJIRAGETRequest(new URL(urlToFilterResults +
                        "&startAt=" + (i * Constants.PAGE_SIZE) + "&maxResults=" +
                        (i + 1) * Constants.PAGE_SIZE + "&fields=key,assignee"), authorizationValue);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObjectFromSplitSearchURL = (JSONObject) jsonParser.parse(responseFromSplitSearchUrl);
                JSONArray issues = (JSONArray) jsonObjectFromSplitSearchURL.get(Constants.ISSUE);
                for (Object issue : issues) {
                    try {
                        JSONObject issueInJson = (JSONObject) issue;
                        JSONObject fieldsInJson = (JSONObject) issueInJson.get(Constants.FIELDS);
                        JSONObject assigneeInJson = (JSONObject) fieldsInJson.get(Constants.ASSIGNEE);
                        //create new JIRAIssue
                        JIRAIssues.add(new org.wso2.dailyPatchInformation.JIRAData.JIRAIssue(issueInJson.get(Constants.JIRA_KEY).toString(),
                                assigneeInJson.get(Constants.EMAIL).toString()));
                    } catch (NullPointerException e) {
                        String errorMessage = "Failed to extract JIRA issue's field data";
                        LOGGER.error(errorMessage, e);
                        throw new ExtractingFromResponseStreamException(errorMessage, e);
                    }
                }
            } catch (MalformedURLException e) {
                String errorMessage = "Url defined to access JIRA is malformed";
                LOGGER.error(errorMessage, e);
                throw new AccessJIRAException(errorMessage, e);
            } catch (ParseException e) {
                String errorMessage = "Failed to parse jsonObjectFromSplitSearchURL String to Json";
                LOGGER.error(errorMessage, e);
                throw new ParsingToJsonException(errorMessage, e);
            }
        }
        return JIRAIssues;
    }

    /**
     * Returns the response returned by a http call as a String
     *
     * @param url to which the http get request is sent
     * @return http response as a String
     * @throws JIRAException Failed to connect to JIRA and return the http response as a String
     */
    private String sendJIRAGETRequest(URL url, String authorizationValue) throws JIRAException {

        HttpURLConnection connection = null;

        try {
            //open and set connection values
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty(Constants.AUTH, authorizationValue);
                connection.setRequestProperty(Constants.CONTENT, Constants.CONTENT_TYPE); //TODO
                connection.setRequestMethod(Constants.GET);
            } catch (IOException e) {
                String errorMessage = "Failed to open and set up Http Connection successfully";
                LOGGER.error(errorMessage, e);
                throw new JIRAException(errorMessage, e);
            }

            if (connection.getResponseCode() == 200) { //TODO - check if already defined
                try (BufferedReader dataInputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    //dataInputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = dataInputStream.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();
                } catch (IOException e) {
                    String errorMessage = "Failed to read from JIRA Response Stream";
                    LOGGER.error(errorMessage, e);
                    throw new JIRAException(errorMessage, e);
                }
            } else {
                String errorMessage = "Failed to get expected JIRA response, response code: " +
                        connection.getResponseCode() + " returned";
                LOGGER.error(errorMessage);
                throw new JIRAException(errorMessage);
            }
        } catch (IOException e) {
            String errorMessage = "Failed to get Response code";
            LOGGER.error(errorMessage, e);
            throw new AccessJIRAException(errorMessage, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}











