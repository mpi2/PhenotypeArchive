/*
 * store the result of a fishers test calculation
 */
DROP TABLE IF EXISTS stats_categorical_results;
CREATE TABLE stats_categorical_results (

    id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    control_id                 INT(10) UNSIGNED,
    control_sex                ENUM('female', 'hermaphrodite', 'male'),
    experimental_id            INT(10) UNSIGNED,
    experimental_sex           ENUM('female', 'hermaphrodite', 'male'),
    experimental_zygosity      ENUM('homozygote', 'heterozygote', 'hemizygote'),
    external_db_id             INT(10),
    project_id                 INT(10) UNSIGNED,
    organisation_id            INT(10) UNSIGNED,
    pipeline_id                INT(10) UNSIGNED,
    parameter_id               INT(10) UNSIGNED,
	colony_id                  VARCHAR(200),
	dependent_variable         VARCHAR(200),
    mp_acc                     VARCHAR(20) NULL,
    mp_db_id                   INT(10) NULL,
    control_selection_strategy VARCHAR(100),
    male_controls              INT(10) UNSIGNED,
    male_mutants               INT(10) UNSIGNED,
    female_controls            INT(10) UNSIGNED,
    female_mutants             INT(10) UNSIGNED,
    metadata_group             VARCHAR(50) DEFAULT '',  
    statistical_method         VARCHAR(50),
    status                     VARCHAR(200),
    category_a                 TEXT,
    category_b                 TEXT,
    p_value                    FLOAT,
    effect_size                FLOAT,
    raw_output                 MEDIUMTEXT,
    
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
    project_id                       INT(10) UNSIGNED,
    organisation_id                  INT(10) UNSIGNED,
    pipeline_id                      INT(10) UNSIGNED,
	parameter_id                     INT(10) UNSIGNED,
	colony_id                        VARCHAR(200),
	dependent_variable               VARCHAR(200),
    control_selection_strategy       VARCHAR(100),
    mp_acc                           VARCHAR(20) NULL,
    mp_db_id                         INT(10) NULL,
    male_controls                    INT(10) UNSIGNED,
    male_mutants                     INT(10) UNSIGNED,
    female_controls                  INT(10) UNSIGNED,
    female_mutants                   INT(10) UNSIGNED,
    metadata_group                   VARCHAR(50) DEFAULT '',  
	statistical_method               VARCHAR(200),
	status                           VARCHAR(200),
	batch_significance               BOOLEAN,
	variance_significance            BOOLEAN,
	null_test_significance           FLOAT,
	genotype_parameter_estimate      FLOAT,
	genotype_stderr_estimate         FLOAT,
	genotype_effect_pvalue           FLOAT,
	gender_parameter_estimate        FLOAT,
	gender_stderr_estimate           FLOAT,
	gender_effect_pvalue             FLOAT,
	weight_parameter_estimate        FLOAT,
	weight_stderr_estimate           FLOAT,
	weight_effect_pvalue             FLOAT,
	gp1_genotype                     VARCHAR(200),
	gp1_residuals_normality_test     FLOAT,
	gp2_genotype                     VARCHAR(200),
	gp2_residuals_normality_test     FLOAT,
	blups_test                       FLOAT,
	rotated_residuals_normality_test FLOAT,
	intercept_estimate               FLOAT,
	intercept_stderr_estimate        FLOAT,
	interaction_significance         BOOLEAN,
	interaction_effect_pvalue        FLOAT,
	gender_female_ko_estimate        FLOAT,
	gender_female_ko_stderr_estimate FLOAT,
	gender_female_ko_pvalue          FLOAT,
	gender_male_ko_estimate          FLOAT,
	gender_male_ko_stderr_estimate   FLOAT,
	gender_male_ko_pvalue            FLOAT,
	classification_tag               VARCHAR(200),
	additional_information           TEXT,
	raw_output                       MEDIUMTEXT,

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

