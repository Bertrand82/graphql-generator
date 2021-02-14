package bg.graphql.tool.util;



import java.io.File;
import java.io.IOException;
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
	public void write(final JavaFile javafile) {
		try {
			javafile.writeTo(dir);			
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public File getDir() {
		return dir;
	}

}


