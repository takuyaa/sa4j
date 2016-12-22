package com.github.takuyaa.sa4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class CLI {
    public static void main (String[] args) {
        String impl = args[0];
        System.out.println("target implementation:" + impl);

        for (int i = 1; i < args.length; ++i) {
            System.out.print(args[i] + ": ");
            try {
                byte[] T = readFile(args[i]);

                long start = new Date().getTime();
                if ("sais".equals(impl)) {
                    int n = T.length;
                    int[] SA = new int[n];
                    new sais().suffixsort(T, SA, n);
                } else {
                    new SuffixArray(T);
                }
                long finish = new Date().getTime();

                System.out.println(((finish - start) / 1000.0) + " sec");

                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] readFile(String path) throws IOException {
        File f = new File(path);
        FileInputStream s = new FileInputStream(f);

        int n = (int) f.length();
        System.out.print(n + " bytes ... ");

        byte[] T = new byte[n];
        s.read(T);
        s.close();

        return T;
    }
}
