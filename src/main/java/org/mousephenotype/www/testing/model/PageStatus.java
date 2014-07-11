/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mousephenotype.www.testing.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent a page status
 * for Selenium testing purposes. Use this class to accumulate fail messages and
 * counts for later presentation to the testing interface.
 * 
 */
public class PageStatus {
    private final List<String> failMessages;
    private Status status;

    public PageStatus() {
        failMessages = new ArrayList();
        status = Status.OK;
    }

    public List<String> getFailMessages() {
        return failMessages;
    }

    public void addFail(String failMessage) {
        this.failMessages.add(failMessage);
        status = Status.FAIL;
    }
    
    public void addFail(List<String> failMessages) {
        this.failMessages.addAll(failMessages);
        status = Status.FAIL;
    }

    @Override
    public String toString() {
        return "total fail messages: " + failMessages.size();
    }

    /**
     * Returns all of the fail messages in a String, each message terminated
     * with a newline, suitable for display. Returns an empty string if there
     * are no fail messages.
     * 
     * @return  all of the fail messages in a String, each message terminated
     * with a newline, suitable for display. Returns an empty string if there
     * are no fail messages.
     */
    public String toStringFailMessages() {
        StringBuilder sb = new StringBuilder();
        if (failMessages != null) {
            for (String s : failMessages) {
                sb.append(s);
                sb.append("\n");
            }
        }

        return sb.toString();
    }
    
    
    // GETTERS AND SETTERS
    
    
    public Status getStatus() {
        return status;
    }
    
    
    // PUBLIC INNER CLASSES
    
    
    public enum Status {
        OK,
        FAIL
    }

}
