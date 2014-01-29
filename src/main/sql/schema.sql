/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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

-- KOMP2 database definition

ALTER DATABASE komp2 CHARACTER SET utf8 COLLATE utf8_general_ci;
set NAMES utf8;
SET collation_connection = utf8_general_ci;

--
-- Drop all the tables if they exist
--
DROP TABLE IF EXISTS meta_info;
DROP TABLE IF EXISTS allele;
DROP TABLE IF EXISTS biological_model;
DROP TABLE IF EXISTS biological_model_allele;
DROP TABLE IF EXISTS biological_model_strain;
DROP TABLE IF EXISTS biological_model_genomic_feature;
DROP TABLE IF EXISTS biological_model_phenotype;
DROP TABLE IF EXISTS biological_model_sample;
DROP TABLE IF EXISTS biological_sample;
DROP TABLE IF EXISTS biological_sample_relationship;
DROP TABLE IF EXISTS categorical_observation;
DROP TABLE IF EXISTS coord_system;
DROP TABLE IF EXISTS experiment;
DROP TABLE IF EXISTS experiment_observation;
DROP TABLE IF EXISTS external_db;
DROP TABLE IF EXISTS genomic_feature;
DROP TABLE IF EXISTS ilar;
DROP TABLE IF EXISTS image_record_observation;
DROP TABLE IF EXISTS live_sample;
DROP TABLE IF EXISTS metadata_observation;
DROP TABLE IF EXISTS multidimensional_observation;
DROP TABLE IF EXISTS observation;
DROP TABLE IF EXISTS observation_population;
DROP TABLE IF EXISTS population;
DROP TABLE IF EXISTS ontology_relationship;
DROP TABLE IF EXISTS ontology_term;
DROP TABLE IF EXISTS organisation;
DROP TABLE IF EXISTS participant;
DROP TABLE IF EXISTS phenotype_annotation_type;
DROP TABLE IF EXISTS phenotype_call_summary;
DROP TABLE IF EXISTS phenotype_annotation;
DROP TABLE IF EXISTS phenotype_parameter;
DROP TABLE IF EXISTS phenotype_parameter_lnk_eq_annotation;
DROP TABLE IF EXISTS phenotype_parameter_eq_annotation;
DROP TABLE IF EXISTS phenotype_parameter_lnk_ontology_annotation;
DROP TABLE IF EXISTS phenotype_parameter_lnk_increment;
DROP TABLE IF EXISTS phenotype_parameter_lnk_option;
DROP TABLE IF EXISTS phenotype_parameter_ontology_annotation;
DROP TABLE IF EXISTS phenotype_parameter_increment;
DROP TABLE IF EXISTS phenotype_parameter_option;
DROP TABLE IF EXISTS phenotype_pipeline;
DROP TABLE IF EXISTS phenotype_pipeline_procedure;
DROP TABLE IF EXISTS phenotype_procedure;
DROP TABLE IF EXISTS phenotype_procedure_meta_data;
DROP TABLE IF EXISTS phenotype_procedure_parameter;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS seq_region;
DROP TABLE IF EXISTS strain;
DROP TABLE IF EXISTS synonym;
DROP TABLE IF EXISTS text_observation;
DROP TABLE IF EXISTS time_series_observation;
DROP TABLE IF EXISTS unidimensional_observation;
DROP TABLE IF EXISTS xref;
DROP TABLE IF EXISTS image_record_observation;
DROP TABLE IF EXISTS dimension;
DROP TABLE IF EXISTS parameter_association;
DROP TABLE IF EXISTS procedure_meta_data;

/**
 * Contains meta information about the database like
 * the version of the code that can run safely on the data
 * the mouse assembly version of the data
 * the different phenodeviant calls made for this version
 * the version of the database schema
 */
CREATE TABLE meta_info (
	id                          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	property_key                VARCHAR(255) NOT NULL DEFAULT '',
	property_value              VARCHAR(255) NOT NULL DEFAULT '',
	description                 TEXT,

    PRIMARY KEY (id),
    UNIQUE KEY key_idx (property_key),
    KEY value_idx (property_value)

) COLLATE=utf8_general_ci ENGINE=MyISAM;
    
