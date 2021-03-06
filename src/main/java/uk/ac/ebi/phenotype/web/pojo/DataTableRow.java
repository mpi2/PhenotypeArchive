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
package uk.ac.ebi.phenotype.web.pojo;

import uk.ac.ebi.phenotype.chart.ChartUtils;
import uk.ac.ebi.phenotype.pojo.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * Abstract representation of a single row in a gene or phenotype page
 * "phenotypes" HTML table. This class is a repository for common code but does
 * not (and must not) contain compareTo methods as they differ by [gene or
 * phenotype] page. Making the class abstract forces users to instantiate
 * a subclass that extends this class, such as GenePageTableRow or
 * PhenotypePageTableRow. Should more flavours of the "phenotypes" HTML table
 * be needed, simply extend this class and write a compareTo method.
 * 
 * This class used to be called PhenotypeRow serving gene and phenotype pages
 * but was broken out into this abstract class and two concrete classes to
 * architect correct "phenotypes" HTML page row ordering.
 */
public abstract class DataTableRow implements Comparable<DataTableRow> {

    private Map<String, String> config;
    protected OntologyTerm phenotypeTerm;
    protected GenomicFeature gene;
    protected Allele allele;
    protected List<String> sexes;
    protected ZygosityType zygosity;
    protected int projectId;
    protected String phenotypingCenter;
    protected Procedure procedure;
    protected Parameter parameter;
    protected String dataSourceName;//to hold the name of the origin of the data e.g. Europhenome or WTSI Mouse Genetics Project
    protected String graphUrl;
    protected Pipeline pipeline;
    protected Double pValue;
    protected boolean isPreQc;
    protected String gid;

    public DataTableRow() { }


    public DataTableRow(PhenotypeCallSummary pcs, String baseUrl, Map<String, String> config) {

	    this.config = config;
        List<String> sex = new ArrayList<String>();
        sex.add(pcs.getSex().toString());
        this.setGid(pcs.getgId());
        this.setPreQc(pcs.isPreQC());
        this.setGene(pcs.getGene());
        this.setAllele(pcs.getAllele());
        this.setSexes(sex);
        this.setPhenotypeTerm(pcs.getPhenotypeTerm());
        this.setPipeline(pcs.getPipeline());
		// zygosity representation depends on source of information
        // we need to know what the data source is so we can generate appropriate link on the page

        this.pValue = pcs.getpValue();
        this.setDataSourceName(pcs.getDatasource().getName());

        this.setZygosity(pcs.getZygosity());
        if (pcs.getExternalId() != null) {
            this.setProjectId(pcs.getExternalId());
        }

        this.setProcedure(pcs.getProcedure());
        this.setParameter(pcs.getParameter());
        this.setPhenotypingCenter(pcs.getPhenotypingCenter());

        this.setGraphUrl(baseUrl);

    }

    @Override
    public abstract int compareTo(DataTableRow o);


    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

	/**
	 * @return the gid
	 */
	public String getGid() {
	
		return gid;
	}

	
	/**
	 * @param gid the gid to set
	 */
	public void setGid(String gid) {
	
		this.gid = gid;
	}


	/**
	 * @return the pValue
	 */
	public Double getpValue() {
	
		return pValue;
	}
	
	/**
	 * @param pValue the pValue to set
	 */
	public void setpValue(Double pValue) {
	
		this.pValue = pValue;
	}
	
	/**
	 * @return the isPreQc
	 */
	public boolean isPreQc() {
	
		return isPreQc;
	}
	
	/**
	 * @param isPreQc the isPreQc to set
	 */
	public void setPreQc(boolean isPreQc) {
	
		this.isPreQc = isPreQc;
	}

	public void setPValue(Double pValue) {
        this.pValue = pValue;
    }

    public Double getPrValue() {
        return this.pValue;
    }

