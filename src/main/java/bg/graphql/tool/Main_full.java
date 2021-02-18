package bg.graphql.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bg.persistence.tool.ParserPackageEntities;
import bg.util.tools.FileInLineCompiler;

public class Main_full {

	public static void main(String[] args) throws Exception{
		GeneratorClassesFromGraphQL generator = new GeneratorClassesFromGraphQL("/schema/schema.graphqls");
		File[] filesSrcJava  = generator.getFileEntities();
		System.out.println("- -------- ---- ---- ------ files.length "+filesSrcJava.length);
		FileInLineCompiler compiler = FileInLineCompiler.getInstance();
		List<Class<?>> classesEntities = compiler.getClasses(filesSrcJava);
		System.out.println("- -------- ---- ---- ------ classes.length "+classesEntities.size());
		File dSrcGenerated = new File("generated3");
		dSrcGenerated.mkdirs();
		ParserPackageEntities parserPackageEntities = new ParserPackageEntities(PackageNameService.getPackageModelTemp(),classesEntities);
		parserPackageEntities.generateHibernateXMLMapping(dSrcGenerated);
		parserPackageEntities.generateJavaSources(dSrcGenerated);
	}
	
	

	
}
