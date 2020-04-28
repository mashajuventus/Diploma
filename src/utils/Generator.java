package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter(new File("polygons"))) {
            int sum = 20;
            int cnt = 4;
            int alr = 0;
            List<Integer> sizes = new ArrayList<>();
            for (int i = 0; i < cnt - 1; i++) {
                int n = 2 * (Math.abs(new Random(i + 100).nextInt()) % 4) + 2;
                alr += n;
                sizes.add(n);
            }
            if (sum - alr > 2) {
                sizes.add(sum - alr);
            } else {
                sizes.add(4);
            }

            int odds = 0;
            for (int s : sizes) {
                if (s % 2 == 1) {
                    odds++;
                }
            }
            int k;
            if (odds % 2 == 1) {
                k = Math.abs(new Random(9).nextInt()) % 5 * 2 + 3;
                sizes.add(k);
            } else {
                k = 0;
            }
            System.out.println(sizes);

            List<Integer> ids = new ArrayList<>();
            List<Integer> edg = new ArrayList<>();
            for (int i = 0; i < sizes.size(); i++) {
                int s = sizes.get(i);
                for (int j = 0; j < s; j++) {
                    ids.add(i);
                    edg.add(j);
                }
            }
            int ss = 0;
            for (int s : sizes) {
                ss += s;
            }

            writer.println(sizes.size());
            for (int s : sizes) {
                writer.print(s + " ");
            }
            writer.println();
            for (int i = 0; i < ss / 2; i++) {
                int ix = Math.abs(new Random(102).nextInt()) % ids.size();
                writer.print(ids.get(ix));
                writer.print(' ');
                writer.print(edg.get(ix));
                writer.print(' ');
                ids.remove(ix);
                edg.remove(ix);

                int iy = Math.abs(new Random(i * 190).nextInt()) % ids.size();
                writer.print(ids.get(iy));
                writer.print(' ');
                writer.print(edg.get(iy));
                writer.println();
                ids.remove(iy);
                edg.remove(iy);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
