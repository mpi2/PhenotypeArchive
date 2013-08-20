package uk.ac.ebi.phenotype.stats;

public class Data {
	private String parameterId="";
	/**
	 * get the ParameterId of this if we want it for a link i.e. there is one in the phenotype call summary table
	 * @return
	 */
	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	private int colspan=1;
	public Data(String string) {
		this.dataString=string;
	}
	
	public Data(String string, String paramId) {
		this.dataString=string;
		this.parameterId=paramId;
	}
	
	public Data(String string, int colspan) {
		this.dataString=string;
		this.colspan=colspan;
	}
	public int getColspan() {
		return colspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public String getDataString() {
		return dataString;
	}
	public void setDataString(String dataString) {
		this.dataString = dataString;
	}
	private String dataString="";
	
	@Override
	public String toString(){
		return "'"+dataString+"' colspan="+colspan;
	}
}