/**
@table project
@desc This table stores information about each phenotyping project
We made this table generic enough to store legacy project data
*/
CREATE TABLE project (
	id                          INT(10) UNSIGNED NOT NULL,
	name                        VARCHAR(255) NOT NULL DEFAULT '',
	fullname                    VARCHAR(255) NOT NULL DEFAULT '',
	description                 TEXT,

    PRIMARY KEY (id),
    UNIQUE KEY name_idx (name)

    ) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE organisation (

    id                          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    name                        VARCHAR(255) NOT NULL DEFAULT '',
    fullname                    VARCHAR(255) NOT NULL DEFAULT '',
    country                     VARCHAR(50),

    PRIMARY KEY (id),
    UNIQUE KEY name_idx (name)

    ) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE participant (

    project_id                  INT(10) UNSIGNED NOT NULL,
    organisation_id             INT(10) UNSIGNED NOT NULL,
    role                        VARCHAR(255) NOT NULL DEFAULT '',

    KEY project (project_id),
    KEY organisation (organisation_id)

    ) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Very mouse specific . What about Zebrafish? ZFIN
 */
CREATE TABLE ilar (

    labcode                     VARCHAR(50) NOT NULL,
    status                      ENUM('active', 'pending', 'retired'),
    investigator                VARCHAR(255) NOT NULL DEFAULT '',
    organisation                VARCHAR(255) NOT NULL DEFAULT '',
	
	PRIMARY KEY (labcode)
	
	) COLLATE=utf8_general_ci ENGINE=MyISAM;	

/**
 * This table holds all the external datasources (MGI, Ensembl)
 */	

-- this table holds all the external/ASTD database names 
CREATE TABLE external_db (

    id                          INT(10) UNSIGNED NOT NULL,
    name                        VARCHAR(100) NOT NULL,
    short_name                  VARCHAR(40) NOT NULL,
    version                     VARCHAR(15) NOT NULL DEFAULT '',
    version_date                DATE not NULL,
    
    PRIMARY   KEY (id),
    UNIQUE    KEY name_idx (name, version, version_date)

) COLLATE=utf8_general_ci ENGINE=MyISAM;
	
/**
 * This table will store the ontological terms we need for controlled vocabulary
 */
