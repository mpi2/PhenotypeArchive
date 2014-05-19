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



package org.mousephenotype.www.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mrelac
 */
public class GraphParsingStatus {
    private int fail;
    private int pass;
    private int total;
    private final List<String> failMessages;
    private final List<String> passMessages;

    public GraphParsingStatus() {
        total = 0;
        pass = 0;
        fail = 0;
        failMessages = new ArrayList();
        passMessages = new ArrayList();
    }

    public int getFail() {
        return fail;
    }

    public int getPass() {
        return pass;
    }

    public int getTotal() {
        return total;
    }

    public List<String> getFailMessages() {
        return failMessages;
    }

    public List<String> getPassMessages() {
        return passMessages;
    }

    public void addFail(String failMessage) {
        this.failMessages.add(failMessage);
        fail++;
        total++;
    }
    public void addFail(List<String> failMessages) {
        this.failMessages.addAll(failMessages);
        fail += failMessages.size();
        total += failMessages.size();
    }

    public void addPass(String passMessage) {
        this.passMessages.add(passMessage);
        pass++;
        total++;
    }

    @Override
    public String toString() {
        return "total graph links: " + total + " (" + pass + " pass, " + fail + " fail)";
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

    /**
     * Returns all of the pass messages in a String, each message terminated
     * with a newline, suitable for display. Returns an empty string if there
     * are no pass messages.
     * 
     * @return  all of the pass messages in a String, each message terminated
     * with a newline, suitable for display. Returns an empty string if there
     * are no pass messages.
     */
    public String toStringPassMessages() {
        StringBuilder sb = new StringBuilder();
        if (passMessages != null) {
            for (String s : passMessages) {
                sb.append(s);
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
