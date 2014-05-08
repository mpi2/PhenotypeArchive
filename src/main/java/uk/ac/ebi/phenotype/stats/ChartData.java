package uk.ac.ebi.phenotype.stats;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;

public class ChartData {
	BiologicalModel expBiologicalModel;
	private ExperimentDTO experiment;
	private String chart;
	String organisation = "";
	private Float min = new Float(0);
	private Float max = new Float(1000000000);
	private String id;
	private Map<String, List<DiscreteTimePoint>> lines;
	
	public Map<String, List<DiscreteTimePoint>> getLines() {
		return lines;
	}

	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public BiologicalModel getExpBiologicalModel() {
		return expBiologicalModel;
	}

	public void setExpBiologicalModel(BiologicalModel expBiologicalModel) {
		this.expBiologicalModel = expBiologicalModel;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}
	
	public void alterMinMax(double d, double e){
		String chartString = getChart();
		String newChartString = chartString.replace("min: 0", "min: "+d);
		newChartString = newChartString.replace("max: 2", "max: "+e);
		setChart(newChartString);
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}

	public String getChart() {
		return chart;
	}

	public void setChart(String chart) {
		this.chart = chart;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	/**
	 * @return the experiment
	 */
	public ExperimentDTO getExperiment() {
		return experiment;
	}

	/**
	 * @param experiment the experiment to set
	 */
	public void setExperiment(ExperimentDTO experiment) {
		this.experiment = experiment;
	}

	public void setLines(Map<String, List<DiscreteTimePoint>> lines) {
		this.lines=lines;
		
	}
        
        public Set<Float> getUniqueTimePoints(){
            Set timeSet=new TreeSet();
            for(String key: this.lines.keySet()){
                List<DiscreteTimePoint> line = this.lines.get(key);
                for(DiscreteTimePoint point: line){
                    Float time = point.getDiscreteTime();
                    if(!timeSet.contains(time)){
                       timeSet.add(time);
                    }
                    
                }
            }
            return timeSet;
        }

}
