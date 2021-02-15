package bg.graphql.tool;

import java.io.File;

import bg.persistence.tool.ParserPackageEntities;

public class Main_2_ProcessEntities {
	
	/**
	 * Input : The entities java
	 * Output : The stack spring boot , the orm_mapping to create database.
	 * @param q
	 * @throws Exception
	 */
	public static void main(String[] q) throws Exception{
		File dSrcGenerated = new File("generated2");
		dSrcGenerated.mkdirs();
		ParserPackageEntities parserPackageEntities = new ParserPackageEntities(PackageNameService.getPackageModelTemp());
		parserPackageEntities.generateHibernateXMLMapping(dSrcGenerated);
		parserPackageEntities.generateJavaSources(dSrcGenerated);
	}
	
	

}
