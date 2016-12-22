package com.github.takuyaa.sa4j;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.BitSet;

public class SuffixArrayTest {
    @Test
    public void testSuffixArrayWithSentinel() throws Exception {
        String S = "aababcabddabcab$";
        int[] SA = new SuffixArray(S).array;

        assertEquals(15, SA[0]);
        assertEquals( 0, SA[1]);
        assertEquals(13, SA[2]);
        assertEquals( 1, SA[3]);
        assertEquals(10, SA[4]);
        assertEquals( 3, SA[5]);
        assertEquals( 6, SA[6]);
        assertEquals(14, SA[7]);
        assertEquals( 2, SA[8]);
        assertEquals(11, SA[9]);
        assertEquals( 4, SA[10]);
        assertEquals( 7, SA[11]);
        assertEquals(12, SA[12]);
        assertEquals( 5, SA[13]);
        assertEquals( 9, SA[14]);
        assertEquals(8, SA[15]);
    }

    @Test
    public void testSuffixArrayWithoutSentinel() throws Exception {
        String S = "aababcabddabcab";
        int[] SA = new SuffixArray(S).array;

        assertEquals( 0, SA[0]);
        assertEquals(13, SA[1]);
        assertEquals( 1, SA[2]);
        assertEquals(10, SA[3]);
        assertEquals( 3, SA[4]);
        assertEquals( 6, SA[5]);
        assertEquals(14, SA[6]);
        assertEquals( 2, SA[7]);
        assertEquals(11, SA[8]);
        assertEquals( 4, SA[9]);
        assertEquals( 7, SA[10]);
        assertEquals(12, SA[11]);
        assertEquals( 5, SA[12]);
        assertEquals( 9, SA[13]);
        assertEquals( 8, SA[14]);
    }

    @Test
    public void testSuffixArrayWithoutSentinel2() throws Exception {
        String S = "abracadabra";
        int[] SA = new SuffixArray(S).array;

        assertEquals(10, SA[0]);
        assertEquals( 7, SA[1]);
        assertEquals( 0, SA[2]);
        assertEquals( 3, SA[3]);
        assertEquals( 5, SA[4]);
        assertEquals( 8, SA[5]);
        assertEquals( 1, SA[6]);
        assertEquals( 4, SA[7]);
        assertEquals( 6, SA[8]);
        assertEquals( 9, SA[9]);
        assertEquals( 2, SA[10]);
    }

    @Test
    public void testSuffixArray1by1LMS() throws Exception {
        String S = "cacacbcacab";
        int[] SA = new SuffixArray(S).array;
        isValidSA(S, SA, S.length());
    }

    @Test
    public void testSuffixArrayLongStringOfTwoCharacters() throws Exception {
        String S = "bbaabbaabbabbabbabbabbabbaabbaabbabbaabbaabbabbabbaabbaabbabbaabbabbabbaabbaabbaabbabbaabbaabbabbaabbaabbaabbabbabbabbabbaabbaabbabbaabbabba";
        int[] SA = new SuffixArray(S).array;
        isValidSA(S, SA, S.length());
    }

    @Test
    public void testSuffixArrayEmptyString() throws Exception {
        String S = "";
        int[] SA = new SuffixArray(S).array;
        assertEquals(0, SA.length);
    }

    @Test
    public void testSuffixArrayOneCharacterString() throws Exception {
        String S = "a";
        int[] SA = new SuffixArray(S).array;
        assertEquals(1, SA.length);
        assertEquals(0, SA[0]);
    }

