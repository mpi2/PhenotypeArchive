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
package uk.ac.ebi.phenotype.rest.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import uk.ac.ebi.phenotype.bean.GenomicFeatureBean;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;

public class AtomUtil {

        public static Map<String, Object> marshallerProperties;
        
        static {
                marshallerProperties = new HashMap<String, Object>();
                marshallerProperties.put("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
        }
        
        public static Feed genomicFeatureFeed(List<GenomicFeatureBean> genomicFeatures, Jaxb2Marshaller marshaller) {
                Feed feed = new Feed();
                feed.setFeedType("atom_1.0");
                feed.setTitle("GenomicFeature Atom Feed");
                
                List<Entry> entries = new ArrayList<Entry>();
                for(GenomicFeatureBean e : genomicFeatures) {
                        StreamResult result = new StreamResult(new ByteArrayOutputStream());
                        
                        marshaller.setMarshallerProperties(marshallerProperties);
                        marshaller.marshal(e, result);
                        String xml = result.getOutputStream().toString();
                        
                        Entry entry = new Entry();
                        entry.setId(e.getAccession());
                        entry.setTitle(e.getName());
                        Content content = new Content();
                        content.setType(Content.XML);
                        content.setValue(xml);
                        
                        List<Content> contents = new ArrayList<Content>();
                        contents.add(content);
                        entry.setContents(contents);
                        
                        entries.add(entry);
                }
                feed.setEntries(entries);
                return feed;
        }
}
