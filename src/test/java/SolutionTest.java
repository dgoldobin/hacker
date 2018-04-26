import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class SolutionTest {
    @Test
    public void testStdout() throws Exception {
        var buf = new ByteArrayOutputStream();
        System.setIn(getClass().getResourceAsStream("/input.txt"));
        System.setOut(new PrintStream(buf));

        Solution.main(null);

        Scanner got = new Scanner(buf.toString());
        Scanner expected = new Scanner(getClass().getResourceAsStream("/expected.txt"));
        int idx = 0;
        while (got.hasNext() && expected.hasNext()) {
            assertEquals(String.format("Item at postition %d", ++idx), expected.next(), got.next());
        }
        assertEquals("EOF", !expected.hasNext(), !got.hasNext());
    }
}