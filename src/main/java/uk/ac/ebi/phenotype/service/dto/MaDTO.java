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
package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;
import java.util.Objects;


/**
 * Created by mrelac on 19/11/2014.
 */
public class MaDTO {
    public static final String DATA_TYPE = "dataType";
    public static final String MA_ID = "ma_id";
    public static final String MA_TERM = "ma_term";
    public static final String MA_TERM_SYNONYM = "ma_term_synonym";
    public static final String ONTOLOGY_SUBSET = "ontology_subset";

    public static final String CHILD_MA_ID = "child_ma_id";
    public static final String CHILD_MA_TERM = "child_ma_term";
    public static final String CHILD_MA_TERM_SYNONYM = "child_ma_term_synonym";
    public static final String CHILD_MA_ID_TERM = "child_ma_idTerm";

    public static final String TOP_LEVEL_MA_ID = "top_level_ma_id";
    public static final String TOP_LEVEL_MA_TERM = "top_level_ma_term";
//    public static final String TOP_LEVEL_MA_TERM_SYNONYM = "top_level_ma_term_synonym";

    public static final String SELECTED_TOP_LEVEL_MA_ID = "selected_top_level_ma_id";
    public static final String SELECTED_TOP_LEVEL_MA_TERM = "selected_top_level_ma_term";
    public static final String SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "selected_top_level_ma_term_synonym";

    // mp to hp mapping
    public static final String HP_ID = "hp_id";
    public static final String HP_TERM = "hp_term";
    public static final String GO_ID = "go_id";

    // gene core stuff
    public static final String P_VALUE = "p_value";
    public static final String MGI_ACCESSION_ID = "mgi_accession_id";
    public static final String MARKER_SYMBOL = "marker_symbol";
    public static final String MARKER_NAME = "marker_name";
    public static final String MARKER_SYNONYM = "marker_synonym";
    public static final String MARKER_TYPE = "marker_type";
    public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";

    // latest mouse status
    public static final String STATUS = "status";

    // phenotyping status
    public static final String IMITS_PHENOTYPE_STARTED = "imits_phenotype_started";
    public static final String IMITS_PHENOTYPE_COMPLETE = "imits_phenotype_complete";
    public static final String IMITS_PHENOTYPE_STATUS = "imits_phenotype_status";

    // centers
    public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
    public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
    public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
    public static final String LEGACY_PHENOTYPE_STATUS = "legacy_phenotype_status";

    // allele level fields of a gene
    public static final String ALLELE_NAME = "allele_name";

    // disease core stuff
    public static final String TYPE = "type";
    public static final String DISEASE_ID = "disease_id";
    public static final String DISEASE_SOURCE = "disease_source";
    public static final String DISEASE_TERM = "disease_term";
    public static final String DISEASE_ALTS = "disease_alts";
    public static final String DISEASE_CLASSES = "disease_classes";
    public static final String DISEASE_HUMAN_PHENOTYPES = "disease_human_phenotypes";
    public static final String HUMAN_CURATED = "human_curated";
    public static final String MOUSE_CURATED = "mouse_curated";
    public static final String MGI_PREDICTED = "mgi_predicted";
    public static final String IMPC_PREDICTED = "impc_predicted";
    public static final String MGI_PREDICTED_KNOWN_GENE = "mgi_predicted_known_gene";
    public static final String IMPC_PREDICTED_KNOWN_GENE = "impc_predicted_known_gene";
    public static final String MGI_NOVEL_PREDICTED_IN_LOCUS = "mgi_novel_predicted_in_locus";
    public static final String IMPC_NOVEL_PREDICTED_IN_LOCUS = "impc_novel_predicted_in_locus";

    // images core stuff
    public static final String ANNOTATION_TERM_ID = "annotationTermId";
    public static final String ANNOTATION_TERM_NAME = "annotationTermName";
    public static final String NAME = "name";
    public static final String ACCESSION = "accession";
    public static final String EXP_NAME = "expName";

    public static final String LARGE_THUMBNAIL_FILE_PATH = "largeThumbnailFilePath";
    public static final String SMALL_THUMBNAIL_FILE_PATH = "smallThumbnailFilePath";

    public static final String INFERRED_MA_TERM_ID = "inferredMaTermId";
    public static final String INFERRED_MA_TERM_NAME = "inferredMaTermName";
    public static final String ANNOTATED_HIGHER_LEVEL_MA_TERM_ID = "annotatedHigherLevelMaTermId";
    public static final String ANNOTATED_HIGHER_LEVEL_MA_TERM_NAME = "annotatedHigherLevelMaTermName";
    public static final String ANNOTATED_HIGHER_LEVEL_MP_TERM_ID = "annotatedHigherLevelMpTermId";
    public static final String ANNOTATED_HIGHER_LEVEL_MP_TERM_NAME = "annotatedHigherLevelMpTermName";
    public static final String INFERRED_HIGHER_LEVEL_MA_TERM_ID = "inferredHigherLevelMaTermId";
    public static final String INFERRED_HIGHER_LEVEL_MA_TERM_NAME = "inferredHigherLevelMaTermName";

    public static final String ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_NAME = "annotated_or_inferred_higherLevelMaTermName";
    public static final String ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_ID = "annotated_or_inferred_higherLevelMaTermId";

    public static final String SYMBOL = "symbol";
    public static final String SANGER_SYMBOL = "sangerSymbol";
    public static final String GENE_NAME = "geneName";
    public static final String SUBTYPE = "subtype";
    public static final String GENE_SYNONYMS = "geneSynonyms";

    public static final String MA_TERM_ID = "maTermId";
    public static final String MA_TERM_NAME = "maTermName";
    public static final String MP_TERM_ID = "mpTermId";
    public static final String MP_TERM_NAME = "mpTermName";
    public static final String EXP_NAME_EXP = "expName_exp";
    public static final String SYMBOL_GENE = "symbol_gene";
    public static final String TOP_LEVEL = "topLevel";

    public static final String ALLELE_SYMBOL = "allele_symbol";
    public static final String ALLELE_ID = "allele_id";

    public static final String STRAIN_NAME = "strain_name";
    public static final String STRAIN_ID = "strain_id";
    public static final String GENETIC_BACKGROUND = "genetic_background";

    public static final String PIPELINE_NAME = "pipeline_name";
    public static final String PIPELINE_STABLE_ID = "pipeline_stable_id";
    public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key";


    public static final String PROCEDURE_NAME = "procedure_name";
    public static final String PROCEDURE_STABLE_ID = "procedure_stable_id";
    public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key";

    public static final String PARAMETER_NAME = "parameter_name";
    public static final String PARAMETER_STABLE_ID = "parameter_stable_id";
    public static final String PARAMETER_STABLE_KEY = "parameter_stable_key";

    public static final String MP_ID = "mp_id";
    public static final String MP_TERM = "mp_term";
    public static final String MP_TERM_SYNONYM = "mp_term_synonym";

    public static final String TOP_LEVEL_MP_ID = "top_level_mp_id";
    public static final String TOP_LEVEL_MP_TERM = "top_level_mp_term";
    public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";

    public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
    public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
    public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";

    public static final String CHILD_MP_ID = "child_mp_id";
    public static final String CHILD_MP_TERM = "child_mp_term";
    public static final String CHILD_MP_TERM_SYNONYM = "child_mp_term_synonym";

