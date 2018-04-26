import java.util.*;
import java.util.stream.*;

import static java.lang.System.*;
import static java.lang.Math.*;

public class Solution {
    final static int MAX = 4096;

    public static void main(String[] args) {
        Scanner sc = new Scanner(in);
        int[] top = IntStream.generate(sc::nextInt).limit(4).sorted().toArray();

        int[][] ab = new int[top[1] + 1][];
        ab[0] = new int[MAX];
        for (int b = 1; b <= top[1]; ++b) {
            ab[b] = ab[b - 1].clone();
            for (int a = min(top[0], b); a > 0; --a)
                ++ab[b][a ^ b];
        }
        long result = 0;
        for (int c = 1; c <= top[2]; ++c) {
            int aMax = min(top[0], c), bMax = min(top[1], c);
            result += (aMax * bMax - aMax * (aMax - 1) / 2) * (top[3] - c + 1);
            for (int d = c; d <= top[3]; ++d)
                result -= ab[bMax][c ^ d];
        }
        out.println(result);
    }
}
