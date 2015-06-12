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

package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.phenotype.service.DiseaseService;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.MpService;
import uk.ac.ebi.phenotype.web.controller.sitemap.XmlSitemap;
import uk.ac.ebi.phenotype.web.controller.sitemap.XmlSitemapIndex;
import uk.ac.ebi.phenotype.web.controller.sitemap.XmlUrl;
import uk.ac.ebi.phenotype.web.controller.sitemap.XmlUrlSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mrelac
 * 
 * This controller creates sitemap files conforming to http://www.sitemap.org
 * for google crawlers for genes, phenotypes, and diseases. Each sitemap file
 * contains a link to every gene, phenotype, and disease page that phenotypeArchive
 * knows about. Since there are multiple sitemaps, a sitemap index file is required
 * that points to the sitemap files. That file is created here as well.
 */
@Controller
public class SiteMapController {
    
    @Autowired
    private GeneService geneService;
    
    @Autowired
    private MpService mpService;
    
    @Autowired
    private DiseaseService diseaseService;
    
    /**
     * Create a gene sitemap XML file
     * 
     * Side Effects: Creates a physical file <code>gene_sitemap.xml</code> in the
     * web deployment root directory containing every gene web page we offer.
     * 
     * @param request <code>HttpServletRequest</code> instance
     * @return an XML object containing a gene sitemap
     * @throws SolrServerException 
     */
    @RequestMapping(value = "/sitemap_genes.xml", method = RequestMethod.GET, produces = "application/xml; charset=utf-8")
    @ResponseBody
    public String createSitemapGenes(HttpServletRequest request, HttpServletResponse response) throws SolrServerException {

        List<String> geneIds = new ArrayList(geneService.getAllGenes());

        String mappedHostname = (String)request.getAttribute("mappedHostname");
        String baseUrl = (String)request.getAttribute("baseUrl");
        XmlUrlSet xmlUrlSet = new XmlUrlSet();

        // Generate the links.
        for (String geneId : geneIds) {
            String target = mappedHostname + baseUrl + "/genes/" + geneId;
            create(xmlUrlSet, target, XmlUrl.Priority.MEDIUM);
        }

        return getSitemapFile(xmlUrlSet);
    }
    
    /**
     * Create a phenotype sitemap XML file
     * 
     * Side Effects: Creates a physical file <code>phenotype_sitemap.xml</code> in the
     * web deployment root directory containing every phenotype web page we offer.
     * 
     * @param request <code>HttpServletRequest</code> instance
     * @return an XML object containing a phenotype sitemap
     * @throws SolrServerException 
     */
    @RequestMapping(value = "/sitemap_phenotypes.xml", method = RequestMethod.GET, produces = "application/xml; charset=utf-8")
    @ResponseBody
    public String createSitemapPhenotypes(HttpServletRequest request, HttpServletResponse response) throws SolrServerException {

        List<String> phenotypeIds = new ArrayList(mpService.getAllPhenotypes());
        String mappedHostname = (String)request.getAttribute("mappedHostname");
        String baseUrl = (String)request.getAttribute("baseUrl");
        XmlUrlSet xmlUrlSet = new XmlUrlSet();

        // Generate the links.
        for (String phenotypeId : phenotypeIds) {
            String target = mappedHostname + baseUrl + "/phenotypes/" + phenotypeId;
            create(xmlUrlSet, target, XmlUrl.Priority.MEDIUM);
        }

        return getSitemapFile(xmlUrlSet);
    }
    
    /**
     * Create a disease sitemap XML file
     * 
     * Side Effects: Creates a physical file <code>disease_sitemap.xml</code> in the
     * web deployment root directory containing every disease web page we offer.
     * 
     * @param request <code>HttpServletRequest</code> instance
     * @return an XML object containing a disease sitemap
     * @throws SolrServerException 
     */
    @RequestMapping(value = "/sitemap_diseases.xml", method = RequestMethod.GET, produces = "application/xml; charset=utf-8")
    @ResponseBody
    public String createSitemapDiseases(HttpServletRequest request, HttpServletResponse response) throws SolrServerException {
        List<String> diseases = new ArrayList(diseaseService.getAllDiseases());
        String mappedHostname = (String)request.getAttribute("mappedHostname");
        String baseUrl = (String)request.getAttribute("baseUrl");
        XmlUrlSet xmlUrlSet = new XmlUrlSet();

        // Generate the links.
        for (String phenotypeId : diseases) {
            String target = mappedHostname + baseUrl + "/disease/" + phenotypeId;
            create(xmlUrlSet, target, XmlUrl.Priority.MEDIUM);
        }

        return getSitemapFile(xmlUrlSet);
    }
    
    /**
     * Create a sitemap index XML file for all of our sitemap xml files
     * 
     * Side Effects: Creates a physical file <code>sitemap_index.xml</code> in the
     * web deployment root directory containing pointers to all of our sitemap files
     * 
     * @param request <code>HttpServletRequest</code> instance
     * @return an XML object containing a sitemap index
     */
    @RequestMapping(value = "/sitemap_index.xml", method = RequestMethod.GET, produces = "application/xml; charset=utf-8")
    @ResponseBody
    public String createSitemapIndex(HttpServletRequest request, HttpServletResponse response) {

        String mappedHostname = (String)request.getAttribute("mappedHostname");
        String baseUrl = (String)request.getAttribute("baseUrl");
        XmlSitemapIndex xmlSitemapIndex = new XmlSitemapIndex();
        
        String[] sitemapUrls = { "/sitemap_genes.xml", "/sitemap_phenotypes.xml", "/sitemap_diseases.xml" };

        for (String sitemapUrl : sitemapUrls) {
            String location = mappedHostname + baseUrl + sitemapUrl;
            XmlSitemap xmlSitemap = new XmlSitemap(location);
            xmlSitemapIndex.addSitemap(xmlSitemap);
        }

        return getSitemapFile(xmlSitemapIndex);
    }
    

    // PRIVATE METHODS
    
    
    private void create(XmlUrlSet xmlUrlSet, String link, XmlUrl.Priority priority) {
        xmlUrlSet.addUrl(new XmlUrl(link, priority));
    }

    private String getSitemapFile(Object xmlObject) {

        StringWriter writer = new StringWriter(2048);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(xmlObject.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            // Set the XML root tag to not include that standalone="yes" attribute
            // Why U no work, JAXB?
            //jaxbMarshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            jaxbMarshaller.marshal(xmlObject, writer);

        } catch (JAXBException e) {
            e.printStackTrace();
        }


        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + writer.toString();
    }

    /**
     * Creates a sitemap file withname <code>filename</code> in the web server
     * root directory
     * @param filename The name of the xml file to create
     * @param xmlObject The xml object details to store in the xml file
     * 
     * PHYSICAL FILE GENERATION IS NOT NEEDED. LEAVE THE METHOD IN CASE WE NEED PHYSICAL FILE GENERATION IN THE FUTURE
     * BUT REMOVE THE PART THAT CREATES THE FILE.
     */
    private void createSitemapFile(String filename, Object xmlObject) {

//        File file = new File(filename);
//        
//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(xmlObject.getClass());
//            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//            
//            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            
//            jaxbMarshaller.marshal(xmlObject, file);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
    }
    
}
