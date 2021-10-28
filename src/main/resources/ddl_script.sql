CREATE DATABASE IF NOT EXISTS matcha;

CREATE FUNCTION table_exists(tabName varchar(30)) RETURNS BOOLEAN 
    DETERMINISTIC 
    RETURN (SELECT IF (EXISTS (SELECT * 
                            FROM INFORMATION_SCHEMA.TABLES 
                            WHERE TABLE_SCHEMA = 'matcha' 
                                AND  TABLE_NAME = tabName),
                            TRUE, FALSE))
;
CREATE FUNCTION column_exists(tabName varchar(30), columnName varchar(30)) RETURNS BOOLEAN 
    DETERMINISTIC     
    RETURN (SELECT IF (EXISTS (SELECT *
                               FROM INFORMATION_SCHEMA.COLUMNS
                               WHERE TABLE_SCHEMA = 'matcha'
                                 AND  TABLE_NAME = tabName
                                 AND COLUMN_NAME = columnName),
                       TRUE, FALSE))
;
CREATE PROCEDURE execute_immediate(query VARCHAR(1000)) 
    MODIFIES SQL DATA
    SQL SECURITY DEFINER
BEGIN
    SET @q = query;
    PREPARE stmt FROM @q;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END
;
CREATE PROCEDURE alert_table(tabName varchar(30), params varchar(100))
    MODIFIES SQL DATA
    SQL SECURITY DEFINER
BEGIN
    SET @SQL := CONCAT('ALTER TABLE ', tabName, ' ADD  ', params);
    PREPARE stmt FROM @SQL;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END
;

CREATE PROCEDURE createTabs()  
BEGIN
    IF NOT table_exists('USER') THEN CALL execute_immediate(
            'CREATE TABLE matcha.USER(
                            ID MEDIUMINT AUTO_INCREMENT,
                            CONFIRM VARCHAR(55),
                            NAME VARCHAR(30),
                            LASTNAME VARCHAR(30),
                            MIDDLENAME VARCHAR(30),
                            BIRTHDAY DATE,
                            YEARS_OLD MEDIUMINT,
                            EMAIL VARCHAR(30),
                            PASSWORD VARCHAR(255),
                            LOCATION VARCHAR(180),
                            USER_CARD MEDIUMINT,
                            FILTER_PARAMS MEDIUMINT,
                    PRIMARY KEY (ID));'
        );
    END IF;

    IF NOT table_exists('USER_CARD') THEN CALL execute_immediate(
            'CREATE TABLE matcha.USER_CARD(
                            ID MEDIUMINT AUTO_INCREMENT,
                            BIOGRAPHY varchar(300),
                            WORKPLACE varchar(70),
                            POSITION varchar(70),
                            EDUCATION VARCHAR(120),
                            GENDER VARCHAR(40),
                            SEXUAL_PREFERENCE VARCHAR(40),
                            TAGS VARCHAR(150),
                            RATING DOUBLE,
                            PHOTOS_PARAMS VARCHAR(32),
                            MAIN_PHOTO MEDIUMINT,
                            USER_ID MEDIUMINT,
                    PRIMARY KEY (ID));'
        );
    END IF;

    IF NOT table_exists('LINK') THEN CALL execute_immediate(
            'CREATE TABLE matcha.LINK(
                            ID MEDIUMINT AUTO_INCREMENT,
                            URL VARCHAR(200),
                            OPEN MEDIUMINT,
                    PRIMARY KEY (ID));'
        );
    END IF;

    IF NOT table_exists('JWT') THEN CALL execute_immediate(
            'CREATE TABLE matcha.JWT(
                            ID MEDIUMINT AUTO_INCREMENT,
                            USER_ID MEDIUMINT,
                            TOKEN VARCHAR(400),
                            TYPE VARCHAR(30)
                    PRIMARY KEY (ID));'
        );
    END IF;
    
    IF NOT table_exists('LIKES_ACTION') THEN CALL execute_immediate(
            'CREATE TABLE matcha.LIKES_ACTION(
                            ID MEDIUMINT AUTO_INCREMENT,
                            CREATION_TIME timestamp(6) DEFAULT CURRENT_TIMESTAMP,
                            FROM_USR MEDIUMINT,
                            TO_USR MEDIUMINT,
                            ACTION VARCHAR(30),
                    PRIMARY KEY (ID));'
        );
    END IF;

    IF NOT table_exists('FILTER_PARAMS') THEN CALL execute_immediate(
            'CREATE TABLE matcha.FILTER_PARAMS(
                            ID INTEGER AUTO_INCREMENT,
                            AGE_BY MEDIUMINT,
                            AGE_TO MEDIUMINT,
                            RATING DOUBLE,
                            COMMON_TAGS_COUNT MEDIUMINT,
                            LOCATION varchar(32) NOT NULL,
                    PRIMARY KEY(ID));'
        );
    END IF;
    IF table_exists('FILTER_PARAMS') AND NOT column_exists('FILTER_PARAMS', 'LOCATION') 
        THEN CALL alert_table('FILTER_PARAMS', 'LOCATION VARCHAR(32) NOT NULL');
    END IF;

    IF NOT table_exists('WEB_SOCKET_MESSAGE') THEN CALL execute_immediate(
            'CREATE TABLE matcha.WEB_SOCKET_MESSAGE(
                            ID MEDIUMINT, CREATION_TIME timestamp(6) DEFAULT CURRENT_TIMESTAMP(6),
                            CHAT_ID MEDIUMINT NOT NULL,
                            FROM_ID MEDIUMINT NOT NULL, 
                            TYPE VARCHAR(30) NOT NULL,
                            TO_ID MEDIUMINT, TYPE VARCHAR(30),
                            TYPE_INFO VARCHAR(30),
                            STATUS VARCHAR(30),
                            CONTENT BLOB,
                    PRIMARY KEY (ID));'
        );
    END IF;
    IF table_exists('WEB_SOCKET_MESSAGE') AND NOT column_exists('WEB_SOCKET_MESSAGE', 'FROM_ID')
        THEN CALL alert_table('WEB_SOCKET_MESSAGE', 'FROM_ID MEDIUMINT NOT NULL');
    END IF;

    IF NOT table_exists('CHAT_AFFILIATION') THEN CALL execute_immediate(
            'CREATE TABLE matcha.CHAT_AFFILIATION(
                            ID MEDIUMINT AUTO_INCREMENT,
                            CREATION_TIME timestamp(6) DEFAULT CURRENT_TIMESTAMP(6),
                            FROM_USR MEDIUMINT,
                            TO_USR MEDIUMINT,
                            CHAT_ID MEDIUMINT,
                    PRIMARY KEY (ID))'
        );
    END IF;
    IF table_exists('CHAT_AFFILIATION') AND NOT column_exists('CHAT_AFFILIATION', 'TO_USR')
        THEN CALL alert_table('WEB_SOCKET_MESSAGE', 'TO_USR MEDIUMINT');
    END IF;
    
END
;


call createTabs
;


DROP FUNCTION table_exists
;
DROP FUNCTION column_exists
;
DROP PROCEDURE execute_immediate
;
DROP PROCEDURE alert_table
;
DROP PROCEDURE createTabs
;
