package bg.graphql.tool.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
/**
 * Genere à partir d'un graphQL une application spring
 * @author w1
 *
 */
public class GraphqlMojo extends AbstractMojo{

	
	@Parameter(property = "msg", defaultValue = "from maven")
	protected String msg;

	@Parameter(property = "pathGraphQL", defaultValue = "/schema/schema.graphqls")
	protected String pathGraphQL;
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		
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
