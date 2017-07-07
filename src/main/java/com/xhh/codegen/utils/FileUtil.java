package com.xhh.codegen.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




/**
 * 文件操作工具类
 * @author tengen
 */
public class FileUtil {
    private static final Log logger = LogFactory.getLog(FileUtil.class);

    public static final int BUFFER_SIZE = 4096;

    //---------------------------------------------------------------------
    // Copy methods for java.io.File
    //---------------------------------------------------------------------

    /**
     * 拷贝输入文件内容到输出文件
     *
     * @param in
     *            the file to copy from
     * @param out
     *            the file to copy to
     * @throws IOException
     *             in case of I/O errors
     */
    public static void copy(File in, File out) throws IOException {
        copy(new BufferedInputStream(new FileInputStream(in)),
                new BufferedOutputStream(new FileOutputStream(out)));
    }

    /**
     * 拷贝字节数组到文件
     *
     * @param in
     *            the byte array to copy from
     * @param out
     *            the file to copy to
     * @throws IOException
     *             in case of I/O errors
     */
    public static void copy(byte[] in, File out) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(in);
        OutputStream outStream = new BufferedOutputStream(new FileOutputStream(
                out));
        copy(inStream, outStream);
    }

    /**
     * 拷贝输入文件内容到新的字节数组
     *
     * @param in
     *            the file to copy from
     * @return the new byte array that has been copied to
     * @throws IOException
     *             in case of I/O errors
     */
    public static byte[] copyToByteArray(File in) throws IOException {
        return copyToByteArray(new BufferedInputStream(new FileInputStream(in)));
    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.InputStream / java.io.OutputStream
    //---------------------------------------------------------------------

    /**
     * 拷贝输入流到输出流，当完成后关闭流。
     *
     * @param in
     *            the stream to copy from
     * @param out
     *            the stream to copy to
     * @throws IOException
     *             in case of I/O errors
     */
    public static void copy(InputStream in, OutputStream out)
            throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int nrOfBytes = -1;
            while ((nrOfBytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, nrOfBytes);
            }
            out.flush();
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                logger.warn("Could not close InputStream", ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.warn("Could not close OutputStream", ex);
            }
        }
    }

    /**
     * 拷贝字节数组到输出流，完成后关闭流。
     *
     * @param in
     *            the byte array to copy from
     * @param out
     *            the OutputStream to copy to
     * @throws IOException
     *             in case of I/O errors
     */
    public static void copy(byte[] in, OutputStream out) throws IOException {
        try {
            out.write(in);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                logger.warn("Could not close OutputStream", ex);
            }
        }
    }

    /**
     * 拷贝输入流内容到字节数组,完成后关闭流。
     *
     * @param in
     *            the stream to copy from
     * @return the new byte array that has been copied to
     * @throws IOException
     *             in case of I/O errors
     */
    public static byte[] copyToByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.Reader / java.io.Writer
    //---------------------------------------------------------------------

    /**
     * 拷贝Reader内容到Writer
     *
     * @param in
     *            the Reader to copy from
     * @param out
     *            the Writer to copy to
     * @throws IOException
     *             in case of I/O errors
     */
    public static void copy(Reader in, Writer out) throws IOException {
        try {
            char[] buffer = new char[BUFFER_SIZE];
            int nrOfBytes = -1;
            while ((nrOfBytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, nrOfBytes);
            }
            out.flush();
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                logger.warn("Could not close Reader", ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.warn("Could not close Writer", ex);
            }
        }
    }

    /**
     * 拷贝字符串内容到Writer
     *
     * @param in
     *            the String to copy from
     * @param out
     *            the Writer to copy to
     * @throws IOException
     *             in case of I/O errors
     */
    public static void copy(String in, Writer out) throws IOException {
        try {
            out.write(in);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                logger.warn("Could not close Writer", ex);
            }
        }
    }

    /**
     * 将Reader内容转换成字符串
     *
     * @param in
     *            the reader to copy from
     * @throws IOException
     *             in case of I/O errors
     */
    public static String copyToString(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }

    public static String copyToString(String filePath) throws IOException {
        BufferedReader is = new BufferedReader(new FileReader(filePath));
        return copyToString(is);
    }

    public static String copyToString(String filePath,String uniCode) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filePath));
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(fis, uniCode));
        StringBuffer strBuffer = new StringBuffer();
        String tmpstr="";
        while((tmpstr=buffReader.readLine())!=null) {
            strBuffer.append(tmpstr);
            strBuffer.append("\n");
        }
        buffReader.close();
        fis.close();
        return strBuffer.toString();
    }

    /**
     * 关联路径下的文件, 如果存在则返回该文件,如果文件不存在，将创建该文件。
     *
     * @param directory
     * @param fileName
     */
    public static File assignFile(String directory, String fileName) {
        // 关联(建立)文件路径
        File dir = new File(directory);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                logger.warn("Can't create directory : " + directory);
            }
        } else if (!dir.isDirectory()) {
            logger.warn(directory + "is not a directory");
        }
        // 关联(建立)文件
        File _file = new File(dir, fileName);
        try {
            if (!_file.exists()) {
                logger.warn("file [" + fileName
                        + "] not found, create the file.");
                _file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _file;
    }

    /**
     * 关联路径下的文件, 如果存在则返回该文件,如果文件不存在，将创建该文件。
     */
    public static File assignFile(File file) {
        String path = file.getPath();
        String dir = path.substring(0, path.lastIndexOf(File.separator));
        String name = path.substring(path.lastIndexOf(File.separator) + 1, path
                .length());
        return assignFile(dir, name);
    }

    /**
     * <h3>将文件或路径改为小写</h3>
     * 参数renameDirectory为真时可改变 文件夹的名称为小写
     *
     * @param filepath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void lowerCaseFile(File file, boolean renameDirectory)
            throws FileNotFoundException, IOException {

        if (file.isFile()) {
            /*
             * 如果是文件，改为小写
             */
            String lowerNameFilePath = file.getParent() + File.separator
                    + file.getName().toLowerCase();
            File lowerFile = new File(lowerNameFilePath);
            file.renameTo(lowerFile);
        } else if (file.isDirectory()) {
            /*
             * 如果是路径，并且renameDirectory为true， 改写路径名称为小写，否则发出警告
             */
            if (renameDirectory) {
                String lowerNameDir = file.getParent() + File.separator
                        + file.getName().toLowerCase();
                File lowerDir = new File(lowerNameDir);
                file.renameTo(lowerDir);
            }
        }
    }

    /**
     * <h3>将指定路径的文件或路径改为小写</h3>
     * 如果renameDirectory == true 才可以改写路径
     *
     * @param filepath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void lowerCaseFile(String nFile, boolean renameDirectory)
            throws FileNotFoundException, IOException {

        File file = new File(nFile);
        lowerCaseFile(file, renameDirectory);
    }

    /**
     * 将该文件夹下的所有文件该为小写 如果renameDirectory为true将路径也改为小写
     *
     * @param dir
     * @param renameDirectory
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void lowerCaseDirectory(File dir, boolean renameDirectory)
            throws FileNotFoundException, IOException {

        if ((dir == null) || !dir.isDirectory()) {
            return;
        }

        File[] entries = dir.listFiles();
        int sz = entries.length;
        for (int i = 0; i < sz; i++) {
            if (entries[i].isDirectory()) {
                lowerCaseDirectory(entries[i], renameDirectory);
            } else {
                lowerCaseFile(entries[i], renameDirectory);
            }
        }
        lowerCaseFile(dir, renameDirectory);
    }

    /**
     * 删除目录
     *
     * @param file
     * @throws IOException
     */
    public static void deleteDirectory(File file) {
        if ((file == null) || !file.isDirectory()) {
            return;
        }
        File[] entries = file.listFiles();
        int sz = entries.length;
        for (int i = 0; i < sz; i++) {
            if (entries[i].isDirectory()) {
                deleteDirectory(entries[i]);
            } else {
                entries[i].delete();
            }
        }
        file.delete();
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }

    /**
     * 新建目录
     *
     * @param folderPath
     * @return boolean
     */
    public static void newFolder(String folderPath) {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.mkdir();
            }
        } catch (Exception e) {
            logger.warn("Could not create new folder ", e);
        }
    }

    /**
     * 新建文件
     *
     * @param filePathAndName
     *            String 文件路径及名称 如c:/moumoulrc.txt
     * @param fileContent
     *            String 文件内容
     * @return boolean
     */
    public static void newFile(String filePathAndName, String fileContent) {
        try {
            File myFilePath = new File(filePathAndName);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileWriter resultFile = new FileWriter(myFilePath);
            PrintWriter myFile = new PrintWriter(resultFile);
            myFile.println(fileContent);
            resultFile.close();
        } catch (IOException e) {
            logger.warn("Could not create new file ", e);
        }
    }

    public static void newFile(String filePathAndName, String fileContent,String uniCode) {
        try {
            File myFilePath = new File(filePathAndName);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(myFilePath);
            OutputStreamWriter osw = new OutputStreamWriter(fos,uniCode);
            osw.write(fileContent);
            //PrintWriter myFile = new PrintWriter(osw);
            //myFile.println(fileContent);
            osw.close();
            fos.close();
        } catch (IOException e) {
            logger.warn("Could not create new file ", e);
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath
     *            String 原文件路径 如：c:/moumoulrc.txt
     * @param newPath
     *            String 复制后路径 如：f:/moumo
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream input = new FileInputStream(oldPath);
                FileOutputStream output = new FileOutputStream(newPath);
                copy(input, output);
            }
        } catch (Exception e) {
            logger.warn("Could not copy file ", e);
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath
     *            String 原文件路径 如：c:/fqf
     * @param newPath
     *            String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs();
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not copy folder ", e);

        }

    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath
     * @param newPath
     */
    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        deleteDirectory(new File(oldPath));
    }

    /**
     * 移动文件夹到指定目录
     *
     * @param oldPath
     * @param newPath
     */
    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        deleteDirectory(new File(oldPath));
    }

}
