/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a color palette given a set of p-values
 */

public class ColorCodingPalette {

	// Palette (should be moved to another package)
	static List<List<int[]>> rgbOrangeRedPalette = null;
	
	public static final float MIN_PVALUE = 0.0001f;
	
	static {
		
		rgbOrangeRedPalette = new ArrayList<List<int[]>>();
		
		List<int[]> p1 = new ArrayList<int[]>();
		p1.add(new int[] {254,253,227});
		p1.add(new int[] {232,187,74});
		p1.add(new int[] {200,132,51});
		rgbOrangeRedPalette.add(p1);
		
		p1 = new ArrayList<int[]>();
		p1.add(new int[] {254,253,252,215});
		p1.add(new int[] {240,204,141,48});
		p1.add(new int[] {217,138,89,31});
		rgbOrangeRedPalette.add(p1);
		
		p1 = new ArrayList<int[]>();
		p1.add(new int[] {254,253,252,227,179});
		p1.add(new int[] {240,204,141,74,0});
		p1.add(new int[] {217,138,89,51,0});
		rgbOrangeRedPalette.add(p1);
          
		p1 = new ArrayList<int[]>();
		p1.add(new int[] {254,253,253,252,227,179});
		p1.add(new int[] {240,212,187,141,74,0});
		p1.add(new int[] {217,158,132,89,51,0});
		rgbOrangeRedPalette.add(p1);
		
		p1 = new ArrayList<int[]>();
		p1.add(new int[] {254,253,253,252,239,215,153});
		p1.add(new int[] {240,212,187,141,101,48,0});
		p1.add(new int[] {217,158,132,89,72,31,0});
		rgbOrangeRedPalette.add(p1);
		
		p1 = new ArrayList<int[]>();
		p1.add(new int[] {255,254,253,253,252,239,215,153});
		p1.add(new int[] {247,232,212,187,141,101,48,0});
		p1.add(new int[] {236,200,158,132,89,72,31,0});
		rgbOrangeRedPalette.add(p1);
		
		p1 = new ArrayList<int[]>();
		p1.add(new int[] {255,254,253,253,252,239,215,179,127});
		p1.add(new int[] {247,232,212,187,141,101,48,0,0});
		p1.add(new int[] {236,200,158,132,89,72,31,0,0});
		rgbOrangeRedPalette.add(p1);		
		
	}
	
	double[] colors = null;
	double zLimMin = 0;
	double zLimMax = 0;
	List<int[]> palette = null;
	
	/**
	 * @return the colors
	 */
	public double[] getColors() {
		return colors;
	}

	/**
	 * @param colors the colors to set
	 */
	public void setColors(double[] colors) {
		this.colors = colors;
	}

	/**
	 * @return the zLimMin
	 */
	public double getzLimMin() {
		return zLimMin;
	}

	/**
	 * @param zLimMin the zLimMin to set
	 */
	public void setzLimMin(double zLimMin) {
		this.zLimMin = zLimMin;
	}

	/**
	 * @return the zLimMax
	 */
	public double getzLimMax() {
		return zLimMax;
	}

	/**
	 * @param zLimMax the zLimMax to set
	 */
	public void setzLimMax(double zLimMax) {
		this.zLimMax = zLimMax;
	}

	/**
	 * @return the palette
	 */
	public List<int[]> getPalette() {
		return palette;
	}

	/**
	 * @param palette the palette to set
	 */
	public void setPalette(List<int[]> palette) {
		this.palette = palette;
	}

	public void convertPvalueToColorIndex(List<Float> pValues, int maxColorIndex, double scale) {
		convertPvalueToColorIndex(pValues, maxColorIndex, scale, ColorCodingPalette.MIN_PVALUE);
	}

	/**
	 * Convert p values to a color index to color nodes in a graph. 
	 * The P-values are fit into a range from 1 to maColorIndex by applying a scale. 
	 * Before fitting, P-value are transformed by taking a log10, and a minimal 
	 * P-value is needed to avoid -Inf results for very small P-values. 
	 * Scale can either be a number or set to 0 in which case color coding is such 
	 * that all P-values fit into the range. 
	 * See http://search.bioconductor.jp/codes/11308 for an implementation in
	 * BioConductor
	 * @param pValues the P-values
	 * @param maxColorIndex the maximal color index to return
	 * @param scale the color is calculated liked -log10( p.value) * scale, thus 
	 * scale is used to scale the -log10 to the desired range. Either a number 
	 * or set to 0 for automatic scaling
	 * @param minimalPValue the minimal P-value we accept (to avoid infinity for
	 * values close to zero like Monte Carlo process)
	 */
	public void convertPvalueToColorIndex(List<Float> pValues, int maxColorIndex, double scale, double minimalPValue){

		// check p-values
		Double apv[] = pValues.toArray(new Double[]{});
		// convert to color space
		double[] colors = new double[apv.length];

		// to scale from 0 to max color index
		double maxColor = 0;

		// highlight the significant p-value
		for (int i = 0; i<apv.length; i++) {
			if (apv[i] < minimalPValue) {
				apv[i] = minimalPValue;
				colors[i] = -Math.log10(apv[i]);
				if (colors[i] > maxColor) {
					maxColor = colors[i];
				}
			}
		}

		// automatic scaling, scale colors.vals into [0, max.color.index]
		if( scale == 0 ){
			scale = maxColorIndex / maxColor; 
		}

		// scale 
		for (int i = 0; i<colors.length; i++) {
			colors[i]*=scale;
			colors[i] = Math.round(colors[i]);
			// check whether any color is greater than the maxColorIndex
			if (colors[i] > maxColorIndex) {
				colors[i] = maxColorIndex;
			}
		}

		// useless?
		zLimMax = maxColorIndex/scale;
		//  list(col=round(color.vals), zlim=c(0, max.color.index/scale))
	}

	public List<int[]> getPalette(int nbColors) {
		// default palette - 9 colors
		if (nbColors-2 > 2 && nbColors-2 < 8) {
			return rgbOrangeRedPalette.get(nbColors-2);
		}
		return rgbOrangeRedPalette.get(1); // 3 colors
	}
	
	/**
	 * All in one: given a set of p-value
	 * @param pValues
	 * @param maxColorIndex
	 * @param scale
	 * @param minimalPValue
	 */
	public void generateColors(List<Float> pValues, int maxColorIndex, double scale, double minimalPValue) {
		
		List<int[]> palette = getPalette(maxColorIndex);
		
		  //enrich = obj@stats$setE.log2FC > 0
		  
		convertPvalueToColorIndex( 
				pValues, 
				maxColorIndex, 
				scale, 
				minimalPValue);
	}

}
