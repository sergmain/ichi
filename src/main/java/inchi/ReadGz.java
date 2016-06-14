package inchi;

import org.apache.commons.io.LineIterator;

import java.io.*;
import java.nio.charset.Charset;

public class ReadGz {

    public static void main(String[] args) throws IOException {
        InchiConfig config = new InchiConfig();
        if (!config.init(args)) {
            return;
        }
        config.printCurrentSettings();

        InputStream is = DbLoader.load(config);
        LineIterator it = new LineIterator(new InputStreamReader(is, Charset.defaultCharset()));
        int lines=0;
        while (it.hasNext()) {
            String line = it.next();
            if (line.isEmpty()) {
                continue;
            }
            lines++;
        }
        System.out.println("lines = " + lines);

    }
}
