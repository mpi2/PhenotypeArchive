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

package uk.ac.ebi.phenotype.service.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.solr.client.solrj.beans.Field;

/**
 * This DTO holds the code and data for transferring images from Sanger.
 * 
 * Images annotated to this MA
 * 
 * @author mrelac
 */
public class SangerImageDTO {
    public static final String MA_TERM_ID = "maTermId";
    
    public static final String ID = "id";       // image ID (unique key)
    
    public static final String PROCEDURE_NAME = "procedure_name";
    public static final String EXP_NAME = "expName";
    public static final String EXP_NAME_EXP = "expName_exp";
    public static final String SYMBOL_GENE = "symbol_gene";
    
    // Genes annotated to this MA through images
    public static final String MGI_ACCESSION_ID = "mgi_accession_id";
    public static final String MARKER_SYMBOL = "marker_symbol";
    public static final String MARKER_NAME = "marker_name";
    public static final String MARKER_SYNONYM = "marker_synonym";
    public static final String MARKER_TYPE = "marker_type";
    public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";
    
    // Latest project status (ES cells/mice production status)
    public static final String STATUS = "status";
    
    // Latest mice phenotyping status for faceting
    public static final String IMITS_PHENOTYPE_STARTED = "imits_phenotype_started";
    public static final String IMITS_PHENOTYPE_COMPLETE = "imits_phenotype_complete";
    public static final String IMITS_PHENOTYPE_STATUS = "imits_phenotype_status";
    
    // Phenotyping status
    public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
    public static final String LEGACY_PHENOTYPE_STATUS = "legacy_phenotype_status";
    
    // Production/phenotyping centers
    public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
    public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
    
    // Alleles of a gene
    public static final String ALLELE_NAME = "allele_name";
    
    @Field(MA_TERM_ID)
    private List<String> maTermId;
    
    @Field(ID)
    private String id;
    
    @Field(PROCEDURE_NAME)
    private List<String> procedureName;
    
    @Field(EXP_NAME)
    private List<String> expName;
    
    @Field(EXP_NAME_EXP)
    private List<String> expNameExp;
    
    @Field(SYMBOL_GENE)
    private List<String> symbolGene;
    
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
    
    @Field(STATUS)
    private List<String> status;
    
    @Field(IMITS_PHENOTYPE_STARTED)
    private List<String> imitsPhenotypeStarted;
    
    @Field(IMITS_PHENOTYPE_COMPLETE)
    private List<String> imitsPhenotypeComplete;
    
    @Field(IMITS_PHENOTYPE_STATUS)
    private List<String> imitsPhenotypeStatus;
    
    @Field(LATEST_PHENOTYPE_STATUS)
    private List<String> latestPhenotypeStatus;
    
    @Field(LEGACY_PHENOTYPE_STATUS)
    private Integer legacyPhenotypeStatus;
    
    @Field(LATEST_PRODUCTION_CENTRE)
    private List<String> latestProductionCentre;
    
    @Field(LATEST_PHENOTYPING_CENTRE)
    private List<String> latestPhenotypingCentre;
    
    @Field(ALLELE_NAME)
    private List<String> alleleName;
    
    @Field("hp_id")
    private String hpId;
    
    
	public String getHpId() {
	
		return hpId;
	}

	
	public void setHpId(String hpId) {
	
		this.hpId = hpId;
	}

	
	public String getHpTerm() {
	
		return hpTerm;
	}

	
	public void setHpTerm(String hpTerm) {
	
		this.hpTerm = hpTerm;
	}



	@Field("hp_term")
    private String hpTerm;

    
    // SETTERS AND GETTERS
    
    
    public List<String> getMaTermId() {
        return maTermId;
    }

