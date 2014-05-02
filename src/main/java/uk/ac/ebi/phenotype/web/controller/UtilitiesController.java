package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.service.ObservationService;

/**
 * Class that encapsulates all internal use displays for testing - not for users
 */
@Controller
public class UtilitiesController {

    private final Logger log = LoggerFactory.getLogger(UtilitiesController.class);

    @Autowired
    private ObservationService os;

    /**
     * For Testing not for users- view the parameters and genes as links to
     * statistics pages
     *
     * @param start default 0 if not specified
     * @param length default 100 if not specified
     * @param observationType e.g. unidimensional, categorical, etc.
     * @param parameterIds e.g. ESLIM_016_001_004
     * @param model the spring model
     * @return the name of the success view to render
     *
     * @throws GenomicFeatureNotFoundException
     * @throws URISyntaxException
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping("/stats/statslinks")
    public String statsLinksView(
            @RequestParam(required = false, value = "start") Integer start,
            @RequestParam(required = false, value = "length") Integer length,
            @RequestParam(required = false, value = "observationType") String observationType,
            @RequestParam(required = false, value = "parameterId") String[] parameterIds,
            Model model)
            throws GenomicFeatureNotFoundException, IOException, URISyntaxException, SQLException {
        log.debug("calling stats links");

        //equivalent url from solr service http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=observationType:unidimensional&wt=json&indent=true&start=0&rows=10
        ObservationType oType = null;

        for (ObservationType type : ObservationType.values()) {
            if (type.name().equalsIgnoreCase(observationType)) {
                oType = type;
            }
        }
        log.debug("calling observation type=" + oType);

        List<String> paramIds = getParamsAsList(parameterIds);
        getLinksForStats(start, length, model, oType, paramIds);

        return "statsLinksList";
    }

    private void getLinksForStats(Integer start, Integer length, Model model, ObservationType type, List<String> parameterIds) throws IOException, URISyntaxException, SQLException {
        if (start == null) {
            start = 0;
        }
        if (length == null) {
            length = 100;
        }
        model.addAttribute("statsLinks", os.getLinksListForStats(start, length, type, parameterIds));
    }

    /**
     * Convert String array to a List
     *
     * @param parameterIds
     * @return
     */
    private List<String> getParamsAsList(String[] parameterIds) {
        List<String> paramIds;
        if (parameterIds == null) {
            paramIds = Collections.emptyList();
        } else {
            paramIds = Arrays.asList(parameterIds);
        }
        return paramIds;
    }
}
