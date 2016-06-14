package ichi;

import org.apache.commons.io.LineIterator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.TreeSet;

public class DictStat {

    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("docs/dict.txt");

        LineIterator it = new LineIterator(new InputStreamReader(fis, Charset.defaultCharset()));
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int count2char = 0;
        TreeSet<Character> set = new TreeSet<>();
        while (it.hasNext()) {
            String line = it.next();

            if (line.length()>max) max = line.length();
            if (line.length()<min) min = line.length();
            if (line.length()==2) count2char++;

            for (int i = 0; i < line.length(); i++) {
                set.add(line.charAt(i));
            }
        }
        fis.close();

        System.out.println("min = " + min);
        System.out.println("max = " + max);
        System.out.println("count2char = " + count2char);
        System.out.println("set size:"  + set.size());
        for (Character character : set) {
            System.out.println(character);
        }
        System.out.println("size: " + (set.last() - set.first() + 1));
    }


}
