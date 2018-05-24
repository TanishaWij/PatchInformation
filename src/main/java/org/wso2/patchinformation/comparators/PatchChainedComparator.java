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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Implements the Comparator class to order objects of the OpenPatch class by more than one  attribute.
 */
public class PatchChainedComparator implements Comparator<Patch>, Serializable {

    private List<Comparator<Patch>> listComparators;

    public PatchChainedComparator(Comparator<Patch>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    public int compare(Patch p1, Patch p2) {
        for (Comparator<Patch> comparator : listComparators) {
            int result = comparator.compare(p1, p2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}