    @Test
    public void testCountAndSetTypes() throws Exception {
        String T = "aababcabddabcab$";
        BaseArray S = new StringArray(T);
        int[] count = new int[Character.MAX_VALUE];
        BitSet isTypeS = new BitSet(T.length());
        BitSet isTypeLMS = new BitSet(T.length());
        SuffixArray.countAndSetTypes(S, count, isTypeS, isTypeLMS, T.length());

        assertEquals(1, count['$']);
        assertEquals(6, count['a']);
        assertEquals(5, count['b']);
        assertEquals(2, count['c']);
        assertEquals(2, count['d']);

        assertEquals( true, isTypeS.get(0));
        assertEquals( true, isTypeS.get(1));
        assertEquals(false, isTypeS.get(2));
        assertEquals( true, isTypeS.get(3));
        assertEquals( true, isTypeS.get(4));
        assertEquals(false, isTypeS.get(5));
        assertEquals( true, isTypeS.get(6));
        assertEquals( true, isTypeS.get(7));
        assertEquals(false, isTypeS.get(8));
        assertEquals(false, isTypeS.get(9));
        assertEquals( true, isTypeS.get(10));
        assertEquals( true, isTypeS.get(11));
        assertEquals(false, isTypeS.get(12));
        assertEquals( true, isTypeS.get(13));
        assertEquals(false, isTypeS.get(14));
        assertEquals(false, isTypeS.get(15));

        assertEquals(false, isTypeLMS.get(0));
        assertEquals(false, isTypeLMS.get(1));
        assertEquals(false, isTypeLMS.get(2));
        assertEquals( true, isTypeLMS.get(3));
        assertEquals(false, isTypeLMS.get(4));
        assertEquals(false, isTypeLMS.get(5));
        assertEquals( true, isTypeLMS.get(6));
        assertEquals(false, isTypeLMS.get(7));
        assertEquals(false, isTypeLMS.get(8));
        assertEquals(false, isTypeLMS.get(9));
        assertEquals( true, isTypeLMS.get(10));
        assertEquals(false, isTypeLMS.get(11));
        assertEquals(false, isTypeLMS.get(12));
        assertEquals( true, isTypeLMS.get(13));
        assertEquals(false, isTypeLMS.get(14));
        assertEquals(false, isTypeLMS.get(15));
    }

    @Test
    public void testGetBucketHeadPointersAlphabet() throws Exception {
        String S = "aababcabddabcab$";
        int[] count = getCount(S, Character.MAX_VALUE);
        int[] bucketPointHead = getbucketHeadPointers(count);
        assertEquals( 0, bucketPointHead['$']);
        assertEquals( 1, bucketPointHead['a']);
        assertEquals( 7, bucketPointHead['b']);
        assertEquals(12, bucketPointHead['c']);
        assertEquals(14, bucketPointHead['d']);
    }

    @Test
    public void testGetBucketHeadPointersHiragana() throws Exception {
        String S = "ああいあいうあいええあいうあい$";
        int[] count = getCount(S, Character.MAX_VALUE);
        int[] bucketPointHead = getbucketHeadPointers(count);
        assertEquals( 0, bucketPointHead['$']);
        assertEquals( 1, bucketPointHead['あ']);
        assertEquals( 7, bucketPointHead['い']);
        assertEquals(12, bucketPointHead['う']);
        assertEquals(14, bucketPointHead['え']);
    }

    @Test
    public void testGetBucketHeadPointersNoDollar() throws Exception {
        String S = "aababcabddabcab";
        int[] count = getCount(S, Character.MAX_VALUE);
        int[] bucketPointHead = getbucketHeadPointers(count);
        assertEquals( 0, bucketPointHead['a']);
        assertEquals( 6, bucketPointHead['b']);
        assertEquals(11, bucketPointHead['c']);
        assertEquals(13, bucketPointHead['d']);
    }

    @Test
    public void testGetBucketPointTailAlphabet() throws Exception {
        String S = "aababcabddabcab$";
        int[] count = getCount(S, Character.MAX_VALUE);
        int[] bucketPointTail = getbucketTailPointers(count);
        assertEquals( 0, bucketPointTail['$']);
        assertEquals( 6, bucketPointTail['a']);
        assertEquals(11, bucketPointTail['b']);
        assertEquals(13, bucketPointTail['c']);
        assertEquals(15, bucketPointTail['d']);
    }