    // catchall field, containing all other searchable text fields
    public static final String TEXT = "text";
    public static final String AUTO_SUGGEST = "auto_suggest";

    // bucket list qf
    public static final String GENE_QF = "geneQf";
    public static final String MP_QF = "mpQf";
    public static final String DISEASE_QF = "diseaseQf";
    public static final String MA_QF = "maQf";


    @Field(DATA_TYPE)
    private String dataType;

    @Field(MA_ID)
    private String maId;

    @Field(MA_TERM)
    private String maTerm;

    @Field(MA_TERM_SYNONYM)
    private List<String> maTermSynonym;

    @Field(ONTOLOGY_SUBSET)
    private List<String> ontologySubset;

    @Field(CHILD_MA_ID)
    private List<String> childMaId;

    @Field(CHILD_MA_TERM)
    private List<String> childMaTerm;

    @Field(CHILD_MA_TERM_SYNONYM)
    private List<String> childMaTermSynonym;

    @Field(CHILD_MA_ID_TERM)
    private List<String> childMaIdTerm;


    @Field(TOP_LEVEL_MA_ID)
    private List<String> topLevelMaId;

    @Field(TOP_LEVEL_MA_TERM)
    private List<String> topLevelMaTerm;

//    @Field(TOP_LEVEL_MA_TERM_SYNONYM)
//    private List<String> topLevelMaTermSynonym;

    @Field(SELECTED_TOP_LEVEL_MA_ID)
    private List<String> selectedTopLevelMaId;

    @Field(SELECTED_TOP_LEVEL_MA_TERM)
    private List<String> selectedTopLevelMaTerm;

