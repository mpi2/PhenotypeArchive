package uk.ac.ebi.phenotype.web.pojo;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.DatasourceEntityId;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;


public class AnatomyPageTableRow extends DataTableRow{

	
	String expression;
	
    public AnatomyPageTableRow() {
        super();
    }
    
    public AnatomyPageTableRow(ImageDTO image, String maId) {

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
	
}
