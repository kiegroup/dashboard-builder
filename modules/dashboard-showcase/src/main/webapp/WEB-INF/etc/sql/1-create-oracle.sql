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

CREATE SEQUENCE hibernate_sequence START with 1000 nomaxvalue;

CREATE TABLE dashb_data_source(
   dbid NUMBER(38,0) PRIMARY KEY,
   ds_type  varchar2(512),
   name varchar2(512),
   jndi_path  varchar2(512),
   ds_url varchar2(512),
   jdbc_driver_class  varchar2(512),
   user_name varchar2(512),
   passwd varchar2(512),
   test_query  varchar2(2000)
);

CREATE TABLE dashb_data_source_table (
   dbid NUMBER(38,0) PRIMARY KEY,
   name varchar (512),
   data_source varchar (512),
   selected varchar2(512)
);

CREATE TABLE dashb_data_source_column (
   dbid NUMBER(38,0) PRIMARY KEY,
   name varchar (512),
   sql_type integer,
   data_source varchar (512),
   table_name varchar (512),
   primary_key varchar (5),
   identity1 varchar (5)
);

CREATE TABLE dashb_installed_module (
   name varchar2(256) PRIMARY KEY,
   version NUMBER(38,0)
);

CREATE TABLE dashb_permission(
   id_permission NUMBER(28,0) PRIMARY KEY,
   principal_class varchar2(100),
   principal_name varchar2(100),
   permission_class varchar2(100) NOT NULL,
   permission_resource varchar2(100) NOT NULL,
   permission_action varchar2(100),
   permission_readonly varchar2(1)
);

CREATE TABLE dashb_workspace (
   id_workspace varchar2(40) PRIMARY KEY,
   look varchar2(100),
   envelope varchar2(100),
   url varchar2(3000) unique,
   default_workspace varchar2(1) not null,
   home_search_mode NUMBER(4,0) DEFAULT 0 NOT NULL
);

CREATE TABLE dashb_workspace_home (
   id_workspace varchar2(40) NOT NULL,
   id_role   varchar2(100) NOT NULL,
   id_section NUMBER(28,0),
   constraint dashb_workspace_home_pk PRIMARY KEY(id_workspace, id_role),
   constraint dashb_workspace_home_fk FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE
);

CREATE TABLE dashb_workspace_parameter (
   id_workspace varchar2(40) NOT NULL,
   id_parameter varchar2(100) NOT NULL,
   language varchar2(10) NOT NULL,
   value varchar2(4000),
   constraint dashb_workspace_parameter_pk PRIMARY KEY(id_workspace, id_parameter, language),
   constraint dashb_workspace_parameter_fk FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE
);

CREATE TABLE dashb_graphic_resource (
   dbid NUMBER(28,0),
   workspace_id varchar2(40),
   section_id NUMBER(28,0),
   panel_id NUMBER(28,0),
   id varchar2(3000),
   resource_type  varchar2(3000),
   zip blob,
   status NUMBER(1,0),
   last_modified DATE,
   constraint dashb_graphic_resource_pk PRIMARY KEY(dbid)
);

CREATE TABLE dashb_section (
   id_section NUMBER(28,0) NOT NULL,
   id_workspace varchar2(40) NOT NULL,
   id_template varchar2(100) NOT NULL,
   position INTEGER NOT NULL,
   visible varchar2(1),
   region_spacing NUMBER(4,0),
   panel_spacing NUMBER(4,0),
   id_parent NUMBER(28,0),
   url varchar2(3000),
   dbid NUMBER(28,0) PRIMARY KEY,
   skin varchar2(100),
   envelope varchar2(100),
   constraint dashb_section_fk FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE
);

CREATE TABLE dashb_section_i18n (
   id_section NUMBER(28,0) NOT NULL,
   language varchar2(10) NOT NULL,
   title varchar2(200),
   constraint dashb_section_i18n_pk PRIMARY KEY (id_section, language),
   constraint dashb_section_i18n_fk FOREIGN KEY (id_section) REFERENCES dashb_section(dbid)
);

