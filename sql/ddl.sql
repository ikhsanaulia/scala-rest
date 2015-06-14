DROP TABLE device_identifier;
DROP TABLE device_model;
DROP TABLE user_session;
DROP TABLE users;

CREATE TABLE device_model
(
  manufactur text NOT NULL,
  model text NOT NULL,
  name text NOT NULL,
  description text NOT NULL,
  CONSTRAINT "PK_DEVICE_MODEL" PRIMARY KEY (manufactur, model)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE device_model
  OWNER TO postgres;

  
CREATE TABLE device_identifier
(
  id varchar(100) NOT NULL,
  id_type varchar(4) NOT NULL,
  manufactur text NOT NULL,
  model text NOT NULL,
  created_time timestamp with time zone NOT NULL,
  updated_time timestamp with time zone NOT NULL,
  CONSTRAINT "PK_DEVICE_IDENTIFIER" PRIMARY KEY (id, id_type),
  CONSTRAINT "FK_DEVICE_IDENTIFIER_DEVICE_MODEL" FOREIGN KEY (manufactur, model)
      REFERENCES device_model (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE device_identifier
  OWNER TO postgres;

CREATE INDEX "IDX_DEVICE_IDENTIFIER_MANUFACTUR_MODEL"
  ON device_identifier
  USING btree
  (manufactur COLLATE pg_catalog."default", model COLLATE pg_catalog."default");


CREATE TABLE users
(
  id bigint NOT NULL,
  name bigint NOT NULL,
  created_time timestamp with time zone NOT NULL,
  updated_time timestamp with time zone NOT NULL,
  CONSTRAINT "PK_USER" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO postgres;



CREATE TABLE user_session
(
  id varchar(36) NOT NULL,
  device_identifier_id bigint NOT NULL,
  user_id bigint NOT NULL,
  created_time timestamp with time zone NOT NULL,
  updated_time timestamp with time zone NOT NULL,
  CONSTRAINT "PK_USER_SESSION" PRIMARY KEY (id),
  CONSTRAINT "FK_USER_SESSION_DEVICE_IDENTIFIER" FOREIGN KEY (device_identifier_id)
      REFERENCES device_identifier (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "FK_USER_SESSION_USER" FOREIGN KEY (user_id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_session
  OWNER TO postgres;

CREATE INDEX "IDX_USER_SESSION_DEVICE_IDENTIFIER_ID"
  ON user_session
  USING btree
  (device_identifier_id);

CREATE INDEX "IDX_USER_SESSION_USER_ID"
  ON user_session
  USING btree
  (user_id);