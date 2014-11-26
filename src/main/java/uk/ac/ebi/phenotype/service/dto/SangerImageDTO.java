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

import java.util.List;
import java.util.Objects;
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

    
    
}