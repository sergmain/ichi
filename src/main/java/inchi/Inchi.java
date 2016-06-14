package inchi;

import inchi.exception.TerminateApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class Inchi {

    public static void main(String[] args) throws IOException {

        try {
            InchiConfig config = new InchiConfig();
            if (!config.init(args)) {
                return;
            }
            config.printCurrentSettings();

            System.out.print("Load dictionary and build index ... ");
            InputStream is = DictionaryLoader.load(config);
            DictIndex dictIndex = new DictIndex(is);
            is.close();
            System.out.println("Done.");

            Iterator<InchiBean> it;
            List<InchiBean> beans = DbFilter.find(config, dictIndex);
            it = beans.iterator();

//          it = inchi.SomeTrash.getIterator();
            System.out.println("\n\n");
            while (it.hasNext()) {
                InchiBean inchiBean = it.next();
                System.out.println( String.format("%s, %s, %s", inchiBean.key, dictIndex.findString(inchiBean.key), inchiBean.compound ));
            }
        } catch (TerminateApplication e) {
            // do nothing
        }
    }

}
