package bg.graphql.tool;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserBuilder;
import graphql.language.Document;
import graphql.parser.Parser;

public class Main_1_ToolProcessSchemaGraphQL {
/**
 * Input : the schema graphQl
 * Output : the  entities java
 * @param args
 * @throws Exception
 */
	public static void main(String[] args) throws Exception {
		String sGraphQls = Files.readString(Paths.get(Main_1_ToolProcessSchemaGraphQL.class.getResource("/schema/schema.graphqls").toURI()));
		System.out.println("Start " + sGraphQls);
		new GeneratorClassesFromGraphQL("/schema/schema.graphqls");
		

	}

}
