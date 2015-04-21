package uk.ac.ebi.phenotype.chart;

public enum ChartType {

    UNIDIMENSIONAL_SCATTER_PLOT
  , UNIDIMENSIONAL_BOX_PLOT
  , UNIDIMENSIONAL_ABR_PLOT
  , CATEGORICAL_STACKED_COLUMN
  , TIME_SERIES_LINE
  , PIE
  , TIME_SERIES_LINE_BODYWEIGHT
  , PREQC;

    public String getName() {
        return this.toString();
    }
}
