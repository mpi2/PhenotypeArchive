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
package uk.ac.ebi.phenotype.data.impress;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.phenotype.dao.DatasourceDAO;
import uk.ac.ebi.phenotype.dao.OntologyTermDAO;
import uk.ac.ebi.phenotype.pojo.*;

import java.util.*;


/**
 *
 * Utilities provide a set of utility methods for IMPRESS client
 *
 * @author Gautier Koscielny
 * @see Parameter
 */
@Component
public class Utilities {

	private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

	@Autowired
	OntologyTermDAO ontologyTermDAO;

	@Autowired
	DatasourceDAO datasourceDAO;

	private Integer efoDbId=null;
	private final Set<String> expectedDpc = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("9.5", "12.5", "13.5", "14.5", "15.5", "18.5")));



	/**
	 * Returns the observation type based on the parameter, when the parameter
	 * has a valid dataType.
	 *
	 * @param parameter a valid <code>Parameter</code> instance
	 * @return The observation type based on the parameter, when the parameter
	 * has a valid data type (<code>parameter.getDatatype()</code>).
	 *
	 * <b>NOTE: if the parameter does not have a valid data type, this function
	 * may return incorrect results. When the parameter datatype is unknown,
	 * call <code>checktype(Parameter p, String value)</code> with a sample data
	 * value to get the correct observation type.</b>
	 */
	public ObservationType checkType(Parameter parameter) {

		ObservationType ret=null;
		try {
			ret = checkType(parameter, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}


	/**
	 * Returns the observation type based on the parameter and a sample
	 * parameter value.
	 *
	 * @param parameter a valid <code>Parameter</code> instance
	 * @param value a string representing parameter sample data (e.g. a floating
	 * point number or anything else).
	 * @return The observation type based on the parameter and a sample
	 * parameter value. If <code>value</code> is a floating point number and
	 * <code>parameter</code> does not have a valid data type,
	 * <code>value</code> is used to disambiguate the graph type: the
	 * observation type will be either <i>time_series</i> or
	 * <i>unidimensional</i>; otherwise, it will be interpreted as
	 * <i>categorical</i>.
	 */
	public ObservationType checkType(Parameter parameter, String value) {

		Map<String, String> MAPPING = new HashMap<>();
		MAPPING.put("M-G-P_022_001_001_001", "FLOAT");
		MAPPING.put("M-G-P_022_001_001", "FLOAT");
		MAPPING.put("ESLIM_006_001_035", "FLOAT");
		MAPPING = Collections.unmodifiableMap(MAPPING);

		ObservationType observationType = null;

		Float valueToInsert = 0.0f;

		String datatype = parameter.getDatatype();
		if (MAPPING.containsKey(parameter.getStableId())) {
			datatype = MAPPING.get(parameter.getStableId());
		}

		if (parameter.isMetaDataFlag()) {

			observationType = ObservationType.metadata;

		} else {

			if (parameter.isOptionsFlag()) {

				observationType = ObservationType.categorical;

			} else {

				if (datatype.equals("TEXT")) {

					observationType = ObservationType.text;

				} else if (datatype.equals("DATETIME")) {

					observationType = ObservationType.datetime;

				} else if (datatype.equals("BOOL")) {

					observationType = ObservationType.categorical;

				} else if (datatype.equals("FLOAT") || datatype.equals("INT")) {

					if (parameter.isIncrementFlag()) {

						observationType = ObservationType.time_series;

					} else {

						observationType = ObservationType.unidimensional;

					}

					try {
						if (value != null) {
							valueToInsert = Float.valueOf(value);
						}
					} catch (NumberFormatException ex) {
						logger.debug("Invalid float value: " + value);
						//TODO probably should throw an exception!
					}

				} else if (datatype.equals("IMAGE") || (datatype.equals("") && parameter.getName().contains("images"))) {

					observationType = ObservationType.image_record;

				} else if (datatype.equals("") && !parameter.isOptionsFlag() && !parameter.getName().contains("images")) {

					// is that a number or a category?
					try {
						// check whether it's null
						if (value != null && !value.equals("null")) {

							valueToInsert = Float.valueOf(value);
						}
						if (parameter.isIncrementFlag()) {
							observationType = ObservationType.time_series;
						} else {
							observationType = ObservationType.unidimensional;
						}

					} catch (NumberFormatException ex) {
						observationType = ObservationType.categorical;
					}
				}
			}
		}

		return observationType;
	}


	/**
	 * Always return EFO term for "embryo stage" for now
	 * Future enhancement (once EFO gets a term for "embryonic day 9.5") would be
	 * to return the actual term associated with the stage parameter
	 *
	 * @param stage the stage from impress
	 * @param stageUnit the stage unit applicable to stage
	 * @return the term associated with the correct stage
	 */
	public OntologyTerm getStageTerm(String stage, StageUnitType stageUnit) {

		// Fail fast if the stage is not a "number"
		try {
			Float.parseFloat(stage);
		} catch (NumberFormatException e) {
			return null;
		}

		initializeEfoDbId();

		switch (stageUnit) {

			case DPC:

				// Mouse gestation is 20 days
				if(Float.parseFloat(stage)>21) {
					return null;
				}

				String termName = String.format("embryonic day %s", stage);

				if( ! expectedDpc.contains(stage)) {
					logger.warn("Unexpected value for embryonic DCP stage: "+stage);
				}

				OntologyTerm term = ontologyTermDAO.getOntologyTermByName(termName);
				if (term==null) {
					// Term not found -- create it
					term = createOntologyTerm(termName);
				}

				return term;

			case THEILER:

				return ontologyTermDAO.getOntologyTermByName(String.format("TS%s,embryo", stage));

			default:
				return null;
		}
	}


	/**
	 * Create an EFO OntologyTerm for the passed in termName
	 *
	 * @param termName the name of the term to create
	 * @return the (already persisted) ontology term
	 */
	public OntologyTerm createOntologyTerm(String termName) {

		initializeEfoDbId();

		String termAcc = "NULL-" + DigestUtils.md5Hex(termName).substring(0,9).toUpperCase();

		logger.info("Creating EFO term for name '%s' (Accession: %s)", termName, termAcc);

		OntologyTerm term;
		term = new OntologyTerm();
		term.setId(new DatasourceEntityId(termAcc,efoDbId));
		term.setDescription(termName);
		term.setName(termName);
		ontologyTermDAO.batchInsertion(Arrays.asList(term));

		return term;
	}

	private void initializeEfoDbId() {

		if(efoDbId==null) {
			efoDbId = datasourceDAO.getDatasourceByShortName("EFO").getId();
		}
	}

}
