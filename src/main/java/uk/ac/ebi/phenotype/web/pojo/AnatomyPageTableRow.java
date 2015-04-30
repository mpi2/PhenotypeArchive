package uk.ac.ebi.phenotype.web.pojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.DatasourceEntityId;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;


public class AnatomyPageTableRow extends DataTableRow{

	
	String expression;
	String imageUrl;
    List<OntologyTerm> anatomy;
    String anatomyLinks;
    int numberOfImages = 0;
    
    public AnatomyPageTableRow() {
        super();
    }
    
    
    public AnatomyPageTableRow(ImageDTO image, String maId, String baseUrl, String expressionValue) {

    	super();
        List<String> sex = new ArrayList<String>();
        sex.add(image.getSex().toString());
        GenomicFeature gene = new GenomicFeature();
        gene.setSymbol(image.getGeneSymbol());
        gene.setId(new DatasourceEntityId(image.getGeneAccession(), 0));
        Allele allele = new Allele();
        allele.setSymbol(image.getAlleleSymbol());
        this.setGene(gene);
        this.setAllele(allele);
        this.setSexes(sex);
        this.setDataSourceName(image.getDataSourceName());
        this.setZygosity(image.getZygosity() != null ? ZygosityType.valueOf(image.getZygosity()) : ZygosityType.not_applicable);
        Procedure proc = new Procedure();
        proc.setName(image.getProcedureName());
        proc.setStableId(image.getProcedureStableId());
        Parameter param = new Parameter();
        param.setName(image.getParameterName());      
        this.setProcedure(proc);
        this.setParameter(param);
        this.setPhenotypingCenter(image.getPhenotypingCenter());
              
        List<OntologyTerm> anatomyTerms = new ArrayList<>();
        for (int i = 0; i < image.getMaTermId().size(); i++){
        	if (image.getExpression(image.getMaTermId().get(i)).equalsIgnoreCase(expressionValue)){
	        	OntologyTerm anatomy = new OntologyTerm();
	        	DatasourceEntityId maIdDei = new DatasourceEntityId(image.getMaTermId().get(i), -1);
	        	anatomy.setId(maIdDei);
	        	anatomy.setName(image.getMaTerm().get(i));
	        	anatomyTerms.add(anatomy);
        	}
        }
               
        this.setExpression(expressionValue);
        this.setAnatomy(anatomyTerms);
        this.setImageUrl(buildImageUrl(baseUrl, maId, image.getMaTerm().get(image.getMaTermId().indexOf(maId))));
        this.setAnatomyLinks(getAnatomyWithLinks(baseUrl));
        this.numberOfImages ++;
    }
    
    
    public String getAnatomyWithLinks(String baseUrl){
    	String links = "<a href=\"" + baseUrl + "/anatomy/";
    	for (int i = 0; i < anatomy.size(); i++){
    		links += anatomy.get(i).getId().getAccession() + "\">" + anatomy.get(i).getName() + "</a>";
    		if (i != anatomy.size()-1 ){
    			links += ", <a href=\"" + baseUrl + "/anatomy/";
    		}
    	}
    	
    	return links;
    }
    
    
    public String buildImageUrl(String baseUrl, String maId, String maTerm){
    	
    	String url = baseUrl + "/impcImages/images?q=*:*&defType=edismax&wt=json&fq=(";
        url += ImageDTO.MA_ID + ":\"";
        url += maId + "\" OR " + ImageDTO.SELECTED_TOP_LEVEL_MA_ID + ":\"" + maId + "\"";
        url += " OR " + ImageDTO.INTERMEDIATE_LEVEL_MA_TERM_ID + ":\"" + maId + "\"";
    	
    	url += ") ";
    	
    	if (getGene().getSymbol()!= null){
    		url += " AND " + ImageDTO.GENE_SYMBOL + ":" + this.getGene().getSymbol();		
    	} else {
    		url += " AND " + ImageDTO.BIOLOGICAL_SAMPLE_GROUP + ":control";
    	}
    	if (getParameter() != null){
    		url += " AND " + ImageDTO.PARAMETER_NAME + ":\"" + getParameter().getName() + "\"";
    	}
    	url += "&title=gene " + this.getGene().getSymbol() + " in " + maTerm + ""; 
    	return url;
    }
    
    public void addImage(){
    	this.numberOfImages ++;
    }
    
	@Override
	public int compareTo(DataTableRow o) {

		return 0;
	}

	public String getExpression() {
	
		return expression;
	}
	
	public void setExpression(String expression) {
	
		this.expression = expression;
	}
 	
	public String getKey(){
		return getAllele().getSymbol() + getZygosity().name() + getParameter().getName() + getExpression();
	}

	public boolean equals(AnatomyPageTableRow obj) {
	    return this.getKey().equalsIgnoreCase(obj.getKey());
	}
	
	public int getNumberOfImages() {
	
		return numberOfImages;
	}
	
	public void setNumberOfImages(int numberOfImages) {
	
		this.numberOfImages = numberOfImages;
	}


	public void addSex(String sex){
		
		if (!sexes.contains(sex)){
			sexes.add(sex);
		}		
	}

	
	public String getImageUrl() {
	
		return imageUrl;
	}

	
	public void setImageUrl(String imageUrl) {
	
		this.imageUrl = imageUrl;
	}

	
	public List<OntologyTerm> getAnatomy() {
	
		return anatomy;
	}

	
	public void setAnatomy(List<OntologyTerm> anatomy) {
	
		this.anatomy = anatomy;
	}

	
	public String getAnatomyLinks() {
	
		return anatomyLinks;
	}

	
	public void setAnatomyLinks(String anatomyLinks) {
	
		this.anatomyLinks = anatomyLinks;
	}
	
}
