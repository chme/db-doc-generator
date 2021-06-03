CREATE TABLE person (
  id NUMBER(9,0) PRIMARY KEY,
  name VARCHAR2(20),
  created_at TIMESTAMP COMMENT 'Creation timestamp'
);

COMMENT ON TABLE person IS 'Store person information';
COMMENT ON COLUMN person.name IS 'Test comment for col';

CREATE TABLE address (
  id NUMBER(9,0) PRIMARY KEY,
  person_id NUMBER(9,0),
  address VARCHAR2(20),
  created_at TIMESTAMP,
  CONSTRAINT my_fk_1 FOREIGN KEY (person_id) REFERENCES person (id),
  CONSTRAINT my_idx_1 UNIQUE (address, created_at)
);

--COMMENT ON INDEX my_fk_1 IS 'Test comment on index';