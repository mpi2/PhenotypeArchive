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
 * for Selenium testing purposes. Use this class to accumulate error and warning
 * messages for later presentation to the testing interface.
 * 
 */
public class PageStatus {
    private final List<String> errorMessages;
    private final List<String> warningMessages;

    public PageStatus() {
        errorMessages = new ArrayList();
        warningMessages = new ArrayList();
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }
    
    public void add(PageStatus pageStatus) {
        errorMessages.addAll(pageStatus.errorMessages);
        warningMessages.addAll(pageStatus.warningMessages);
    }

    public void addError(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }
    
    public void addError(List<String> errorMessages) {
        this.errorMessages.addAll(errorMessages);
    }

    public void addWarning(String warningMessage) {
        this.warningMessages.add(warningMessage);
    }
    
    public void addWarning(List<String> warningMessage) {
        this.warningMessages.addAll(warningMessage);
    }
    
    public boolean hasErrors() {
        return (errorMessages.size() > 0);
    }
    
    public boolean hasWarnings() {
        return (warningMessages.size() > 0);
    }

    @Override
    public String toString() {
        return "total fail messages: " + errorMessages.size();
    }

    /**
     * @return  All of the error messages in a String, each message terminated
     * with a newline, suitable for display. Returns an empty string if there
     * are no error messages.
     */
    public String toStringErrorMessages() {
        StringBuilder sb = new StringBuilder();
        if (errorMessages != null) {
            for (String s : errorMessages) {
                sb.append(s);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * @return  All of the warning messages in a String, each message terminated
     * with a newline, suitable for display. Returns an empty string if there
     * are no warning messages.
     */
    public String toStringWarningMessages() {
        StringBuilder sb = new StringBuilder();
        if (warningMessages != null) {
            for (String s : warningMessages) {
                sb.append(s);
                sb.append("\n");
            }
        }

        return sb.toString();
    }
    
    
    // GETTERS AND SETTERS
    
}
