/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author jwarren
 */
@Controller
@RequestMapping("/documentation")
public class DocumentationController {
    
    @RequestMapping("/{page}")
    public String documentation(@PathVariable String page){
        System.out.println("documentation controller called");
        return "documentation/"+page;
    }
}
