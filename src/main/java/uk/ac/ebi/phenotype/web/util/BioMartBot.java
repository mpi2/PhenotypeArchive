package uk.ac.ebi.phenotype.web.util;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpHost;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsNames;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

/** 
* BioMart data fetcher abstract class.
* Most of the BioMart http client task includes the following steps
*  1. connection to a web server
*  2. post of a xml query
*  3. processing the tabular character separated response 
* 
* This BioMartBot class encapsulate all the logic. 
* Subclasses implement the process method.
* 
* @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
* @version $Revision: 2484 $
*  @since June 2012
*/

public abstract class BioMartBot {

	protected String configurationFile = "";
	protected String bioMartURL = "http://www.europhenome.org/biomart/martservice";
	
	protected HttpClient httpclient;
	protected HttpPost httppost;
	protected HttpEntity responseEntity;
	
	protected final int CONNECTION_TIME_OUT = 10000;
	
	Logger log = Logger.getLogger(this.getClass().getCanonicalName());
	
	/**
	 * @param args
	 */
	
/*	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String file = "/Users/koscieln/Documents/workspace/PhenotypeArchive/docs/Datasources/iMits_v3.xml";
		String url = "http://www.i-dcc.org/biomart/martservice";

		BioMartBot bot = new BioMartBot();

		bot.postData(url, file);

	}*/

	/** 
	 * URL to request. 
	 * @param url
	 */
	public void initConnection(String url) {
	
		// Create a new HttpClient and Post Header
		final HttpParams httpParams = new BasicHttpParams();
		// httpclient.getParams().setParameter("http.socket.timeout", new Integer(1000));
	    HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIME_OUT);
	    HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIME_OUT);
		
		httpclient = new DefaultHttpClient(httpParams);
		
		// check system properties for proxy settings
		if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
			HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")), "http");
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		} else if (System.getProperty("HTTP_PROXY_HOST") != null && System.getProperty("HTTP_PROXY_PORT") != null) {
			log.info("Using proxy settings:\t" + System.getProperty("HTTP_PROXY_HOST") + "\t" + System.getProperty("HTTP_PROXY_PORT"));
			HttpHost proxy = new HttpHost(System.getProperty("HTTP_PROXY_HOST"), Integer.parseInt(System.getProperty("HTTP_PROXY_PORT")), "http");
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		
		httppost = new HttpPost(url);
		
	}
	
	public String getQueryFromFile(String filename) {
		
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = null;

		try {
			scanner = new Scanner(new FileInputStream(filename));

			while (scanner.hasNextLine()){
				text.append(scanner.nextLine() + NL);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally{
			scanner.close();
		}
		return text.toString();
	}
	
	public HttpEntity executeRequest(String text) throws ClientProtocolException, IOException {
		
		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("query", text));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Execute HTTP Post Request
		HttpResponse response = httpclient.execute(httppost);

		return response.getEntity() ;
		
	}
	
	public abstract void processResponse() throws IOException;
/*	{
		responseEntity.writeTo(System.out);
	}*/
	
	public void postDataFromText(String url, String text) throws ConnectTimeoutException{
		initConnection(url);
		try {
			responseEntity = executeRequest(text);
			
			if (responseEntity != null) {

				this.processResponse();
				
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void postData(String url, String filename) {
		initConnection(url);

		String text = getQueryFromFile(filename);


		try {

			responseEntity = executeRequest(text);

			if (responseEntity != null) {

				this.processResponse();
				
			}


		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	protected void finalize() {
		httpclient.getConnectionManager().shutdown();
	}
}
