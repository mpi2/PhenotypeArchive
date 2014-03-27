
--
-- Table structure for table `ANN_ANNOTATION`
--
DROP TABLE IF EXISTS `PHN_REQUIRED_OBSERVATION`;
CREATE TABLE `PHN_REQUIRED_OBSERVATION` (
  `ID` int(11) NOT NULL,
  `OBSERVATION_TYPE_ID` int(11) NOT NULL,
 `ORDER_BY` int(11) NOT NULL,
  `DISPLAY_NAME` varchar(256) NOT NULL,
   `ACTIVE` int(11) NOT NULL,
  `CREATED_DATE` date DEFAULT NULL,
  `CREATOR_ID` int(11) DEFAULT NULL,
  `EDIT_DATE` date DEFAULT NULL,
  `EDITED_BY` varchar(256) DEFAULT NULL,
  `CHECK_NUMBER` int(11) DEFAULT NULL,
  `SOP_ID` int(10) DEFAULT NULL,
  `DEFAULT_VALUE` varchar(256) DEFAULT NULL,
   `MANDATORY_OBSERVATION` int(10) DEFAULT 1,
   `SECTION_TITLE` VARCHAR (256),
   `IS_MACHINE_DATA` int(10) DEFAULT 0,
    `IS_SERIES_DATA` int(10) DEFAULT 0,
  `VIEW_COLUMN_NAME` varchar(30) DEFAULT NULL,
 `PARENT_REQ_OBS_ID` int(10) DEFAULT NULL,
`IS_VIEWABLE` int(10) DEFAULT 1, 
 `UNIQUE_NAME` VARCHAR(256), 
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS  `PHN_EXPERIMENT`;
CREATE TABLE `PHN_EXPERIMENT` (
  `ID` int(11) NOT NULL,
  `SOP_ID` int(11) NOT NULL,
  `MOUSE_ID` int(11) NOT NULL,
  `IS_COMPLETE` varchar(256) DEFAULT NULL,
  `CREATED_DATE` date DEFAULT NULL,
  `CREATOR_ID` int(11) NOT NULL,
  `EDIT_DATE` date DEFAULT NULL,
  `EDITED_BY` varchar(256) DEFAULT NULL,
  `CHECK_NUMBER` int(11) NOT NULL,
  `EXPERIMENT_SET_ID` int(11) NOT NULL,
  `QC_COMMENTS` varchar(256) DEFAULT NULL,
  `QC_FAILURE_REASON_ID` int(11) NOT NULL,
  `COMMENTS` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `PHN_OBSERVATION`;
CREATE TABLE `PHN_OBSERVATION` (
  `ID` int(11) NOT NULL,
    `EXPERIMENT_ID` int(11) NOT NULL,
`REQUIRED_OBSERVATION` int(11) NOT NULL,
`VALUE` varchar(256) DEFAULT NULL,
`INSTRUMENT_ID` int(11) NOT NULL,
`OBSERVATION_TIME` timestamp,
`CREATED_DATE` date DEFAULT NULL,
`CREATOR_ID` int(11) NOT NULL,
`EDIT_DATE` date DEFAULT NULL,
`EDITED_BY` varchar(256) DEFAULT NULL,
`CHECK_NUMBER` int(11) NOT NULL,
`QC_COMMENTS` varchar(256) DEFAULT NULL,
`IS_FAILED_VALIDATION` int(11) DEFAULT 0,
`QC_FAILED` int(11) DEFAULT 0,
`PARENT_OBSERVATION_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `IMPC_MOUSE_ALLELE_MV`;
CREATE TABLE `IMPC_MOUSE_ALLELE_MV` (
  `MOUSE_ID` int(11) NOT NULL,
`MOUSE_NAME` varchar(256) DEFAULT NULL,
`GENDER` varchar(256) DEFAULT NULL,
`AGE_IN_WEEKS` varchar(256) DEFAULT NULL,
`GENE` varchar(256) DEFAULT NULL,
`COLONY_PREFIX` varchar(1024) DEFAULT NULL,
`COLONY_ID` int(11) NOT NULL,
`ALLELE` varchar(1024) DEFAULT NULL,
`GENOTYPE` varchar(256) DEFAULT NULL,
`FULL_GENOTYPE` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`MOUSE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `ANN_ANNOTATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ANN_ANNOTATION` (
  `ID` int(11) DEFAULT NULL,
  `TERM_NAME` varchar(256) DEFAULT NULL,
  `TERM_ID` varchar(128) DEFAULT NULL,
  `ONTOLOGY_DICT_ID` int(11) DEFAULT NULL,
  `EDIT_DATE` date DEFAULT NULL,
  `EDITED_BY` varchar(128) DEFAULT NULL,
  `CREATED_DATE` date DEFAULT NULL,
  `CREATOR_ID` int(11) DEFAULT NULL,
  `CHECK_NUMBER` int(11) DEFAULT NULL,
  `FOREIGN_KEY_ID` int(11) DEFAULT NULL,
  `FOREIGN_TABLE_NAME` varchar(30) DEFAULT NULL,
  KEY `TERM_ID` (`TERM_ID`),
  KEY `ID` (`ID`),
  KEY `FOREIGN_KEY_ID` (`FOREIGN_KEY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ANN_ONTOLOGY_DICT`
--

DROP TABLE IF EXISTS `ANN_ONTOLOGY_DICT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ANN_ONTOLOGY_DICT` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(256) DEFAULT NULL,
  `DESCRIPTION` varchar(2048) DEFAULT NULL,
  `ACTIVE` tinyint(4) DEFAULT NULL,
  `ORDER_BY` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_DCF_IMAGE_VW`
--

DROP TABLE IF EXISTS `IMA_DCF_IMAGE_VW`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_DCF_IMAGE_VW` (
  `ID` int(11) DEFAULT NULL,
  `DOWNLOAD_FILE_PATH` varchar(255) DEFAULT NULL,
  `DCF_ID` int(11) DEFAULT NULL,
  `MOUSE_ID` int(11) DEFAULT NULL,
  `EXPERIMENT_ID` int(11) DEFAULT NULL,
  `SMALL_THUMBNAIL_FILE_PATH` varchar(255) DEFAULT NULL,
  `PUBLISHED_STATUS_ID` varchar(255) DEFAULT NULL,
  `FULL_RESOLUTION_FILE_PATH` varchar(255) DEFAULT NULL,
  `LARGE_THUMBNAIL_FILE_PATH` varchar(255) DEFAULT NULL,
  `PUBLISHED_STATUS` varchar(255) DEFAULT NULL,
  `QC_STATUS_ID` int(11) DEFAULT NULL,
  `QC_STATUS` varchar(255) DEFAULT NULL,
  KEY `ID` (`ID`),
  KEY `DCF_ID` (`DCF_ID`),
  KEY `MOUSE_ID` (`MOUSE_ID`),
  KEY `EXPERIMENT_ID` (`EXPERIMENT_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_EXPERIMENT_DICT`
--

DROP TABLE IF EXISTS `IMA_EXPERIMENT_DICT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_EXPERIMENT_DICT` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(128) DEFAULT NULL,
  `DESCRIPTION` varchar(4000) DEFAULT NULL,
  `ORDER_BY` int(11) DEFAULT NULL,
  `ACTIVE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_IMAGE_RECORD`
--

DROP TABLE IF EXISTS `IMA_IMAGE_RECORD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_IMAGE_RECORD` (
  `ID` int(11) NOT NULL,
  `FOREIGN_TABLE_NAME` varchar(256) DEFAULT NULL,
  `FOREIGN_KEY_ID` int(11) DEFAULT NULL,
  `ORIGINAL_FILE_NAME` varchar(1024) DEFAULT NULL,
  `CREATOR_ID` int(11) DEFAULT NULL,
  `CREATED_DATE` date DEFAULT NULL,
  `EDITED_BY` varchar(64) DEFAULT NULL,
  `EDIT_DATE` date DEFAULT NULL,
  `CHECK_NUMBER` int(11) DEFAULT NULL,
  `FULL_RESOLUTION_FILE_PATH` varchar(256) DEFAULT NULL,
  `SMALL_THUMBNAIL_FILE_PATH` varchar(256) DEFAULT NULL,
  `LARGE_THUMBNAIL_FILE_PATH` varchar(256) DEFAULT NULL,
  `DOWNLOAD_FILE_PATH` varchar(256) DEFAULT NULL,
  `SUBCONTEXT_ID` int(11) DEFAULT NULL,
  `QC_STATUS_ID` int(11) DEFAULT NULL,
  `PUBLISHED_STATUS_ID` int(11) DEFAULT NULL,
  `organisation` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FOREIGN_KEY_ID` (`FOREIGN_KEY_ID`),
  KEY `SUBCONTEXT_ID` (`SUBCONTEXT_ID`),
  KEY `FOREIGN_TABLE_NAME` (`FOREIGN_TABLE_NAME`),
  KEY `organisation` (`organisation`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_IMAGE_TAG`
--

DROP TABLE IF EXISTS `IMA_IMAGE_TAG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_IMAGE_TAG` (
  `ID` int(11) DEFAULT NULL,
  `IMAGE_RECORD_ID` int(11) DEFAULT NULL,
  `TAG_TYPE_ID` int(11) DEFAULT NULL,
  `TAG_NAME` varchar(256) DEFAULT NULL,
  `TAG_VALUE` varchar(4000) DEFAULT NULL,
  `CREATOR_ID` int(11) DEFAULT NULL,
  `CREATED_DATE` date DEFAULT NULL,
  `EDITED_BY` varchar(64) DEFAULT NULL,
  `EDIT_DATE` date DEFAULT NULL,
  `CHECK_NUMBER` int(11) DEFAULT NULL,
  `X_START` float DEFAULT NULL,
  `Y_START` float DEFAULT NULL,
  `X_END` float DEFAULT NULL,
  `Y_END` float DEFAULT NULL,
  KEY `IMAGE_RECORD_ID` (`IMAGE_RECORD_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_IMAGE_TAG_TYPE`
--

DROP TABLE IF EXISTS `IMA_IMAGE_TAG_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_IMAGE_TAG_TYPE` (
  `NAME` varchar(128) DEFAULT NULL,
  `ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_IMPORT_CONTEXT`
--

DROP TABLE IF EXISTS `IMA_IMPORT_CONTEXT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_IMPORT_CONTEXT` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_IMPORT_LOG`
--

DROP TABLE IF EXISTS `IMA_IMPORT_LOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_IMPORT_LOG` (
  `LOG_ID` varchar(4000) DEFAULT NULL,
  `LOG_MESSAGE` varchar(4000) DEFAULT NULL,
  `LOG_TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LOG_STATUS` varchar(128) DEFAULT NULL,
  `LOG_URL` varchar(4000) DEFAULT NULL,
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_PREDEFINED_TAG`
--

DROP TABLE IF EXISTS `IMA_PREDEFINED_TAG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_PREDEFINED_TAG` (
  `ID` int(11) DEFAULT NULL,
  `TAG_TYPE_ID` int(11) DEFAULT NULL,
  `TAG_NAME` varchar(256) DEFAULT NULL,
  `EXPERIMENT_DICT_ID` int(11) DEFAULT NULL,
  `ORDER_BY` int(11) DEFAULT NULL,
  `ALLOW_MULTIPLE` tinyint(4) DEFAULT '0',
  `ALLOW_IN_ROI` tinyint(4) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_PREDEFINED_TAG_VALUE`
--

DROP TABLE IF EXISTS `IMA_PREDEFINED_TAG_VALUE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_PREDEFINED_TAG_VALUE` (
  `ID` int(11) DEFAULT NULL,
  `PREDEFINED_TAG_ID` int(11) DEFAULT NULL,
  `TAG_VALUE` varchar(4000) DEFAULT NULL,
  `ORDER_BY` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_PUBLISHED_DICT`
--

DROP TABLE IF EXISTS `IMA_PUBLISHED_DICT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_PUBLISHED_DICT` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(512) DEFAULT NULL,
  `DESCRIPTION` varchar(512) DEFAULT NULL,
  `ORDER_BY` int(11) DEFAULT NULL,
  `ACTIVE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_QC_DICT`
--

DROP TABLE IF EXISTS `IMA_QC_DICT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_QC_DICT` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(512) DEFAULT NULL,
  `DESCRIPTION` varchar(512) DEFAULT NULL,
  `ORDER_BY` int(11) DEFAULT NULL,
  `ACTIVE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMA_SUBCONTEXT`
--

DROP TABLE IF EXISTS `IMA_SUBCONTEXT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMA_SUBCONTEXT` (
  `ID` int(11) NOT NULL,
  `IMPORT_CONTEXT_ID` int(11) DEFAULT NULL,
  `EXPERIMENT_DICT_ID` int(11) DEFAULT NULL,
  `IS_DEFAULT` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MTS_GENOTYPE_DICT`
--

DROP TABLE IF EXISTS `MTS_GENOTYPE_DICT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MTS_GENOTYPE_DICT` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(50) NOT NULL,
  `ORDER_BY` int(11) NOT NULL,
  `DESCRIPTION` varchar(25) NOT NULL,
  `ACTIVE` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MTS_MOUSE_ALLELE`
--

DROP TABLE IF EXISTS `MTS_MOUSE_ALLELE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MTS_MOUSE_ALLELE` (
  `ID` int(11) NOT NULL,
  `MOUSE_ID` int(11) NOT NULL,
  `ALLELE_ID` int(11) NOT NULL,
  `GENOTYPE_DICT_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `MOUSE_ID` (`MOUSE_ID`,`ALLELE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PHN_STD_OPERATING_PROCEDURE`
--

DROP TABLE IF EXISTS `PHN_STD_OPERATING_PROCEDURE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PHN_STD_OPERATING_PROCEDURE` (
  `ID` int(11) NOT NULL,
  `PROCEDURE_ID` int(11) NOT NULL,
  `NAME` varchar(256) NOT NULL,
  `ACTIVE` int(11) NOT NULL,
  `CREATED_DATE` date DEFAULT NULL,
  `CREATOR_ID` int(11) DEFAULT NULL,
  `EDIT_DATE` date DEFAULT NULL,
  `EDITED_BY` varchar(256) DEFAULT NULL,
  `CHECK_INT` int(11) DEFAULT NULL,
  `TERMINAL_PROCEDURE` int(10) DEFAULT NULL,
  `IS_HIDDEN` int(1) DEFAULT NULL,
  `VIEW_NAME` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `PROCEDURE_ID` (`PROCEDURE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `ima_image_record_annotation_vw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ima_image_record_annotation_vw` (
  `IMAGE_RECORD_ID` int(11) DEFAULT NULL,
  `TERM_ID` varchar(128) DEFAULT NULL,
  `TERM_NAME` varchar(256) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS `mts_mouse_allele_mv`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mts_mouse_allele_mv` (
  `MOUSE_ID` int(11) NOT NULL,
  `ALLELE` varchar(250) NOT NULL,
  PRIMARY KEY (`MOUSE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
