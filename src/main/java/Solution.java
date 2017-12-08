import java.util.*;

import static java.lang.System.*;
import static java.lang.Math.*;

public class Solution {
    private static Scanner sc = new Scanner(in).useLocale(Locale.ROOT);
    private static Integer MAX_CHARS = 10;

    public static void main(String[] args) {
        char[] str = new char[50000];
        Arrays.fill(str, 'a');

        List<Long> times = new ArrayList<>();
        times.add(currentTimeMillis());

        int[] dist = new int[str.length];
        int[] prevCharPos = new int[MAX_CHARS];
        for (int i = 0; i < str.length; ++i) {
            int c = str[i] - 'a';
            dist[i] = i - prevCharPos[c] + 1;
            prevCharPos[c] = i + 1;
        }
        times.add(currentTimeMillis());

        Integer[] sa = new Integer[dist.length];
        for (int i = 0; i < sa.length; ++i)
            sa[i] = i;
        Arrays.sort(sa, (a, b) -> {
            for (int i = 0; a + i < dist.length && b + i < dist.length; ++i) {
                int diff = min(i, dist[a + i]) - min(i, dist[b + i]);
                if (diff != 0)
                    return diff;
            }
            return a - b;
        });
        times.add(currentTimeMillis());
        int[][] lcp = lcp(sa, dist);

        times.add(currentTimeMillis());

        for (int i = 0; i < times.size() - 1; ++i)
            out.printf("%.3f sec\n", (times.get(i + 1) - times.get(i)) / 1000.0);
    }

    private static int[][] lcp(Integer[] sa, int[] str) {
        int[] revSa = new int[sa.length], log = new int[sa.length], lcp1 = new int[sa.length-1];
        for (int i = 0; i < revSa.length; ++i) {
            revSa[sa[i]] = i;
            log[i] = log[i / 2] + 1;
        }
        for (int i = 0, curLcp = 0; i < str.length; ++i) {
            int sa = revSa[i];
            if (sa < lcp1.length) {

            }
        }


        int[][] lcp = new int[log[log.length - 1] + 1][];

        return lcp;
    }

    private static int getLco(int[][] lcp, int from, int to) {
        if (from == to)
            return lcp[0].length - from;
        int sa1 = min(lcp[0][from], lcp[0][to]);
        int sa2 = max(lcp[0][from], lcp[0][to]);
        int log = lcp[1][sa2 - sa1];
        return min(lcp[log][sa1], lcp[log][sa2 - 1 << (log - 2)]);

    }
}
