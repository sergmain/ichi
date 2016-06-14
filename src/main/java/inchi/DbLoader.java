package inchi;

import inchi.exception.TerminateApplication;
import org.apache.http.client.fluent.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class DbLoader {

    public static final String INCHI_FILE_NAME = "chembl_21_chemreps-215162561526.txt.gz";

    public static InputStream load(InchiConfig config) throws IOException {
        InputStream is;
        File file;
        if (config.isHttpDbUrl) {

            String tempDirName = System.getProperty("java.io.tmpdir");

            File tempDir = new File(tempDirName);
            if (!tempDir.exists()) {
                throw new IllegalStateException("Temp dir defined in system property 'java.io.tmpdir' doesn't exist");
            }
            if (!tempDir.canWrite()) {
                throw new IllegalStateException("Temp dir defined in system property 'java.io.tmpdir' isn't writtable");
            }
            file = new File(tempDir, INCHI_FILE_NAME);
            boolean isLoadFromWeb = true;
            if (file.exists()) {
                if (config.isReloadDb) {
                    try {
                        file.delete();
                    } catch (Throwable th) {
                        System.out.print("Can delete cached file " + file.getName() + ". Will be used new temporary file.");
                        // createTempFile(String prefix, String suffix, File directory)
                        file = File.createTempFile("inchi-" + System.nanoTime(), ".gz", tempDir);
                    }
                } else {
                    System.out.println("Cached file " + file.getAbsolutePath() + " already exists.");
                    isLoadFromWeb = false;
                }
            }
            if (isLoadFromWeb) {
                try {
                    System.out.println("Load db ...");
                    Request.Get(config.dbUrl)
    //                    .viaProxy(new HttpHost("myproxy", 8080))
                            .execute().saveContent(file);
                } catch (Throwable e) {
                    System.out.println("Error get db file from " + config.dbUrl+". Error: " + e.getMessage());;
                    throw new TerminateApplication();
                }
            }

        } else {
            file = new File(config.dbUrl);
        }
        is = new GZIPInputStream(new FileInputStream(file));

        return is;
    }
}