    @Test
    public void testGetBucketTailPointersHiragana() throws Exception {
        String S = "ああいあいうあいええあいうあい$";
        int[] count = getCount(S, Character.MAX_VALUE);
        int[] bucketPointTail = getbucketTailPointers(count);
        assertEquals( 0, bucketPointTail['$']);
        assertEquals( 6, bucketPointTail['あ']);
        assertEquals(11, bucketPointTail['い']);
        assertEquals(13, bucketPointTail['う']);
        assertEquals(15, bucketPointTail['え']);
    }

    @Test
    public void testGetBucketTailPointersNoDollar() throws Exception {
        String S = "aababcabddabcab";
        int[] count = getCount(S, Character.MAX_VALUE);
        int[] bucketPointTail = getbucketTailPointers(count);
        assertEquals( 5, bucketPointTail['a']);
        assertEquals(10, bucketPointTail['b']);
        assertEquals(12, bucketPointTail['c']);
        assertEquals(14, bucketPointTail['d']);
    }

    @Test
    public void testSuffixArrayForSmallCorpus() throws Exception {
        byte[] T = readText("corpus/gauntlet/abac");

        int[] SAFromByteArray = new SuffixArray(T).array;
        isValidSA(T, SAFromByteArray, T.length);

        String S = new String(T);
        int[] SAFromString = new SuffixArray(S).array;
        isValidSA(S, SAFromString, S.length());

        assertArrayEquals(SAFromByteArray, SAFromString);
    }

    @Test
    public void testSuffixArrayForMediumCorpus1() throws Exception {
        byte[] T = readText("corpus/gauntlet/fss9");

        int[] SAFromByteArray = new SuffixArray(T).array;
        isValidSA(T, SAFromByteArray, T.length);

        String S = new String(T);
        int[] SAFromString = new SuffixArray(S).array;
        isValidSA(S, SAFromString, S.length());

        assertArrayEquals(SAFromByteArray, SAFromString);
    }

    @Test
    public void testSuffixArrayForMediumCorpus2() throws Exception {
        byte[] T = readText("corpus/gauntlet/houston");

        int[] SAFromByteArray = new SuffixArray(T).array;
        isValidSA(T, SAFromByteArray, T.length);

        String S = new String(T);
        int[] SAFromString = new SuffixArray(S).array;
        isValidSA(S, SAFromString, S.length());

        assertArrayEquals(SAFromByteArray, SAFromString);
    }

    @Test
    public void testSuffixArrayForMediumCorpus3() throws Exception {
        byte[] T = readText("corpus/gauntlet/paper5x80");

        int[] SAFromByteArray = new SuffixArray(T).array;
        isValidSA(T, SAFromByteArray, T.length);

        String S = new String(T);
        int[] SAFromString = new SuffixArray(S).array;
        isValidSA(S, SAFromString, S.length());

        assertArrayEquals(SAFromByteArray, SAFromString);
    }

    @Ignore
    public void testSuffixArrayForLargeCorpus() throws Exception {
        byte[] T = readText("corpus/gauntlet/abba");

        int[] SAFromByteArray = new SuffixArray(T).array;
        isValidSA(T, SAFromByteArray, T.length);

        String S = new String(T);
        int[] SAFromString = new SuffixArray(S).array;
        isValidSA(S, SAFromString, S.length());

        assertArrayEquals(SAFromByteArray, SAFromString);
    }

    @Test
    public void testSuffixArrayForProblematicBinary1() throws Exception {
        byte[] T = readText("corpus/gauntlet/test1");
        int[] SAFromByteArray = new SuffixArray(T).array;
        isValidSA(T, SAFromByteArray, T.length);
    }

