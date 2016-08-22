--
-- Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

-- NOTE: For the DB2, the default page size if not enough, it must be 16384
-- CREATE DATABASE dashb PAGESIZE 16384

-- NOTE that Sybase ASE does not have "sequence", instead you should use IDENTITY columns.
-- Solution: Implement a custom function to obtain next values. See HibernateInitializer#nativeToHiloReplaceableDialects
CREATE TABLE hibernate_unique_key (
  next_hi bigint NOT NULL
);

INSERT INTO hibernate_unique_key VALUES(1);

CREATE PROCEDURE nextVal
AS
  UPDATE hibernate_unique_key SET next_hi = next_hi + 1;

CREATE FUNCTION currVal
  returns int
As
  BEGIN
    declare @Result int
    select @Result = next_hi FROM hibernate_unique_key
    RETURN (@Result)
  END;


CREATE TABLE dashb_data_source (
   dbid bigint NOT NULL,
   ds_type  varchar(512) NULL,
   name varchar(512) NULL,
   jndi_path  varchar(512) NULL,
   ds_url varchar(512) NULL,
   jdbc_driver_class  varchar(512) NULL,
   user_name varchar(512) NULL,
   passwd varchar(512) NULL,
   test_query  varchar(2000) NULL,
   PRIMARY KEY (dbid)
);
CREATE TABLE dashb_data_source_table (
   dbid bigint NOT NULL,
   name varchar (512) NULL,
   data_source varchar (512) NULL,
   selected varchar(512) NULL,
   PRIMARY KEY (dbid)
);
CREATE TABLE dashb_data_source_column (
   dbid bigint NOT NULL,
   name varchar (512) NULL,
   sql_type integer NULL,
   data_source varchar (512) NULL,
   table_name varchar (512) NULL,
   primary_key varchar (5) NULL,
   identity1 varchar (5) NULL,
   PRIMARY KEY (dbid)
);
CREATE TABLE dashb_installed_module (
   name varchar(256) NOT NULL,
   version bigint NULL,
   PRIMARY KEY (name)
);

CREATE TABLE dashb_permission (
   id_permission DECIMAL(28,0) NOT NULL,
   principal_class VARCHAR(100) NULL,
   principal_name VARCHAR(100) NULL,
   permission_class VARCHAR(100) NOT NULL,
   permission_resource VARCHAR(100) NOT NULL,
   permission_action VARCHAR(100) NULL,
   permission_readonly smallint NULL,
   PRIMARY KEY (id_permission)
);

CREATE TABLE dashb_workspace (
   id_workspace VARCHAR(40) NOT NULL,
   look VARCHAR(100) NULL,
   envelope VARCHAR(100) NULL,
   -- BZ-1030407: url column - DB2 unique constraint must be inserted in a not-null column,
   -- but when duplicating a workspace the url is initially null. So, in DB2 case (not other dbms) the url
   -- column cannot contain a unique constraint.
   url varchar(3000) NULL,
   default_workspace smallint NOT NULL,
   home_search_mode DECIMAL(4,0) NOT NULL,
   PRIMARY KEY (id_workspace)
);

ALTER TABLE dashb_workspace replace home_search_mode default 0;

CREATE TABLE dashb_workspace_home (
   id_workspace VARCHAR(40) NOT NULL,
   id_role   VARCHAR(100) NOT NULL,
   id_section numeric(28,0) NULL,
   PRIMARY KEY(id_workspace, id_role),
   FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace)
);

CREATE TABLE dashb_workspace_parameter (
   id_workspace VARCHAR(40) NOT NULL,
   id_parameter VARCHAR(100) NOT NULL,
   language VARCHAR(10) NOT NULL,
   value VARCHAR(4000) NULL,
   PRIMARY KEY(id_workspace, id_parameter, language),
   FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace)
);

CREATE TABLE dashb_graphic_resource (
   dbid NUMERIC(28,0) NOT NULL,
   workspace_id VARCHAR(40) NULL,
   section_id numeric(28,0) NULL,
   panel_id numeric(28,0) NULL,
   id VARCHAR(3000) NULL,
   resource_type VARCHAR(3000) NULL,
   zip IMAGE NULL,
   status smallint NULL,
   last_modified DATETIME NULL,
   PRIMARY KEY(dbid)
);


CREATE TABLE dashb_section (
   id_section NUMERIC(28,0) NOT NULL,
   id_workspace VARCHAR(40) NOT NULL,
   id_template VARCHAR(100) NOT NULL,
   position BIGINT NOT NULL,
   visible smallint NULL,
   region_spacing NUMERIC(4,0) NULL,
   panel_spacing NUMERIC(4,0) NULL,
   id_parent NUMERIC(28,0) NULL,
   url varchar(3000) NULL,
   dbid NUMERIC(28,0) NOT NULL,
   skin VARCHAR(100) NULL,
   envelope VARCHAR(100) NULL,
   PRIMARY KEY (dbid),
   FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace)
);