CREATE TABLE ontology_term (
    acc                      VARCHAR(20) NOT NULL,
    db_id                    INT(10) NOT NULL,
    name                     TEXT NOT NULL,
    description              TEXT,
    is_obsolete              TINYINT(1) DEFAULT 0,
    PRIMARY   KEY (acc, db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE ontology_relationship (
    a_acc                      VARCHAR(20) NOT NULL,
    a_db_id                    INT(10) NOT NULL,
    b_acc                      VARCHAR(20) NOT NULL,
    b_db_id                    INT(10) NOT NULL,
    relationship               VARCHAR(30) NOT NULL,
    
    KEY (a_acc, a_db_id),
    KEY (b_acc, b_db_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * The sequence and genomic information goes there.
 * We picked up some of the table from Ensembl to build our sequence information
 * At the moment, we don't plan to have multiple coordinate system for the same
 * genome.
 */ 

CREATE TABLE coord_system (

  id                          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  name                        VARCHAR(40) NOT NULL, 
  strain_acc                  VARCHAR(20) DEFAULT NULL,
  strain_db_id                INT(10) UNSIGNED DEFAULT NULL,
  db_id                       INT(10) NOT NULL,

  PRIMARY   KEY (id),
  UNIQUE    KEY name_idx (db_id, strain_db_id),
  KEY db_idx (db_id),
  KEY strain_idx (strain_db_id)
  
) COLLATE=utf8_general_ci ENGINE=MyISAM;
	
	
CREATE TABLE seq_region (

  id                          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  name                        VARCHAR(40) NOT NULL,
  coord_system_id             INT(10) UNSIGNED NOT NULL,
  length                      INT(10) UNSIGNED NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY name_cs_idx (name, coord_system_id),
  KEY cs_idx (coord_system_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;	

/**
 * Genomic feature table
 * Contains any genomic site, whether functional or not 
 * that can be mapped through formal genetic analysis
 */
CREATE TABLE genomic_feature (
    acc                       VARCHAR(20) NOT NULL,
    db_id                     INT(10) NOT NULL,
    symbol                    VARCHAR(100) NOT NULL,
    name                      VARCHAR(200) NOT NULL,
    biotype_acc               VARCHAR(20) NOT NULL,
    biotype_db_id             INT(10) NOT NULL,
    subtype_acc               VARCHAR(20),
    subtype_db_id             INT(10),
    seq_region_id             INT(10) UNSIGNED,
    seq_region_start          INT(10) UNSIGNED DEFAULT 0,
    seq_region_end            INT(10) UNSIGNED DEFAULT 0,
    seq_region_strand         TINYINT(2) DEFAULT 0,
    cm_position               VARCHAR(40),
    status                    ENUM('active', 'withdrawn') NOT NULL DEFAULT 'active',   
    
    PRIMARY   KEY (acc, db_id),
    KEY genomic_feature_symbol_idx (symbol),
    KEY genomic_feature_acc_idx (acc),
    KEY seq_region_idx (seq_region_id),
    KEY biotype_idx (biotype_acc, biotype_db_id),
    KEY subtype_idx (subtype_acc, subtype_db_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE synonym (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    acc                       VARCHAR(20) NOT NULL,
    db_id                     INT(10) NOT NULL,
    symbol                    TEXT NOT NULL,

    PRIMARY KEY (id),
    KEY genomic_feature_idx (acc, db_id),
    KEY genomic_feature_acc_idx (acc)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE FULLTEXT INDEX synonym_symbol_idx ON synonym (symbol);

/**
 * Genomic feature cross-reference from other datasources.
 */
CREATE TABLE xref (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    acc                       VARCHAR(20) NOT NULL,
    db_id                     INT(10) NOT NULL,
    xref_acc                  VARCHAR(20) NOT NULL,
    xref_db_id                INT(10) NOT NULL,
    
    PRIMARY KEY (id),
    KEY genomic_feature_idx (acc, db_id),
    KEY xref_idx (xref_acc, xref_db_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Allele table
 * Contains sequence variant of a gene recognized by a DNA assay (polymorphic)
 * or a variant phenotype (mutant)
 */
CREATE TABLE allele (

    acc                       VARCHAR(20) NOT NULL,
    db_id                     INT(10) NOT NULL,
    gf_acc                    VARCHAR(20),
    gf_db_id                  INT(10),
    biotype_acc               VARCHAR(20),
    biotype_db_id             INT(10),    
    symbol					  VARCHAR(100) NOT NULL,
    name                      VARCHAR(200) NOT NULL,

    PRIMARY KEY (acc, db_id),
    KEY genomic_feature_idx (gf_acc, gf_db_id),
    KEY biotype_idx (biotype_acc, biotype_db_id),
    KEY symbol_idx (symbol)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Strain table
 */
CREATE TABLE strain (

    acc                       VARCHAR(20) NOT NULL,
    db_id                     INT(10) NOT NULL,
    biotype_acc               VARCHAR(20),
    biotype_db_id             INT(10),
    name                      VARCHAR(200) NOT NULL,

    PRIMARY KEY (acc, db_id),
    KEY biotype_idx (biotype_acc, biotype_db_id),
    KEY name_idx (name)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE biological_model (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    db_id                     INT(10) NOT NULL, 
    allelic_composition       VARCHAR(200) NOT NULL,
    genetic_background        VARCHAR(200) NOT NULL,
    zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote') DEFAULT NULL,
    PRIMARY KEY (id),
    KEY allelic_composition_idx (allelic_composition),
    KEY genetic_background_idx (genetic_background)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE biological_model_allele (

    biological_model_id       INT(10) UNSIGNED NOT NULL,
    allele_acc                VARCHAR(20) NOT NULL,
    allele_db_id              INT(10) NOT NULL,
    
    KEY biological_model_idx (biological_model_id),
    KEY allele_idx (allele_acc, allele_db_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE biological_model_strain (

    biological_model_id       INT(10) UNSIGNED NOT NULL,
    strain_acc                VARCHAR(20) NOT NULL,
    strain_db_id              INT(10) NOT NULL,
    
    KEY biological_model_idx (biological_model_id),
    KEY strain_idx (strain_acc, strain_db_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * This table is an association table between the 
 * allele table, the phenotype information and biological model
 */
CREATE TABLE biological_model_phenotype (

    biological_model_id       INT(10) UNSIGNED NOT NULL,
    phenotype_acc             VARCHAR(20) NOT NULL,
    phenotype_db_id           INT(10) NOT NULL,
    
    KEY biological_model_idx (biological_model_id),
    KEY phenotype_idx (phenotype_acc, phenotype_db_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE biological_model_genomic_feature (

    biological_model_id       INT(10) UNSIGNED NOT NULL,
    gf_acc                    VARCHAR(20) NOT NULL,
    gf_db_id                  INT(10) NOT NULL,
    
    KEY biological_model_idx (biological_model_id),
    KEY genomic_feature_idx (gf_acc, gf_db_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Links a sample to a biological model
 */
CREATE TABLE biological_model_sample (

    biological_model_id       INT(10) UNSIGNED NOT NULL,
    biological_sample_id      INT(10) UNSIGNED NOT NULL,
    
    KEY biological_model_idx (biological_model_id),
    KEY biological_sample_idx (biological_sample_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Experimental sample
 * Contains information about a sample
 * A sample can be an animal specimen, an organ, cell, sperm, etc. 
 * The EFO ontology can be used to reference 'whole organism', 'animal fluid', 'animal body part'
 * A sample group defines what role or experimental group the sample belongs to. It can be 'control' / 'experimental'
 */
CREATE TABLE biological_sample (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    external_id               VARCHAR(100), 
    db_id                     INT(10),
    sample_type_acc           VARCHAR(20) NOT NULL,
    sample_type_db_id         INT(10) NOT NULL,            
    sample_group              VARCHAR(100) NOT NULL,
    organisation_id           INT(10) UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    KEY external_id_idx(external_id),
    KEY external_db_idx(db_id),
	KEY group_idx (sample_group),
	KEY sample_type_idx (sample_type_acc, sample_type_db_id),

	KEY organisation_idx (organisation_id)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * An animal sample is a type of sample
 * The discriminative value is on the sample type
 */

CREATE TABLE live_sample (

    id                        INT(10) UNSIGNED NOT NULL,
    colony_id                 VARCHAR(100) NOT NULL,
    developmental_stage_acc   VARCHAR(20) NOT NULL,
    developmental_stage_db_id INT(10) NOT NULL, 
    sex                       ENUM('female', 'hermaphrodite', 'male'),
    zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote'),
    date_of_birth             TIMESTAMP,
    
    PRIMARY KEY (id),
    KEY colony_idx (colony_id),
	KEY gender_idx (sex),
	KEY zygosity_idx (zygosity),    
	KEY developmental_stage_idx (developmental_stage_acc, developmental_stage_db_id)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM;	
    
/**
 * One sample can refer to another sample
 * Example one: organ to whole organism as a part_of relationship
 */
CREATE TABLE biological_sample_relationship (

    biological_sample_a_id     INT(10),
    biological_sample_b_id     INT(10),
    relationship               VARCHAR(30) NOT NULL,
    
    KEY sample_a_idx (biological_sample_a_id),
    KEY sample_b_idx (biological_sample_b_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * experiment
 * A scientific procedure undertaken to make a discovery, test a hypothesis, or
 * demonstrate a known fact. 
 * An experiment has several observation associated to it.
 * See table observation
 */
CREATE TABLE experiment (

    id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    db_id                      INT(10) UNSIGNED NOT NULL,
    external_id                VARCHAR(50),
    date_of_experiment         TIMESTAMP NULL DEFAULT NULL,
    organisation_id            INT(10) UNSIGNED NOT NULL,  
    metadata_combined          TEXT,  
    metadata_group             VARCHAR(50) DEFAULT '',  
    
    PRIMARY KEY(id),
    KEY external_db_idx(db_id),
    KEY organisation_idx(organisation_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Links multiple observations to experiment
 */

CREATE TABLE experiment_observation (
    experiment_id              INT(10) UNSIGNED NOT NULL,
    observation_id             INT(10) UNSIGNED NOT NULL,
    
    KEY experiment_idx(experiment_id),
    KEY observation_idx(observation_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * observation
 * An observation is a experimental parameter measurement (data point, image 
 * record, etc.) 
 * of a phenotype of a given control/experimental biological sample.
 * Measurement are diverse. observation_type is the discriminator in this table.
 * Children observation tables represent the diversity of the type of 
 * observations.
 * db_id: indicates where the data are coming from. Convenient when selecting or
 * deleting data from this table
 * missing: tells if there was no data for this observation.
 */
CREATE TABLE observation (

    id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    db_id                      INT(10) UNSIGNED NOT NULL,
	biological_sample_id       INT(10) UNSIGNED NOT NULL,
	parameter_id               INT(10) UNSIGNED NOT NULL,
	parameter_stable_id        varchar(30) NOT NULL,
	population_id              INT(10) UNSIGNED NOT NULL,
	observation_type           enum('categorical', 'image_record', 'unidimensional', 'multidimensional', 'time_series', 'metadata', 'text'),
	missing                    TINYINT(1) DEFAULT 0,
	parameter_status varchar(450) DEFAULT NULL,
	PRIMARY KEY(id),
	KEY biological_sample_idx(biological_sample_id),
	KEY parameter_idx(parameter_id),
	KEY parameter_stable_idx(parameter_stable_id),
	KEY population_idx(population_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


/**
 * observation_population
 * An observation is a experimental parameter measurement (data point, image 
 * record, etc.) 
 * of a phenotype of a given control/experimental biological sample.
 * id: primary surrogate key for this table
 * population_id: grouping index for collection observations together
 * observation_id: the observation belonging to this population
 */
CREATE TABLE observation_population (

    id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	observation_id             INT(10) UNSIGNED NOT NULL,
	population_id              INT(10) UNSIGNED NOT NULL,

	PRIMARY KEY(id),
	KEY population_observation_idx(population_id, observation_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE population (

    id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    parameter_id               INT(10) UNSIGNED NOT NULL,
    organisation_id            INT(10) UNSIGNED NOT NULL,
    acc                        VARCHAR(20) NOT NULL,
    db_id                      INT(10) NOT NULL,
    zygosity                   ENUM('homozygote', 'heterozygote', 'hemizygote'),
    sex                        ENUM('female', 'hermaphrodite', 'male'),
    control_batches            INT(10) UNSIGNED NOT NULL DEFAULT 0,
    experimental_batches       INT(10) UNSIGNED NOT NULL DEFAULT 0,
    concurrent_controls        TINYINT(1) DEFAULT 0,

    PRIMARY KEY(id),
    KEY parameter_idx(parameter_id),
    KEY genomic_feature_idx (acc, db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


/**
 * text_observation
 * Free text to annotate a phenotype
 */
CREATE TABLE text_observation (

    id                        INT(10) UNSIGNED NOT NULL,
    text                      TEXT,
    
    PRIMARY KEY(id),
    KEY text_idx(text(255))
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * categorical_observation
 * Categorical phenotypic observation like
 * coat hair pattern: mono-colored, multi-colored, spotted, etc.
 */
CREATE TABLE categorical_observation (

    id                        INT(10) UNSIGNED NOT NULL,
    category                  VARCHAR(200) NOT NULL,
    
    PRIMARY KEY(id),
    KEY category_idx(category)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/** 
 * image_record_observation
 * Link to single/multiple image/movie records in the database
 */
/** OBSOLETED 2013-12-09 
CREATE TABLE image_record_observation (

    id                        INT(10) UNSIGNED NOT NULL,
    image_record_id           INT(11) UNSIGNED NOT NULL,
    
    PRIMARY KEY(id),
    KEY image_record_idx(image_record_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;
*/

/** 
 * unidimensional_observation
 * Unidimensional data point measurement
 */
CREATE TABLE unidimensional_observation (

    id                        INT(10) UNSIGNED NOT NULL,
    data_point                FLOAT NOT NULL,
    
    PRIMARY KEY(id),
    KEY data_point_idx(data_point)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/** 
 * multidimensional_observation
 * multidimensional data point measurement
 * data_value: store the value of the observation in dimension 'dimension'
 * order_index: useful for time series or series of values. Keep the order
 * of observations in multiple dimension
 * dimension: dimension definition (x, y, z, t, etc.). It can also be used to
 * store multiple series of observations if needed.
 */
CREATE TABLE multidimensional_observation (

    id                        INT(10) UNSIGNED NOT NULL,
    data_point                FLOAT NOT NULL,
    order_index               INT(10) NOT NULL,
	dimension                 VARCHAR(40) NOT NULL,
    
    PRIMARY KEY(id),
    KEY data_point_idx(data_point, order_index),
    KEY dimension_idx(dimension)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * time_series_observation
 * A time series is a sequence of observations which are ordered in time 
 * (or space).
 */
CREATE TABLE time_series_observation (

    id                        INT(10) UNSIGNED NOT NULL,
    data_point                FLOAT NOT NULL,
    time_point                TIMESTAMP,
    discrete_point            FLOAT,
    
    PRIMARY KEY(id),
    KEY data_point_idx(data_point, time_point)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * metadata_observation
 * Some experimental settings can change from one experiment to another.
 * This table stores the meta information associated to the observation
 * as value property
 */
CREATE TABLE metadata_observation (

	id                        INT(10) UNSIGNED NOT NULL,
	property_value            VARCHAR(100) NOT NULL,
	
	PRIMARY KEY(id),
	KEY property_value_idx(property_value)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM;


/**
 * --------------------------
 * Phenotype pipeline tables
 * --------------------------
 */

/**
@table phenotype_pipeline
@desc This table stores information about each phenotyping pipeline
At the moment, this information is managed by MRC Harwell in 2 central
resources called EMPReSS and IMPReSS
The Phenotype archive stores this information for convenience

@column id                    internal id of the pipeline. Primary key.
@column stable_id             stable id from external resource
@column db_id                 external db id
@column name                  pipeline name.

@see external_db
*/
CREATE TABLE phenotype_pipeline (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    stable_id                 VARCHAR(40) NOT NULL,
    db_id                     INT(10) NOT NULL,
    name                      VARCHAR(100) NOT NULL,
    description               VARCHAR(200),
    major_version             INT(10) NOT NULL DEFAULT 1,
    minor_version             INT(10) NOT NULL DEFAULT 0,
    stable_key                INT(10) DEFAULT 0,
    PRIMARY KEY (id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
@table phenotype_pipeline_procedure
@desc This table joins associations between procedures and pipelines

@column pipeline_id           pipeline internal id. Foreign key references to the @link phenotype_pipeline table.
@column procedure_id          procedure internal id. Foreign key references to the @link phenotype_procedure table.

@see phenotype_pipeline
@see phenotype_procedure
*/
CREATE TABLE phenotype_pipeline_procedure (

    pipeline_id               INT(10) UNSIGNED NOT NULL,
    procedure_id              INT(10) UNSIGNED NOT NULL,

    KEY pipeline_idx (pipeline_id),
    KEY procedure_idx (procedure_id),
    UNIQUE (pipeline_id, procedure_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
@table phenotype_procedure
@desc This table stores information about each phenotyping procedure

@column id                    internal id of the procedure. Primary key
@column stable_id             stable id from external resource
@column db_id                 external db id
@column name                  procedure name.

@see phenotype_pipeline
@see external_db

*/
CREATE TABLE phenotype_procedure (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    stable_key                INT(10) DEFAULT 0,
    stable_id                 VARCHAR(40) NOT NULL,
    db_id                     INT(10) NOT NULL,
    name                      VARCHAR(200) NOT NULL,
    description               TEXT,
    major_version             INT(10) NOT NULL DEFAULT 1,
    minor_version             INT(10) NOT NULL DEFAULT 0,
    is_mandatory              TINYINT(1) DEFAULT 0,

    PRIMARY KEY (id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
@table phenotype_procedure_meta_data
@desc This table stores information about each phenotyping procedure meta data
In EMPReSS, currently we store information about aging only.

@column procedure_id          The procedure this annotation belongs too. Foreign key reference to the @link phenotype_procedure table.
@column meta_name             name of the meta data
@column value                 value of the meta data as a string

@see phenotype_procedure

*/
CREATE TABLE phenotype_procedure_meta_data (

    procedure_id              INT(10) UNSIGNED NOT NULL,
	meta_name				  VARCHAR(40) NOT NULL,
    meta_value                VARCHAR(40) NOT NULL,
    
    KEY procedure_meta_data_idx (procedure_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
@table phenotype_procedure_parameter
@desc This table joins associations between procedures and parameters

@column procedure_id          procedure internal id. Foreign key references to the @link phenotype_procedure table.
@column parameter_id          parameter internal id. Foreign key references to the @link phenotype_parameter table.

@see phenotype_procedure
@see phenotype_parameter
*/
CREATE TABLE phenotype_procedure_parameter (

    procedure_id              INT(10) UNSIGNED NOT NULL,
    parameter_id              INT(10) UNSIGNED NOT NULL,

    KEY procedure_idx (procedure_id),
    KEY parameter_idx (parameter_id),
    UNIQUE (procedure_id, parameter_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;


-- because of IMPReSS we have a different length for stable_id
-- description for parameter is TEXT

CREATE TABLE phenotype_parameter (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    stable_id                 VARCHAR(30) NOT NULL,
    db_id                     INT(10) NOT NULL,
    name                      VARCHAR(200) NOT NULL,
    description               TEXT,
    major_version             INT(10) NOT NULL DEFAULT 1,
    minor_version             INT(10) NOT NULL DEFAULT 0,
    unit                      VARCHAR(20) NOT NULL,
    datatype                  VARCHAR(20) NOT NULL,
    parameter_type            VARCHAR(30) NOT NULL,
    formula                   TEXT,
    required                  TINYINT(1) DEFAULT 0,
    metadata                  TINYINT(1) DEFAULT 0,
    important                 TINYINT(1) DEFAULT 0,
    derived                   TINYINT(1) DEFAULT 0,
    annotate                  TINYINT(1) DEFAULT 0,
    increment                 TINYINT(1) DEFAULT 0,
    options                   TINYINT(1) DEFAULT 0,
    sequence                  INT(10) UNSIGNED NOT NULL,
    media                     TINYINT(1) DEFAULT 0,
    data_analysis             TINYINT(1) DEFAULT 0,
    data_analysis_notes       TEXT,
    stable_key                INT(10) DEFAULT 0,
 
    PRIMARY KEY (id),
    KEY parameter_stable_id_idx (stable_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE phenotype_parameter_lnk_option (

    parameter_id              INT(10) UNSIGNED NOT NULL,
	option_id                 INT(10) UNSIGNED NOT NULL,
	
    KEY parameter_idx (parameter_id),
    KEY option_idx (option_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;
    
CREATE TABLE phenotype_parameter_option (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    name                      VARCHAR(200) NOT NULL,
    description               VARCHAR(200),    

    PRIMARY KEY (id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE phenotype_parameter_lnk_increment (

    parameter_id              INT(10) UNSIGNED NOT NULL,
	increment_id              INT(10) UNSIGNED NOT NULL,
	
    KEY parameter_idx (parameter_id),
    KEY increment_idx (increment_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE phenotype_parameter_increment (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    increment_value           VARCHAR(200) NOT NULL,
    increment_datatype        VARCHAR(20) NOT NULL,
    increment_unit            VARCHAR(40) NOT NULL,
    increment_minimum         VARCHAR(20) NOT NULL,
    
    PRIMARY KEY (id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

-- truncate table phenotype_parameter; truncate table phenotype_pipeline; truncate table phenotype_procedure; truncate table phenotype_pipeline_procedure; truncate table phenotype_procedure_meta_data; truncate table phenotype_parameter_option; truncate table phenotype_parameter_increment;

CREATE TABLE phenotype_parameter_lnk_ontology_annotation (

    annotation_id             INT(10) UNSIGNED NOT NULL,
    parameter_id              INT(10) UNSIGNED NOT NULL,

    KEY parameter_idx (parameter_id),
    KEY annotation_idx (annotation_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * We compute the significance of the occurence of some observation
 * by comparison with WT/control animals.
 * 
 */
CREATE TABLE phenotype_parameter_ontology_annotation (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    event_type                enum('abnormal', 'abnormal_specific', 'increased', 'decreased', 'inferred', 'trait'),
    option_id                 INT(10) UNSIGNED,
    ontology_acc              VARCHAR(20),
    ontology_db_id            INT(10),
    
    PRIMARY KEY (id),
    KEY ontology_idx (ontology_acc, ontology_db_id),
    KEY option_idx (option_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_lnk_eq_annotation (

    annotation_id             INT(10) UNSIGNED NOT NULL,
    parameter_id              INT(10) UNSIGNED NOT NULL,

    KEY parameter_idx (parameter_id),
    KEY annotation_idx (annotation_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * We compute the significance of the occurence of some observation
 * by comparison with WT/control animals.
 * 
 */
CREATE TABLE phenotype_parameter_eq_annotation (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    event_type                enum('abnormal', 'abnormal_specific', 'increased', 'decreased', 'inferred', 'trait'),
    option_id                 INT(10) UNSIGNED,
    sex                       ENUM('female', 'hermaphrodite', 'male'), 
    ontology_acc              VARCHAR(20),
    ontology_db_id            INT(10),
    quality_acc               VARCHAR(20),
    quality_db_id             INT(10),
    
    PRIMARY KEY (id),
    KEY ontology_idx (ontology_acc, ontology_db_id),
    KEY quality_idx (quality_acc, quality_db_id),
    KEY option_idx (option_id)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * Phenodeviant result summary
 */

CREATE TABLE phenotype_call_summary (

    id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    external_id               VARCHAR(20) NULL,
    external_db_id            INT(10),
    project_id                INT(10) UNSIGNED NOT NULL,
    organisation_id           INT(10) UNSIGNED NOT NULL,
    gf_acc                    VARCHAR(20),
    gf_db_id                  INT(10),
    strain_acc                VARCHAR(20),
    strain_db_id              INT(10),
    allele_acc                VARCHAR(20),
    allele_db_id              INT(10),
    sex                       ENUM('female', 'hermaphrodite', 'male'),
    zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote'),
    parameter_id              INT(10) UNSIGNED NOT NULL,
    procedure_id              INT(10) UNSIGNED NOT NULL,
    pipeline_id               INT(10) UNSIGNED NOT NULL,

    mp_acc                    VARCHAR(20) NOT NULL,
    mp_db_id                  INT(10) NOT NULL,
    
    p_value                   FLOAT NULL DEFAULT 1,
    effect_size               FLOAT NULL DEFAULT 0,

    PRIMARY KEY (id),
    KEY parameter_call_idx (parameter_id),
    KEY procedure_call_idx (procedure_id),
    KEY pipeline_call_idx (pipeline_id),
    KEY mp_call_idx (mp_acc)
    
) COLLATE=utf8_general_ci ENGINE=MyISAM;



/*
 * Tables below are for the storage of media/image information
 */
CREATE TABLE image_record_observation (

	id int(11) NOT NULL AUTO_INCREMENT,
	sample_id int(11) DEFAULT NULL,
	original_file_name varchar(1024) DEFAULT NULL,
	creator_id int(11) DEFAULT NULL,
	full_resolution_file_path varchar(256) DEFAULT NULL,
	small_thumbnail_file_path varchar(256) DEFAULT NULL,
	large_thumbnail_file_path varchar(256) DEFAULT NULL,
	download_file_path varchar(256) DEFAULT NULL,
	organisation_id int(10) NOT NULL DEFAULT '0',
	increment_value varchar(45) DEFAULT NULL,
	file_type varchar(45) DEFAULT NULL,
	media_sample_local_id varchar(45) DEFAULT NULL,
	media_section_id varchar(45) DEFAULT NULL,
	
	PRIMARY KEY (id)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM ;


CREATE TABLE dimension (

	dim_id int(11) NOT NULL AUTO_INCREMENT,
	parameter_association_id int(11) NOT NULL,
	id varchar(45) NOT NULL,
	origin varchar(45) NOT NULL,
	unit varchar(45) DEFAULT NULL,
	value decimal(65,10) DEFAULT NULL,
	
	PRIMARY KEY (dim_id, parameter_association_id)
	
) COLLATE=utf8_general_ci ENGINE=MyISAM ;


CREATE TABLE parameter_association (

	id int(11) NOT NULL AUTO_INCREMENT,
	observation_id varchar(45) NOT NULL,
	parameter_id varchar(45) NOT NULL,
	sequence_id int(11) DEFAULT NULL,
	dim_id varchar(45) DEFAULT NULL,

	PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM ;


CREATE TABLE procedure_meta_data (

	id int(11) NOT NULL AUTO_INCREMENT,
	parameter_id varchar(45) NOT NULL,
	sequence_id varchar(45) DEFAULT NULL,
	parameter_status varchar(450) DEFAULT NULL,
	value varchar(450) DEFAULT NULL,
	procedure_id varchar(45) NOT NULL,
	experiment_id int(11) NOT NULL,
	observation_id int(11) DEFAULT '0',

	PRIMARY KEY (id),
	KEY procedure_meta_data_experiment_idx (experiment_id),
	KEY procedure_meta_data_parameter_idx (parameter_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM ;


