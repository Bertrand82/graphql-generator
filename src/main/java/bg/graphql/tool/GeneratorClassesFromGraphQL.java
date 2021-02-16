package bg.graphql.tool;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.squareup.javapoet.TypeName;

import bg.graphql.tool.util.JavaPoetHelper;
import bg.graphql.tool.util.JavaPoetWriter;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;
import graphql.parser.Parser;

public class GeneratorClassesFromGraphQL {
	public String pathSchemagraphQl;
	List<GeneratorClassType> listGeneratorClassType = new ArrayList<GeneratorClassType>();
	JavaPoetWriter javapoetWritter = new JavaPoetWriter(new File("generated1"));

	public GeneratorClassesFromGraphQL(String pathSchemagraphQl) throws Exception {
		this.pathSchemagraphQl=pathSchemagraphQl;
		InputStream inStream = GeneratorClassesFromGraphQL.class.getResourceAsStream(pathSchemagraphQl);
		Reader readerSchema = new InputStreamReader(inStream);
		Parser parser = new Parser();
		Document document = parser.parseDocument(readerSchema);
		document.getChildren().forEach((e) -> {
			//System.out.println("doc child  "+e.getClass().getName() + "  " + e);
		});
		List<ObjectTypeDefinition> listQueryMutation = new ArrayList<>();
		for (Definition<?> definition : document.getDefinitions()) {
			if (definition instanceof ObjectTypeDefinition) {
				ObjectTypeDefinition oDefinition = (ObjectTypeDefinition) definition;
				String name = oDefinition.getName();
				if (name.equals("Query")) {

					listQueryMutation.add(oDefinition);

				} else if (name.equals("Mutation")) {
					listQueryMutation.add(oDefinition);
					

				} else {
					GeneratorClassType generatorType = new GeneratorClassType(oDefinition);
					listGeneratorClassType.add(generatorType);
					javapoetWritter.write(generatorType.getJavaFileGenerator(pathSchemagraphQl));
				}		
				
			}
		}
		GeneratorDataFetcher generatorDataFetcher = new GeneratorDataFetcher(listQueryMutation,this);
		javapoetWritter.write(generatorDataFetcher.getListJavaFiles());

	}

	public boolean isFieldPrimitif(TypeName retourTypeNameSpan, String argumentName) {
		String cArgumentName = JavaPoetHelper.capitalizeFirstLetter(argumentName);
		System.err.println(" cArgumentName "+cArgumentName+" list.size  "+listGeneratorClassType.size());
		for(GeneratorClassType generatorType :this.listGeneratorClassType) {
			System.err.println(" cArgumentName "+cArgumentName+"  "+generatorType.classSimpleName);
			if(generatorType.classSimpleName.equals(cArgumentName)){
				return false;
			}
		}
		return true;
	}

}
