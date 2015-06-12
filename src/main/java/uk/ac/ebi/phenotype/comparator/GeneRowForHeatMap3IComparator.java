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
				score += 20;
			}
		}
		return score;
	}
	
}