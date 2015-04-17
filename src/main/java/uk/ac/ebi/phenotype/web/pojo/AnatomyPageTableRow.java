package uk.ac.ebi.phenotype.web.pojo;

import java.util.ArrayList;
import java.util.List;

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
	
    public AnatomyPageTableRow() {
        super();
    }
    
    public AnatomyPageTableRow(ImageDTO image, String maId, String baseUrl) {

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
        this.setZygosity(ZygosityType.valueOf(image.getZygosity()));
        this.setExpression(image.getExpression(maId));
        Procedure proc = new Procedure();
        proc.setName(image.getProcedureName());
        proc.setStableId(image.getProcedureStableId());
        Parameter param = new Parameter();
        param.setName(image.getParameterName());      
        this.setProcedure(proc);
        this.setParameter(param);
        this.setPhenotypingCenter(image.getPhenotypingCenter());
        int pos = image.getMaTermId().indexOf(maId);
        OntologyTerm anatomy = new OntologyTerm();
        DatasourceEntityId maIdDei = new DatasourceEntityId(maId, -10);
        anatomy.setId(maIdDei);
        anatomy.setName(image.getMaTerm().get(pos));
        this.setAnatomyTerm(anatomy);
        this.setImageUrl(buildImageUrl(maId, baseUrl));
        
    }
    
    
    public String buildImageUrl(String maId, String baseUrl){
    	
    	String url = baseUrl + "/impcImages/images?q=*:*&defType=edismax&wt=json&fq=" + ImageDTO.MA_TERM + ":";
    	url += this.getAnatomyTerm().getName();
    	url += " AND " + ImageDTO.GENE_SYMBOL + ":" + this.getGene().getSymbol();		
    	
    	return url;
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
	
}
