package bg.graphql.tool;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import bg.graphql.tool.util.JavaPoetWriter;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;
import graphql.parser.Parser;

public class GeneratorClassesFromGraphQL {
	List<GeneratorClassType> list = new ArrayList<GeneratorClassType>();
	JavaPoetWriter javapoetWritter = new JavaPoetWriter(new File("generated1"));

	public GeneratorClassesFromGraphQL(String pathSchemagraphQl) throws Exception {
		InputStream inStream = GeneratorClassesFromGraphQL.class.getResourceAsStream(pathSchemagraphQl);
		Reader readerSchema = new InputStreamReader(inStream);
		Parser parser = new Parser();
		Document document = parser.parseDocument(readerSchema);
		document.getChildren().forEach((e) -> {
			//System.out.println("doc child  "+e.getClass().getName() + "  " + e);
		});
		List<ObjectTypeDefinition> listQuery = new ArrayList();
		List<ObjectTypeDefinition> listMutation = new ArrayList();
		for (Definition<?> definition : document.getDefinitions()) {
			if (definition instanceof ObjectTypeDefinition) {
				ObjectTypeDefinition oDefinition = (ObjectTypeDefinition) definition;
				String name = oDefinition.getName();
				if (name.equals("Query")) {

					listQuery.add(oDefinition);

				} else if (name.equals("Mutation")) {
					listMutation.add(oDefinition);
					

				} else {
					GeneratorClassType generatorType = new GeneratorClassType(oDefinition);
					javapoetWritter.write(generatorType.getJavaFileGenerator(pathSchemagraphQl));
				}
				
				
			}
		}
		GeneratorDataFetcher generatorDataFetcher = new GeneratorDataFetcher(GeneratorDataFetcher.TYPE.QUERY,listQuery,pathSchemagraphQl);
		javapoetWritter.write(generatorDataFetcher.getListJavaFiles());

	}

}
