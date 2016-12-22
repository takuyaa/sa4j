package com.github.takuyaa.sa4j;

import java.util.Arrays;
import java.util.BitSet;

interface BaseArray {
    int get(int i);
    void set(int i, int value);
    void fill(int value);
}

class StringArray implements BaseArray {
    private String str;

    StringArray(String str) {
        this.str = str;
    }

    public int get(int i) {
        return str.charAt(i);
    }

    public void set(int i, int value) {
        throw new UnsupportedOperationException();
    }

    public void fill(int value) {
        throw new UnsupportedOperationException();
    }
}

class ByteArray implements BaseArray {
    private byte[] array;

    ByteArray(byte[] array) {
        this.array = array;
    }

    public int get(int i) {
        return array[i] & 0xff;
    }

    public void set(int i, int value) {
        throw new UnsupportedOperationException();
    }

    public void fill(int value) {
        throw new UnsupportedOperationException();
    }
}

class IntArray implements BaseArray {
    int[] array;

    IntArray(int[] array) {
        this.array = array;
    }

    public int[] getArray() {
        return array;
    }

    public int get(int i) {
        return array[i];
    }

    public void set(int i, int value) {
        array[i] = value;
    }

    public void fill(int value) {
        Arrays.fill(array, value);
    }

    public void fill(int to, int value) {
        throw new UnsupportedOperationException();
    }
}

class IntArrayWithOffset extends IntArray {
    private int offset;

    IntArrayWithOffset(int[] array, int offset) {
        super(array);
        this.offset = offset;
    }

    public int get(int i) {
        return array[offset + i];
    }

    public void set(int i, int value) {
        array[offset + i] = value;
    }

    public void fill(int to, int value) {
        Arrays.fill(array, offset, offset + to, value);
    }
}

public class SuffixArray {
    int[] array;

    public SuffixArray(String s) {
        int n = s.length();

        BaseArray S = new StringArray(s);

        int[] sa = new int[n];
        Arrays.fill(sa, -1);
        IntArray SA = new IntArray(sa);

        SAIS(S, SA, n, Character.MAX_VALUE);
        this.array = SA.getArray();
    }

    public SuffixArray(byte[] array) {
        int n = array.length;

        BaseArray S = new ByteArray(array);

        int[] sa = new int[n];
        Arrays.fill(sa, -1);
        IntArray SA = new IntArray(sa);

        SAIS(S, SA, n, Character.MAX_VALUE);
        this.array = SA.getArray();
    }

    public int[] getArray() {
        return array;
    }

    /**
     * Construct suffix array of input S and set to given array SA.
     * This method would be called recursively.
     * @param S input string
     * @param SA suffix array to construct (filled with minus values initially)
     * @param n length of S
     * @param k max character value
     */
    static void SAIS(BaseArray S, BaseArray SA, int n, int k) {
        if (n == 0) {
            return;
        }

        int[] count = new int[k + 1];     // takes 4 * k bytes of memory / call
        int[] pointers = new int[k + 1];  // takes 4 * k bytes of memory / call
        BitSet isTypeS = new BitSet(n);   // takes n bits of memory / call
        BitSet isTypeLMS = new BitSet(n); // takes n bits of memory / call

        int n1 = countAndSetTypes(S, count, isTypeS, isTypeLMS, n);

        // stage 1
        // prepare buckets (2 buckets takes 8 * k bytes of memory / call)
        int[] bucketHeadPointers = getbucketHeadPointers(count); // head index
        int[] bucketTailPointers = getbucketTailPointers(count); // tail index

        BaseArray P1 = new IntArray(new int[n1]); // takes at most 4 * n bytes of memory (total)

        sortLMSByFirstCharacter(S, SA, P1, isTypeLMS, bucketTailPointers, pointers, n, n1);
        sortTypeL(S, SA, isTypeS, bucketHeadPointers, pointers, n);
        sortTypeS(S, SA, isTypeS, bucketTailPointers, pointers, n);

        int[] LMS = new int[n1]; // takes at most 4 * n bytes of memory (total)
        extractSortedLMS(SA, LMS, isTypeLMS, n);

        BaseArray names = new IntArray(new int[(n + 1) / 2]); // takes at most 4 * n bytes of memory (total)

        int maxCh = nameLMSSubstrings(S, LMS, names, isTypeLMS, n, n1);

        // stage 2
        // check last 'name' whether names are unique or duplicated
        if (maxCh < n1) {
            // not unique LMS-Substrings

            BaseArray S1 = new IntArray(new int[n1]); // takes at most 4 * n bytes of memory (total)
            extractRenamedString(S1, names, n);

            BaseArray SA1 = new IntArray(new int[n1]); // takes at most 4 * n bytes of memory (total)
            SAIS(S1, SA1, n1, maxCh);

            for (int i = 0; i < n1; i++) {
                // TODO P1 のかわりに isTypeLMS を使う
                LMS[i] = P1.get(SA1.get(i)); // overwrite to reduce memory
            }
        }

        // stage 3
        // now, LMS is Type-LMS suffix array (LMSSA)
        // induce LMSSA to SA
        SA.fill(0);
        setTypeLMS(S, SA, LMS, bucketTailPointers, pointers, n1);
        sortTypeL(S, SA, isTypeS, bucketHeadPointers, pointers, n);
        sortTypeS(S, SA, isTypeS, bucketTailPointers, pointers, n);
    }