    @Test
    public void testSuffixArrayForProblematicBinary2() throws Exception {
        byte[] T = readText("corpus/gauntlet/test2");
        int[] SAFromByteArray = new SuffixArray(T).array;
        isValidSA(T, SAFromByteArray, T.length);
    }

    @Test
    public void testSuffixArrayForProblematicBinary3() throws Exception {
        byte[] T = readText("corpus/gauntlet/test3");
        int[] SAFromByteArray = new SuffixArray(T).array;
        isValidSA(T, SAFromByteArray, T.length);
    }

    private byte[] readText(String path) throws Exception {
        File f = new File(path);
        FileInputStream s = new FileInputStream(f);
        int n = (int) f.length();
        byte[] T = new byte[n];
        s.read(T);
        s.close();
        return T;
    }

    private static boolean isValidSA(String S, int[] SA, int n) {
        int[] count = getCount(S, Character.MAX_VALUE);
        int[] bucketPointHead = getbucketHeadPointers(count);

        char lastChar = S.charAt(n - 1);
        int headPointer = bucketPointHead[lastChar]; // store head bucket pointer of last character
        bucketPointHead[lastChar] += 1; // considering virtual sentinel

        /*
          Check SA by First-Last Property (See also LF-mapping)
          L: Last character (BWT character)
          F: First character
         */
        for(int i = 0; i < n; i++) {
            int l = SA[i] > 0 ? SA[i] - 1 : n - 1; // considering virtual sentinel
            char L = S.charAt(l);

            int bucketPointer = SA[i] > 0 ? bucketPointHead[L] : headPointer; // considering virtual sentinel
            int f = SA[bucketPointer];
            char F = S.charAt(f);

            if (SA[i] > 0) { // considering virtual sentinel
                bucketPointHead[L] += 1;
            }

            assertEquals(f, l);
            assertEquals(F, L);
        }
        return true;
    }

    private static boolean isValidSA(byte[] S, int[] SA, int n) {
        int[] count = getCount(S, Character.MAX_VALUE);
        int[] bucketPointHead = getbucketHeadPointers(count);

        char lastChar = (char) S[n - 1];
        int headPointer = bucketPointHead[lastChar]; // store head bucket pointer of last character
        bucketPointHead[lastChar] += 1; // considering virtual sentinel

        /*
          Check SA by First-Last Property (See also LF-mapping)
          L: Last character (BWT character)
          F: First character
         */
        for(int i = 0; i < n; i++) {
            int l = SA[i] > 0 ? SA[i] - 1 : n - 1; // considering virtual sentinel
            char L = (char) S[l];

            int bucketPointer = SA[i] > 0 ? bucketPointHead[L] : headPointer; // considering virtual sentinel
            int f = SA[bucketPointer];
            char F = (char) S[f];

            if (SA[i] > 0) { // considering virtual sentinel
                bucketPointHead[L] += 1;
            }

            assertEquals(f, l);
            assertEquals(F, L);
        }
        return true;
    }

    private static int[] getCount(String S, int maxCh) {
        int[] count = new int[maxCh + 1];
        for (int i = S.length() - 1; i >= 0; i--) {
            char ch = S.charAt(i);
            count[ch]++;
        }
        return count;
    }

    private static int[] getCount(byte[] S, int maxCh) {
        int[] count = new int[maxCh + 1];
        for (int i = S.length - 1; i >= 0; i--) {
            char ch = (char) S[i];
            count[ch]++;
        }
        return count;
    }

    private static int[] getbucketHeadPointers(int[] count) {
        int sum = 0;
        int[] bucket = new int[count.length];
        for (int i = 0; i < count.length; i++) {
            sum += count[i];
            bucket[i] = sum - count[i];
        }
        return bucket;
    }

    private static int[] getbucketTailPointers(int[] count) {
        int sum = -1;
        int[] bucket = new int[count.length];
        for (int i = 0; i < count.length; i++) {
            sum += count[i];
            bucket[i] = sum;
        }
        return bucket;
    }
}
