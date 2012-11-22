/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.phenotype.util;

import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextFinalizer implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {
	}

	public void contextDestroyed(ServletContextEvent sce) {

		// Try to kill the TOMCAT abandoned connection clean up thread
		// to prevent it from holding the whole context in memory causing
		// a leak

		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

		for (Thread t : threadArray) {
			if (t.getName().contains("Abandoned connection cleanup thread")) {
				synchronized (t) {
					t.stop(); // don't complain, it works
				}
			}
		}
	}

}
