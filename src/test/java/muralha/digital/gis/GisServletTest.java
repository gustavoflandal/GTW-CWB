package muralha.digital.gis;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.lang.reflect.Method;

public class GisServletTest {

    @Test
    public void escJsonNulo() throws Exception {
        Assert.assertEquals(invokeEscJson(null), "");
    }

    @Test
    public void escJsonComAspas() throws Exception {
        Assert.assertEquals(invokeEscJson("nome \"teste\""), "nome \\\"teste\\\"");
    }

    @Test
    public void escJsonComBarra() throws Exception {
        Assert.assertEquals(invokeEscJson("a\\b"), "a\\\\b");
    }

    @Test
    public void escXmlNulo() throws Exception {
        Assert.assertEquals(invokeEscXml(null), "");
    }

    @Test
    public void escXmlComAmpersand() throws Exception {
        Assert.assertEquals(invokeEscXml("A & B"), "A &amp; B");
    }

    @Test
    public void escXmlComTags() throws Exception {
        Assert.assertEquals(invokeEscXml("<tag>"), "&lt;tag&gt;");
    }

    private String invokeEscJson(String s) throws Exception {
        GisServlet servlet = new GisServlet();
        Method m = GisServlet.class.getDeclaredMethod("escJson", String.class);
        m.setAccessible(true);
        return (String) m.invoke(servlet, s);
    }

    private String invokeEscXml(String s) throws Exception {
        GisServlet servlet = new GisServlet();
        Method m = GisServlet.class.getDeclaredMethod("escXml", String.class);
        m.setAccessible(true);
        return (String) m.invoke(servlet, s);
    }
}