CREATE TABLE dashb_panel_instance (
   dbid NUMBER(28,0) PRIMARY KEY,
   id_instance NUMBER(28,0) NOT NULL,
   id_workspace varchar2(40) NOT NULL,
   provider varchar2(100) NOT NULL,
   serialization BLOB,
   constraint dashb_panel_instance_fk FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace)
);


CREATE TABLE dashb_panel (
   dbid NUMBER(28,0) PRIMARY KEY,
   id_panel NUMBER(28,0) NOT NULL,
   id_instance NUMBER(28,0) NOT NULL,
   id_section NUMBER(28,0) NOT NULL,
   id_region varchar2(100),
   position NUMBER(4,0) NOT NULL,
   constraint dashb_panel_fk FOREIGN KEY (id_section) REFERENCES dashb_section(dbid)
);

CREATE TABLE dashb_panel_parameter (
   dbid NUMBER(28,0) PRIMARY KEY,
   id_parameter varchar2(100) NOT NULL,
   id_instance NUMBER(28,0) NOT NULL,
   value blob,
   language varchar2(10),
   constraint dashb_panel_parameter_fk FOREIGN KEY (id_instance) REFERENCES dashb_panel_instance(dbid)
);

CREATE TABLE dashb_panel_html (
   dbid NUMBER(28,0) PRIMARY KEY,
   id_instance NUMBER(28,0) NOT NULL,
   constraint dashb_panel_html_fk FOREIGN KEY (id_instance) REFERENCES dashb_panel_instance(dbid)
);

CREATE TABLE dashb_panel_html_i18n (
   id_text NUMBER(28,0) NOT NULL,
   language varchar2(10) NOT NULL,
   html_text blob,
   constraint dashb_panel_html_i18n_pk PRIMARY KEY (id_text, language),
   constraint dashb_panel_html_i18n_fk FOREIGN KEY (id_text) REFERENCES dashb_panel_html(dbid)
);

CREATE TABLE dashb_allowed_panel (
   id_workspace varchar2(40) NOT NULL,
   id_panel_provider varchar2(3000)NOT NULL,
   CONSTRAINT bui_wspace_prov_allowed_pkey PRIMARY KEY(id_workspace, id_panel_provider),
   CONSTRAINT fk_workspace_id FOREIGN KEY (id_workspace)
   REFERENCES dashb_workspace(id_workspace)
   ON DELETE CASCADE
);

CREATE TABLE dashb_data_provider (
   id NUMBER(28,0) NOT NULL,
   code varchar2(128) NOT NULL,
   provider_uid varchar2(128) NOT NULL,
   provider_xml BLOB NOT NULL,
   data_properties_xml BLOB,
   can_edit char(1),
   can_edit_properties char(1),
   can_delete char(1),
   constraint dashb_data_provider_pk PRIMARY KEY (id)
);

CREATE TABLE dashb_kpi (
   id NUMBER(28,0) NOT NULL,
   id_data_provider NUMBER(28,0) NOT NULL,
   code varchar2(128) NOT NULL,
   displayer_uid varchar2(128) NOT NULL,
   displayer_xml BLOB NOT NULL,
   constraint dashb_kpi_pk PRIMARY KEY (id),
   constraint dashb_kpi_fk FOREIGN KEY (id_data_provider) REFERENCES dashb_data_provider(id)
);

CREATE TABLE dashb_kpi_i18n (
   id_kpi NUMBER(28,0) NOT NULL,
   language varchar2(10) NOT NULL,
   description varchar2(512),
   constraint dashb_kpi_i18n_pk PRIMARY KEY (id_kpi, language),
   constraint dashb_kpi_i18n_fk FOREIGN KEY (id_kpi) REFERENCES dashb_kpi(id)
);

CREATE TABLE dashb_data_provider_i18n (
   id_data_provider NUMBER(28,0) NOT NULL,
   language varchar2(10) NOT NULL,
   description varchar2(512),
   constraint  dashb_data_provider_i18n_pk PRIMARY KEY (id_data_provider, language),
   constraint  dashb_data_provider_i18n_fk FOREIGN KEY (id_data_provider) REFERENCES dashb_data_provider(id)
);

