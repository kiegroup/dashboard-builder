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
  pk BIGINT NOT NULL,
  processinstanceid BIGINT NOT NULL,
  processname VARCHAR(255) NOT NULL,
  status INTEGER,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP,
  identity VARCHAR(255),
  processversion VARCHAR(255),
  duration BIGINT,
  PRIMARY KEY (pk)
);

CREATE INDEX idx_ps_pk on processinstancelog(pk);
CREATE INDEX idx_ps_pid on processinstancelog(processinstanceid);
CREATE INDEX idx_ps_name on processinstancelog(processname);

CREATE table bamtasksummary (
  pk BIGINT NOT NULL,
  taskid BIGINT NOT NULL,
  taskname VARCHAR(255) NOT NULL,
  createddate TIMESTAMP,
  startdate TIMESTAMP,
  userid VARCHAR(255),
  processinstanceid BIGINT,
  enddate TIMESTAMP,
  duration BIGINT,
  status VARCHAR(16),
  PRIMARY KEY (pk)
);

CREATE INDEX idx_ts_pk on bamtasksummary(pk);
CREATE INDEX idx_ts_tid on bamtasksummary(taskid);