CREATE TABLE dashb_section_i18n (
   id_section NUMERIC(28,0) NOT NULL,
   language VARCHAR(10) NOT NULL,
   title VARCHAR(200) NULL,
   PRIMARY KEY (id_section, language),
   FOREIGN KEY (id_section) REFERENCES dashb_section(dbid)
);

CREATE TABLE dashb_panel_instance (
   dbid NUMERIC(28,0) NOT NULL,
   id_instance NUMERIC(28,0) NOT NULL,
   id_workspace VARCHAR(40) NOT NULL,
   provider VARCHAR(100) NOT NULL,
   serialization TEXT NULL,
   PRIMARY KEY (dbid),
   FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace)
);

CREATE TABLE dashb_panel (
   dbid NUMERIC(28,0) NOT NULL,
   id_panel NUMERIC(28,0) NOT NULL,
   id_instance NUMERIC(28,0) NOT NULL,
   id_section NUMERIC(28,0) NOT NULL,
   id_region VARCHAR(100) NULL,
   position NUMERIC(4,0) NOT NULL,
   PRIMARY KEY (dbid),
   FOREIGN KEY (id_section) REFERENCES dashb_section(dbid)
);

CREATE TABLE dashb_panel_parameter (
   dbid NUMERIC(28,0) NOT NULL,
   id_parameter VARCHAR(100) NOT NULL,
   id_instance NUMERIC(28,0) NOT NULL,
   value TEXT NULL,
   language VARCHAR(10) NULL,
   PRIMARY KEY (dbid),
   FOREIGN KEY (id_instance) REFERENCES dashb_panel_instance(dbid)
);

CREATE TABLE dashb_panel_html (
   dbid NUMERIC(28,0) NOT NULL,
   id_instance NUMERIC(28,0) NOT NULL,
   PRIMARY KEY (dbid),
   FOREIGN KEY (id_instance) REFERENCES dashb_panel_instance(dbid)
);

CREATE TABLE dashb_panel_html_i18n (
   id_text NUMERIC(28,0) NOT NULL,
   language VARCHAR(10) NOT NULL,
   html_text TEXT NULL,
   PRIMARY KEY (id_text, language),
   FOREIGN KEY (id_text) REFERENCES dashb_panel_html(dbid)
);

CREATE TABLE dashb_allowed_panel (
   id_workspace VARCHAR(40) NOT NULL,
   id_panel_provider VARCHAR(2500) NOT NULL,
   CONSTRAINT dashb_workspace_provider_allowed_pkey PRIMARY KEY(id_workspace, id_panel_provider),
   CONSTRAINT fk_workspace_id FOREIGN KEY (id_workspace)
   REFERENCES dashb_workspace(id_workspace)
);

CREATE TABLE dashb_data_provider (
   id NUMERIC(28,0) NOT NULL,
   code VARCHAR(128) NOT NULL,
   provider_uid TEXT NOT NULL,
   provider_xml TEXT NOT NULL,
   data_properties_xml TEXT NULL,
   can_edit smallint NULL,
   can_edit_properties smallint NULL,
   can_delete smallint NULL,
   PRIMARY KEY (id)
);

CREATE TABLE dashb_kpi (
   id NUMERIC(28,0) NOT NULL,
   id_data_provider NUMERIC(28,0) NOT NULL,
   code VARCHAR(128) NOT NULL,
   displayer_uid VARCHAR(128) NOT NULL,
   displayer_xml TEXT NOT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (id_data_provider) REFERENCES dashb_data_provider(id)
);

CREATE TABLE dashb_kpi_i18n (
   id_kpi NUMERIC(28,0) NOT NULL,
   language VARCHAR(10) NOT NULL,
   description VARCHAR(512) NULL,
   PRIMARY KEY (id_kpi, language),
   FOREIGN KEY (id_kpi) REFERENCES dashb_kpi(id)
);

CREATE TABLE dashb_data_provider_i18n (
   id_data_provider NUMERIC(28,0) NOT NULL,
   language VARCHAR(10) NOT NULL,
   description VARCHAR(512) NULL,
   PRIMARY KEY (id_data_provider, language),
   FOREIGN KEY (id_data_provider) REFERENCES dashb_data_provider(id)
);

CREATE TABLE dashb_cluster_node (
   id_node NUMERIC(28,0) NOT NULL,
   node_address VARCHAR(50) NOT NULL,
   startup_time DATETIME NOT NULL,
   node_status VARCHAR(100) NULL,
   PRIMARY KEY (id_node)
);

-- Create the trigger that simulates the cascade delete for dashb_workspace
create trigger workspacedeletecascade
on dashb_workspace
for delete
as
  delete dashb_workspace_home
  from dashb_workspace_home, deleted
  where dashb_workspace_home.id_workspace = deleted.id_workspace
delete dashb_workspace_parameter
from dashb_workspace_parameter, deleted
where dashb_workspace_parameter.id_workspace = deleted.id_workspace
delete dashb_section
from dashb_section, deleted
where dashb_section.id_workspace = deleted.id_workspace
delete dashb_allowed_panel
from dashb_allowed_panel, deleted
where dashb_allowed_panel.id_workspace = deleted.id_workspace; 