    public void setMaTermId(List<String> maId) {
        this.maTermId = maId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(List<String> procedureName) {
        this.procedureName = procedureName;
    }

    public List<String> getExpName() {
        return expName;
    }

    public void setExpName(List<String> expName) {
        this.expName = expName;
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

    public List<String> getLatestPhenotypeStatus() {
        return latestPhenotypeStatus;
    }

    public void setLatestPhenotypeStatus(List<String> latestPhenotypeStatus) {
        this.latestPhenotypeStatus = latestPhenotypeStatus;
    }

    public Integer getLegacyPhenotypeStatus() {
        return legacyPhenotypeStatus;
    }

    public void setLegacyPhenotypeStatus(Integer legacyPhenotypeStatus) {
        this.legacyPhenotypeStatus = legacyPhenotypeStatus;
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

    public List<String> getAlleleName() {
        return alleleName;
    }

    public void setAlleleName(List<String> alleleName) {
        this.alleleName = alleleName;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.maTermId);
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.procedureName);
        hash = 37 * hash + Objects.hashCode(this.expName);
        hash = 37 * hash + Objects.hashCode(this.expNameExp);
        hash = 37 * hash + Objects.hashCode(this.symbolGene);
        hash = 37 * hash + Objects.hashCode(this.mgiAccessionId);
        hash = 37 * hash + Objects.hashCode(this.markerSymbol);
        hash = 37 * hash + Objects.hashCode(this.markerName);
        hash = 37 * hash + Objects.hashCode(this.markerSynonym);
        hash = 37 * hash + Objects.hashCode(this.markerType);
        hash = 37 * hash + Objects.hashCode(this.humanGeneSymbol);
        hash = 37 * hash + Objects.hashCode(this.status);
        hash = 37 * hash + Objects.hashCode(this.imitsPhenotypeStarted);
        hash = 37 * hash + Objects.hashCode(this.imitsPhenotypeComplete);
        hash = 37 * hash + Objects.hashCode(this.imitsPhenotypeStatus);
        hash = 37 * hash + Objects.hashCode(this.latestPhenotypeStatus);
        hash = 37 * hash + Objects.hashCode(this.legacyPhenotypeStatus);
        hash = 37 * hash + Objects.hashCode(this.latestProductionCentre);
        hash = 37 * hash + Objects.hashCode(this.latestPhenotypingCentre);
        hash = 37 * hash + Objects.hashCode(this.alleleName);
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
        final SangerImageDTO other = (SangerImageDTO) obj;
        if ( ! Objects.equals(this.maTermId, other.maTermId)) {
            return false;
        }
        if ( ! Objects.equals(this.id, other.id)) {
            return false;
        }
        if ( ! Objects.equals(this.procedureName, other.procedureName)) {
            return false;
        }
        if ( ! Objects.equals(this.expName, other.expName)) {
            return false;
        }
        if ( ! Objects.equals(this.expNameExp, other.expNameExp)) {
            return false;
        }
        if ( ! Objects.equals(this.symbolGene, other.symbolGene)) {
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
        if ( ! Objects.equals(this.latestPhenotypeStatus, other.latestPhenotypeStatus)) {
            return false;
        }
        if ( ! Objects.equals(this.legacyPhenotypeStatus, other.legacyPhenotypeStatus)) {
            return false;
        }
        if ( ! Objects.equals(this.latestProductionCentre, other.latestProductionCentre)) {
            return false;
        }
        if ( ! Objects.equals(this.latestPhenotypingCentre, other.latestPhenotypingCentre)) {
            return false;
        }
        if ( ! Objects.equals(this.alleleName, other.alleleName)) {
            return false;
        }
        return true;
    }
    


	// <field column="dataType" name="dataType"/>
	// <field column="FULL_RESOLUTION_FILE_PATH"
	// name="fullResolutionFilePath" />
	// <field column="LARGE_THUMBNAIL_FILE_PATH"
	// name="largeThumbnailFilePath" />
	// <field column="ORIGINAL_FILE_NAME" name="originalFileName" />
	// <field column="SMALL_THUMBNAIL_FILE_PATH"
	// name="smallThumbnailFilePath" />
	// <field column="institute" name="institute" />
	
	@Field("dataType")
	String dataType;
	@Field("fullResolutionFilePath")
	String fullResolutionFilePath;
	@Field("largeThumbnailFilePath")
	String largeThumbnailFilePath;
	@Field("originalFileName")
	String originalFileName;
	@Field("smallThumbnailFilePath")
	String smallThumbnailFilePath;
	@Field("institute")
	String institute;
	// need method to get imaDcfImageView data for here
	@Field("dcfId")
	String dcfId;
	@Field("dcfExpId")
	String dcfExpId;
	@Field("sangerProcedureName")
	String sangerProcedureName;
	@Field("genotypeString")
	String genotypeString;
	@Field("geneName")
	private String geneName;
	@Field("expName")
	private String experimentName;
	@Field("expName_exp")
	private String expName_exp;
	@Field("procedure_name")
	private String procedure_name;
	@Field("geneSynonyms")
	private List<String> synonyms;
	@Field("tagValue")
	private List<String> tagValues;
	@Field("tagName")
	private List<String> tagNames;
	@Field("annotationTermId")
	private List<String> annotationTermIds;
	@Field("annotationTermName")
	private List<String> annotationTermNames;
	@Field("maTermId")
	private List<String> maIds;
	@Field("ma_term")
	private List<String> ma_terms;
	@Field("maTermName")
	private List<String> maTermName;
	@Field("ma_term_synonym")
	private List<String> maTermSynonym;
	@Field("subtype")
	private String subtype;
	

	@Field("mp_id")
	private List<String> mp_id;
	@Field("mp_term_synonym")
	private Set<String> mpSyns;
	// <field column="term_id" name="selected_top_level_ma_id" />
	// <field column="name" name="selected_top_level_ma_term" />
	@Field("selected_top_level_ma_id")
	private List<String> maTopLevelTermIds;
	@Field("selected_top_level_ma_term_synonym")
	private ArrayList<String> selectedTopLevelMaTermSynonym;
//	<field column="name" name="annotatedHigherLevelMpTermName" />
//	<field column="mpTerm" name="annotatedHigherLevelMpTermId" />
	@Field("annotatedHigherLevelMpTermName")
	private List<String> annotatedHigherLevelMpTermName;

	@Field("annotatedHigherLevelMpTermId")
	private List<String> annotatedHigherLevelMpTermId;
	
	@Field("top_level_mp_term_synonym")
	private Set<String> topLevelMpTermSynonym; 

	
	
	public Set<String> getTopLevelMpTermSynonym() {
	
		return topLevelMpTermSynonym;
	}



	
	public void setTopLevelMpTermSynonym(Set<String> topLevelMpTermSynonym) {
	
		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
	}



	public List<String> getAnnotatedHigherLevelMpTermName() {
	
		return annotatedHigherLevelMpTermName;
	}


	
	public void setAnnotatedHigherLevelMpTermName(List<String> annotatedHigherLevelMpTermName) {
	
		this.annotatedHigherLevelMpTermName = annotatedHigherLevelMpTermName;
	}


	
	public List<String> getAnnotatedHigherLevelMpTermId() {
	
		return annotatedHigherLevelMpTermId;
	}


	
	public void setAnnotatedHigherLevelMpTermId(List<String> annotatedHigherLevelMpTermId) {
	
		this.annotatedHigherLevelMpTermId = annotatedHigherLevelMpTermId;
	}


	public List<String> getMaTopLevelTermIds() {

		return maTopLevelTermIds;
	}


	public ArrayList<String> getSelectedTopLevelMaTermSynonym() {

		return selectedTopLevelMaTermSynonym;
	}


	public void setSelectedTopLevelMaTermSynonym(ArrayList<String> selectedTopLevelMaTermSynonym) {

		this.selectedTopLevelMaTermSynonym = selectedTopLevelMaTermSynonym;
	}


	public void setMaTopLevelTermIds(List<String> maTopLevelTermIds) {

		this.maTopLevelTermIds = maTopLevelTermIds;
	}


	public List<String> getMaTopLevelTerms() {

		return maTopLevelTerms;
	}


	public void setMaTopLevelTerms(List<String> maTopLevelTerms) {

		this.maTopLevelTerms = maTopLevelTerms;
	}

	@Field("selected_top_level_ma_term")
	private List<String> maTopLevelTerms;


	public List<String> getMp_id() {

		return mp_id;
	}


	public void setMpSynonyms(Set<String> mpSyns) {

		this.mpSyns = mpSyns;

	}


	public void setMp_id(List<String> mp_id) {

		this.mp_id = mp_id;
	}


	public List<String> getMpTermId() {

		return mpTermId;
	}


	public void setMpTermId(List<String> mpTermId) {

		this.mpTermId = mpTermId;
	}


	public List<String> getMpTerm() {

		return mpTerm;
	}

	@Field("mpTermId")
	private List<String> mpTermId;
	@Field("mp_term")
	private List<String> mpTerm;
	@Field("mpTermName")
	private List<String> mpTermName;


	public List<String> getMpTermName() {

		return mpTermName;
	}


	public void setMpTermName(List<String> mpTermName) {

		this.mpTermName = mpTermName;
	}


	


	public void setMpTerm(List<String> mpTerm) {

		this.mpTerm = mpTerm;

	}


	public void setMpId(List<String> mpTermId) {

		this.mpTermId = mpTermId;

	}


	public String getSubtype() {

		return subtype;
	}



	public void setSubtype(String subtype) {

		this.subtype = subtype;
	}


	public List<String> getMaTermSynonym() {

		return maTermSynonym;
	}


	public void setMaTermSynonym(List<String> maTermSynonym) {

		this.maTermSynonym = maTermSynonym;
	}


	public List<String> getMaTermName() {

		return maTermName;
	}


	public void setMaTermName(List<String> maTermName) {

		this.maTermName = maTermName;
	}


	public String getGeneName() {

		return geneName;
	}


	public void setMaTerm(List<String> ma_terms) {

		this.ma_terms = ma_terms;

	}


	public void setMaId(List<String> ma_ids) {

		this.maIds = ma_ids;

	}


	public void setAnnotationTermName(List<String> annotationTermNames) {

		this.annotationTermNames = annotationTermNames;

	}


	public void setAnnotationTermId(List<String> annotationTermIds) {

		this.annotationTermIds = annotationTermIds;

	}


	public void setTagValues(List<String> tagValues) {

		this.tagValues = tagValues;

	}


	public void setTagNames(List<String> tagNames) {

		this.tagNames = tagNames;

	}


	public void setSynonyms(List<String> syns) {

		this.synonyms = syns;

	}



	public void setExperimentName(String name) {

		this.experimentName = name;

	}


	public String getAccession() {

		return accession;
	}


	public String getSymbol() {

		return symbol;
	}

	@Field("accession")
	private String accession;
	@Field("symbol")
	private String symbol;


	public String getGenotypeString() {

		return genotypeString;
	}


	public void setGeneName(String geneName) {

		this.geneName = geneName;

	}


	public void setAccession(String accession) {

		this.accession = accession;

	}


	public void setSymbol(String symbol) {

		this.symbol = symbol;

	}


	public void setGenotypeString(String genotypeString) {

		this.genotypeString = genotypeString;
	}


	public String getAgeInWeeks() {

		return ageInWeeks;
	}


	public void setAgeInWeeks(String ageInWeeks) {

		this.ageInWeeks = ageInWeeks;
	}

	@Field("ageInWeeks")
	String ageInWeeks;

	@Field("sangerSymbol")
	String sangerSymbol;


	public String getSangerSymbol() {

		return sangerSymbol;
	}


	public void setSangerSymbol(String sangerSymbol) {

		this.sangerSymbol = sangerSymbol;
	}


	public String getAllele_accession() {

		return allele_accession;
	}


	public void setAllele_accession(String allele_accession) {

		this.allele_accession = allele_accession;
	}

	@Field("allele_accession")
	String allele_accession;



	public String getDataType() {

		return dataType;
	}


	public void setDataType(String dataType) {

		this.dataType = dataType;
	}


	public String getFullResolutionFilePath() {

		return fullResolutionFilePath;
	}


	public void setFullResolutionFilePath(String fullResolutionFilePath) {

		this.fullResolutionFilePath = fullResolutionFilePath;
	}


	public String getLargeThumbnailFilePath() {

		return largeThumbnailFilePath;
	}


	public void setLargeThumbnailFilePath(String largeThumbnailFilePath) {

		this.largeThumbnailFilePath = largeThumbnailFilePath;
	}


	public String getOriginalFileName() {

		return originalFileName;
	}


	public void setOriginalFileName(String originalFileName) {

		this.originalFileName = originalFileName;
	}


	public String getSmallThumbnailFilePath() {

		return smallThumbnailFilePath;
	}


	public void setSmallThumbnailFilePath(String smallThumbnailFilePath) {

		this.smallThumbnailFilePath = smallThumbnailFilePath;
	}


	public String getInstitute() {

		return institute;
	}


	public void setInstitute(String institute) {

		this.institute = institute;
	}


	public String getDcfId() {

		return dcfId;
	}


	public void setDcfId(String dcfId) {

		this.dcfId = dcfId;
	}


	public String getDcfExpId() {

		return dcfExpId;
	}


	public void setDcfExpId(String dcfExpId) {

		this.dcfExpId = dcfExpId;
	}


	public String getSangerProcedureName() {

		return sangerProcedureName;
	}


	public void setSangerProcedureName(String sangerProcedureName) {

		this.sangerProcedureName = sangerProcedureName;
	}


	public String getSangerProcedureId() {

		return sangerProcedureId;
	}


	public void setSangerProcedureId(String sangerProcedureId) {

		this.sangerProcedureId = sangerProcedureId;
	}

	String sangerProcedureId;

	//
	// <entity dataSource="komp2ds" name="imaDcfImageView"
	// query="SELECT DCF_ID, NAME, PROCEDURE_ID, EXPERIMENT_ID, MOUSE_ID FROM `IMA_DCF_IMAGE_VW` dcf, IMA_IMAGE_RECORD ir, PHN_STD_OPERATING_PROCEDURE stdOp WHERE dcf.id=ir.id and dcf.dcf_id=stdOp.id and ir.id=${ima_image_record.ID}">
	// <field column="DCF_ID" name="dcfId" />
	// <field column="EXPERIMENT_ID" name="dcfExpId" />
	// <field column="NAME" name="sangerProcedureName" />
	// <field column="PROCEDURE_ID" name="sangerProcedureId" />
	// </entity>


    
    
}