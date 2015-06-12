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
package uk.ac.ebi.phenotype.data.imits;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EncodedOrganisationConversionMap {

    public Map<String, String> imitsCenters = new HashMap<String, String>();
    public Map<String, String> dccCenterMap = new HashMap<String, String>();

    public EncodedOrganisationConversionMap() {
        imitsCenters.put("Harwell", "MRC Harwell");
        imitsCenters.put("JAX", "The Jackson Laboratory");
        imitsCenters.put("BCM", "Baylor College of Medicine");
        imitsCenters.put("UCD", "UC Davis");
        imitsCenters.put("WTSI", "Wellcome Trust Sanger Institute");
        imitsCenters.put("HMGU", "Helmholtz Zentrum München");
        imitsCenters.put("APN", "Australian Phenomics Network");
        imitsCenters.put("Monterotondo", "CNR Monterotondo");
        imitsCenters.put("TCP", "The Toronto Centre for Phenogenomics");
        imitsCenters.put("ICS", "Institut Clinique de la Souris");
        imitsCenters.put("MARC", "Model Animal Research Center of Nanjing University");
        imitsCenters.put("RIKEN BRC", "RIKEN BioResource Center");

        dccCenterMap.put("NCOM", "CMHD");
        dccCenterMap.put("H", "MRC Harwell");
        dccCenterMap.put("J", "JAX");
        dccCenterMap.put("UCD", "UC Davis");
        dccCenterMap.put("GMC", "HMGU");
    }

}
