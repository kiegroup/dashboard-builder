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

-- Integration with the jBPM history log

CREATE TABLE processinstancelog (
  pk NUMBER(38,0) NOT NULL PRIMARY KEY USING INDEX (CREATE INDEX idx_ps_pk ON bamprocesssummary(pk)),
  processinstanceid NUMBER(38,0) NOT NULL,
  processname VARCHAR2(255) NOT NULL,
  status INTEGER,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP,
  user_identity VARCHAR2(255),
  processversion VARCHAR2(255),
  duration NUMBER(38,0)
);

-- CREATE INDEX idx_ps_pk on bamprocesssummary(pk);
CREATE INDEX idx_ps_pid on bamprocesssummary(processinstanceid);
CREATE INDEX idx_ps_name on bamprocesssummary(processname);

CREATE table bamtasksummary (
  pk NUMBER(38,0) NOT NULL PRIMARY KEY USING INDEX(CREATE INDEX idx_ts_pk on bamtasksummary(pk)),
  taskid NUMBER(38,0) NOT NULL,
  taskname VARCHAR2(255) NOT NULL,
  createddate TIMESTAMP,
  startdate TIMESTAMP,
  userid VARCHAR2(255),
  processinstanceid NUMBER(38,0),
  enddate TIMESTAMP,
  duration NUMBER(38,0),
  status VARCHAR2(16)
);

-- CREATE INDEX idx_ts_pk on bamtasksummary(pk);
CREATE INDEX idx_ts_tid on bamtasksummary(taskid);