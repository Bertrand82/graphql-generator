package bg.graphql.tool.util;



import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.squareup.javapoet.JavaFile;

/**
 * @author c82bgui
 *
 */
public class JavaPoetWriter {

	private final File dir;

	public JavaPoetWriter(File dir) {
		this.dir = dir;
		dir.mkdirs();
	}

	public void write(final List<JavaFile> javafiles) {
		for (JavaFile jf : javafiles) {
			write(jf);
		}
	}
	/**
	 * @param pJavafile
	 */
	public File write(final JavaFile javafile) {
		try {
			javafile.writeTo(dir);
			Path outputDirectory = dir.toPath();
			
			Path outputPath = outputDirectory.resolve(javafile.packageName.replace('.', File.separatorChar)).resolve(javafile.typeSpec.name + ".java");
			File file = outputPath.toFile();
			return file;
		} catch (final IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public File getDir() {
		return dir;
	}

}


