/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.ui.controller;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.ui.HTTPSettings;

import java.io.File;

/**
 * Generates and delete temporal directories according to session identifier.
 */
public class SessionTmpDirFactory {

    /**
     * SessionTmpDirFactory constructor comment.
     */
    private SessionTmpDirFactory() {
        super();
    }

    /**
     * Deletes directory and content
     */
    public static void deleteTmpDir(javax.servlet.http.HttpServletRequest req) {
        File dir = new File(SessionTmpDirFactory.getTmpDir(req));

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int iFile = 0; iFile < files.length; iFile++)
                files[iFile].delete();

            if (dir.exists() && dir.isDirectory())
                dir.delete();
        }
    }

    /**
     * Returns the directory name for a session, creating it if it doesn't exist.
     */
    public static String getTmpDir(javax.servlet.http.HttpServletRequest req) {
        String file = Application.lookup().getBaseAppDirectory() + "/" + HTTPSettings.lookup().getDownloadDir() + File.separator + req.getSession().getId();
        File f = new File(file);
        if (!f.exists()) f.mkdirs();
        return file;
    }
}