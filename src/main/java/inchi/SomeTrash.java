package inchi;

import org.apache.commons.io.LineIterator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: Serg
 * Date: 14.06.2016
 * Time: 18:21
 */
public class SomeTrash {

    private static Iterator<InchiBean> getIterator(DictIndex dictIndex) throws FileNotFoundException {
        List<InchiBean> list = new ArrayList<>();
        InputStream is = new FileInputStream("docs/data.txt");

        LineIterator it = new LineIterator(new InputStreamReader(is, Charset.defaultCharset()));
        while (it.hasNext()) {
            String line = it.next();
            if (line.isEmpty()) {
                continue;
            }
            list.add( new InchiBean("compound", line, dictIndex.findString(line)));
        }
        return list.iterator();
    }
}
