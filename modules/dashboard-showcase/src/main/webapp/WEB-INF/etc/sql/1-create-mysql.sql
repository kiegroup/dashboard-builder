-- ENABLE_CUSTOM_DELIMITER



--
-- Copyright (C) 2012 JBoss Inc
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE TABLE hibernate_unique_key (
  next_hi bigint(50) NOT NULL
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

INSERT INTO hibernate_unique_key VALUES(1);
-- CUSTOM_DELIMITER

CREATE PROCEDURE nextVal()
MODIFIES SQL DATA
BEGIN
    UPDATE hibernate_unique_key SET next_hi = next_hi + 1;
END
-- CUSTOM_DELIMITER

CREATE FUNCTION currVal()
RETURNS int
READS SQL DATA
BEGIN
    return (SELECT next_hi FROM hibernate_unique_key);
END
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_source (
    dbid bigint(50) primary key,
    ds_type  varchar(512),
    name varchar(512),
    jndi_path  varchar(512),
    ds_url varchar(512),
    jdbc_driver_class  varchar(512),
    user_name varchar(512),
    passwd varchar(512),
    test_query  varchar(2000)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_source_table (
    dbid bigint(50) primary key,
    name varchar (512),
    data_source varchar (512),
    selected varchar(512)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_source_column (
    dbid bigint(50) primary key,
    name varchar (512),
    sql_type int(4),
    data_source varchar (512),
    table_name varchar (512),
    primary_key varchar (5),
    identity1 varchar (5)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_installed_module (
    name varchar(255) primary key,
    version bigint(50)
) ENGINE = INNODB;
  -- CUSTOM_DELIMITER

-- Translated from postgresql using procedure
 -- 1. add ENGINE = INNODB for each table
 -- 2. change timestamp to datetime
 -- 3. change numeric to bigint
 -- 4. change bit to tinyint(1)
 -- 5. bigint(x,0) -> bigint(x)
 -- 6. bigint(x,y) -> decimal(x,y)
 -- 7. bytea -> longblob
 -- 8. varchar -> longblob

CREATE TABLE dashb_permission (
  id_permission bigint(50) PRIMARY KEY,
  principal_class varchar(100),
  principal_name varchar(100),
  permission_class varchar(100) NOT NULL,
  permission_resource varchar(100) NOT NULL,
  permission_action varchar(100),
  permission_readonly tinyint(1)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_workspace (
  id_workspace varchar(40) PRIMARY KEY,
  look varchar(100),
  envelope varchar(100),
  url varchar(255) unique, -- 255 is max for unique key in utf8!!!
  default_workspace tinyint(1) NOT null,
  home_search_mode bigint(50) DEFAULT 0 NOT NULL
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_workspace_home (
  id_workspace varchar(40) NOT NULL,
  id_role varchar(100) NOT NULL,
  id_section bigint(50),
  PRIMARY KEY(id_workspace, id_role),
  FOREIGN KEY(id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_workspace_parameter (
  id_workspace varchar(40) NOT NULL,
  id_parameter varchar(100) NOT NULL,
  language varchar(10) NOT NULL,
  value varchar(3000),
  PRIMARY KEY(id_workspace, id_parameter, language)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

ALTER TABLE dashb_workspace_parameter add
   FOREIGN KEY(id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_graphic_resource (
  dbid bigint(50) not null primary key,
  workspace_id varchar(40),
  section_id bigint,
  panel_id bigint,
  id varchar(3000),
  resource_type varchar(3000),
  zip longblob,
  status tinyint(1),
  last_modified datetime
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_section (
  id_section bigint(50) NOT NULL,
  id_workspace varchar(40) NOT NULL,
  id_template varchar(100) NOT NULL,
  position bigint(50) NOT NULL,
  visible tinyint(1),
  region_spacing bigint,
  panel_spacing bigint,
  id_parent bigint,
  url varchar(3000),
  dbid bigint(50) primary key,
  skin varchar(100),
  envelope varchar(100)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

ALTER TABLE dashb_section add
  FOREIGN KEY(id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_section_i18n (
  id_section bigint(50) NOT NULL,
  language varchar(10) NOT NULL,
  title varchar(200),
  PRIMARY KEY(id_section, language),
  FOREIGN KEY(id_section) REFERENCES dashb_section(dbid)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel_instance (
  dbid bigint(50) PRIMARY KEY,
  id_instance bigint(50) NOT NULL,
  id_workspace varchar(40) NOT NULL,
  provider varchar(100) NOT NULL,
  serialization blob,
  FOREIGN KEY(id_workspace) REFERENCES dashb_workspace (id_workspace)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel (
  dbid bigint(50) PRIMARY KEY,
  id_panel bigint(50) NOT NULL,
  id_instance bigint(50) NOT NULL,
  id_section bigint(50) NOT NULL,
  id_region varchar(100),
  position bigint(50) NOT NULL,
  FOREIGN KEY(id_section) REFERENCES dashb_section(dbid)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel_parameter (
  dbid bigint(50) PRIMARY KEY,
  id_parameter varchar(100) NOT NULL,
  id_instance bigint(50) NOT NULL,
  value text,
  language varchar(10),
  FOREIGN KEY(id_instance) REFERENCES dashb_panel_instance(dbid)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel_html (
  dbid bigint(50) PRIMARY KEY,
  id_instance bigint(50) NOT NULL,
  FOREIGN KEY (id_instance) REFERENCES dashb_panel_instance(dbid)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel_html_i18n (
  id_text bigint(50) NOT NULL,
  language varchar(10) NOT NULL,
  html_text blob,
  PRIMARY KEY(id_text, language),
  FOREIGN KEY (id_text) REFERENCES dashb_panel_html(dbid)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_allowed_panel (
  id_workspace varchar(40) NOT NULL,
  id_panel_provider varchar(255) NOT NULL, -- 255 is max for unique key!!!
  CONSTRAINT dashb_workspace_allowed_panel_pkey PRIMARY KEY(id_workspace, id_panel_provider),
  CONSTRAINT fk_workspace_id FOREIGN KEY(id_workspace) REFERENCES dashb_workspace(id_workspace)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_provider (
    id bigint(28) NOT NULL,
    code varchar(128) NOT NULL,
    provider_uid varchar(128) NOT NULL,
    provider_xml longblob NOT NULL,
    data_properties_xml longblob,
    can_edit tinyint(1),
    can_edit_properties tinyint(1),
    can_delete tinyint(1),
    PRIMARY KEY(id)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_kpi (
    id bigint(28) NOT NULL,
    id_data_provider bigint(28) NOT NULL,
    code varchar(128) NOT NULL,
    displayer_uid varchar(128) NOT NULL,
    displayer_xml longblob NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(id_data_provider) REFERENCES dashb_data_provider(id)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_kpi_i18n (
   id_kpi bigint(28) NOT NULL,
   language varchar(10) NOT NULL,
   description varchar(512),
   PRIMARY KEY(id_kpi, language),
   FOREIGN KEY(id_kpi) REFERENCES dashb_kpi(id)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_provider_i18n (
   id_data_provider bigint(28) NOT NULL,
   language varchar(10) NOT NULL,
   description varchar(512),
   PRIMARY KEY(id_data_provider, language),
   FOREIGN KEY(id_data_provider) REFERENCES dashb_data_provider(id)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

