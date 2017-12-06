import java.util.*;

import static java.lang.System.*;
import static java.lang.Math.*;

public class Solution {
    private static Scanner sc = new Scanner(in).useLocale(Locale.ROOT);
    private static Integer MAX_CHARS = 10;

    public static void main(String[] args) {
        char[] str = new char[50000];
        Arrays.fill(str, 'a');

        Integer[] dist = new Integer[str.length];
        int[] prevCharPos = new int[MAX_CHARS];
        for (int i = 0; i < str.length; ++i) {
            int c = str[i] - 'a';
            dist[i] = i - prevCharPos[c] + 1;
            prevCharPos[c] = i + 1;
        }

        Arrays.sort(dist, (a, b) -> {
            for (int i = 0; a + i < dist.length && b + i < dist.length; ++i) {
                int diff = min(i, dist[a + i]) - min(i, dist[b + i]);
                if (diff != 0)
                    return diff;
            }
            return a - b;
        });

    }
}
