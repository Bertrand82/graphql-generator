package bg.graphql.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import bg.persistence.tool.ParserPackageEntities;
import bg.util.tools.CompilerFileInLine;

public class Main_full {

	private static Logger logger = Logger.getLogger(Main_full.class.getName());

	public static void main(String[] args) throws Exception{
		String pathGraphQL = "/schema/schema.graphqls";
		File dirGeneratedModel = new File("generated11");
		File dirSrcGeneratedSpring = new File("generated33");
		processGenerationFullFromGraphQl(pathGraphQL,dirGeneratedModel,dirSrcGeneratedSpring );
	}
	
	public static void processGenerationFullFromGraphQl(String pathGraphQL,File dirGeneratedModel ,File dirSrcGeneratedSpring ) throws Exception{
		
		// GraphQl Processing et génération des sources
		GeneratorClassesFromGraphQL generator = new GeneratorClassesFromGraphQL(pathGraphQL,dirGeneratedModel);
		File[] filesSrcJava  = generator.getFileEntities();
		// Compilation des sources générés
		logger.info("filesSrcJava.length "+filesSrcJava.length);
		CompilerFileInLine compiler = CompilerFileInLine.getInstance();
		List<Class<?>> classesEntities = compiler.getClasses(filesSrcJava);
		logger.info(("classesEntities.length "+classesEntities.size()));
		
		dirSrcGeneratedSpring.mkdirs();
		// Lectures des "entities" et generation de la stack Spring
		ParserPackageEntities parserPackageEntities = new ParserPackageEntities(PackageNameService.getPackageModelTemp(),classesEntities);
		parserPackageEntities.generateHibernateXMLMapping(dirSrcGeneratedSpring);
		parserPackageEntities.generateJavaSources(dirSrcGeneratedSpring);
	}
	
	

	
}
