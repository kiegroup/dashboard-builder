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

-- Integration with the jBPM history log

CREATE TABLE processinstancelog (
  pk bigint NOT NULL,
  processinstanceid bigint NOT NULL,
  processname varchar(255) NOT NULL,
  status int(4),
  start_date datetime NOT NULL,
  end_date datetime,
  user_identity varchar(255),
  processversion varchar(255),
  duration bigint,
  PRIMARY KEY (pk)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE INDEX idx_ps_pk on bamprocesssummary(pk);
-- CUSTOM_DELIMITER
CREATE INDEX idx_ps_pid on bamprocesssummary(processinstanceid);
-- CUSTOM_DELIMITER
CREATE INDEX idx_ps_name on bamprocesssummary(processname);
-- CUSTOM_DELIMITER

CREATE table bamtasksummary (
  pk bigint NOT NULL,
  taskid bigint NOT NULL,
  taskname varchar(255) NOT NULL,
  createddate datetime,
  startdate datetime,
  userid varchar(255),
  processinstanceid bigint,
  enddate datetime,
  duration bigint,
  status varchar(16),
  PRIMARY KEY (pk)
) ENGINE = INNODB;
-- CUSTOM_DELIMITER

CREATE INDEX idx_ts_pk on bamtasksummary(pk);
-- CUSTOM_DELIMITER
CREATE INDEX idx_ts_tid on bamtasksummary(taskid);
-- CUSTOM_DELIMITER