    /**
     * Iterate string S from last index to first.
     * This method have side-effects to return multiple arrays (count, isTypeS, isTypeLMS),
     * but don't require additional memory.
     * @param S input string
     * @param count empty array for storing count
     * @param isTypeS empty array for storing whether S-Type or not
     * @param isTypeLMS empty array for storing whether leftmost S-Type or not
     * @return number of LMS-Type characters
     */
    static int countAndSetTypes(BaseArray S, int[] count, BitSet isTypeS, BitSet isTypeLMS, int n) {
        // process last index of S
        int i = n - 1;
        int ch = S.get(i);

        count[ch]++;
        isTypeS.set(i, false); // last character is always Type-L considering virtual sentinel

        int countLMS = 0;
        int ch2;
        for (i = n - 2; i >= 0; i--) {
            ch2 = ch;
            ch = S.get(i);

            count[ch]++;
            isTypeS.set(i, ch < ch2 || (ch == ch2 && isTypeS.get(i + 1))); // ch < ch2 なら S-Type, 同じ文字なら次の文字と同じ Type

            // this condition is true when:
            //       [i] [i+1]
            // Type:  L    S
            if (!isTypeS.get(i) && isTypeS.get(i + 1)) {
                isTypeLMS.set(i + 1, true);
                countLMS++;
            }
        }
        return countLMS;
    }

    /**
     * Prepare head pointer bucket
     * @param count array of character count
     * @return pointers which points head of each character's bucket
     */
    private static int[] getbucketHeadPointers(int[] count) {
        int sum = 0;
        int[] bucket = new int[count.length];
        for (int i = 0; i < count.length; i++) {
            sum += count[i];
            bucket[i] = sum - count[i];
        }
        return bucket;
    }

    /**
     * Prepare tail pointer bucket
     * @param count array of character count
     * @return pointers which points tail of each character's bucket
     */
    private static int[] getbucketTailPointers(int[] count) {
        int sum = -1;
        int[] bucket = new int[count.length];
        for (int i = 0; i < count.length; i++) {
            sum += count[i];
            bucket[i] = sum;
        }
        return bucket;
    }

    /**
     * Sort LMS-Type by first character, and construct P1
     */
    private static void sortLMSByFirstCharacter(BaseArray S, BaseArray SA, BaseArray P1, BitSet isTypeLMS, int[] bucketTailPointers, int[] pointers, int n, int n1) {
        Arrays.fill(pointers, 0);
        int p1 = n1 - 1;
        for (int i = n - 1; i >= 0; i--) {
            if (isTypeLMS.get(i)) {
                int ch = S.get(i);
                int pointer = bucketTailPointers[ch] - pointers[ch];
                pointers[ch]++; assert SA.get(pointer) <= 0;
                SA.set(pointer, i);

                P1.set(p1--, i);
            }
        }
    }

    /**
     * Sort L-Type
     */
    private static void sortTypeL(BaseArray S, BaseArray SA, BitSet isTypeS, int[] bucketHeadPointers, int[] pointers, int n) {
        Arrays.fill(pointers, 0);

        // consider virtual sentinel
        // if sentinel exists, first we look at the character in front of sentinel
        {
            int ch = S.get(n - 1);
            int pointer = bucketHeadPointers[ch] + pointers[ch];
            pointers[ch]++;
            assert SA.get(pointer) <= 0;
            SA.set(pointer, n - 1);
        }

        for (int i = 0; i < n; i++) {
            if (SA.get(i) <= 0) {
                continue;
            }
            if (!isTypeS.get(SA.get(i) - 1)) {
                // L-Type
                int ch = S.get(SA.get(i) - 1);
                int pointer = bucketHeadPointers[ch] + pointers[ch];
                pointers[ch]++; assert SA.get(pointer) <= 0;
                SA.set(pointer, SA.get(i) - 1);
            }
        }
    }

