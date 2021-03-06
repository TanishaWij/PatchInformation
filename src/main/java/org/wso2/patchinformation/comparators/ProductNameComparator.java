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

package org.wso2.patchinformation.comparators;

import org.wso2.patchinformation.pmt.Patch;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Implements the Comparator class to order objects of the OpenPatch class by it's "productName" attribute.
 */
public class ProductNameComparator implements Comparator<Patch>, Serializable {

    public int compare(Patch p1, Patch p2) {
        return p1.getProductName().compareTo(p2.getProductName());
    }
}
