DELIMITER //
CREATE FUNCTION table_exists(tabName varchar(30)) RETURNS BOOLEAN
    DETERMINISTIC
BEGIN
    DECLARE result varchar(30);

    SET result = (SELECT IF (EXISTS (SELECT *
       FROM INFORMATION_SCHEMA.TABLES
       WHERE TABLE_SCHEMA = 'matcha'
       AND  TABLE_NAME = tabName), TRUE, FALSE));

    RETURN (result);
END//

CREATE FUNCTION column_exists(tabName varchar(30), columnName varchar(30)) RETURNS BOOLEAN
    DETERMINISTIC
BEGIN
    DECLARE result varchar(30);
    SET result =
        (SELECT IF (EXISTS (SELECT *
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = 'matcha'
            AND  TABLE_NAME = tabName
            AND COLUMN_NAME = columnName),
        TRUE, FALSE));

    RETURN (result);
END//

CREATE PROCEDURE execute_immediate(IN query MEDIUMTEXT)
    MODIFIES SQL DATA
    SQL SECURITY DEFINER
BEGIN
    SET @q = query;
    PREPARE stmt FROM @q;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
CREATE PROCEDURE aaa()
BEGIN
    IF NOT table_exists('jwt') THEN CALL execute_immediate(
        'CREATE TABLE matcha.CHAT_AFFILIATION(
                        ID MEDIUMINT AUTO_INCREMENT,
                        CREATION_TIME timestamp(6) DEFAULT CURRENT_TIMESTAMP(6),
                        FROM_USR MEDIUMINT,
                        FROM_TO MEDIUMINT,
                        CHAT_ID MEDIUMINT, PRIMARY KEY (ID))'
        );
    END IF;

    IF NOT table_exists('jwt') THEN CALL execute_immediate(
            'CREATE TABLE matcha.CHAT_AFFILIATION(
                            ID MEDIUMINT AUTO_INCREMENT,
                            CREATION_TIME timestamp(6) DEFAULT CURRENT_TIMESTAMP(6),
                            FROM_USR MEDIUMINT,
                            FROM_TO MEDIUMINT,
                            CHAT_ID MEDIUMINT, PRIMARY KEY (ID))'
        );
    END IF;

    IF NOT table_exists('jwt') THEN CALL execute_immediate(
            'CREATE TABLE matcha.CHAT_AFFILIATION(
                            ID MEDIUMINT AUTO_INCREMENT,
                            CREATION_TIME timestamp(6) DEFAULT CURRENT_TIMESTAMP(6),
                            FROM_USR MEDIUMINT,
                            FROM_TO MEDIUMINT,
                            CHAT_ID MEDIUMINT, PRIMARY KEY (ID))'
        );
    END IF;

END;

call aaa;
