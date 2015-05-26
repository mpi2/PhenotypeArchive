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

package uk.ac.ebi.phenotype.web.controller.sitemap;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *
 * @author mrelac
 */
@XmlAccessorType(value = XmlAccessType.NONE)
@XmlRootElement(name = "urlset")
public class XmlUrlSet {

    private final static int MAX_SITEMAP_ENTRIES = 500;

    @XmlElements({@XmlElement(name = "url", type = XmlUrl.class)})
    private final List<XmlUrl> xmlUrls = new ArrayList();

    public void addUrl(XmlUrl xmlUrl) {

        final int size = xmlUrls.size();

        // Reservoir sampling to get random entries in the sitemap (this will get them all
        // eventually, but not exceed the sitemap entry size restriction)
        if (size >= MAX_SITEMAP_ENTRIES) {
            // Randomly replace elements in the reservoir with a decreasing probability.
            int idx = new Double(Math.floor(Math.random() * size)).intValue();
            if (idx < MAX_SITEMAP_ENTRIES) {
                xmlUrls.set(idx, xmlUrl);
            }
        } else {
            xmlUrls.add(xmlUrl);
        }
    }


    public Collection<XmlUrl> getXmlUrls() {
        return xmlUrls;
    }
}
