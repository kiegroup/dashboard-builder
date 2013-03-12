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
package org.jboss.dashboard.commons.io;

import org.jboss.dashboard.commons.text.StringUtil;

import java.io.*;
import java.util.Vector;

/**
 * Utilities to work with files.
 */
public class FileUtil {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Constructs an empty object.
     * This constructor is private to prevent instantiating this class.
     */
    private FileUtil() {
    }

    /**
     * Test whether the file denoted by a pathname exists
     *
     * @param fileName File pathname
     * @return true if and only if the file denoted by this
     *         pathname exists; false otherwise
     */
    public static boolean existFile(String fileName) {
        return new File(fileName).exists();
    }

    /**
     * Test if the time a file was last modified is before, after or
     * equals than the time another file was last modified
     *
     * @param fileName1 First file pathname
     * @param fileName2 Second file pathname
     * @return 0 if and only if the instant of time both files was last
     *         modified is the same. &lt;0 if the second file was the last modified
     *         file and &gt;0 if the first file was the last modified.
     */
    public static long compFileDate(String fileName1, String fileName2) {
        File file1 = new File(fileName1);
        File file2 = new File(fileName2);

        return file1.lastModified() - file2.lastModified();
    }

    /**
     * Test if the second file was the last modified
     *
     * @param fileName1 First file pathname
     * @param fileName2 Second file pathname
     * @return true if and only if the second file is the last modified;
     *         false otherwise
     */
    public static boolean isUpdateFile(String fileName1, String fileName2) {
        File file1 = new File(fileName1);
        File file2 = new File(fileName2);

        if (!file1.exists()) {
            return file2.exists();
        } else {
            if (file2.exists()) {
                return file2.lastModified() - file1.lastModified() >= 0;
            } else {
                return false;
            }
        }
    }

    /**
     * Rename a file
     *
     * @param oldFileName Old file name
     * @param newFileName New file name
     */
    public static void renameFile(String oldFileName, String newFileName) {
        File oldF = new File(oldFileName);
        File newF = new File(newFileName);

        oldF.renameTo(newF);
    }

    /**
     * Delete the file or directory denoted by a pathname.
     * If this pathname denotes a directory, then the directory must be empty in
     * order to be deleted
     *
     * @param fileName File pathname
     */
    public static void deleteFile(String fileName) {
        File file = new File(fileName);

        file.delete();
    }

    /**
     * Replace the ocurrences of a string in a File with new string
     *
     * @param file    Source file
     * @param newfile Destination file. If this parameter is 'null'
     *                the changes are stored in the source file.
     * @param cad     String to replace
     * @param newcad  New string
     * @throws IOException
     */
    public static void replace(String file, String newfile, String cad,
                               String newcad)
            throws IOException {
        FileWriter fw;
        BufferedReader reader;
        BufferedWriter writer;
        StringBuffer buf = new StringBuffer();
        String line;

        // Leer los datos del fichero y reemplazar las cadenas
        reader = new BufferedReader(new FileReader(file));
        line = reader.readLine();
        while (line != null) {
            buf.append(StringUtil.replaceAll(line, cad, newcad)).append("\n");
            line = reader.readLine();
        }
        reader.close();

        // Escribir los nuevos datos en el fichero
        if (newfile == null || newfile.equals("")) {
            fw = new FileWriter(file);
        } else {
            fw = new FileWriter(newfile);
        }

        writer = new BufferedWriter(fw);
        writer.write(buf.toString());
        writer.flush();
        writer.close();
        fw.close();
    }

    /**
     * Replace the ocurrences of a list of strings in a File with new strings
     *
     * @param file    Source file
     * @param newfile Destination file. If this parameter is 'null'
     *                the changes are stored in the source file.
     * @param cads    String list to replace
     * @param newcads New string list
     * @throws IOException
     */
    public static void replace(String file, String newfile, String[] cads,
                               String[] newcads)
            throws IOException {
        FileWriter fw;
        BufferedReader reader;
        BufferedWriter writer;
        StringBuffer buf = new StringBuffer();
        String line;

        // Leer los datos del fichero y reemplazar las cadenas
        reader = new BufferedReader(new FileReader(file));
        line = reader.readLine();
        while (line != null) {
            buf.append(StringUtil.replaceAll(line, cads, newcads))
                    .append("\n");

            line = reader.readLine();
        }
        reader.close();

        // Escribir los nuevos datos en el fichero
        if (newfile == null || newfile.equals("")) {
            fw = new FileWriter(file);
        } else {
            fw = new FileWriter(newfile);
        }

        writer = new BufferedWriter(fw);
        writer.write(buf.toString());
        writer.flush();
        writer.close();
        fw.close();
    }

    /**
     * Make a backup copy of a file denoted by a pathname, renaming the
     * original file into a *.bak file. If there is a previous file with
     * these name, it is deleted
     *
     * @param fileName File pathname
     */
    public static void makeBackupFile(String fileName) {
        String bakFile;
        int extIndex = fileName.lastIndexOf('.');

        if (extIndex == -1) {
            bakFile = fileName + ".bak";
        } else {
            bakFile = fileName.substring(0, extIndex) + ".bak";
        }

        if (existFile(bakFile)) {
            deleteFile(bakFile);
        }

        renameFile(fileName, bakFile);
    }


