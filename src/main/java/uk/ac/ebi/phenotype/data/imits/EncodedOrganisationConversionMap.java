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
