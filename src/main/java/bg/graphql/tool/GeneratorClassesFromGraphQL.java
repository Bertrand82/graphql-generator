package bg.graphql.tool;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.squareup.javapoet.TypeName;

import bg.graphql.tool.util.JavaPoetHelper;
import bg.graphql.tool.util.JavaPoetWriter;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;
import graphql.parser.Parser;

public class GeneratorClassesFromGraphQL {
	private final String pathSchemagraphQl;
	private final List<GeneratorClassType> listGeneratorClassType = new ArrayList<GeneratorClassType>();
	private final File dirSrcOut;
	private final JavaPoetWriter javapoetWritter;
	List<File> listFileEntities = new ArrayList<File>();

	private static Logger logger = Logger.getLogger(GeneratorClassesFromGraphQL.class.getName());


	public GeneratorClassesFromGraphQL(String pathSchemagraphQl) throws Exception {
		this(pathSchemagraphQl, new File("generated1"));
	}

	public GeneratorClassesFromGraphQL(String pathSchemagraphQl, File dirOut2) throws Exception {

		logger.info("Start processing graphQl : " + pathSchemagraphQl);
		this.dirSrcOut = dirOut2;
		javapoetWritter = new JavaPoetWriter(this.dirSrcOut);
		this.pathSchemagraphQl = pathSchemagraphQl;
		init();
		logger.info("End dirSrcOut : " + dirSrcOut.getPath());
	}

	private void init() throws Exception {
		InputStream inStream = GeneratorClassesFromGraphQL.class.getResourceAsStream(pathSchemagraphQl);
		Reader readerSchema = new InputStreamReader(inStream);
		Parser parser = new Parser();
		Document document = parser.parseDocument(readerSchema);
		document.getChildren().forEach((e) -> {
			// System.out.println("doc child "+e.getClass().getName() + " " + e);
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
					File fileJavaSrc = javapoetWritter.write(generatorType.getJavaFileGenerator(pathSchemagraphQl));
					this.listFileEntities.add(fileJavaSrc);
				}

			}
		}
		GeneratorDataFetcher generatorDataFetcher = new GeneratorDataFetcher(listQueryMutation, this);
		javapoetWritter.write(generatorDataFetcher.getListJavaFiles());

	}

	public boolean isFieldPrimitif(TypeName retourTypeNameSpan, String argumentName) {
		String cArgumentName = JavaPoetHelper.capitalizeFirstLetter(argumentName);
		for (GeneratorClassType generatorType : this.listGeneratorClassType) {
			if (generatorType.classSimpleName.equals(cArgumentName)) {
				return false;
			}
		}
		return true;
	}

	public String getPathSchemagraphQl() {
		return pathSchemagraphQl;
	}

	public File getDirSrcOut() {
		return dirSrcOut;
	}

	public File[] getFileEntities() {
		File[] fArray = new File[listFileEntities.size()];
		return listFileEntities.toArray(fArray);
	}

}
