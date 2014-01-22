/*
 * store the result of a fishers test calculation
 */
DROP TABLE IF EXISTS stats_categorical_results;
CREATE TABLE stats_categorical_results (

    id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    control_selection_strategy VARCHAR(100),
	statistical_method         VARCHAR(50),
	status                     VARCHAR(200),
	raw_output                 TEXT,
    control_id                 INT(10) UNSIGNED NOT NULL,
    control_sex                ENUM('female', 'hermaphrodite', 'male'),
    control_zygosity           ENUM('homozygote', 'heterozygote', 'hemizygote'),
    experimental_id            INT(10) UNSIGNED NOT NULL,
    experimental_sex           ENUM('female', 'hermaphrodite', 'male'),
    experimental_zygosity      ENUM('homozygote', 'heterozygote', 'hemizygote'),
    organisation_id            INT(10) UNSIGNED NOT NULL,
    parameter_id               INT(10) UNSIGNED NOT NULL,
    category_a                 VARCHAR(200) NOT NULL,
    category_b                 VARCHAR(200) NOT NULL,
    p_value                    FLOAT NOT NULL,
    effect_size                FLOAT NOT NULL,
    
    PRIMARY KEY (id),
    KEY control_idx (control_id),
    KEY experimental_idx (experimental_id),
    KEY parameter_idx (parameter_id)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * store the result of a PhenStats calculation
 */
DROP TABLE IF EXISTS stats_unidimensional_results;
CREATE TABLE stats_unidimensional_results (

	id                               INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    control_selection_strategy       VARCHAR(100),
	statistical_method               VARCHAR(50),
	status                           VARCHAR(200),
	raw_output                       TEXT,
	control_id                       INT(10) UNSIGNED NOT NULL,
	experimental_id                  INT(10) UNSIGNED NOT NULL,
	experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote'),
	colony_id                        VARCHAR(200) NOT NULL,
	organisation_id                  INT(10) UNSIGNED NOT NULL,
	parameter_id                     INT(10) UNSIGNED NOT NULL,
	dependant_variable               VARCHAR(200) NOT NULL,
	batch_significance               BOOLEAN NOT NULL,
	variance_significance            BOOLEAN NOT NULL,
	null_test_significance           FLOAT NOT NULL,
	genotype_parameter_estimate      FLOAT NULL,
	genotype_stderr_estimate         FLOAT NULL,
	genotype_effect_pvalue           FLOAT NULL,
	gender_parameter_estimate        FLOAT NULL,
	gender_stderr_estimate           FLOAT NULL,
	gender_effect_pvalue             FLOAT NULL,
	weight_parameter_estimate        FLOAT NULL,
	weight_stderr_estimate           FLOAT NULL,
	weight_effect_pvalue             FLOAT NULL,
	gp1_genotype                     VARCHAR(200) NOT NULL,
	gp1_residuals_normality_test     FLOAT NULL,
	gp2_genotype                     VARCHAR(200) NOT NULL,
	gp2_residuals_normality_test     FLOAT NULL,
	blups_test                       FLOAT NULL,
	rotated_residuals_normality_test FLOAT NULL,
	intercept_estimate               FLOAT NULL,
	intercept_stderr_estimate        FLOAT NULL,
	interaction_significance         BOOLEAN NOT NULL,
	interaction_effect_pvalue        FLOAT NULL,
	gender_female_ko_estimate        FLOAT NULL,
	gender_female_ko_stderr_estimate FLOAT NULL,
	gender_female_ko_pvalue          FLOAT NULL,
	gender_male_ko_estimate          FLOAT NULL,
	gender_male_ko_stderr_estimate   FLOAT NULL,
	gender_male_ko_pvalue            FLOAT NULL,
	classification_tag               VARCHAR(200) NULL,
	cohensf                          TEXT NULL,

	PRIMARY KEY (id),
	KEY parameter_idx (parameter_id)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * Store the relationship between a phenotype call and the result that
 * produced the call.  Could refer to either the stats_categorical_result
 * or a stats_unidimensional_result
 */
DROP TABLE IF EXISTS stat_result_phenotype_call_summary;
CREATE TABLE stat_result_phenotype_call_summary (

    result_id                 INT(10) UNSIGNED NOT NULL,
    phenotype_call_summary_id INT(10) UNSIGNED NOT NULL,

    PRIMARY KEY (phenotype_call_summary_id, result_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

