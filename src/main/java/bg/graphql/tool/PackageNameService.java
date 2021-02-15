package bg.graphql.tool;

public class PackageNameService {

	
	
	public static String getPackagePojo() {
		return Common.getSpringBaseRepository()+".pojo";
	}
	
	public static String getPackageRepository() {
		return Common.getSpringBaseRepository()+".repository";
	}
	
	public static String getPackageModelTemp() {
		return "bg.generated.graphql.model";
	}
}
