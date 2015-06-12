/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/



package org.mousephenotype.www.testing.exception;

/**
 *
 * @author mrelac
 */
public class GraphTestException extends Exception {

    public GraphTestException() {
        super("");
    }

    public GraphTestException(String message) {
        super(message);
    }
    
    public GraphTestException(Exception e) {
        super(e);
    }
    
    public GraphTestException(String message, Exception e) {
        super(message, e);
    }
}
