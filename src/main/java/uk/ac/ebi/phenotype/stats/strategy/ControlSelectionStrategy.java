
package uk.ac.ebi.phenotype.stats.strategy;

import java.util.List;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;

/**
 * Strategy pattern interface for swapping in different control selection
 * strategies.
 */
public interface ControlSelectionStrategy {
    public List<ExperimentDTO> execute(String geneAcc, ZygosityType zygosity, List<SexType> sexes, String parameterId, String metadataGroup);
}
