package ichi;

import org.apache.commons.io.LineIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class DbFilter {

    public static class DbSortedList {
        public final int nameLength;
        public LinkedList<IchiBean> list = new LinkedList<>();

        public DbSortedList(int nameLength) {
            this.nameLength = nameLength;
        }
    }

    public static class DbSorter {
        int currentsize = 0;
        final int targetSize;

        public List<DbSortedList> list = new ArrayList<>();

        public DbSorter(int targetSize) {
            this.targetSize = targetSize;
        }

        public void put(IchiBean bean) {
            int len = bean.substring.length();
            DbSortedList tmp = null;
            for (DbSortedList dbSortedList : list) {
                if (dbSortedList.nameLength==len) {
                    tmp = dbSortedList;
                }
            }
            if (tmp==null) {
                tmp = new DbSortedList(len);
                list.add(tmp);
            }
            tmp.list.add(bean);

            if (currentsize==targetSize) {
                DbSortedList minLengthList = null;
                for (DbSortedList dbSortedList : list) {
                    if (dbSortedList.list.isEmpty()) {
                        continue;
                    }
                    if (minLengthList==null) {
                        minLengthList = dbSortedList;
                    }
                    else {
                        if (minLengthList.nameLength > dbSortedList.nameLength) {
                            minLengthList = dbSortedList;
                        }
                    }
                }

                if (minLengthList==null) {
                    throw new IllegalStateException("minLengthList is null");
                }
                try {
                    minLengthList.list.removeLast();
                } catch (NoSuchElementException e) {
                    throw e;
                }
            }
            else {
                currentsize++;
            }
        }
    }

    private final static Comparator<DbSortedList> DB_SORTED_LIST_COMPARATOR = new Comparator<DbSortedList>() {
        @Override
        public int compare(DbSortedList t1, DbSortedList t2) {
            return Integer.compare(t2.nameLength, t1.nameLength);
        }
    };

    public static List<IchiBean> find(IchiConfig config, DictIndex dictIndex) throws IOException {
        List<IchiBean> result = new ArrayList<>(10000);
        InputStream is = DbLoader.load(config);

        DbSorter dbSorter = new DbSorter(config.numberOfLines);
        LineIterator it = new LineIterator(new InputStreamReader(is, Charset.defaultCharset()));
        // skip fields' names
        String dump = it.next();
        while (it.hasNext()) {
            String line = it.next();
            if (line.isEmpty()) {
                continue;
            }

            IchiBean bean = create(line, dictIndex);
            if (bean==null) {
                continue;
            }
            dbSorter.put(bean);
        }
        is.close();
        Collections.sort(dbSorter.list, DB_SORTED_LIST_COMPARATOR);

        for (DbSortedList dbSortedList : dbSorter.list) {
            for (IchiBean ichiBean : dbSortedList.list) {
                result.add(ichiBean);
            }
        }
        return result;
    }

    private static IchiBean create(String line, DictIndex dictIndex) {
/*
        int idx = line.indexOf('\t');
        int i2 = line.indexOf('\t', idx+1);
        int i3 = line.indexOf('\t', i2+1);
        String key = line.substring(i3 + 1);
        IchiBean bean = new IchiBean(line.substring(0, idx), key.trim());
*/
        // for chembl_id
        int idx = line.indexOf('\t');
        if (idx == -1) {
            System.out.println("Broken line, 1st tab wasn't found: " + line + ", skip");
        }
        // for canonical_smiles
        int idxNext = line.indexOf('\t', idx + 1);
        if (idx == -1) {
            System.out.println("Broken line, 2nd tab wasn't found: " + line + ", skip");
        }
        //  standard_inchi
        idxNext = line.indexOf('\t', idxNext + 1);
        if (idx == -1) {
            System.out.println("Broken line, 3rd tab wasn't found: " + line + ", skip");
        }
        //  standard_inchi_key
        int keyIdx = line.indexOf('\t', idxNext + 1);

        String key;
        if (keyIdx==-1) {
            key = line.substring(idxNext + 1);
        }
        else {
            key = line.substring(idxNext + 1, keyIdx);
        }
        //key = key.trim();

        String substring;
        try {
            substring = dictIndex.findString(key);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw e;
        }
        return new IchiBean( line.substring(0, idx), key, substring);

    }


}
