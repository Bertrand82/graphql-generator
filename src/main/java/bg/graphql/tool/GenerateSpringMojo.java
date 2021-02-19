package bg.graphql.tool;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import bg.graphql.tool.GenerateModelRepositoryControllerFromGraphQL;
/**
 * Genere à partir d'un graphQL une application spring
 * @author bg
 *
 */
@Mojo( name = "generateSpring")
public class GenerateSpringMojo extends AbstractMojo{

	@Parameter(readonly = true, defaultValue = "${project}")
	private MavenProject project;
	
	@Parameter(property = "msg", defaultValue = "from maven")
	protected String msg ="";

	@Parameter(property = "pathGraphQL", defaultValue = "/schema/schema.graphqls")
	protected String pathGraphQL;
	
	@Parameter
	protected File dirGeneratedModel = new File("generated11");
	
	@Parameter
	File dirSrcGeneratedSpring = new File("generated33");
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			project.getBuild().getDirectory();
			//String pathGraphQL = "/schema/schema.graphqls";
			msg +=" pathGraphQL "+pathGraphQL+" dirGeneratedModel "+dirGeneratedModel.getName()+" dirSrcGeneratedSpring "+dirSrcGeneratedSpring.getName();
			GenerateModelRepositoryControllerFromGraphQL.processGenerationFullFromGraphQl(pathGraphQL,dirGeneratedModel,dirSrcGeneratedSpring );
		} catch (Exception e) {			
			throw new MojoExecutionException(msg,e);
		}
	}
	
	protected void cleanDirectory(File dir) {
		try {
			getLog().info("Clean directory exists : " + dir.exists() + "  " + dir.getAbsolutePath());
			if (dir.exists()) {
				int i = cleanDirectoryRecursif(dir);
				getLog().info("            " + i + "  files deleted ");
			}
		} catch (Exception e) {
			getLog().error("deleting exception " + e.getMessage());
		}
	}

	private int cleanDirectoryRecursif(File dir) {
		
		int i = 0;
		if (dir == null) {
			return 0;
		}
		
	getLog().info("Clean Directory "+dir.getAbsolutePath());
		if (dir.exists()) {
			for (File child : dir.listFiles()) {
				if (child.isDirectory()) {
					i += cleanDirectoryRecursif(child);
				} else if (child.delete()) {
					i++;
				}				
			}
			return i;
		} else {
			return 0;
		}
	}


}