    @Field(SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
    private List<String> selectedTopLevelMaTermSynonym;

    // mp to hp mapping
    @Field(HP_ID)
    private List<String> hpId;

    @Field(HP_TERM)
    private List<String> hpTerm;

    @Field(GO_ID)
    private List<String> goId;

    // gene core stuff
    @Field(P_VALUE)
    private List<Float> pValue;

    @Field(MGI_ACCESSION_ID)
    private List<String> mgiAccessionId;

    @Field(MARKER_SYMBOL)
    private List<String> markerSymbol;

    @Field(MARKER_NAME)
    private List<String> markerName;

    @Field(MARKER_SYNONYM)
    private List<String> markerSynonym;

    @Field(MARKER_TYPE)
    private List<String> markerType;

    @Field(HUMAN_GENE_SYMBOL)
    private List<String> humanGeneSymbol;


    // latest mouse status
    @Field(STATUS)
    private List<String> status;


    // phenotyping status
    @Field(IMITS_PHENOTYPE_STARTED)
    private List<String> imitsPhenotypeStarted;

    @Field(IMITS_PHENOTYPE_COMPLETE)
    private List<String> imitsPhenotypeComplete;

    @Field(IMITS_PHENOTYPE_STATUS)
    private List<String> imitsPhenotypeStatus;


    // centers
    @Field(LATEST_PRODUCTION_CENTRE)
    private List<String> latestProductionCentre;

    @Field(LATEST_PHENOTYPING_CENTRE)
    private List<String> latestPhenotypingCentre;

    @Field(LATEST_PHENOTYPE_STATUS)
    private List<String> latestPhenotypeStatus;

    @Field(LEGACY_PHENOTYPE_STATUS)
    private List<Integer> legacyPhenotypeStatus;


    // allele level fields of a gene
    @Field(ALLELE_NAME)
    private List<String> alleleName;


    // disease core stuff
    @Field(TYPE)
    private List<String> type;

    @Field(DISEASE_ID)
    private List<String> diseaseId;

    @Field(DISEASE_SOURCE)
    private List<String> diseaseSource;

    @Field(DISEASE_TERM)
    private List<String> diseaseTerm;

    @Field(DISEASE_ALTS)
    private List<String> diseaseAlts;

    @Field(DISEASE_CLASSES)
    private List<String> diseaseClasses;

    @Field(DISEASE_HUMAN_PHENOTYPES)
    private List<String> diseaseHumanPhenotypes;

    @Field(HUMAN_CURATED)
    private List<Boolean> humanCurated;

    @Field(MOUSE_CURATED)
    private List<Boolean> mouseCurated;

    @Field(MGI_PREDICTED)
    private List<Boolean> mgiPredicted;

    @Field(IMPC_PREDICTED)
    private List<Boolean> impcPredicted;

    @Field(MGI_PREDICTED_KNOWN_GENE)
    private List<Boolean> mgiPredictedKnownGene;

    @Field(IMPC_PREDICTED_KNOWN_GENE)
    private List<Boolean> impcPredictedKnownGene;

    @Field(MGI_NOVEL_PREDICTED_IN_LOCUS)
    private List<Boolean> mgiNovelPredictedInLocus;

    @Field(IMPC_NOVEL_PREDICTED_IN_LOCUS)
    private List<Boolean> impcNovelPredictedInLocus;


    // images core stuff
    @Field(ANNOTATION_TERM_ID)
    private List<String> annotationTermId;

    @Field(ANNOTATION_TERM_NAME)
    private List<String> annotationTermName;

    @Field(NAME)
    private List<String> name;

    @Field(ACCESSION)
    private List<String> accession;

    @Field(EXP_NAME)
    private List<String> expName;

    @Field(LARGE_THUMBNAIL_FILE_PATH)
    private List<String> largeThumbnailFilePath;

    @Field(SMALL_THUMBNAIL_FILE_PATH)
    private List<String> smallThumbnailFilePath;

    @Field(INFERRED_MA_TERM_ID)
    private List<String> inferredMaTermId;

    @Field(INFERRED_MA_TERM_NAME)
    private List<String> inferredMaTermName;

    @Field(ANNOTATED_HIGHER_LEVEL_MA_TERM_ID)
    private List<String> annotatedHigherLevelMaTermId;

    @Field(ANNOTATED_HIGHER_LEVEL_MA_TERM_NAME)
    private List<String> annotatedHigherLevelMaTermName;

    @Field(ANNOTATED_HIGHER_LEVEL_MP_TERM_ID)
    private List<String> annotatedHigherLevelMpTermId;

    @Field(ANNOTATED_HIGHER_LEVEL_MP_TERM_NAME)
    private List<String> annotatedHigherLevelMpTermName;

    @Field(INFERRED_HIGHER_LEVEL_MA_TERM_ID)
    private List<String> inferredHigherLevelMaTermId;

    @Field(INFERRED_HIGHER_LEVEL_MA_TERM_NAME)
    private List<String> inferredHigherLevelMaTermName;

    @Field(ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_NAME)
    private List<String> annotatedOrInferredHigherLevelMaTermName;

    @Field(ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_ID)
    private List<String> annotatedOrInferredHigherLevelMaTermId;

    @Field(SYMBOL)
    private List<String> symbol;

    @Field(SANGER_SYMBOL)
    private List<String> sangerSymbol;

    @Field(GENE_NAME)
    private List<String> geneName;

    @Field(SUBTYPE)
    private List<String> subtype;

    @Field(GENE_SYNONYMS)
    private List<String> geneSynonyms;

    @Field(MA_TERM_ID)
    private List<String> maTermId;

    @Field(MA_TERM_NAME)
    private List<String> maTermName;

    @Field(MP_TERM_ID)
    private List<String> mpTermId;

    @Field(MP_TERM_NAME)
    private List<String> mpTermName;

    @Field(EXP_NAME_EXP)
    private List<String> expNameExp;

    @Field(SYMBOL_GENE)
    private List<String> symbolGene;

    @Field(TOP_LEVEL)
    private List<String> topLevel;

    @Field(ALLELE_SYMBOL)
    private List<String> alleleSymbol;

    @Field(ALLELE_ID)
    private List<String> alleleId;

    @Field(STRAIN_NAME)
    private List<String> strainName;

    @Field(STRAIN_ID)
    private List<String> strainId;

    @Field(GENETIC_BACKGROUND)
    String geneticBackground;

    @Field(PIPELINE_NAME)
    private List<String> pipelineName;

    @Field(PIPELINE_STABLE_ID)
    private List<String> pipelineStableId;

    @Field(PIPELINE_STABLE_KEY)
    private List<String> pipelineStableKey;

    @Field(PROCEDURE_NAME)
    private List<String> procedureName;

    @Field(PROCEDURE_STABLE_ID)
    private List<String> procedureStableId;

    @Field(PROCEDURE_STABLE_KEY)
    private List<String> procedureStableKey;

    @Field(PARAMETER_NAME)
    private List<String> parameterName;

    @Field(PARAMETER_STABLE_ID)
    private List<String> parameterStableId;

    @Field(PARAMETER_STABLE_KEY)
    private List<String> parameterStableKey;

    @Field(MP_ID)
    private List<String> mpId;

    @Field(MP_TERM)
    private List<String> mpTerm;

    @Field(MP_TERM_SYNONYM)
    private List<String> mpTermSynonym;

    @Field(TOP_LEVEL_MP_ID)
    private List<String> topLevelMpId;

    @Field(TOP_LEVEL_MP_TERM)
    private List<String> topLevelMpTerm;

    @Field(TOP_LEVEL_MP_TERM_SYNONYM)
    private List<String> topLevelMpTermSynonym;


    @Field(INTERMEDIATE_MP_ID)
    private List<String> intermediateMpId;

    @Field(INTERMEDIATE_MP_TERM)
    private List<String> intermediateMpTerm;

    @Field(INTERMEDIATE_MP_TERM_SYNONYM)
    private List<String> intermediateMpTermSynonym;

    @Field(CHILD_MP_ID)
    private List<String> childMpId;

    @Field(CHILD_MP_TERM)
    private List<String> childMpTerm;

    @Field(CHILD_MP_TERM_SYNONYM)
    private List<String> childMpTermSynonym;

    // catchall field, containing all other searchable text fields
    @Field(TEXT)
    private List<String> text;

    @Field(AUTO_SUGGEST)
    private List<String> autoSuggest;

    // bucket list qf
    @Field(GENE_QF)
    private List<String> geneQf;

    @Field(MP_QF)
    private List<String> mpQf;

    @Field(DISEASE_QF)
    private List<String> diseaseQf;

    @Field(MA_QF)
    private List<String> maQf;
        
        
        // SETTERS AND GETTERS

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getMaId() {
        return maId;
    }

    public void setMaId(String maId) {
        this.maId = maId;
    }

    public String getMaTerm() {
        return maTerm;
    }

    public void setMaTerm(String maTerm) {
        this.maTerm = maTerm;
    }

    public List<String> getMaTermSynonym() {
        return maTermSynonym;
    }

    public void setMaTermSynonym(List<String> maTermSynonym) {
        this.maTermSynonym = maTermSynonym;
    }

    public List<String> getOntologySubset() {
        return ontologySubset;
    }

    public void setOntologySubset(List<String> ontologySubset) {
        this.ontologySubset = ontologySubset;
    }

    public List<String> getChildMaId() {
        return childMaId;
    }

    public void setChildMaId(List<String> childMaId) {
        this.childMaId = childMaId;
    }

    public List<String> getChildMaTerm() {
        return childMaTerm;
    }

    public void setChildMaTerm(List<String> childMaTerm) {
        this.childMaTerm = childMaTerm;
    }

    public List<String> getChildMaTermSynonym() {
        return childMaTermSynonym;
    }

    public void setChildMaTermSynonym(List<String> childMaTermSynonym) {
        this.childMaTermSynonym = childMaTermSynonym;
    }

    public List<String> getChildMaIdTerm() {
        return childMaIdTerm;
    }

    public void setChildMaIdTerm(List<String> childMaIdTerm) {
        this.childMaIdTerm = childMaIdTerm;
    }

    public List<String> getTopLevelMaId() {
        return topLevelMaId;
    }

    public void setTopLevelMaId(List<String> topLevelMaId) {
        this.topLevelMaId = topLevelMaId;
    }

    public List<String> getTopLevelMaTerm() {
        return topLevelMaTerm;
    }

    public void setTopLevelMaTerm(List<String> topLevelMaTerm) {
        this.topLevelMaTerm = topLevelMaTerm;
    }

//    public List<String> getTopLevelMaTermSynonym() {
//        return topLevelMaTermSynonym;
//    }
//
//    public void setTopLevelMaTermSynonym(List<String> topLevelMaTermSynonym) {
//        this.topLevelMaTermSynonym = topLevelMaTermSynonym;
//    }

    public List<String> getSelectedTopLevelMaId() {
        return selectedTopLevelMaId;
    }

    public void setSelectedTopLevelMaId(List<String> selectedTopLevelMaId) {
        this.selectedTopLevelMaId = selectedTopLevelMaId;
    }

    public List<String> getSelectedTopLevelMaTerm() {
        return selectedTopLevelMaTerm;
    }

    public void setSelectedTopLevelMaTerm(List<String> selectedTopLevelMaTerm) {
        this.selectedTopLevelMaTerm = selectedTopLevelMaTerm;
    }

    public List<String> getSelectedTopLevelMaTermSynonym() {
        return selectedTopLevelMaTermSynonym;
    }

    public void setSelectedTopLevelMaTermSynonym(List<String> selectedTopLevelMaTermSynonym) {
        this.selectedTopLevelMaTermSynonym = selectedTopLevelMaTermSynonym;
    }

    public List<String> getHpId() {
        return hpId;
    }

    public void setHpId(List<String> hpId) {
        this.hpId = hpId;
    }

    public List<String> getHpTerm() {
        return hpTerm;
    }

    public void setHpTerm(List<String> hpTerm) {
        this.hpTerm = hpTerm;
    }

    public List<String> getGoId() {
        return goId;
    }

    public void setGoId(List<String> goId) {
        this.goId = goId;
    }

    public List<Float> getpValue() {
        return pValue;
    }

    public void setpValue(List<Float> pValue) {
        this.pValue = pValue;
    }

    public List<String> getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(List<String> mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public List<String> getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(List<String> markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public List<String> getMarkerName() {
        return markerName;
    }

    public void setMarkerName(List<String> markerName) {
        this.markerName = markerName;
    }

    public List<String> getMarkerSynonym() {
        return markerSynonym;
    }

    public void setMarkerSynonym(List<String> markerSynonym) {
        this.markerSynonym = markerSynonym;
    }

    public List<String> getMarkerType() {
        return markerType;
    }

    public void setMarkerType(List<String> markerType) {
        this.markerType = markerType;
    }

    public List<String> getHumanGeneSymbol() {
        return humanGeneSymbol;
    }

    public void setHumanGeneSymbol(List<String> humanGeneSymbol) {
        this.humanGeneSymbol = humanGeneSymbol;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getImitsPhenotypeStarted() {
        return imitsPhenotypeStarted;
    }

    public void setImitsPhenotypeStarted(List<String> imitsPhenotypeStarted) {
        this.imitsPhenotypeStarted = imitsPhenotypeStarted;
    }

    public List<String> getImitsPhenotypeComplete() {
        return imitsPhenotypeComplete;
    }

    public void setImitsPhenotypeComplete(List<String> imitsPhenotypeComplete) {
        this.imitsPhenotypeComplete = imitsPhenotypeComplete;
    }

    public List<String> getImitsPhenotypeStatus() {
        return imitsPhenotypeStatus;
    }

    public void setImitsPhenotypeStatus(List<String> imitsPhenotypeStatus) {
        this.imitsPhenotypeStatus = imitsPhenotypeStatus;
    }

    public List<String> getLatestProductionCentre() {
        return latestProductionCentre;
    }

    public void setLatestProductionCentre(List<String> latestProductionCentre) {
        this.latestProductionCentre = latestProductionCentre;
    }

    public List<String> getLatestPhenotypingCentre() {
        return latestPhenotypingCentre;
    }

    public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {
        this.latestPhenotypingCentre = latestPhenotypingCentre;
    }

    public List<String> getLatestPhenotypeStatus() {
        return latestPhenotypeStatus;
    }

    public void setLatestPhenotypeStatus(List<String> latestPhenotypeStatus) {
        this.latestPhenotypeStatus = latestPhenotypeStatus;
    }

    public List<Integer> getLegacyPhenotypeStatus() {
        return legacyPhenotypeStatus;
    }

    public void setLegacyPhenotypeStatus(List<Integer> legacyPhenotypeStatus) {
        this.legacyPhenotypeStatus = legacyPhenotypeStatus;
    }

    public List<String> getAlleleName() {
        return alleleName;
    }

    public void setAlleleName(List<String> alleleName) {
        this.alleleName = alleleName;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<String> getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(List<String> diseaseId) {
        this.diseaseId = diseaseId;
    }

    public List<String> getDiseaseSource() {
        return diseaseSource;
    }

    public void setDiseaseSource(List<String> diseaseSource) {
        this.diseaseSource = diseaseSource;
    }

    public List<String> getDiseaseTerm() {
        return diseaseTerm;
    }

    public void setDiseaseTerm(List<String> diseaseTerm) {
        this.diseaseTerm = diseaseTerm;
    }

    public List<String> getDiseaseAlts() {
        return diseaseAlts;
    }

    public void setDiseaseAlts(List<String> diseaseAlts) {
        this.diseaseAlts = diseaseAlts;
    }

    public List<String> getDiseaseClasses() {
        return diseaseClasses;
    }

    public void setDiseaseClasses(List<String> diseaseClasses) {
        this.diseaseClasses = diseaseClasses;
    }

    public List<String> getDiseaseHumanPhenotypes() {
        return diseaseHumanPhenotypes;
    }

    public void setDiseaseHumanPhenotypes(List<String> diseaseHumanPhenotypes) {
        this.diseaseHumanPhenotypes = diseaseHumanPhenotypes;
    }

    public List<Boolean> getHumanCurated() {
        return humanCurated;
    }

    public void setHumanCurated(List<Boolean> humanCurated) {
        this.humanCurated = humanCurated;
    }

    public List<Boolean> getMouseCurated() {
        return mouseCurated;
    }

    public void setMouseCurated(List<Boolean> mouseCurated) {
        this.mouseCurated = mouseCurated;
    }

    public List<Boolean> getMgiPredicted() {
        return mgiPredicted;
    }

    public void setMgiPredicted(List<Boolean> mgiPredicted) {
        this.mgiPredicted = mgiPredicted;
    }

    public List<Boolean> getImpcPredicted() {
        return impcPredicted;
    }

    public void setImpcPredicted(List<Boolean> impcPredicted) {
        this.impcPredicted = impcPredicted;
    }

    public List<Boolean> getMgiPredictedKnownGene() {
        return mgiPredictedKnownGene;
    }

    public void setMgiPredictedKnownGene(List<Boolean> mgiPredictedKnownGene) {
        this.mgiPredictedKnownGene = mgiPredictedKnownGene;
    }

    public List<Boolean> getImpcPredictedKnownGene() {
        return impcPredictedKnownGene;
    }

    public void setImpcPredictedKnownGene(List<Boolean> impcPredictedKnownGene) {
        this.impcPredictedKnownGene = impcPredictedKnownGene;
    }

    public List<Boolean> getMgiNovelPredictedInLocus() {
        return mgiNovelPredictedInLocus;
    }

    public void setMgiNovelPredictedInLocus(List<Boolean> mgiNovelPredictedInLocus) {
        this.mgiNovelPredictedInLocus = mgiNovelPredictedInLocus;
    }

    public List<Boolean> getImpcNovelPredictedInLocus() {
        return impcNovelPredictedInLocus;
    }

    public void setImpcNovelPredictedInLocus(List<Boolean> impcNovelPredictedInLocus) {
        this.impcNovelPredictedInLocus = impcNovelPredictedInLocus;
    }

    public List<String> getAnnotationTermId() {
        return annotationTermId;
    }

    public void setAnnotationTermId(List<String> annotationTermId) {
        this.annotationTermId = annotationTermId;
    }

    public List<String> getAnnotationTermName() {
        return annotationTermName;
    }

    public void setAnnotationTermName(List<String> annotationTermName) {
        this.annotationTermName = annotationTermName;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getAccession() {
        return accession;
    }

    public void setAccession(List<String> accession) {
        this.accession = accession;
    }

    public List<String> getExpName() {
        return expName;
    }

    public void setExpName(List<String> expName) {
        this.expName = expName;
    }

    public List<String> getLargeThumbnailFilePath() {
        return largeThumbnailFilePath;
    }

    public void setLargeThumbnailFilePath(List<String> largeThumbnailFilePath) {
        this.largeThumbnailFilePath = largeThumbnailFilePath;
    }

    public List<String> getSmallThumbnailFilePath() {
        return smallThumbnailFilePath;
    }

    public void setSmallThumbnailFilePath(List<String> smallThumbnailFilePath) {
        this.smallThumbnailFilePath = smallThumbnailFilePath;
    }

    public List<String> getInferredMaTermId() {
        return inferredMaTermId;
    }

    public void setInferredMaTermId(List<String> inferredMaTermId) {
        this.inferredMaTermId = inferredMaTermId;
    }

    public List<String> getInferredMaTermName() {
        return inferredMaTermName;
    }

    public void setInferredMaTermName(List<String> inferredMaTermName) {
        this.inferredMaTermName = inferredMaTermName;
    }

    public List<String> getAnnotatedHigherLevelMaTermId() {
        return annotatedHigherLevelMaTermId;
    }

    public void setAnnotatedHigherLevelMaTermId(List<String> annotatedHigherLevelMaTermId) {
        this.annotatedHigherLevelMaTermId = annotatedHigherLevelMaTermId;
    }

    public List<String> getAnnotatedHigherLevelMaTermName() {
        return annotatedHigherLevelMaTermName;
    }

    public void setAnnotatedHigherLevelMaTermName(List<String> annotatedHigherLevelMaTermName) {
        this.annotatedHigherLevelMaTermName = annotatedHigherLevelMaTermName;
    }

    public List<String> getAnnotatedHigherLevelMpTermId() {
        return annotatedHigherLevelMpTermId;
    }

    public void setAnnotatedHigherLevelMpTermId(List<String> annotatedHigherLevelMpTermId) {
        this.annotatedHigherLevelMpTermId = annotatedHigherLevelMpTermId;
    }

    public List<String> getAnnotatedHigherLevelMpTermName() {
        return annotatedHigherLevelMpTermName;
    }

    public void setAnnotatedHigherLevelMpTermName(List<String> annotatedHigherLevelMpTermName) {
        this.annotatedHigherLevelMpTermName = annotatedHigherLevelMpTermName;
    }

    public List<String> getInferredHigherLevelMaTermId() {
        return inferredHigherLevelMaTermId;
    }

    public void setInferredHigherLevelMaTermId(List<String> inferredHigherLevelMaTermId) {
        this.inferredHigherLevelMaTermId = inferredHigherLevelMaTermId;
    }

    public List<String> getInferredHigherLevelMaTermName() {
        return inferredHigherLevelMaTermName;
    }

    public void setInferredHigherLevelMaTermName(List<String> inferredHigherLevelMaTermName) {
        this.inferredHigherLevelMaTermName = inferredHigherLevelMaTermName;
    }

    public List<String> getAnnotatedOrInferredHigherLevelMaTermName() {
        return annotatedOrInferredHigherLevelMaTermName;
    }

    public void setAnnotatedOrInferredHigherLevelMaTermName(List<String> annotatedOrInferredHigherLevelMaTermName) {
        this.annotatedOrInferredHigherLevelMaTermName = annotatedOrInferredHigherLevelMaTermName;
    }

    public List<String> getAnnotatedOrInferredHigherLevelMaTermId() {
        return annotatedOrInferredHigherLevelMaTermId;
    }

    public void setAnnotatedOrInferredHigherLevelMaTermId(List<String> annotatedOrInferredHigherLevelMaTermId) {
        this.annotatedOrInferredHigherLevelMaTermId = annotatedOrInferredHigherLevelMaTermId;
    }

    public List<String> getSymbol() {
        return symbol;
    }

    public void setSymbol(List<String> symbol) {
        this.symbol = symbol;
    }

    public List<String> getSangerSymbol() {
        return sangerSymbol;
    }

    public void setSangerSymbol(List<String> sangerSymbol) {
        this.sangerSymbol = sangerSymbol;
    }

    public List<String> getGeneName() {
        return geneName;
    }

    public void setGeneName(List<String> geneName) {
        this.geneName = geneName;
    }

    public List<String> getSubtype() {
        return subtype;
    }

    public void setSubtype(List<String> subtype) {
        this.subtype = subtype;
    }

    public List<String> getGeneSynonyms() {
        return geneSynonyms;
    }

    public void setGeneSynonyms(List<String> geneSynonyms) {
        this.geneSynonyms = geneSynonyms;
    }

    public List<String> getMaTermId() {
        return maTermId;
    }

    public void setMaTermId(List<String> maTermId) {
        this.maTermId = maTermId;
    }

    public List<String> getMaTermName() {
        return maTermName;
    }

    public void setMaTermName(List<String> maTermName) {
        this.maTermName = maTermName;
    }

    public List<String> getMpTermId() {
        return mpTermId;
    }

    public void setMpTermId(List<String> mpTermId) {
        this.mpTermId = mpTermId;
    }

    public List<String> getMpTermName() {
        return mpTermName;
    }

    public void setMpTermName(List<String> mpTermName) {
        this.mpTermName = mpTermName;
    }

    public List<String> getExpNameExp() {
        return expNameExp;
    }

    public void setExpNameExp(List<String> expNameExp) {
        this.expNameExp = expNameExp;
    }

    public List<String> getSymbolGene() {
        return symbolGene;
    }

    public void setSymbolGene(List<String> symbolGene) {
        this.symbolGene = symbolGene;
    }

    public List<String> getTopLevel() {
        return topLevel;
    }

    public void setTopLevel(List<String> topLevel) {
        this.topLevel = topLevel;
    }

    public List<String> getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(List<String> alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    public List<String> getAlleleId() {
        return alleleId;
    }

    public void setAlleleId(List<String> alleleId) {
        this.alleleId = alleleId;
    }

    public List<String> getStrainName() {
        return strainName;
    }

    public void setStrainName(List<String> strainName) {
        this.strainName = strainName;
    }

    public List<String> getStrainId() {
        return strainId;
    }

    public void setStrainId(List<String> strainId) {
        this.strainId = strainId;
    }

    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
    }

    public List<String> getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(List<String> pipelineName) {
        this.pipelineName = pipelineName;
    }

    public List<String> getPipelineStableId() {
        return pipelineStableId;
    }

    public void setPipelineStableId(List<String> pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    public List<String> getPipelineStableKey() {
        return pipelineStableKey;
    }

    public void setPipelineStableKey(List<String> pipelineStableKey) {
        this.pipelineStableKey = pipelineStableKey;
    }

    public List<String> getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(List<String> procedureName) {
        this.procedureName = procedureName;
    }

    public List<String> getProcedureStableId() {
        return procedureStableId;
    }

    public void setProcedureStableId(List<String> procedureStableId) {
        this.procedureStableId = procedureStableId;
    }

    public List<String> getProcedureStableKey() {
        return procedureStableKey;
    }

    public void setProcedureStableKey(List<String> procedureStableKey) {
        this.procedureStableKey = procedureStableKey;
    }

    public List<String> getParameterName() {
        return parameterName;
    }

    public void setParameterName(List<String> parameterName) {
        this.parameterName = parameterName;
    }

    public List<String> getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(List<String> parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    public List<String> getParameterStableKey() {
        return parameterStableKey;
    }

    public void setParameterStableKey(List<String> parameterStableKey) {
        this.parameterStableKey = parameterStableKey;
    }

    public List<String> getMpId() {
        return mpId;
    }

    public void setMpId(List<String> mpId) {
        this.mpId = mpId;
    }

    public List<String> getMpTerm() {
        return mpTerm;
    }

    public void setMpTerm(List<String> mpTerm) {
        this.mpTerm = mpTerm;
    }

    public List<String> getMpTermSynonym() {
        return mpTermSynonym;
    }

    public void setMpTermSynonym(List<String> mpTermSynonym) {
        this.mpTermSynonym = mpTermSynonym;
    }

    public List<String> getTopLevelMpId() {
        return topLevelMpId;
    }

    public void setTopLevelMpId(List<String> topLevelMpId) {
        this.topLevelMpId = topLevelMpId;
    }

    public List<String> getTopLevelMpTerm() {
        return topLevelMpTerm;
    }

    public void setTopLevelMpTerm(List<String> topLevelMpTerm) {
        this.topLevelMpTerm = topLevelMpTerm;
    }

    public List<String> getTopLevelMpTermSynonym() {
        return topLevelMpTermSynonym;
    }

    public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {
        this.topLevelMpTermSynonym = topLevelMpTermSynonym;
    }

    public List<String> getIntermediateMpId() {
        return intermediateMpId;
    }

    public void setIntermediateMpId(List<String> intermediateMpId) {
        this.intermediateMpId = intermediateMpId;
    }

    public List<String> getIntermediateMpTerm() {
        return intermediateMpTerm;
    }

    public void setIntermediateMpTerm(List<String> intermediateMpTerm) {
        this.intermediateMpTerm = intermediateMpTerm;
    }

    public List<String> getIntermediateMpTermSynonym() {
        return intermediateMpTermSynonym;
    }

    public void setIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {
        this.intermediateMpTermSynonym = intermediateMpTermSynonym;
    }

    public List<String> getChildMpId() {
        return childMpId;
    }

    public void setChildMpId(List<String> childMpId) {
        this.childMpId = childMpId;
    }

    public List<String> getChildMpTerm() {
        return childMpTerm;
    }

    public void setChildMpTerm(List<String> childMpTerm) {
        this.childMpTerm = childMpTerm;
    }

    public List<String> getChildMpTermSynonym() {
        return childMpTermSynonym;
    }

    public void setChildMpTermSynonym(List<String> childMpTermSynonym) {
        this.childMpTermSynonym = childMpTermSynonym;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public List<String> getAutoSuggest() {
        return autoSuggest;
    }

    public void setAutoSuggest(List<String> autoSuggest) {
        this.autoSuggest = autoSuggest;
    }

    public List<String> getGeneQf() {
        return geneQf;
    }

    public void setGeneQf(List<String> geneQf) {
        this.geneQf = geneQf;
    }

    public List<String> getMpQf() {
        return mpQf;
    }

    public void setMpQf(List<String> mpQf) {
        this.mpQf = mpQf;
    }

    public List<String> getDiseaseQf() {
        return diseaseQf;
    }

    public void setDiseaseQf(List<String> diseaseQf) {
        this.diseaseQf = diseaseQf;
    }

    public List<String> getMaQf() {
        return maQf;
    }

    public void setMaQf(List<String> maQf) {
        this.maQf = maQf;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.dataType);
        hash = 97 * hash + Objects.hashCode(this.maId);
        hash = 97 * hash + Objects.hashCode(this.maTerm);
        hash = 97 * hash + Objects.hashCode(this.maTermSynonym);
        hash = 97 * hash + Objects.hashCode(this.ontologySubset);
        hash = 97 * hash + Objects.hashCode(this.childMaId);
        hash = 97 * hash + Objects.hashCode(this.childMaTerm);
        hash = 97 * hash + Objects.hashCode(this.childMaTermSynonym);
        hash = 97 * hash + Objects.hashCode(this.childMaIdTerm);
        hash = 97 * hash + Objects.hashCode(this.topLevelMaId);
        hash = 97 * hash + Objects.hashCode(this.topLevelMaTerm);
//        hash = 97 * hash + Objects.hashCode(this.topLevelMaTermSynonym);
        hash = 97 * hash + Objects.hashCode(this.selectedTopLevelMaId);
        hash = 97 * hash + Objects.hashCode(this.selectedTopLevelMaTerm);
        hash = 97 * hash + Objects.hashCode(this.selectedTopLevelMaTermSynonym);
        hash = 97 * hash + Objects.hashCode(this.hpId);
        hash = 97 * hash + Objects.hashCode(this.hpTerm);
        hash = 97 * hash + Objects.hashCode(this.goId);
        hash = 97 * hash + Objects.hashCode(this.pValue);
        hash = 97 * hash + Objects.hashCode(this.mgiAccessionId);
        hash = 97 * hash + Objects.hashCode(this.markerSymbol);
        hash = 97 * hash + Objects.hashCode(this.markerName);
        hash = 97 * hash + Objects.hashCode(this.markerSynonym);
        hash = 97 * hash + Objects.hashCode(this.markerType);
        hash = 97 * hash + Objects.hashCode(this.humanGeneSymbol);
        hash = 97 * hash + Objects.hashCode(this.status);
        hash = 97 * hash + Objects.hashCode(this.imitsPhenotypeStarted);
        hash = 97 * hash + Objects.hashCode(this.imitsPhenotypeComplete);
        hash = 97 * hash + Objects.hashCode(this.imitsPhenotypeStatus);
        hash = 97 * hash + Objects.hashCode(this.latestProductionCentre);
        hash = 97 * hash + Objects.hashCode(this.latestPhenotypingCentre);
        hash = 97 * hash + Objects.hashCode(this.latestPhenotypeStatus);
        hash = 97 * hash + Objects.hashCode(this.legacyPhenotypeStatus);
        hash = 97 * hash + Objects.hashCode(this.alleleName);
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.diseaseId);
        hash = 97 * hash + Objects.hashCode(this.diseaseSource);
        hash = 97 * hash + Objects.hashCode(this.diseaseTerm);
        hash = 97 * hash + Objects.hashCode(this.diseaseAlts);
        hash = 97 * hash + Objects.hashCode(this.diseaseClasses);
        hash = 97 * hash + Objects.hashCode(this.diseaseHumanPhenotypes);
        hash = 97 * hash + Objects.hashCode(this.humanCurated);
        hash = 97 * hash + Objects.hashCode(this.mouseCurated);
        hash = 97 * hash + Objects.hashCode(this.mgiPredicted);
        hash = 97 * hash + Objects.hashCode(this.impcPredicted);
        hash = 97 * hash + Objects.hashCode(this.mgiPredictedKnownGene);
        hash = 97 * hash + Objects.hashCode(this.impcPredictedKnownGene);
        hash = 97 * hash + Objects.hashCode(this.mgiNovelPredictedInLocus);
        hash = 97 * hash + Objects.hashCode(this.impcNovelPredictedInLocus);
        hash = 97 * hash + Objects.hashCode(this.annotationTermId);
        hash = 97 * hash + Objects.hashCode(this.annotationTermName);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.accession);
        hash = 97 * hash + Objects.hashCode(this.expName);
        hash = 97 * hash + Objects.hashCode(this.largeThumbnailFilePath);
        hash = 97 * hash + Objects.hashCode(this.smallThumbnailFilePath);
        hash = 97 * hash + Objects.hashCode(this.inferredMaTermId);
        hash = 97 * hash + Objects.hashCode(this.inferredMaTermName);
        hash = 97 * hash + Objects.hashCode(this.annotatedHigherLevelMaTermId);
        hash = 97 * hash + Objects.hashCode(this.annotatedHigherLevelMaTermName);
        hash = 97 * hash + Objects.hashCode(this.annotatedHigherLevelMpTermId);
        hash = 97 * hash + Objects.hashCode(this.annotatedHigherLevelMpTermName);
        hash = 97 * hash + Objects.hashCode(this.inferredHigherLevelMaTermId);
        hash = 97 * hash + Objects.hashCode(this.inferredHigherLevelMaTermName);
        hash = 97 * hash + Objects.hashCode(this.annotatedOrInferredHigherLevelMaTermName);
        hash = 97 * hash + Objects.hashCode(this.annotatedOrInferredHigherLevelMaTermId);
        hash = 97 * hash + Objects.hashCode(this.symbol);
        hash = 97 * hash + Objects.hashCode(this.sangerSymbol);
        hash = 97 * hash + Objects.hashCode(this.geneName);
        hash = 97 * hash + Objects.hashCode(this.subtype);
        hash = 97 * hash + Objects.hashCode(this.geneSynonyms);
        hash = 97 * hash + Objects.hashCode(this.maTermId);
        hash = 97 * hash + Objects.hashCode(this.maTermName);
        hash = 97 * hash + Objects.hashCode(this.mpTermId);
        hash = 97 * hash + Objects.hashCode(this.mpTermName);
        hash = 97 * hash + Objects.hashCode(this.expNameExp);
        hash = 97 * hash + Objects.hashCode(this.symbolGene);
        hash = 97 * hash + Objects.hashCode(this.topLevel);
        hash = 97 * hash + Objects.hashCode(this.alleleSymbol);
        hash = 97 * hash + Objects.hashCode(this.alleleId);
        hash = 97 * hash + Objects.hashCode(this.strainName);
        hash = 97 * hash + Objects.hashCode(this.strainId);
        hash = 97 * hash + Objects.hashCode(this.geneticBackground);
        hash = 97 * hash + Objects.hashCode(this.pipelineName);
        hash = 97 * hash + Objects.hashCode(this.pipelineStableId);
        hash = 97 * hash + Objects.hashCode(this.pipelineStableKey);
        hash = 97 * hash + Objects.hashCode(this.procedureName);
        hash = 97 * hash + Objects.hashCode(this.procedureStableId);
        hash = 97 * hash + Objects.hashCode(this.procedureStableKey);
        hash = 97 * hash + Objects.hashCode(this.parameterName);
        hash = 97 * hash + Objects.hashCode(this.parameterStableId);
        hash = 97 * hash + Objects.hashCode(this.parameterStableKey);
        hash = 97 * hash + Objects.hashCode(this.mpId);
        hash = 97 * hash + Objects.hashCode(this.mpTerm);
        hash = 97 * hash + Objects.hashCode(this.mpTermSynonym);
        hash = 97 * hash + Objects.hashCode(this.topLevelMpId);
        hash = 97 * hash + Objects.hashCode(this.topLevelMpTerm);
        hash = 97 * hash + Objects.hashCode(this.topLevelMpTermSynonym);
        hash = 97 * hash + Objects.hashCode(this.intermediateMpId);
        hash = 97 * hash + Objects.hashCode(this.intermediateMpTerm);
        hash = 97 * hash + Objects.hashCode(this.intermediateMpTermSynonym);
        hash = 97 * hash + Objects.hashCode(this.childMpId);
        hash = 97 * hash + Objects.hashCode(this.childMpTerm);
        hash = 97 * hash + Objects.hashCode(this.childMpTermSynonym);
        hash = 97 * hash + Objects.hashCode(this.text);
        hash = 97 * hash + Objects.hashCode(this.autoSuggest);
        hash = 97 * hash + Objects.hashCode(this.geneQf);
        hash = 97 * hash + Objects.hashCode(this.mpQf);
        hash = 97 * hash + Objects.hashCode(this.diseaseQf);
        hash = 97 * hash + Objects.hashCode(this.maQf);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MaDTO other = (MaDTO) obj;
        if ( ! Objects.equals(this.dataType, other.dataType)) {
            return false;
        }
        if ( ! Objects.equals(this.maId, other.maId)) {
            return false;
        }
        if ( ! Objects.equals(this.maTerm, other.maTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.maTermSynonym, other.maTermSynonym)) {
            return false;
        }
        if ( ! Objects.equals(this.ontologySubset, other.ontologySubset)) {
            return false;
        }
        if ( ! Objects.equals(this.childMaId, other.childMaId)) {
            return false;
        }
        if ( ! Objects.equals(this.childMaTerm, other.childMaTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.childMaTermSynonym, other.childMaTermSynonym)) {
            return false;
        }
        if ( ! Objects.equals(this.childMaIdTerm, other.childMaIdTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.topLevelMaId, other.topLevelMaId)) {
            return false;
        }
        if ( ! Objects.equals(this.topLevelMaTerm, other.topLevelMaTerm)) {
            return false;
        }
//        if ( ! Objects.equals(this.topLevelMaTermSynonym, other.topLevelMaTermSynonym)) {
//            return false;
//        }
        if ( ! Objects.equals(this.selectedTopLevelMaId, other.selectedTopLevelMaId)) {
            return false;
        }
        if ( ! Objects.equals(this.selectedTopLevelMaTerm, other.selectedTopLevelMaTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.selectedTopLevelMaTermSynonym, other.selectedTopLevelMaTermSynonym)) {
            return false;
        }
        if ( ! Objects.equals(this.hpId, other.hpId)) {
            return false;
        }
        if ( ! Objects.equals(this.hpTerm, other.hpTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.goId, other.goId)) {
            return false;
        }
        if ( ! Objects.equals(this.pValue, other.pValue)) {
            return false;
        }
        if ( ! Objects.equals(this.mgiAccessionId, other.mgiAccessionId)) {
            return false;
        }
        if ( ! Objects.equals(this.markerSymbol, other.markerSymbol)) {
            return false;
        }
        if ( ! Objects.equals(this.markerName, other.markerName)) {
            return false;
        }
        if ( ! Objects.equals(this.markerSynonym, other.markerSynonym)) {
            return false;
        }
        if ( ! Objects.equals(this.markerType, other.markerType)) {
            return false;
        }
        if ( ! Objects.equals(this.humanGeneSymbol, other.humanGeneSymbol)) {
            return false;
        }
        if ( ! Objects.equals(this.status, other.status)) {
            return false;
        }
        if ( ! Objects.equals(this.imitsPhenotypeStarted, other.imitsPhenotypeStarted)) {
            return false;
        }
        if ( ! Objects.equals(this.imitsPhenotypeComplete, other.imitsPhenotypeComplete)) {
            return false;
        }
        if ( ! Objects.equals(this.imitsPhenotypeStatus, other.imitsPhenotypeStatus)) {
            return false;
        }
        if ( ! Objects.equals(this.latestProductionCentre, other.latestProductionCentre)) {
            return false;
        }
        if ( ! Objects.equals(this.latestPhenotypingCentre, other.latestPhenotypingCentre)) {
            return false;
        }
        if ( ! Objects.equals(this.latestPhenotypeStatus, other.latestPhenotypeStatus)) {
            return false;
        }
        if ( ! Objects.equals(this.legacyPhenotypeStatus, other.legacyPhenotypeStatus)) {
            return false;
        }
        if ( ! Objects.equals(this.alleleName, other.alleleName)) {
            return false;
        }
        if ( ! Objects.equals(this.type, other.type)) {
            return false;
        }
        if ( ! Objects.equals(this.diseaseId, other.diseaseId)) {
            return false;
        }
        if ( ! Objects.equals(this.diseaseSource, other.diseaseSource)) {
            return false;
        }
        if ( ! Objects.equals(this.diseaseTerm, other.diseaseTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.diseaseAlts, other.diseaseAlts)) {
            return false;
        }
        if ( ! Objects.equals(this.diseaseClasses, other.diseaseClasses)) {
            return false;
        }
        if ( ! Objects.equals(this.diseaseHumanPhenotypes, other.diseaseHumanPhenotypes)) {
            return false;
        }
        if ( ! Objects.equals(this.humanCurated, other.humanCurated)) {
            return false;
        }
        if ( ! Objects.equals(this.mouseCurated, other.mouseCurated)) {
            return false;
        }
        if ( ! Objects.equals(this.mgiPredicted, other.mgiPredicted)) {
            return false;
        }
        if ( ! Objects.equals(this.impcPredicted, other.impcPredicted)) {
            return false;
        }
        if ( ! Objects.equals(this.mgiPredictedKnownGene, other.mgiPredictedKnownGene)) {
            return false;
        }
        if ( ! Objects.equals(this.impcPredictedKnownGene, other.impcPredictedKnownGene)) {
            return false;
        }
        if ( ! Objects.equals(this.mgiNovelPredictedInLocus, other.mgiNovelPredictedInLocus)) {
            return false;
        }
        if ( ! Objects.equals(this.impcNovelPredictedInLocus, other.impcNovelPredictedInLocus)) {
            return false;
        }
        if ( ! Objects.equals(this.annotationTermId, other.annotationTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.annotationTermName, other.annotationTermName)) {
            return false;
        }
        if ( ! Objects.equals(this.name, other.name)) {
            return false;
        }
        if ( ! Objects.equals(this.accession, other.accession)) {
            return false;
        }
        if ( ! Objects.equals(this.expName, other.expName)) {
            return false;
        }
        if ( ! Objects.equals(this.largeThumbnailFilePath, other.largeThumbnailFilePath)) {
            return false;
        }
        if ( ! Objects.equals(this.smallThumbnailFilePath, other.smallThumbnailFilePath)) {
            return false;
        }
        if ( ! Objects.equals(this.inferredMaTermId, other.inferredMaTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.inferredMaTermName, other.inferredMaTermName)) {
            return false;
        }
        if ( ! Objects.equals(this.annotatedHigherLevelMaTermId, other.annotatedHigherLevelMaTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.annotatedHigherLevelMaTermName, other.annotatedHigherLevelMaTermName)) {
            return false;
        }
        if ( ! Objects.equals(this.annotatedHigherLevelMpTermId, other.annotatedHigherLevelMpTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.annotatedHigherLevelMpTermName, other.annotatedHigherLevelMpTermName)) {
            return false;
        }
        if ( ! Objects.equals(this.inferredHigherLevelMaTermId, other.inferredHigherLevelMaTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.inferredHigherLevelMaTermName, other.inferredHigherLevelMaTermName)) {
            return false;
        }
        if ( ! Objects.equals(this.annotatedOrInferredHigherLevelMaTermName, other.annotatedOrInferredHigherLevelMaTermName)) {
            return false;
        }
        if ( ! Objects.equals(this.annotatedOrInferredHigherLevelMaTermId, other.annotatedOrInferredHigherLevelMaTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.symbol, other.symbol)) {
            return false;
        }
        if ( ! Objects.equals(this.sangerSymbol, other.sangerSymbol)) {
            return false;
        }
        if ( ! Objects.equals(this.geneName, other.geneName)) {
            return false;
        }
        if ( ! Objects.equals(this.subtype, other.subtype)) {
            return false;
        }
        if ( ! Objects.equals(this.geneSynonyms, other.geneSynonyms)) {
            return false;
        }
        if ( ! Objects.equals(this.maTermId, other.maTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.maTermName, other.maTermName)) {
            return false;
        }
        if ( ! Objects.equals(this.mpTermId, other.mpTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.mpTermName, other.mpTermName)) {
            return false;
        }
        if ( ! Objects.equals(this.expNameExp, other.expNameExp)) {
            return false;
        }
        if ( ! Objects.equals(this.symbolGene, other.symbolGene)) {
            return false;
        }
        if ( ! Objects.equals(this.topLevel, other.topLevel)) {
            return false;
        }
        if ( ! Objects.equals(this.alleleSymbol, other.alleleSymbol)) {
            return false;
        }
        if ( ! Objects.equals(this.alleleId, other.alleleId)) {
            return false;
        }
        if ( ! Objects.equals(this.strainName, other.strainName)) {
            return false;
        }
        if ( ! Objects.equals(this.strainId, other.strainId)) {
            return false;
        }
        if ( ! Objects.equals(this.geneticBackground, other.geneticBackground)) {
            return false;
        }
        if ( ! Objects.equals(this.pipelineName, other.pipelineName)) {
            return false;
        }
        if ( ! Objects.equals(this.pipelineStableId, other.pipelineStableId)) {
            return false;
        }
        if ( ! Objects.equals(this.pipelineStableKey, other.pipelineStableKey)) {
            return false;
        }
        if ( ! Objects.equals(this.procedureName, other.procedureName)) {
            return false;
        }
        if ( ! Objects.equals(this.procedureStableId, other.procedureStableId)) {
            return false;
        }
        if ( ! Objects.equals(this.procedureStableKey, other.procedureStableKey)) {
            return false;
        }
        if ( ! Objects.equals(this.parameterName, other.parameterName)) {
            return false;
        }
        if ( ! Objects.equals(this.parameterStableId, other.parameterStableId)) {
            return false;
        }
        if ( ! Objects.equals(this.parameterStableKey, other.parameterStableKey)) {
            return false;
        }
        if ( ! Objects.equals(this.mpId, other.mpId)) {
            return false;
        }
        if ( ! Objects.equals(this.mpTerm, other.mpTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.mpTermSynonym, other.mpTermSynonym)) {
            return false;
        }
        if ( ! Objects.equals(this.topLevelMpId, other.topLevelMpId)) {
            return false;
        }
        if ( ! Objects.equals(this.topLevelMpTerm, other.topLevelMpTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.topLevelMpTermSynonym, other.topLevelMpTermSynonym)) {
            return false;
        }
        if ( ! Objects.equals(this.intermediateMpId, other.intermediateMpId)) {
            return false;
        }
        if ( ! Objects.equals(this.intermediateMpTerm, other.intermediateMpTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.intermediateMpTermSynonym, other.intermediateMpTermSynonym)) {
            return false;
        }
        if ( ! Objects.equals(this.childMpId, other.childMpId)) {
            return false;
        }
        if ( ! Objects.equals(this.childMpTerm, other.childMpTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.childMpTermSynonym, other.childMpTermSynonym)) {
            return false;
        }
        if ( ! Objects.equals(this.text, other.text)) {
            return false;
        }
        if ( ! Objects.equals(this.autoSuggest, other.autoSuggest)) {
            return false;
        }
        if ( ! Objects.equals(this.geneQf, other.geneQf)) {
            return false;
        }
        if ( ! Objects.equals(this.mpQf, other.mpQf)) {
            return false;
        }
        if ( ! Objects.equals(this.diseaseQf, other.diseaseQf)) {
            return false;
        }
        if ( ! Objects.equals(this.maQf, other.maQf)) {
            return false;
        }
        return true;
    }
  

}
