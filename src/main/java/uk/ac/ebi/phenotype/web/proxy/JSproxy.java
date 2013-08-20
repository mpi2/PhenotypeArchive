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
package uk.ac.ebi.phenotype.web.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class proxy
 */
public class JSproxy extends HttpServlet {
	
	static Logger logger = Logger.getLogger(JSproxy.class);
   

	/**
     * @see HttpServlet#HttpServlet()
     */
    public JSproxy() {
        super();
      //System.out.println("loading proxy");
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String queryString=request.getParameter("segment");
		System.out.println(queryString);
		String relativePath=request.getPathInfo();
		System.out.println("queryString="+relativePath);
		
		//response.setHeader("Content-Type", "application/xml");
//		response.setContentType("text/xml");
		//urlString=urlString.replace(" ","+");
		//%3A :
		//something wierd going on with the front ends so I have to do a replace so search works
		//urlString=urlString.replace("%3A",":");
		//logger.debug("urlString after spaces removed="+urlString);
	if(!relativePath.startsWith("/das/mouse_current/")){
		logger.warn("dissallowed url for proxy.php");
		return;
	}
	String urlString=relativePath.replace("/das/mouse_current/","http://gbrowse.informatics.jax.org/cgi-bin/das/mouse_current|Hearing_ear/");
		URLConnection connection = new URL(urlString).openConnection();
		
		//String contentEncoding = connection.getHeaderField("Content-Encoding");//should be"Content-Encoding","ISO-8859-1")
		//System.out.println("content type="+contentEncoding);
		//if (contentType.startsWith("text")) {
		    //String charset = "ISO-8859-1";//this encoding is what the registry uses so must be set here to override default;
		    //System.out.println("charset="+charset);
		    BufferedReader reader = null;
		    PrintWriter writer=response.getWriter();
		    try {
		        reader = new BufferedReader(new InputStreamReader( connection.getInputStream(), "ISO-8859-1"));
		        for (String line; (line = reader.readLine()) != null;) {
		        	System.out.println("line through proxy="+line);
		            writer.println(line);
		        	//System.out.println("changed "+line);
		        }
		    } finally {
		    	if(reader!=null){
		    		reader.close();
		    	}if(writer!=null){
		    		  writer.flush();
					    writer.close();
		    	}
				  
		    }
		    
		   
		    
		//}


	   }  // end of main


		
		
		
		
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
