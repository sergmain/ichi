package ichi;

import org.apache.commons.io.LineIterator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DictIndex {

    public static class Level {
        int maxDepth;
        char ch;
        Level[] levels = new Level['Z' - 'A' + 1];

        public Level(char ch) {
            this.ch = ch;
        }

        public int add(String line, int offset) {
            if (offset >= line.length()) {
                return offset;
            }

            char c;
            try {
                c = line.charAt(offset);
            } catch (StringIndexOutOfBoundsException e) {
                throw e;
            }
            Level level = levels[c - 'A'];
            if (level==null) {
                level = new Level(c);
                levels[c - 'A'] = level;
            }
            int depth = level.add(line, offset + 1);
            if (maxDepth<depth) {
                maxDepth = depth;
            }
            return depth;
        }

        @Override
        public String toString() {
            return "Level{" +
                    "maxDepth=" + maxDepth +
                    ", ch=" + ch +
                    '}';
        }
    }

    Level[] topLevels = new Level['Z' - 'A' + 1];

    public DictIndex(InputStream is) {
        for (int i = 0; i < topLevels.length; i++) {
            topLevels[i] = new Level((char) ('A' + i));
        }

        LineIterator it = new LineIterator(new InputStreamReader(is, Charset.defaultCharset()));
        while (it.hasNext()) {
            String line = it.next();
            addTop(line);
        }
    }

    public void addTop(String line) {
        Level level = topLevels[line.charAt(0) - 'A'];
        level.add(line, 1);
    }

    public static class SubstringIdx {
        public int start;
        public int lenght;

        public SubstringIdx(int start, int lenght) {
            this.start = start;
            this.lenght = lenght;
        }

        public int getTotalLeght() {
            return start + lenght;
        }

        public String substring(String src) {
            return src.substring(start, start+lenght);
        }
    }

    private SubstringIdx find(String src, int start, int lenght) {
        if (start+lenght>src.length() ) {
            return null;
        }

        Level[] levels = topLevels;
        for (int i = 0; i < lenght; i++) {
            if (levels==null) {
                return null;
            }
            Level level;
            try {
                char c = src.charAt(start + i);
                if (c=='-') {
                    return null;
                }
                level = levels[c -'A'];
            } catch (StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException e) {
                throw e;
            }
            if (level==null) {
                return null;
            }
            levels = level.levels;
        }
        SubstringIdx temp = find(src, start, lenght+1);
        if (temp==null) {
            return new SubstringIdx(start, lenght);
        }
        return temp;
    }

    public String findString(String src) {

        SubstringIdx result = null;
        for (int i = 0; i < src.length(); i++) {
            SubstringIdx resultIdx = find(src, i, 1);
            if (resultIdx==null) {
                continue;
            }
            if (resultIdx.getTotalLeght() > src.length()) {
                return resultIdx.substring(src);
            }
            if (result==null || result.lenght <resultIdx.lenght) {
                result = resultIdx;
            }
        }
        return result!=null ? result.substring(src) : null;
    }

    public static void main(String[] args) throws IOException {
//        FileInputStream fis = new FileInputStream("docs/dict-short.txt");
        FileInputStream fis = new FileInputStream("docs/dict.txt");
//        FileInputStream fis = new FileInputStream("file:///./docs/dict.txt");
        DictIndex dictIndex = new DictIndex(fis);
        fis.close();

        fis = new FileInputStream("docs/data.txt");
//        fis = new FileInputStream("docs/data-short.txt");
        LineIterator it = new LineIterator(new InputStreamReader(fis, Charset.defaultCharset()));
        while (it.hasNext()) {
            String line = it.next();
            if (line.isEmpty()) {
                continue;
            }
            System.out.println( String.format("%-20s, %s", line, dictIndex.findString(line)));
        }
    }
}
