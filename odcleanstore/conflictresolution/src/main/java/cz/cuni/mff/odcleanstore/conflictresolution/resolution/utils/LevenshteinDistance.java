package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

/**
 * Implementation of a K-bounded (and normalized K-bounded) Levenshtein distance between two strings.
 * @author Jan Michelfeit
 */
public final class LevenshteinDistance {
    /** Normalization factor for Levenshtein distance - strings with Levenshtein distance >= MAX_DISTANCE is always 1. */
    public static final int MAX_DISTANCE = 128;

    /**
     * Calculates Levenshtein distance normalized to range [0,1].
     * Normalized distance for strings with Levenshtein distance >= MAX_DISTANCE is always 1
     * @param s1 the fist string to compare
     * @param s2 the second string to compare
     * @return Levenshtein distance between s1 and s2 normalized to range [0,1]
     */
    public static double normalizedLevenshteinDistance(CharSequence s1, CharSequence s2) {
        int distance = levenshteinDistanceK(s1, s2);
        double normalizationCoef = Math.min(
                MAX_DISTANCE,
                (s1.length() + s2.length()) / 2);
        double result = distance / normalizationCoef;
        return Math.min(result, 1);
    }

    /**
     * K-bounded Levenshtein distance with threshold {@link MAX_DISTANCE} (maximum allowed
     * distance).
     *
     * Algorithm was adapted from
     * https://github.com/jmcejuela/Levenshtein-MySQL-UDF/blob/master/levenshtein.c#L269
     * with kind permission of its author Juan Miguel Cejuela.
     *
     * Time O(MAX_DISTANCE * L) where L = min(s1.length(), s2.length());
     * space O(MAX_DISTANCE), constant.
     * 
     * @param s1 the first string to compare
     * @param s2 the second string to compare
     * @return levenshtein distance between s1 and s2, or {@link #MAX_DISTANCE} if the distance is
     *         >= MAX_DISTANCE
     */
    public static int levenshteinDistanceK(CharSequence s1, CharSequence s2) {
        CharSequence str1;
        CharSequence str2;
        // Order the strings so that the first one is the shorter one
        if (s1.length() <= s2.length()) {
            str1 = s1;
            str2 = s2;
        } else {
            str1 = s2;
            str2 = s1;
        }
        int length1 = str1.length();
        int length2 = str2.length();
        int lengthDifference = length2 - length1;
        // the original algorithm contains MAX_DISTANCE + 1, but this shouldn't break anything
        int ignore = MAX_DISTANCE;

        if (length1 == 0) {
            return length2 > MAX_DISTANCE ? ignore : length2;
        } else if (length2 == 0) {
            return length1 > MAX_DISTANCE ? ignore : length1;
        } else if (lengthDifference > MAX_DISTANCE) {
            return ignore;
        }

        // left space for insertions
        final int lSize =
                (((MAX_DISTANCE > length2) ? length2 : MAX_DISTANCE) - lengthDifference) / 2;
        final int rSize = lSize + lengthDifference;
        final int stripSize = lSize + rSize + 1; // + 1 for the diagonal cell
        final int stripSizem1 = stripSize - 1; // see later, not to repeat calculations

        int[] distance = new int[2 * stripSize]; // i.e. size 2*(min(MAX_DISTANCE, length2) + 1)

        /* Initialization */
        for (int i = lSize; i < stripSize; i++) { // start from diagonal cell
            distance[i] = i - lSize;
        }

        /* Recurrence */
        int currentRow = stripSize;
        int lastRow = 0;

        // j index for virtual recurrence matrix, jv index for rows
        // bl & br = left & right bounds for j
        int bl, br;
        int im1 = 0, jm1;
        for (int i = 1; i <= length1; i++) {
            int jv;
            // bl = max(i - lsize, 0)
            bl = i - lSize;
            if (bl < 0) {
                jv = Math.abs(bl); // no space for all allowed insertions
                bl = 0;
            } else {
                jv = 0;
            }

            // br = min(i + rsize, m)
            br = i + rSize;
            if (br > length2) {
                br = length2;
            }
            jm1 = bl - 1;
            for (int j = bl; j <= br; j++) {
                if (j == 0) { // postponed part of initialization
                    distance[currentRow + jv] = i;
                } else { // By observation 3, the indices change for the lastrow (always +1)
                    if (str1.charAt(im1) == str2.charAt(jm1)) {
                        distance[currentRow + jv] = distance[lastRow + jv];
                    } else {
                        // get the minimum of these 3 operations
                        int a = (0 == jv) ? ignore : distance[currentRow + jv - 1]; // deletion
                        int b = (stripSizem1 == jv) ? ignore : distance[lastRow + jv + 1]; // insert
                        int c = distance[lastRow + jv]; // substitution
                        int min = a;
                        if (b < min) {
                            min = b;
                        }
                        if (c < min) {
                            min = c;
                        }
                        distance[currentRow + jv] = min + 1;
                    }
                }
                jv++;
                jm1 = j;
            }

            // obsv: the cost of a following diagonal never decreases
            if (distance[currentRow + lSize + lengthDifference] > MAX_DISTANCE) {
                return ignore;
            }

            im1 = i;

            // swap
            currentRow = currentRow ^ stripSize;
            lastRow = lastRow ^ stripSize;
        }

        // only here if levenshtein(str1, str2) <= MAX_DISTANCE
        return distance[lastRow + lSize + lengthDifference]; // distance[length1, length2]
    }

    /** Disable constructor for a utility class. */
    private LevenshteinDistance() {
    }
}
