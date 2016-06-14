package ichi;

import ichi.exception.TerminateApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class Ichi {

    public static void main(String[] args) throws IOException {

        try {
            IchiConfig config = new IchiConfig();
            if (!config.init(args)) {
                return;
            }
            config.printCurrentSettings();

            System.out.print("Load dictionary and build index ... ");
            InputStream is = DictionaryLoader.load(config);
            DictIndex dictIndex = new DictIndex(is);
            is.close();
            System.out.println("Done.");

            Iterator<IchiBean> it;
            List<IchiBean> beans = DbFilter.find(config, dictIndex);
            it = beans.iterator();

//          it = ichi.SomeTrash.getIterator();
            System.out.println("\n\n");
            while (it.hasNext()) {
                IchiBean ichiBean = it.next();
                System.out.println( String.format("%s, %s, %s", ichiBean.key, dictIndex.findString(ichiBean.key), ichiBean.compound ));
            }
        } catch (TerminateApplication e) {
            // do nothing
        }
    }

}
