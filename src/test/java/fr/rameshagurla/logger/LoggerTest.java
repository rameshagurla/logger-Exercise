package fr.rameshagurla.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LoggerTest {

    private static Path output = new File("output.log").toPath();
    private static double javaVersion = Double.parseDouble(System.getProperty("java.specification.version"));
    private static String internalPackage = javaVersion <= 8 ? "sun" : "java.base/jdk.internal";

    @Test
    public void testLogException() {
        Logger.log(new Exception("custom exception"));
        verifyOutput(new String[]{
                "[SEVERE][Test-LoggerTest] java.lang.Exception: custom exception",
                "[SEVERE][Test-LoggerTest] \t fr.rameshagurla.logger.LoggerTest.testLogException(LoggerTest.java:27)",
                "[SEVERE][Test-LoggerTest] \t " + internalPackage + ".reflect.NativeMethodAccessorImpl.invoke0(Native Method)"
        });
    }

    @Test
    public void testLogExceptionMessage() {
        Logger.log(new Exception("custom exception"), "custom message");
        verifyOutput(new String[]{
                "[SEVERE][Test-LoggerTest] custom message: java.lang.Exception: custom exception",
                "[SEVERE][Test-LoggerTest] \t fr.rameshagurla.logger.LoggerTest.testLogExceptionMessage(LoggerTest.java:37)",
                "[SEVERE][Test-LoggerTest] \t " + internalPackage + ".reflect.NativeMethodAccessorImpl.invoke0(Native Method)"
        });
    }

    @Before
    public void setUp() throws IOException {
        if (Files.exists(output)) {
            PrintWriter writer = new PrintWriter(output.toFile());
            writer.print("");
            writer.close();
        }
        Logger.init("logging.properties");
    }

    @Test
    public void testLog() {
        Logger.log("message");
        verifyOutput(new String[]{
                "[INFO][Test-LoggerTest] message"
        });
    }

    @Test
    public void testLogCustomLevel() {
        Logger.log(Level.WARNING, "message");
        verifyOutput(new String[]{
                "[WARNING][Test-LoggerTest] message"
        });
    }

    @Test
    public void testSetLevel() {
        assertEquals(Level.INFO, Logger.getLevel());

        Logger.log(Level.INFO, "message1");
        Logger.log(Level.WARNING, "message2");

        Logger.setLevel(Level.WARNING);
        assertEquals(Level.WARNING, Logger.getLevel());

        Logger.log(Level.INFO, "message3");
        Logger.log(Level.WARNING, "message4");

        verifyOutput(new String[]{
                "[INFO][Test-LoggerTest] message1",
                "[WARNING][Test-LoggerTest] message2",
                "[WARNING][Test-LoggerTest] message4",
        });
    }

    private void verifyOutput(String[] expected) {
        try (FileReader freader = new FileReader(output.toFile())) {
            try (BufferedReader reader = new BufferedReader(freader)) {
                int i = 0;
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0) {
                        if (i >= expected.length) {
                            fail("Too much lines in output file");
                            return;
                        }
                        assertEquals(expected[i++], line);
                    }
                }
                if (i < expected.length)
                    fail("Not enough lines in output file");
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("error when accessing output file");
        }
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println("Java major version is " + javaVersion);
        System.out.println("internalPackage is " + internalPackage);
    }

}