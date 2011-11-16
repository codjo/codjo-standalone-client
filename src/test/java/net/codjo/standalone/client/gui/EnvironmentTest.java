package net.codjo.standalone.client.gui;
import junit.framework.TestCase;
/**
 *
 */
public class EnvironmentTest extends TestCase {
    public void test_toEnumFailure() throws Exception {
        try {
            Environment.toEnum("n'importe koa");
            fail();
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Pas de correspondance pour 'n'importe koa'", ex.getLocalizedMessage());
        }
    }


    public void test_toEnum() throws Exception {
        check(Environment.DEV);
        check(Environment.INT);
        check(Environment.REC);
        check(Environment.PRD);
    }


    private void check(Environment value) {
        assertEquals(value, Environment.toEnum(value.getLabel()));
    }
}
