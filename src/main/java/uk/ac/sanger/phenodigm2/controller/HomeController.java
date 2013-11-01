/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.sanger.phenodigm2.controller;

import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmDao;
import uk.ac.sanger.phenodigm2.model.Disease;
import uk.ac.sanger.phenodigm2.model.DiseaseAssociation;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;

/**
 *
 * @author jj8
 */
@Controller
public class HomeController {
    @Resource(name="globalConfiguration")
    private Map<String, String> config;
    
    @RequestMapping(value = "phenodigm")
    public String home(Model model) {

        return "phenodigm/home";
    }
    
    @RequestMapping(value = "phenodigm/home")
    public String altHome(Model model) {

        return "phenodigm/home";
    }
}
