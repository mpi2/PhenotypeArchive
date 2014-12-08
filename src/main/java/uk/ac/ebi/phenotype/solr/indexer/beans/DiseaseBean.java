/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.solr.indexer.beans;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * @author Matt Pearce
 */
public class DiseaseBean {

	public static final String MGI_ACCESSION_ID = "marker_accession";
	public static final String DISEASE_ID = "disease_id";
	public static final String TYPE = "type";
	public static final String DISEASE_SOURCE = "disease_source";
	public static final String DISEASE_TERM = "disease_term";
	public static final String DISEASE_ALTS = "disease_alts";
	public static final String DISEASE_CLASSES = "disease_classes";
	public static final String HUMAN_CURATED = "human_curated";
	public static final String MOUSE_CURATED = "mouse_curated";
	public static final String MGI_PREDICTED = "mgi_predicted";
	public static final String IMPC_PREDICTED = "impc_predicted";
	public static final String MGI_PREDICTED_KNOWN_GENE = "mgi_predicted_known_gene";
	public static final String IMPC_PREDICTED_KNOWN_GENE = "impc_predicted_known_gene";
	public static final String MGI_NOVEL_PREDICTED_IN_LOCUS = "mgi_novel_predicted_in_locus";
	public static final String IMPC_NOVEL_PREDICTED_IN_LOCUS = "impc_novel_predicted_in_locus";

	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;

	@Field(DISEASE_ID)
	private String diseaseId;

	@Field(TYPE)
	private String type;

	@Field(DISEASE_SOURCE)
	private String diseaseSource;

	@Field(DISEASE_TERM)
	private String diseaseTerm;

	@Field(DISEASE_ALTS)
	private List<String> diseaseAlts;

	@Field(DISEASE_CLASSES)
	private List<String> diseaseClasses;

	@Field(HUMAN_CURATED)
	private boolean humanCurated;

	@Field(MOUSE_CURATED)
	private boolean mouseCurated;

	@Field(MGI_PREDICTED)
	private boolean mgiPredicted;

	@Field(IMPC_PREDICTED)
	private boolean impcPredicted;

	@Field(MGI_PREDICTED_KNOWN_GENE)
	private boolean mgiPredictedKnownGene;

	@Field(IMPC_PREDICTED_KNOWN_GENE)
	private boolean impcPredictedKnownGene;

	@Field(MGI_NOVEL_PREDICTED_IN_LOCUS)
	private boolean mgiNovelPredictedInLocus;

	@Field(IMPC_NOVEL_PREDICTED_IN_LOCUS)
	private boolean impcNovelPredictedInLocus;

	public String getMgiAccessionId() {

		return mgiAccessionId;
	}


	public void setMgiAccessionId(String mgiAccessionId) {

		this.mgiAccessionId = mgiAccessionId;
	}


	public String getType() {

		return type;
	}


	public void setType(String type) {

		this.type = type;
	}


	/**
	 * @return the disease_id
	 */
	public String getDiseaseId() {
		return diseaseId;
	}

	/**
	 * @param disease_id the disease_id to set
	 */
	public void setDiseaseId(String disease_id) {
		this.diseaseId = disease_id;
	}

	/**
	 * @return the disease_source
	 */
	public String getDiseaseSource() {
		return diseaseSource;
	}

	/**
	 * @param disease_source the disease_source to set
	 */
	public void setDiseaseSource(String disease_source) {
		this.diseaseSource = disease_source;
	}

	/**
	 * @return the disease_term
	 */
	public String getDiseaseTerm() {
		return diseaseTerm;
	}

	/**
	 * @param disease_term the disease_term to set
	 */
	public void setDiseaseTerm(String disease_term) {
		this.diseaseTerm = disease_term;
	}

	/**
	 * @return the disease_alts
	 */
	public List<String> getDiseaseAlts() {
		return diseaseAlts;
	}

	/**
	 * @param disease_alts the disease_alts to set
	 */
	public void setDiseaseAlts(List<String> disease_alts) {
		this.diseaseAlts = disease_alts;
	}

	/**
	 * @return the disease_classes
	 */
	public List<String> getDiseaseClasses() {
		return diseaseClasses;
	}

	/**
	 * @param disease_classes the disease_classes to set
	 */
	public void setDiseaseClasses(List<String> disease_classes) {
		this.diseaseClasses = disease_classes;
	}

	/**
	 * @return the human_curated
	 */
	public boolean isHumanCurated() {
		return humanCurated;
	}

	/**
	 * @param human_curated the human_curated to set
	 */
	public void setHumanCurated(boolean human_curated) {
		this.humanCurated = human_curated;
	}

	/**
	 * @return the mouse_curated
	 */
	public boolean isMouseCurated() {
		return mouseCurated;
	}

	/**
	 * @param mouse_curated the mouse_curated to set
	 */
	public void setMouseCurated(boolean mouse_curated) {
		this.mouseCurated = mouse_curated;
	}

	/**
	 * @return the mgi_predicted
	 */
	public boolean isMgiPredicted() {
		return mgiPredicted;
	}

	/**
	 * @param mgi_predicted the mgi_predicted to set
	 */
	public void setMgiPredicted(boolean mgi_predicted) {
		this.mgiPredicted = mgi_predicted;
	}

	/**
	 * @return the impc_predicted
	 */
	public boolean isImpcPredicted() {
		return impcPredicted;
	}

	/**
	 * @param impc_predicted the impc_predicted to set
	 */
	public void setImpcPredicted(boolean impc_predicted) {
		this.impcPredicted = impc_predicted;
	}

	/**
	 * @return the mgi_predicted_known_gene
	 */
	public boolean isMgiPredictedKnownGene() {
		return mgiPredictedKnownGene;
	}

	/**
	 * @param mgi_predicted_known_gene the mgi_predicted_known_gene to set
	 */
	public void setMgiPredictedKnownGene(boolean mgi_predicted_known_gene) {
		this.mgiPredictedKnownGene = mgi_predicted_known_gene;
	}

	/**
	 * @return the impc_predicted_known_gene
	 */
	public boolean isImpcPredictedKnownGene() {
		return impcPredictedKnownGene;
	}

	/**
	 * @param impc_predicted_known_gene the impc_predicted_known_gene to set
	 */
	public void setImpcPredictedKnownGene(boolean impc_predicted_known_gene) {
		this.impcPredictedKnownGene = impc_predicted_known_gene;
	}

	/**
	 * @return the mgi_novel_predicted_in_locus
	 */
	public boolean isMgiNovelPredictedInLocus() {
		return mgiNovelPredictedInLocus;
	}

	/**
	 * @param mgi_novel_predicted_in_locus the mgi_novel_predicted_in_locus to set
	 */
	public void setMgiNovelPredictedInLocus(boolean mgi_novel_predicted_in_locus) {
		this.mgiNovelPredictedInLocus = mgi_novel_predicted_in_locus;
	}

	/**
	 * @return the impc_novel_predicted_in_locus
	 */
	public boolean isImpcNovelPredictedInLocus() {
		return impcNovelPredictedInLocus;
	}

	/**
	 * @param impc_novel_predicted_in_locus the impc_novel_predicted_in_locus to set
	 */
	public void setImpcNovelPredictedInLocus(boolean impc_novel_predicted_in_locus) {
		this.impcNovelPredictedInLocus = impc_novel_predicted_in_locus;
	}

}
