package bg.graphql.tool;

import java.io.File;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class GeneratorPojoTest {

	@Test
	public void test1() throws Exception {
		String pathSchemagraphQl =  "/schema/test1.graphqls";
		Assert.assertNotNull(pathSchemagraphQl);
		InputStream inStream= GeneratorClassesFromGraphQL.class.getResourceAsStream(pathSchemagraphQl);
		Assert.assertNotNull(inStream);
		GeneratorClassesFromGraphQL g =new GeneratorClassesFromGraphQL(inStream,pathSchemagraphQl, new File("generated1Test1"));
		Assert.assertNotNull(g);
	}
}
