import java.util.*;

import static java.lang.System.*;
import static java.lang.Math.*;

public class Solution {
    private static Scanner sc = new Scanner(in).useLocale(Locale.ROOT);
    private static Integer MAX_CHARS = 10;

    public static void main(String[] args) {
        sc.nextInt();
        int cases = sc.nextInt();

        char[] str = sc.next().toCharArray();

        List<Long> times = new ArrayList<>();
        times.add(currentTimeMillis());

        int[] dist = new int[str.length];
        int[] prevCharPos = new int[MAX_CHARS];
        for (int i = 0; i < str.length; ++i) {
            int c = str[i] - 'a';
            dist[i] = i - prevCharPos[c];
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
            return b - a;
        });
        times.add(currentTimeMillis());
        int[][] lcp = lcp(sa, dist);

        StringBuilder sb = new StringBuilder();
        while (cases-- > 0) {
            int pos = sc.nextInt() - 1;
            int length = sc.nextInt() - pos;
            sb.append(getLcpCount(sa, lcp, pos, length));
            sb.append("\n");
        }
        times.add(currentTimeMillis());

        out.println(sb.toString());

//        for (int i = 0; i < times.size() - 1; ++i)
//            out.printf("%.3f sec\n", (times.get(i + 1) - times.get(i)) / 1000.0);
    }

    private static int getLcpCount(Integer[] sa, int[][] lcp, int from, int length) {
        int saFrom = lcp[0][from];
        int min = 0, max = saFrom;
        while (min < max) {
            int cur = (min + max) / 2;
            if (getLcp(lcp, from, sa[cur]) < length)
                min = cur + 1;
            else
                max = cur;
        }
        int first = max;
        min = saFrom;
        max = sa.length;
        while (min < max) {
            int cur = (min + max) / 2;
            if (getLcp(lcp, from, sa[cur]) < length)
                max = cur;
            else
                min = cur + 1;
        }
        if (max == first) {
            out.printf("%d %d %d\n", from, length, saFrom);
        }
        return max - first;
    }

    private static int getLcp(int[][] lcp, int from, int to) {
        if (from == to)
            return lcp[0].length - from;
        int sa1 = min(lcp[0][from], lcp[0][to]);
        int sa2 = max(lcp[0][from], lcp[0][to]);
        int log = lcp[1][sa2 - sa1];
//out.printf("%d..%d = %d\n", from, to, min(lcp[log][sa1], lcp[log][sa2 - (1 << (log - 2))]));
        return min(lcp[log][sa1], lcp[log][sa2 - (1 << (log - 2))]);
    }

    private static int[][] lcp(Integer[] sa, int[] str) {
        int[] revSa = new int[sa.length], log = new int[sa.length], lcp1 = new int[sa.length - 1];
        for (int i = 0; i < revSa.length; ++i) {
            revSa[sa[i]] = i;
            log[i] = log[i / 2] + 1;
        }
        for (int i = 0, curLcp = 0; i < str.length; ++i) {
            int saPtr = revSa[i];
            if (saPtr != lcp1.length) {
                for (int j = sa[saPtr + 1], top = str.length - max(i, j); curLcp < top; ++curLcp)
                    if (min(curLcp, str[i + curLcp]) != min(curLcp, str[j + curLcp]))
                        break;
                lcp1[saPtr] = curLcp;
            }
            if (curLcp > 0)
                --curLcp;
        }
        int[][] lcp = new int[log[log.length - 1] + 1][];
        lcp[0] = revSa;
        lcp[1] = log;
        lcp[2] = lcp1;
        for (int i = 3; i < lcp.length; ++i) {
            lcp[i] = new int[lcp[i-1].length - (1 << (i - 3))];
            for (int j = 0; j < lcp[i].length; ++j)
                lcp[i][j] = min(lcp[i-1][j], lcp[i-1][j + (1 << (i - 3))]);
        }
        return lcp;
    }
}
