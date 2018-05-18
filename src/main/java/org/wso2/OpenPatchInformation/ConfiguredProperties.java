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
package org.wso2.OpenPatchInformation;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * For a given property key, returns the property value
 */
public class ConfiguredProperties {

    private final static Logger logger = Logger.getLogger(Main.class);

    public static String getValueOf(String key) {

        InputStream propertyFile = Main.class.getResourceAsStream("/config.properties");
        Properties prop = new Properties();
        try {
            prop.load(propertyFile);
        } catch (IOException e) {
            logger.warn("Property file could not be read");
        }
        return prop.getProperty(key);
    }
}
