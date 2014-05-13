/*
 * store the result of a fishers test calculation
 */
DROP TABLE IF EXISTS stats_categorical_results;
CREATE TABLE stats_categorical_results (

    id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    control_id                 INT(10) UNSIGNED ,
    control_sex                ENUM('female', 'hermaphrodite', 'male'),
    experimental_id            INT(10) UNSIGNED,
    experimental_sex           ENUM('female', 'hermaphrodite', 'male'),
    experimental_zygosity      ENUM('homozygote', 'heterozygote', 'hemizygote'),
    external_db_id             INT(10),
    project_id                 INT(10) UNSIGNED NOT NULL,
    organisation_id            INT(10) UNSIGNED NOT NULL,
    pipeline_id                INT(10) UNSIGNED NOT NULL,
    parameter_id               INT(10) UNSIGNED NOT NULL,
	colony_id                  VARCHAR(200) NULL,
	dependent_variable         VARCHAR(200) NOT NULL,
    control_selection_strategy VARCHAR(100),
    male_controls              INT(10) UNSIGNED NOT NULL,
    male_mutants               INT(10) UNSIGNED NOT NULL,
    female_controls            INT(10) UNSIGNED NOT NULL,
    female_mutants             INT(10) UNSIGNED NOT NULL,
    metadata_group             VARCHAR(50) DEFAULT '',  
    statistical_method         VARCHAR(50),
    status                     VARCHAR(200),
    category_a                 TEXT,
    category_b                 TEXT,
    p_value                    FLOAT,
    effect_size                FLOAT,
    raw_output                 MEDIUMTEXT NULL,
    
    PRIMARY KEY (id),
    KEY control_idx (control_id),
    KEY experimental_idx (experimental_id),
    KEY organisation_idx (organisation_id),
    KEY pipeline_idx (pipeline_id),
    KEY parameter_idx (parameter_id)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * store the result of a PhenStats calculation
 */
DROP TABLE IF EXISTS stats_unidimensional_results;
CREATE TABLE stats_unidimensional_results (

	id                               INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	control_id                       INT(10) UNSIGNED,
	experimental_id                  INT(10) UNSIGNED,
	experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote'),
    external_db_id                   INT(10),
    project_id                       INT(10) UNSIGNED NOT NULL,
    organisation_id                  INT(10) UNSIGNED NOT NULL,
    pipeline_id                      INT(10) UNSIGNED NOT NULL,
	parameter_id                     INT(10) UNSIGNED NOT NULL,
	colony_id                        VARCHAR(200) NULL,
	dependent_variable               VARCHAR(200) NOT NULL,
    control_selection_strategy       VARCHAR(100),
    male_controls                    INT(10) UNSIGNED NOT NULL,
    male_mutants                     INT(10) UNSIGNED NOT NULL,
    female_controls                  INT(10) UNSIGNED NOT NULL,
    female_mutants                   INT(10) UNSIGNED NOT NULL,
    metadata_group                   VARCHAR(50) DEFAULT '',  
	statistical_method               VARCHAR(200),
	status                           VARCHAR(200),
	batch_significance               BOOLEAN NULL,
	variance_significance            BOOLEAN NULL,
	null_test_significance           FLOAT NULL,
	genotype_parameter_estimate      FLOAT NULL,
	genotype_stderr_estimate         FLOAT NULL,
	genotype_effect_pvalue           FLOAT NULL,
	gender_parameter_estimate        FLOAT NULL,
	gender_stderr_estimate           FLOAT NULL,
	gender_effect_pvalue             FLOAT NULL,
	weight_parameter_estimate        FLOAT NULL,
	weight_stderr_estimate           FLOAT NULL,
	weight_effect_pvalue             FLOAT NULL,
	gp1_genotype                     VARCHAR(200) NULL,
	gp1_residuals_normality_test     FLOAT NULL,
	gp2_genotype                     VARCHAR(200) NULL,
	gp2_residuals_normality_test     FLOAT NULL,
	blups_test                       FLOAT NULL,
	rotated_residuals_normality_test FLOAT NULL,
	intercept_estimate               FLOAT NULL,
	intercept_stderr_estimate        FLOAT NULL,
	interaction_significance         BOOLEAN NULL,
	interaction_effect_pvalue        FLOAT NULL,
	gender_female_ko_estimate        FLOAT NULL,
	gender_female_ko_stderr_estimate FLOAT NULL,
	gender_female_ko_pvalue          FLOAT NULL,
	gender_male_ko_estimate          FLOAT NULL,
	gender_male_ko_stderr_estimate   FLOAT NULL,
	gender_male_ko_pvalue            FLOAT NULL,
	classification_tag               VARCHAR(200) NULL,
	additional_information           TEXT NULL,
	raw_output                       MEDIUMTEXT NULL,

	PRIMARY KEY (id),
    KEY organisation_idx (organisation_id),
    KEY pipeline_idx (pipeline_id),
	KEY parameter_idx (parameter_id)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * Store the relationship between a phenotype call and the result that
 * produced the call.  Could refer to either the stats_categorical_result
 * or a stats_unidimensional_result
 */
DROP TABLE IF EXISTS stat_result_phenotype_call_summary;
CREATE TABLE stat_result_phenotype_call_summary (

    categorical_result_id     INT(10) UNSIGNED DEFAULT NULL,
    unidimensional_result_id  INT(10) UNSIGNED DEFAULT NULL,
    phenotype_call_summary_id INT(10) UNSIGNED NOT NULL,

    PRIMARY KEY (phenotype_call_summary_id, categorical_result_id, unidimensional_result_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

