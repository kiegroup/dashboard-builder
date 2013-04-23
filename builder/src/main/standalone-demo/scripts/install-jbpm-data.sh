#!/usr/bin/env bash
#
# Copyright (C) 2012 JBoss Inc
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

if [ -z "$JAVA_HOME" ] ; then
  echo "Error: JAVA_HOME is not defined."
else
  echo "Populating the jBPM Process Dashboard with some sample data..."
  "$JAVA_HOME/bin/java" -cp ./db/h2*.jar org.h2.tools.RunScript -url "jdbc:h2:./db/dashbuilder" -user "dashbuilder" -password "dashbuilder" -script "./db/jbpm_demo_1000_h2.sql"
  echo "Done."
fi