    /**
     * Restore a backup copy of a file denoted by a pathname, renaming the
     * backup file (*.bak) into a original file. If there is a previous file
     * with these name, it is deleted
     *
     * @param fileName File pathname
     */
    public static void restoreBackupFile(String fileName) {
        String bakFile;
        int extIndex = fileName.lastIndexOf('.');

        if (extIndex == -1) {
            bakFile = fileName + ".bak";
        } else {
            bakFile = fileName.substring(0, extIndex) + ".bak";
        }

        if (existFile(fileName)) {
            deleteFile(fileName);
        }

        renameFile(bakFile, fileName);
    }


    /**
     * Read a file into a buffer sized the size of the file
     *
     * @param filename The file full-path
     * @return The file contents
     */
    public static byte[] readFile(String filename)
            throws FileNotFoundException, IOException {
        File file = new File(filename);
        FileInputStream stream = new FileInputStream(file);

        return readFromInputStream(stream);
    }

    /**
     * Tries to read a file from disk, if it doesn't exist try to read from the
     * CLASSPATH.
     *
     * @param fileName The file full-path or file name (to search into
     *                 CLASSPATH)
     * @return The file contents
     */
    public static byte[] readResourceFile(String fileName)
            throws FileNotFoundException, IOException {
        InputStream stream = null;
        File file = new File(fileName);
        if (file.exists()) {
            // Buscamos primero en disco
            stream = new FileInputStream(fileName);
        } else {
            // Buscamos en el CLASSPATH
            stream = ClassLoader.getSystemResourceAsStream(fileName);
        }

        // Test if exist resource
        if (stream == null) {
            throw new FileNotFoundException(fileName);
        }

        return readFromInputStream(stream);
    }

    /**
     * Reads a set of bytes from an InputStream and returns the data as a byte
     * array.
     *
     * @param is InputStream
     * @return Array of bytes
     */
    public static byte[] readFromInputStream(InputStream is)
            throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int copyBufferSize = 2048;

        try {
            // Transpaso de informacion
            byte[] byteData = new byte[copyBufferSize];
            int readSize;

            while ((readSize = is.read(byteData)) != -1) out.write(byteData, 0, readSize);
        } finally {
            // Cierro los ficheros
            is.close();
            out.close();
        }

