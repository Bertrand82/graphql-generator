package bg.graphql.tool;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class GeneratorPojoTest {

	@Test
	public void test1() throws Exception {
		
		new GeneratorClassesFromGraphQL("schema/test1.graphqls", new File("generated1Test"));
	}
}
