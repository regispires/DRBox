package helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {
    private static Logger log = Logger.getLogger(FileUtils.class.getName());
    
    
    public static void writeFile(File file, byte[] bytes) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    public static Properties loadProperties(String fileName) {
        Properties p = new Properties();
        File f = new File(fileName);
        try {
            FileInputStream fin = new FileInputStream(f);
            p.load(fin);
            log.fine(fileName + " loaded\n" + Utils.propertiesToStr(p));
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            log.log(Level.WARNING,e.getMessage(),e);
        }
        return p;
    }
    
    public static void storeProperties(String fileName, Properties p) {
        try {
            File f = new File(fileName);
            FileOutputStream fout = new FileOutputStream(f);
            p.store(fout, fileName + " file properties");
            log.fine(fileName + " stored\n" + Utils.propertiesToStr(p));
        } catch (Exception e) {
            log.log(Level.WARNING,e.getMessage(),e);
        }
        
    }
    
    /*
    public static void copy(String source, String target) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        
        if (! sourceFile.isFile())
            throw new OperationNotCompletedException("Source file must be a file.");
        
        
    }
    */
}