        return out.toByteArray();
    }

    /**
     * Read all lines of text in a file.  A line is considered to be
     * terminated by any one of a line feed ('\n'), a carriage return ('\r'),
     * or a carriage return followed immediately by a linefeed
     *
     * @param file File pathname
     * @return String vector that contains all lines
     */
    public static Vector readLines(String file) throws IOException {
        Vector contents = new Vector();

        BufferedReader in = new BufferedReader(new FileReader(file));

        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            } else {
                contents.addElement(line);
            }
        }

        in.close();
        return contents;
    }

    /**
     * Write an array of strings to the specified file.  If the second
     * argument is true, then the strings will be written to the end of the
     * file; if it is false, the strings will be written to the beginning,
     * and the file is truncated
     *
     * @param lines  array of strings to write
     * @param file   file pathname
     * @param append if true the lines will be written to the end of file,
     *               otherwise they will be written to the begin of file and
     *               the file is truncated.
     */
    public static void writeLines(Vector lines, String file, boolean append)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(file, append);
        PrintWriter fileOut = new PrintWriter(fos);
        for (int i = 0; i < lines.size(); i++) {
            fileOut.println(lines.elementAt(i));
        }
        fileOut.flush();
        fileOut.close();
    }

    /**
     * Append content to a file
     *
     * @param contents the content
     * @param filename the file full-path
     */
    public static void appendToFile(String contents, String filename)
            throws FileNotFoundException, IOException {
        byte[] array = contents.getBytes();
        appendToFile(array, filename);
    }

    /**
     * Append content to a file
     *
     * @param contents the content
     * @param filename the file full-path
     */
    public static void appendToFile(byte[] contents, String filename)
            throws FileNotFoundException, IOException {
        FileOutputStream stream = new FileOutputStream(filename, true);
        stream.write(contents);
        stream.flush();
        stream.close();
    }

    /**
     * From an existing  source file, and an existing temporal file,
     * copy, in a trunc way, the temporal in place of the source file,
     * and remove the temporal file
     *
     * @param source the existing source file, where to copy the information
     *               from the existing temporal file
     * @param tmp    the existing temporal file from where to copy the data
     *               into the source file
     * @return true if the files operation have finished ok, false if not
     */
    public static boolean swapFile(String source, String tmp) {
        File sourceFile;
        File inputFile;
        boolean result = true;

        inputFile = new File(source);
        sourceFile = new File(tmp);

        result &= inputFile.delete();
        result &= sourceFile.renameTo(inputFile);

        return result;
    }

    /**
     * Copy a file to another file using a static buffer of DEFAULT_BUFFER_SIZE
     *
     * @param in       Source data stream
     * @param destFile Destination file
     * @throws IOException
     */
    public static void copyFile(InputStream in, String destFile)
            throws IOException {
        copyFile(in, destFile, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copy a file to another file using a static buffer of DEFAULT_BUFFER_SIZE
     *
     * @param sourceFile Source file
     * @param destFile   Destination file
     * @throws IOException
     */
    public static void copyFile(String sourceFile, String destFile)
            throws IOException {
        copyFile(sourceFile, destFile, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copy a file to another file using a buffer of maximum size
     *
     * @param sourceFile     Source file
     * @param destFile       Destination file
     * @param copyBufferSize Maximum size of the buffer
     * @throws IOException
     */
    public static void copyFile(String sourceFile, String destFile,
                                int copyBufferSize)
            throws IOException {
        // Abro los ficheros
        FileInputStream in = new FileInputStream(sourceFile);

        copyFile(in, destFile, copyBufferSize);
    }


    /**
     * Copy a file to another file using a buffer of maximum size
     *
     * @param in             Source data stream
     * @param destFile       Destination file
     * @param copyBufferSize Maximum size of the buffer
     * @throws IOException
     */
    public static void copyFile(InputStream in, String destFile,
                                int copyBufferSize)
            throws IOException {
        // Abro los ficheros
        FileOutputStream out = new FileOutputStream(destFile);

        try {
            // Transpaso de informacion
            byte[] data = new byte[copyBufferSize];
            int readSize;

            while ((readSize = in.read(data)) != -1) out.write(data, 0, readSize);
        } finally {
            // Cierro los ficheros
            in.close();
            out.close();
        }
    }

    /**
     * MOve a file to another file using a static buffer of DEFAULT_BUFFER_SIZE
     *
     * @param sourceFile Source file
     * @param destFile   Destination file
     * @throws IOException
     */
    public static void moveFile(String sourceFile, String destFile)
            throws IOException {
        moveFile(sourceFile, destFile, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Move a file to another file using a static buffer of DEFAULT_BUFFER_SIZE
     *
     * @param in       Source data stream
     * @param destFile Destination file
     * @throws IOException
     */
    public static void moveFile(InputStream in, String destFile)
            throws IOException {
        moveFile(in, destFile, DEFAULT_BUFFER_SIZE);
    }

    /**
     * MOve a file to another file using a buffer of maximum size
     *
     * @param sourceFile     Source file
     * @param destFile       Destination file
     * @param moveBufferSize Maximum size of the buffer
     * @throws IOException
     */
    public static void moveFile(String sourceFile, String destFile,
                                int moveBufferSize)
            throws IOException {
        // Abro los ficheros
        FileInputStream in = new FileInputStream(sourceFile);

        moveFile(in, destFile, moveBufferSize);

        deleteFile(sourceFile);
    }

    /**
     * Move a file to another file using a buffer of maximum size
     *
     * @param in             Source data stream
     * @param destFile       Destination file
     * @param moveBufferSize Maximum size of the buffer
     * @throws IOException
     */
    public static void moveFile(InputStream in, String destFile,
                                int moveBufferSize)
            throws IOException {
        // Abro los ficheros
        FileOutputStream out = new FileOutputStream(destFile);

        try {
            // Transpaso de informacion
            byte[] data = new byte[moveBufferSize];
            int readSize;

            while ((readSize = in.read(data)) != -1) out.write(data, 0, readSize);
        } finally {
            // Cierro los ficheros
            in.close();
            out.close();
        }
    }

    /**
     * Creates the directory named by a pathname, if it doesn't exists,
     * including any necessary but nonexistent parent directories.
     *
     * @param dirName Directory pathname
     * @return Directory name.
     */
    public static String createDir(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            file.mkdirs();
        }

        return dirName;
    }

    /**
     * Check if a directory exists and is a directory.
     *
     * @param path the path of the directory to be checked.
     * @return true if the directory exists or false otherwise.
     */
    public static boolean isDirectory(String path) {
        return new File(path).isDirectory();
    }

    /**
     * Gets a non existing filename in the directory passed as
     * a parameter. The file name is a long number
     *
     * @param filePath The path where is going to be placed the file.
     * @return a non existing filename. is a long passed as a String.
     */
    public static String getTemporalFileName(String filePath) {
        String id;
        boolean exists = false;
        File tmpFile;

        do {
            id = new Long(System.currentTimeMillis()).toString();
            tmpFile = new File(filePath + id);
            exists = tmpFile.exists();
        } while (exists);

        return id;
    }

    /**
     * Changes File.separator into a Windows path to Unix path and vice versa.
     *
     * @param p String which contains the path
     * @return string with the transformed path
     */
    public static String changeSeparator(String p) {
        int indexS = p.indexOf('\\');

        if (indexS > -1) {
            if (File.separatorChar != '\\') {
                p = p.replace('\\', File.separatorChar);
            }
        }

        indexS = p.indexOf('/');
        if (indexS > -1) {
            if (File.separatorChar != '/') {
                p = p.replace('/', File.separatorChar);
            }
        }
        return p;
    }
}
