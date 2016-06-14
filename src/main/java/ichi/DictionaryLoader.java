package ichi;

import ichi.exception.TerminateApplication;
import org.apache.http.client.fluent.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DictionaryLoader {

    public static InputStream load(IchiConfig config) throws IOException {
        InputStream is;
        File file;
        if (config.isHttpDictUrl) {

            String tempDirName = System.getProperty("java.io.tmpdir");

            File tempDir = new File(tempDirName);
            if (!tempDir.exists()) {
                throw new IllegalStateException("Temp dir defined in system property 'java.io.tmpdir' doesn't exist");
            }
            if (!tempDir.canWrite()) {
                throw new IllegalStateException("Temp dir defined in system property 'java.io.tmpdir' isn't writtable");
            }
            // createTempFile(String prefix, String suffix, File directory)
            file = File.createTempFile("ichi-dictionary-" + System.nanoTime(), ".txt", tempDir);
            try {
                Request.Get(config.dictUrl)
    //                    .viaProxy(new HttpHost("myproxy", 8080))
                        .execute().saveContent(file);
            } catch (Throwable e) {
                System.out.println("Error get dictionary file from " + config.dictUrl+". Error: " + e.getMessage());;
                throw new TerminateApplication();
            }

        } else {
            file = new File(config.dictUrl);
        }
        is = new FileInputStream(file);

        return is;
    }

}
