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
public class HpDTO {
    
    
    
    public static final String MP_ID = "mp_id";
    public static final String MP_TERM = "mp_term";
    public static final String HP_ID = "hp_id";
    public static final String HP_TERM = "hp_term";
    public static final String HP_SYNONYM = "hp_synonym";
    


    @Field(MP_ID)
    private String mpId;

    @Field(MP_TERM)
    private String mpTerm;

    @Field(HP_ID)
    private String hpId;

    @Field(HP_TERM)
    private String hpTerm;

    @Field(HP_SYNONYM)
    private List<String> hpSynonym;

    public String getMpId() {
        return mpId;
    }

    public void setMpId(String mpId) {
        this.mpId = mpId;
    }

    public String getMpTerm() {
        return mpTerm;
    }

    public void setMpTerm(String mpTerm) {
        this.mpTerm = mpTerm;
    }

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

    public List<String> getHpSynonym() {
        return hpSynonym;
    }

    public void setHpSynonym(List<String> hpSynonym) {
        this.hpSynonym = hpSynonym;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.mpId);
        hash = 23 * hash + Objects.hashCode(this.mpTerm);
        hash = 23 * hash + Objects.hashCode(this.hpId);
        hash = 23 * hash + Objects.hashCode(this.hpTerm);
        hash = 23 * hash + Objects.hashCode(this.hpSynonym);
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
        final HpDTO other = (HpDTO) obj;
        if ( ! Objects.equals(this.mpId, other.mpId)) {
            return false;
        }
        if ( ! Objects.equals(this.mpTerm, other.mpTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.hpId, other.hpId)) {
            return false;
        }
        if ( ! Objects.equals(this.hpTerm, other.hpTerm)) {
            return false;
        }
        if ( ! Objects.equals(this.hpSynonym, other.hpSynonym)) {
            return false;
        }
        return true;
    }

    

}
