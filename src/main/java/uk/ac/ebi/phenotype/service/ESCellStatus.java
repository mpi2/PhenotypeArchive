package uk.ac.ebi.phenotype.service;
/**
 * class to represent the mouse status at the gene level (not allele)
 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/gene/select/?q=*:*&facet=true&facet.field=latest_mouse_status&rows=0
 * thre states currently: <int name="">43785</int>
<int name="Mice Produced">3707</int>
<int name="Assigned for Mouse Production and Phenotyping">1698</int>
but from Cks code it's not that simple and we have from the facets in the search others as well
 * @author jwarren
 *
 */
public enum ESCellStatus {
	NONE("none"),//will/should we get his status?
	ES_CELLS_PRODUCED("ES Cells Produced"),
	ASSIGNED_FOR_ES_CELL_PRODUCTION("ES cells production in progress"),
	NOT_ASSIGNED_FOR_ES_CELL_PRODUCTION("Not Assigned for ES Cell Production");
	
	String label;
	private ESCellStatus(String label){
		this.label=label;
	}
	public String getLabel(){
		return this.label;
	}
	
	public static ESCellStatus getByLabel(String testlabel){
		    if (testlabel != null) {
		      for (ESCellStatus b : ESCellStatus.values()) {
		        if (testlabel.equalsIgnoreCase(b.label)) {
		          return b;
		        }
		      }
		    }
		    return null;
		  }
}
