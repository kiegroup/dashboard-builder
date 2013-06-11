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

CREATE SEQUENCE hibernate_sequence START 1000;

CREATE TABLE dashb_data_source (
   dbid numeric(50,0) PRIMARY KEY,
   ds_type  varchar(512),
   name varchar(512),
   jndi_path  varchar(512),
   ds_url varchar(512),
   jdbc_driver_class  varchar(512),
   user_name varchar(512),
   passwd varchar(512),
   test_query  varchar(2000)
);

CREATE TABLE dashb_data_source_table (
   dbid bigint PRIMARY KEY,
   name varchar (512),
   data_source varchar (512),
   selected varchar(512)
);

CREATE TABLE dashb_data_source_column (
   dbid bigint PRIMARY KEY,
   name varchar (512),
   sql_type integer,
   data_source varchar (512),
   table_name varchar (512),
   primary_key varchar (5),
   identity1 varchar (5)
);

CREATE TABLE dashb_installed_module (
   name varchar(256) PRIMARY KEY,
   version numeric(50,0)
);

CREATE TABLE dashb_permission (
   id_permission NUMERIC(28,0) PRIMARY KEY,
   principal_class VARCHAR(100),
   principal_name VARCHAR(100),
   permission_class VARCHAR(100) NOT NULL,
   permission_resource VARCHAR(100) NOT NULL,
   permission_action VARCHAR(100),
   permission_readonly BOOLEAN
);

CREATE TABLE dashb_workspace (
   id_workspace VARCHAR(40) PRIMARY KEY,
   look VARCHAR(100),
   envelope VARCHAR(100),
   url varchar unique,
   default_workspace boolean not null,
   home_search_mode NUMERIC(4,0) NOT NULL DEFAULT 0
);

CREATE TABLE dashb_workspace_home (
   id_workspace VARCHAR(40) NOT NULL,
   id_role   VARCHAR(100) NOT NULL,
   id_section NUMERIC(28,0),
   PRIMARY KEY(id_workspace, id_role),
   FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE
);

CREATE TABLE dashb_workspace_parameter (
   id_workspace VARCHAR(40) NOT NULL,
   id_parameter VARCHAR(100) NOT NULL,
   language VARCHAR(10) NOT NULL,
   value VARCHAR(4000),
   PRIMARY KEY(id_workspace, id_parameter, language),
   FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE
);

CREATE TABLE dashb_graphic_resource (
   dbid NUMERIC(28,0),
   workspace_id VARCHAR(40),
   section_id numeric(28,0),
   panel_id numeric(28,0),
   id VARCHAR,
   resource_type VARCHAR,
   zip bytea,
   status BOOL,
   last_modified TIMESTAMP,
   PRIMARY KEY(dbid)
);

CREATE TABLE dashb_section (
   id_section NUMERIC(28,0) NOT NULL,
   id_workspace VARCHAR(40) NOT NULL,
   id_template VARCHAR(100) NOT NULL,
   position BIGINT NOT NULL,
   visible BOOLEAN,
   region_spacing NUMERIC(4,0),
   panel_spacing NUMERIC(4,0),
   id_parent NUMERIC(28,0),
   url varchar,
   dbid NUMERIC(28,0) PRIMARY KEY,
   skin VARCHAR(100),
   envelope VARCHAR(100),
   FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE
);

CREATE TABLE dashb_section_i18n (
   id_section NUMERIC(28,0) NOT NULL,
   language VARCHAR(10) NOT NULL,
   title VARCHAR(200),
   PRIMARY KEY (id_section, language),
   FOREIGN KEY (id_section) REFERENCES dashb_section(dbid)
);

CREATE TABLE dashb_panel_instance (
   dbid NUMERIC(28,0) PRIMARY KEY,
   id_instance NUMERIC(28,0) NOT NULL,
   id_workspace VARCHAR(40) NOT NULL,
   provider VARCHAR(100) NOT NULL,
   serialization text,
   FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace)
);

CREATE TABLE dashb_panel (
   dbid NUMERIC(28,0) PRIMARY KEY,
   id_panel NUMERIC(28,0) NOT NULL,
   id_instance NUMERIC(28,0) NOT NULL,
   id_section NUMERIC(28,0) NOT NULL,
   id_region VARCHAR(100),
   position NUMERIC(4,0) NOT NULL,
   FOREIGN KEY (id_section) REFERENCES dashb_section(dbid)
);

CREATE TABLE dashb_panel_parameter (
   dbid NUMERIC(28,0) PRIMARY KEY,
   id_parameter VARCHAR(100) NOT NULL,
   id_instance NUMERIC(28,0) NOT NULL,
   value TEXT,
   language VARCHAR(10),
   FOREIGN KEY (id_instance) REFERENCES dashb_panel_instance(dbid)
);

CREATE TABLE dashb_panel_html (
   dbid NUMERIC(28,0) PRIMARY KEY,
   id_instance NUMERIC(28,0) NOT NULL,
   FOREIGN KEY (id_instance) REFERENCES dashb_panel_instance(dbid)
);

CREATE TABLE dashb_panel_html_i18n (
   id_text NUMERIC(28,0) NOT NULL,
   language VARCHAR(10) NOT NULL,
   html_text TEXT,
   PRIMARY KEY (id_text, language),
   FOREIGN KEY (id_text) REFERENCES dashb_panel_html(dbid)
);

CREATE TABLE dashb_allowed_panel (
   id_workspace VARCHAR(40) NOT NULL,
   id_panel_provider VARCHAR NOT NULL,
   CONSTRAINT dashb_workspace_provider_allowed_pkey PRIMARY KEY(id_workspace, id_panel_provider),
   CONSTRAINT fk_workspace_id FOREIGN KEY (id_workspace)
   REFERENCES dashb_workspace(id_workspace)
   ON DELETE CASCADE
   ON UPDATE NO ACTION
   NOT DEFERRABLE
);

CREATE TABLE dashb_data_provider (
   id NUMERIC(28,0) NOT NULL,
   code VARCHAR(128) NOT NULL,
   provider_uid VARCHAR(128) NOT NULL,
   provider_xml VARCHAR NOT NULL,
   data_properties_xml VARCHAR,
   can_edit BOOLEAN,
   can_edit_properties BOOLEAN,
   can_delete BOOLEAN,
   PRIMARY KEY (id)
);

CREATE TABLE dashb_kpi (
   id NUMERIC(28,0) NOT NULL,
   id_data_provider NUMERIC(28,0) NOT NULL,
   code VARCHAR(128) NOT NULL,
   displayer_uid VARCHAR(128) NOT NULL,
   displayer_xml VARCHAR NOT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (id_data_provider) REFERENCES dashb_data_provider(id)
);

CREATE TABLE dashb_kpi_i18n (
   id_kpi NUMERIC(28,0) NOT NULL,
   language VARCHAR(10) NOT NULL,
   description VARCHAR(512),
   PRIMARY KEY (id_kpi, language),
   FOREIGN KEY (id_kpi) REFERENCES dashb_kpi(id)
);

CREATE TABLE dashb_data_provider_i18n (
   id_data_provider NUMERIC(28,0) NOT NULL,
   language VARCHAR(10) NOT NULL,
   description VARCHAR(512),
   PRIMARY KEY (id_data_provider, language),
   FOREIGN KEY (id_data_provider) REFERENCES dashb_data_provider(id)
);

