CREATE TABLE  `recipients` (
  `tableID` char(36) NOT NULL,
  `emailID` char(36) NOT NULL,
  `address` varchar(255) NOT NULL,
  PRIMARY KEY  USING BTREE (`tableID`),
  KEY `FK_recipient_1` (`emailID`),
  CONSTRAINT `FK_recipient_1` FOREIGN KEY (`emailID`) REFERENCES `emails` (`emailID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;