    public String getPrValueAsString() {
        BigDecimal bd = new BigDecimal(this.pValue);
        bd = bd.round(new MathContext(3));
        double rounded = bd.doubleValue();
        String result = Double.toString(rounded);
        return result;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public String getGraphUrl() {
        return graphUrl;
    }

    public void setGraphUrl(String graphBaseUrl) {
        this.graphUrl = buildGraphUrl(graphBaseUrl);
    }

    public String buildGraphUrl(String baseUrl) {
    	String url= baseUrl;
    	if (!isPreQc){
    		url = ChartUtils.getChartPageUrlPostQc(baseUrl, gene.getId().getAccession(), allele.getId().getAccession(), zygosity, parameter.getStableId(),
    		pipeline.getStableId(), phenotypingCenter);
        } else {
		    // Need to use the drupal base url because phenoview is not mapped under the /data url
		    url = config.get("drupalBaseUrl");
		    url += "/../phenoview/?gid=" + gid;
		    url += "&qeid=" + parameter.getStableId();
        }
        return url;
    }

    public String getPhenotypingCenter() {
        return this.phenotypingCenter;
    }

    public void setPhenotypingCenter(String phenotypingCenter) {
        this.phenotypingCenter = phenotypingCenter;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public OntologyTerm getPhenotypeTerm() {
        return phenotypeTerm;
    }

    public void setPhenotypeTerm(OntologyTerm term) {
        this.phenotypeTerm = term;
    }

    public Allele getAllele() {
        return allele;
    }

    public void setAllele(Allele allele) {
        this.allele = allele;
    }

    public List<String> getSexes() {
        return sexes;
    }

    public void setSexes(List<String> sex) {
        this.sexes = sex;
    }

    public ZygosityType getZygosity() {
        return zygosity;
    }

    public void setZygosity(ZygosityType zygosityType) {
        this.zygosity = zygosityType;
    }

    /**
     * @return the projectId
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public GenomicFeature getGene() {
        return gene;
    }

    public void setGene(GenomicFeature gene) {
        this.gene = gene;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allele == null) ? 0 : allele.hashCode());
        result = prime * result + ((dataSourceName == null) ? 0 : dataSourceName.hashCode());
        result = prime * result + ((phenotypeTerm == null) ? 0 : phenotypeTerm.hashCode());
        result = prime * result + ((phenotypingCenter == null) ? 0 : phenotypingCenter.hashCode());
        result = prime * result + ((zygosity == null) ? 0 : zygosity.hashCode());
        if (gene != null) {
            result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
            result = prime * result + ((procedure == null) ? 0 : procedure.hashCode());
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataTableRow other = (DataTableRow) obj;
        if (allele == null) {
            if (other.allele != null) {
                return false;
            }
        } else if ( ! allele.equals(other.allele)) {
            return false;
        }
        if (dataSourceName == null) {
            if (other.dataSourceName != null) {
                return false;
            }
        } else if ( ! dataSourceName.equals(other.dataSourceName)) {
            return false;
        }
        if (parameter == null) {
            if (other.parameter != null) {
                return false;
            }
        } else if ( ! parameter.equals(other.parameter)) {
            return false;
        }
        if (procedure == null) {
            if (other.procedure != null) {
                return false;
            }
        } else if ( ! procedure.equals(other.procedure)) {
            return false;
        }
        if (gene == null) {
            if (other.gene != null) {
                return false;
            }
        } else if ( ! gene.equals(other.gene)) {
            return false;
        }
        if (phenotypingCenter == null) {
            if (other.phenotypingCenter != null) {
                return false;
            }
        } else if ( ! phenotypingCenter.equals(other.phenotypingCenter)) {
            return false;
        }
        if (phenotypeTerm == null) {
            if (other.phenotypeTerm != null) {
                return false;
            }
        } else if ( ! phenotypeTerm.equals(other.phenotypeTerm)) {
            return false;
        }
        if (zygosity != other.zygosity) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PhenotypeRow [phenotypeTerm=" + phenotypeTerm
                + ", gene=" + gene + ", allele=" + allele + ", sexes=" + sexes
                + ", zygosity=" + zygosity
                + ", projectId=" + projectId + ", procedure=" + procedure
                + ", parameter=" + parameter + ", dataSourceName="
                + dataSourceName + ", phenotypingCenter=" + phenotypingCenter + "]";
    }

    
    public String toTabbedString(String targetPage) {

        String res = "";

        if (targetPage.equalsIgnoreCase("gene")) {
            res = getPhenotypeTerm().getName() + "\t"
                    + getAllele().getSymbol() + "\t"
                    + getZygosity() + "\t"
                    + getSexes().get(0) + "\t"
                    + getProcedure().getName() + " | " + getParameter().getName() + "\t"
                    + getPhenotypingCenter() + "\t"
                    + getDataSourceName() + "\t"
                    + getPrValueAsString() + "\t"
                    + getGraphUrl();
        } else if (targetPage.equalsIgnoreCase("phenotype")) {
            res = getGene().getSymbol() + "\t"
                    + getAllele().getSymbol() + "\t"
                    + getZygosity() + "\t"
                    + getSexes().get(0) + "\t"
                    + getPhenotypeTerm().getName() + "\t"
                    + getProcedure().getName() + " | " + getParameter().getName() + "\t"
                    + getPhenotypingCenter() + "\t"
                    + getDataSourceName() + "\t"
                    + getPrValueAsString() + "\t"
                    + getGraphUrl();
        }
        return res;
    }

}
