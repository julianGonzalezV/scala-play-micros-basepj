# --- !Ups

CREATE TABLE ADDRESS (
    ID NUMBER NOT NULL AUTO_INCREMENT,
    STREET varchar(255) NOT NULL,
    CITY varchar(255) NOT NULL,
    PRIMARY KEY (ID)
);


CREATE TABLE PERSON (
    ID NUMBER NOT NULL AUTO_INCREMENT,
    NAME varchar(255) NOT NULL,
    AGE NUMBER NOT NULL,
    ADDRESS_ID NUMBER NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (ADDRESS_ID) REFERENCES ADDRESS(id),

);

INSERT INTO ADDRESS (ID, STREET, CITY) VALUES (1, 'VERANERAS', 'TULUA');
INSERT INTO ADDRESS (ID, STREET, CITY) VALUES (2, 'LA SEPTIMA', 'CALI');

INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (1, 'JULIANo', 29, 1);
INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (2, 'PAOLA', 27, 1);
INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (3, 'MARIA C1', 47, 1);
INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (4, 'MARIA C2', 47, 1);
INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (5, 'MARIA C3', 35, 1);

INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (6, 'EDWARD', 19, 2);
INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (7, 'ELIZA', 20, 2);
INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (8, 'LISA', 21, 2);
INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (9, 'hOMERO', 22, 2);
INSERT INTO PERSON (ID, NAME, AGE,ADDRESS_ID) VALUES (10, 'March C', 23, 2);

COMMIT;



# --- !Downs

DROP TABLE ADDRESS;
DROP TABLE PERSON;
