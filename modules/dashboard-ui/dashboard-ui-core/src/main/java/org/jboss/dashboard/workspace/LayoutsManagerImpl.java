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
package org.jboss.dashboard.workspace;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.workspace.LayoutsManager;
import org.jboss.dashboard.ui.resources.GraphicElementScopeDescriptor;
import org.jboss.dashboard.ui.resources.Layout;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ApplicationScoped
public class LayoutsManagerImpl extends GraphicElementManagerImpl implements LayoutsManager {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LayoutsManagerImpl.class.getName());

    @Inject @Config("WEB-INF/etc/layouts")
    private String layoutsDir;

    public void start() throws Exception {
        super.classToHandle = Layout.class;
        super.baseDir = layoutsDir;
        super.start();
    }

    /**
     * Determine the scope for the element handled by this manager.
     */
    public GraphicElementScopeDescriptor getElementScopeDescriptor() {
        return GraphicElementScopeDescriptor.SECTION_SCOPED;
    }

    /**
     * Processes an old element descriptor to adapt it to new format.
     *
     * @param xml file to process
     * @throws java.io.IOException
     */
    protected void processOldElement(File xml) throws Exception {
        log.debug("Migrating layout in xml " + xml);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new FileInputStream(xml));
        Element root = doc.getRootElement();
        if (root.getName().equalsIgnoreCase("layout")) {
            String id = root.getAttributeValue("id");
            if (getElement(id, null, null, null) != null) {
                log.debug("Ignoring layout " + id + ". It is one of the database layouts.");
                return;
            }
            String description = root.getChildTextTrim("description");
            String image = root.getAttributeValue("image");
            File imageFile = image == null ? null : new File(Application.lookup().getBaseAppDirectory() + "/" + image);
            File jspFile = new File(Application.lookup().getBaseAppDirectory() + "/layouts/" + id + ".jsp");
            createNewBaseLayout(xml, jspFile, imageFile, id, description);
        } else {
            throw new Exception("Invalid layout format in file " + xml);
        }
    }

    protected void createNewBaseLayout(File xml, File jsp, File image, String id, String description) {
        try {
            log.debug("Creating base layout with id=" + id);
            File destinationZip = new File(Application.lookup().getBaseAppDirectory() + File.separator + baseDir + "/" + id + ".zip");
            if (destinationZip.exists()) {
                log.error("Aborting creation of base skin, as it already exists: " + destinationZip.getName());
                return;
            }
            new File(destinationZip.getParent()).mkdirs();
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destinationZip));
            putFileToZip(xml, zos);//Put xml in zip.
            putFileToZip(jsp, zos);//Put jsp in zip.
            if (image != null && image.exists()) {
                putFileToZip(image, zos);//Put image in zip.
            } else {
                log.debug("Layout has no image: " + image);
            }
            {//Put descriptor in zip.
                ZipEntry zEntry = new ZipEntry(Layout.DESCRIPTOR_FILENAME);
                zos.putNextEntry(zEntry);
                log.debug("Creating entry for file " + zEntry.getName());
                //now write the content of the file to the ZipOutputStream
                Properties p = new Properties();

                //Use ../ for Backwards compatibility. Old jsps must be in the same dir when deploying, because the may use relative paths in includes...
                p.setProperty("resource.JSP", "../" + jsp.getName());
                p.setProperty("resource.XML", xml.getName());
                if (image != null && image.exists())
                    p.setProperty("resource.IMG", image.getName());
                p.setProperty("name.es", description);
                p.setProperty("name.en", description);
                p.setProperty("name.ca", description);
                p.store(zos, "Generated by migration script on ");
            }
            zos.close();
        } catch (Exception e) {
            log.error("Error creating base layout with id " + id, e);
        }
    }

    private void putFileToZip(File file, ZipOutputStream zos) throws IOException {
        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;
        ZipEntry zEntry = new ZipEntry(file.getName());
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        log.debug("Creating entry for file " + zEntry.getName());
        zos.putNextEntry(zEntry);
        //now write the content of the file to the ZipOutputStream
        while ((bytesIn = is.read(readBuffer)) != -1) {
            zos.write(readBuffer, 0, bytesIn);
        }
        //close the Stream
        is.close();
    }


}
