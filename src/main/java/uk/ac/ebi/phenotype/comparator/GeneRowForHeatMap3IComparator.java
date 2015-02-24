package uk.ac.ebi.phenotype.comparator;

import java.util.Comparator;
import java.util.Map;

import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;


public class GeneRowForHeatMap3IComparator implements Comparator<GeneRowForHeatMap> {

	@Override
	public int compare(GeneRowForHeatMap row1, GeneRowForHeatMap row2) {

		int score1 = scoreGeneRowForHeatMap(row1);
		int score2 = scoreGeneRowForHeatMap(row2);
		return Integer.compare(score2, score1);
	}

	private int scoreGeneRowForHeatMap(GeneRowForHeatMap row){
		Map<String, HeatMapCell> cells = row.getXAxisToCellMap();
		int score = 0;
		for (HeatMapCell cell : cells.values()){
			if (cell.getStatus().equalsIgnoreCase(HeatMapCell.THREE_I_COULD_NOT_ANALYSE)){
				score += 1;
			}
			else if (cell.getStatus().equalsIgnoreCase(HeatMapCell.THREE_I_DATA_ANALYSED_NOT_SIGNIFICANT)){
				score += 2;
			}
			else if (cell.getStatus().equalsIgnoreCase(HeatMapCell.THREE_I_DEVIANCE_SIGNIFICANT)){
				score += 2;
			}
		}
		return score;
	}
	
}