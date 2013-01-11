/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.phenotype.imaging.springrest.images;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import uk.ac.ebi.phenotype.imaging.persistence.ImaImageRecord;

/**
 * class for holding a list of image urls and associated data
 * 
 * @author jwarren
 * 
 */

// @XmlRootElement
public class Images {
	private int start;
	private int length;
	private long total;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	// @XmlElement(name = "imaImageRecord", required = true)
	private List<ImaImageRecord> records = new ArrayList<ImaImageRecord>();

	public List<ImaImageRecord> getImages() {
		return records;

	}

	public void setImages(List<ImaImageRecord> list) {
		this.records = list;

	}

}
