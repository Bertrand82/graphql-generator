package bg.graphql.tool;

import java.io.File;

public class MainProcess {

	public static void main(String[] args) throws  Exception{
		String pathGraphQL = "/schema/schema.graphqls";
		File dirGeneratedModel = new File("generated11");
		File dirSrcGeneratedSpring = new File("generated33");
		File dirResourceGeneratedHibernate = new File("generated44");
		GenerateModelRepositoryControllerFromGraphQL.processGenerationFullFromGraphQl(pathGraphQL,dirGeneratedModel,dirSrcGeneratedSpring ,dirResourceGeneratedHibernate);

	}

}
