#!/bin/sh
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
  echo -n "Enter the data script's name, followed by [ENTER]:"
  read demo;

  echo -n "Enter the H2 database URL (leave empty for default jdbc:h2:~/dashbuilder), followed by [ENTER]:"
  read url
  if [ -z "$url" ] ; then url="jdbc:h2:~/dashbuilder"; fi

  echo -n "Enter the H2 database username (leave empty for default dashbuilder), followed by [ENTER]:"
  read user
  if [ -z "$user" ] ; then user="dashbuilder"; fi

  echo -n "Enter the H2 database Password (leave empty for default dashbuilder), followed by [ENTER]:"
  read -s pwd;
  if [ -z "$pwd" ] ; then pwd="dashbuilder"; fi

  if [ -e $demo ] ; then
	echo $'\n'"Loading ..."
	"$JAVA_HOME/bin/java" -cp ../../lib/h2*.jar org.h2.tools.RunScript -url $url -user $user -password $pwd -script ./$demo
  else
	echo $'\n'"Data file $demo not found."
  fi
fi
