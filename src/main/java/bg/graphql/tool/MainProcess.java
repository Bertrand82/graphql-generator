package bg.graphql.tool;

import java.io.File;

public class MainProcess {

	public static void main(String[] args) throws  Exception{
		String pathGraphQL = "/schema/schema.graphqls";
		File dirGeneratedModel = new File("generated11");
		File dirSrcGeneratedSpring = new File("generated33");
		GenerateModelRepositoryControllerFromGraphQL.processGenerationFullFromGraphQl(pathGraphQL,dirGeneratedModel,dirSrcGeneratedSpring );

	}

}
