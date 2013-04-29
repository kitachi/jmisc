use test;

drop table dlIngestJob;

CREATE TABLE dlIngestJob ( 
jobNo      int(11) NOT NULL,
jobTS      varchar(15) NOT NULL, 
topUUID      varchar(100) NOT NULL,
internalId int(11) NOT NULL,  
tableName  varchar(125) NOT NULL,
objType    varchar(50) NOT NULL,   
KEY index1 (jobNo, tableName, objType),
KEY index2 (jobTS, topUUID, tableName, objType)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

drop table dlIngestStatus;

CREATE TABLE `dlIngestStatus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `jobTS` varchar(15) NOT NULL, 
  `jobName` varchar(150) DEFAULT NULL,
  `topUUID` varchar(100) NOT NULL,
  `status` int(11) NOT NULL,
  `statusDescription` varchar(125) NOT NULL,
  `startDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `endDate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index2` (`status`),
  KEY `index3` (`jobName`),
  KEY `index4` (`jobTS`, `topUUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

