package utils;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Class for testing yml file reading */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class YmlFileReaderTest {

    /** Instance of YmlFileReader class */
    private static YmlFileReader reader;

    /** Initialising class */
    @BeforeAll
    public static void setYmlFileReader() {
        reader = new YmlFileReader();
    }

    /** Freeing up resources */
    @AfterAll
    public static void tearDown() {
        reader = null;
    }

    /** Tests reading from file with correct path. Expected value must be the same as value in file */
    @Test
    @Order(1)
    public void testCorrectReading() {
        assertEquals("1", reader.getStringValueFromFile("percentagePerMonth"));
    }

    /** Tests reading from file with incorrect path */
    @Test
    @Order(2)
    public void testIncorrectReading() {
        reader.setPath(reader.getPath() + "1");
        assertNull(reader.getStringValueFromFile("percentagePerMonth"));
    }
}
