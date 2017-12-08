import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class SolutionTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        System.setIn(SolutionTest.class.getResourceAsStream("/input.txt"));
        System.setOut(new PrintStream(out));
    }

    @After
    public void tearDown() throws Exception {
        System.setIn(null);
        System.setOut(null);
    }

    @Test
    public void testMain() throws Exception {
        Solution.main(null);
        assertEquals("124`\r\n", out.toString());
    }
}