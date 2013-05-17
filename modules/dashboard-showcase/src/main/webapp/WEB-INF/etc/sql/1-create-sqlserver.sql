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
    next_hi bigint NOT NULL
);
-- CUSTOM_DELIMITER

INSERT INTO hibernate_unique_key VALUES(1);
-- CUSTOM_DELIMITER

SET ANSI_NULLS ON;
-- CUSTOM_DELIMITER

SET QUOTED_IDENTIFIER ON;
-- CUSTOM_DELIMITER
GO
-- CUSTOM_DELIMITER

CREATE PROCEDURE nextVal
AS
BEGIN
    UPDATE hibernate_unique_key
    SET next_hi = next_hi + 1;
END
-- CUSTOM_DELIMITER

set ANSI_NULLS ON
-- CUSTOM_DELIMITER

set QUOTED_IDENTIFIER ON
-- CUSTOM_DELIMITER
GO
-- CUSTOM_DELIMITER
CREATE FUNCTION currVal()
RETURNS int
AS
BEGIN
	DECLARE @res int;
    SET @res = (SELECT next_hi FROM hibernate_unique_key);
	RETURN (@res);
END
-- CUSTOM_DELIMITER

GO
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_source (
    dbid bigint primary key,
    ds_type  varchar(512),
    name varchar(512),
    jndi_path  varchar(512),
    ds_url varchar(512),
    jdbc_driver_class  varchar(512),
    user_name varchar(512),
    passwd varchar(512),
    test_query  varchar(2000)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_source_table (
    dbid bigint primary key,
    name varchar (512),
    data_source varchar (512),
    selected varchar(512)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_source_column (
    dbid bigint primary key,
    name varchar (512),
    sql_type int,
    data_source varchar (512),
    table_name varchar (512),
    primary_key varchar (5),
    identity1 varchar (5)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_installed_module (
    name varchar(256) primary key,
    version bigint
);
-- CUSTOM_DELIMITER

/*

This file was edited with help of the create.sql of PostgreSQL.
The important changes that were be applied are:
Data types:
bytea 		-> varbinary(max)
int8 		-> bigint
boolean		-> bit
timestamp 	-> datetime
varchar/infinite chars-> nvarchar(max)

constraintS
Constraints have to be declared like in Oracle:
constraint <NAME> primary key (<FIELD>)
constraint <NAME> foreign key (<FIELD>) REFERENCES <TABLE>(<FIELD>)

- In foreign keys NOT DEFERRABLE were took out
- In foreign keys in ON DELETE/ON UPDATE, RESTRICT was substituted by NO ACTION

*/

CREATE TABLE dashb_permission(
  id_permission numeric(28,0) primary key,
  principal_class varchar(100),
  principal_name varchar(100),
  permission_class varchar(100) NOT NULL,
  permission_resource varchar(100) NOT NULL,
  permission_action varchar(100),
  permission_readonly bit
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_workspace (
  id_workspace varchar(40) PRIMARY KEY,
  look varchar(100),
  envelope varchar(100),
  url varchar(256), --cannot be unique, as it doesn't handle empty properly
  default_workspace bit not null,
  home_search_mode numeric(4,0) NOT NULL DEFAULT 0
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_workspace_home (
  id_workspace varchar(40) NOT NULL,
  id_role   varchar(100) NOT NULL,
  id_section numeric(28,0),
  primary key(id_workspace, id_role),
  foreign key (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_workspace_parameter (
  id_workspace varchar(40) NOT NULL,
  id_parameter varchar(100) NOT NULL,
  language varchar(10) NOT NULL,
  value varchar(4000),
  constraint dashb_workspace_parameter_pk primary key(id_workspace, id_parameter, language),
  constraint dashb_workspace_id_fk foreign key (id_workspace) REFERENCES dashb_workspace(id_workspace)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_graphic_resource (
  dbid numeric(28,0),
  workspace_id varchar(40),
  section_id numeric(28,0),
  panel_id numeric(28,0),
  id varchar(512),
  resource_type varchar(256),
  zip varbinary(max),
  status tinyint,
  last_modified datetime,
  constraint bui_graphic_res_PK primary key(dbid)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_section (
  id_section numeric(28,0) NOT NULL,
  id_workspace varchar(40) NOT NULL,
  id_template varchar(100) NOT NULL,
  position bigint NOT NULL,
  visible bit,
  region_spacing numeric(4,0),
  panel_spacing numeric(4,0),
  id_parent numeric(28,0),
  url varchar(256),
  dbid numeric(28,0) primary key,
  skin varchar(100),
  envelope varchar(100),
  constraint dashb_section_fk foreign key (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_section_i18n (
  id_section numeric(28,0) NOT NULL,
  language varchar(10) NOT NULL,
  title varchar(200),
  constraint dashb_section_Ii18n_pk primary key (id_section, language),
  constraint dashb_section_Ii18n_id_fk foreign key (id_section) REFERENCES dashb_section(dbid)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel_instance (
  dbid numeric(28,0) PRIMARY KEY,
  id_instance numeric(28,0) NOT NULL,
  id_workspace varchar(40) NOT NULL,
  provider varchar(100) NOT NULL,
  serialization text,
  constraint dashb_panel_instance_FK FOREIGN KEY (id_workspace) REFERENCES dashb_workspace(id_workspace)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel (
  dbid numeric(28,0) PRIMARY KEY,
  id_panel numeric(28,0) NOT NULL,
  id_instance numeric(28,0) NOT NULL,
  id_section numeric(28,0) NOT NULL,
  id_region varchar(100),
  position numeric(4,0) NOT NULL,
  constraint dashb_panel_id_section_fk foreign key (id_section) REFERENCES dashb_section(dbid)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel_parameter (
  dbid numeric(28,0) PRIMARY KEY,
  id_parameter varchar(100) NOT NULL,
  id_instance numeric(28,0) NOT NULL,
  value text,
  language varchar(10),
  constraint bui_parameter_id_instance_fk foreign key (id_instance) REFERENCES dashb_panel_instance(dbid)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel_html (
  dbid numeric(28,0) PRIMARY KEY,
  id_instance numeric(28,0) NOT NULL,
  constraint dashb_panel_html_code_fk foreign key (id_instance) REFERENCES dashb_panel_instance(dbid)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_panel_html_i18n(
  id_text numeric(28,0) NOT NULL,
  language varchar(10) NOT NULL,
  html_text text,
  constraint dashb_panel_html_i18n_pk primary key (id_text, language),
  constraint dashb_panel_html_i18n_id_text_fk foreign key (id_text) REFERENCES dashb_panel_html(dbid)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_allowed_panel(
  id_workspace varchar(40) NOT NULL,
  id_panel_provider varchar(512) NOT NULL,
  constraint dashb_allowed_panel_pk primary key(id_workspace, id_panel_provider),
  constraint fk_workspace_id foreign key (id_workspace) REFERENCES dashb_workspace(id_workspace) ON DELETE CASCADE ON UPDATE NO ACTION);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_provider (
    id numeric(28,0) NOT NULL,
    code varchar(128) NOT NULL,
    provider_uid varchar(128) NOT NULL,
    provider_xml nvarchar(max) NOT NULL,
    data_properties_xml nvarchar(max),
    can_edit bit,
    can_edit_properties bit,
    can_delete bit,
    primary key (id)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_kpi (
    id numeric(28,0) NOT NULL,
    id_data_provider numeric(28,0) NOT NULL,
    code varchar(128) NOT NULL,
    displayer_uid varchar(128) NOT NULL,
    displayer_xml nvarchar(max) NOT NULL,
    primary key (id),
    foreign key (id_data_provider) REFERENCES dashb_data_provider(id)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_kpi_i18n (
  id_kpi numeric(28,0) NOT NULL,
  language varchar(10) NOT NULL,
  description varchar(512),
  primary key (id_kpi, language),
  foreign key (id_kpi) REFERENCES dashb_kpi(id)
);
-- CUSTOM_DELIMITER

CREATE TABLE dashb_data_provider_i18n (
  id_data_provider numeric(28,0) NOT NULL,
  language varchar(10) NOT NULL,
  description varchar(512),
  primary key (id_data_provider, language),
  foreign key (id_data_provider) REFERENCES dashb_data_provider(id)
);
-- CUSTOM_DELIMITER