    /**
     * Sort S-Type, and set to SA
     */
    private static void sortTypeS(BaseArray S, BaseArray SA, BitSet isTypeS, int[] bucketTailPointers, int[] pointers, int n) {
        Arrays.fill(pointers, 0);
        for (int i = n - 1; i >= 0; i--) {
            if (SA.get(i) <= 0) {
                continue;
            }
            if (isTypeS.get(SA.get(i) - 1)) {
                // S-Type
                int ch = S.get(SA.get(i) - 1);
                int pointer = bucketTailPointers[ch] - pointers[ch];
                pointers[ch]++;
                SA.set(pointer, SA.get(i) - 1);
            }
        }
    }

    /**
     * Extract sorted LMS from SA
     */
    private static void extractSortedLMS(BaseArray SA, int[] LMS, BitSet isTypeLMS, int n) {
        int lmsPointer = 0; // LMSSubstrings index to set
        for (int i = 0; i < n; i++) {
            if (isTypeLMS.get(SA.get(i))) {
                LMS[lmsPointer++] = SA.get(i);
            }
        }
    }

    /**
     * Set LMS-Type to SA
     */
    private static void setTypeLMS(BaseArray S, BaseArray SA, int[] lmsSubstrings, int[] bucketTailPointers, int[] pointers, int n1) {
        Arrays.fill(pointers, 0);
        for (int i = n1 - 1; i >= 0; i--) {
            int lms = lmsSubstrings[i];
            int ch = S.get(lms);
            int pointer = bucketTailPointers[ch] - pointers[ch];
            pointers[ch]++; // assert SA.get(pointer) <= 0;
            SA.set(pointer, lms);
        }
    }

    /**
     * Name all LMSSubstrings.
     * Same LMSSubstrings would have same names.
     * This method have side-effects to an array names, but don't require additional memory.
     * (return max name)
     */
    private static int nameLMSSubstrings(BaseArray S, int[] lmsSubstrings, BaseArray names, BitSet isTypeLMS, int n, int n1) {
        if (n1 == 0) {
            return 0;
        }

        int name = 1;
        if (n1 == 1) {
            names.set(0, name);
            return name;
        }

        names.set(lmsSubstrings[0] / 2, name);

        for (int i = 1; i < n1; i++) {
            int p0 = lmsSubstrings[i - 1];
            int p1 = lmsSubstrings[i];

            // compare first character
            int ch0 = S.get(p0);
            int ch1 = S.get(p1);
            if (ch0 != ch1) {
                names.set(lmsSubstrings[i] / 2, ++name);
                continue;
            }
            if (p0 == n - 1 ^ p1 == n - 1) {
                names.set(lmsSubstrings[i] / 2, ++name);
                continue;
            } else if (p0 == n - 1 /* && p1 == n - 1 */) {
                names.set(lmsSubstrings[i] / 2, name);
                continue;
            }
            p0++;
            p1++;

            // compare after second character
            while (true) {
                ch0 = S.get(p0);
                ch1 = S.get(p1);
                if (ch0 != ch1) {
                    name++;
                    break;
                }
                if (p0 == n - 1 ^ p1 == n - 1) {
                    name++;
                    break;
                } else if (p0 == n - 1 /* && p1 == n - 1 */) {
                    break;
                }
                if (isTypeLMS.get(p0) ^ isTypeLMS.get(p1)) {
                    name++;
                    break;
                } else if (isTypeLMS.get(p0) && isTypeLMS.get(p1)) {
                    break;
                }
                p0++;
                p1++;
            }
            names.set(lmsSubstrings[i] / 2, name);
        }
        return name;
    }

    /**
     * Extract S1 from names (sparse array)
     * @param S1 renamed string to return
     * @param n input size
     * @param names sparse array
     */
    private static void extractRenamedString(BaseArray S1, BaseArray names, int n) {
        int j = 0;
        for (int i = 0; i < n / 2; i++) {
            int name = names.get(i);
            if (name > 0) {
                S1.set(j++, name);
            }
        }
    }
}
