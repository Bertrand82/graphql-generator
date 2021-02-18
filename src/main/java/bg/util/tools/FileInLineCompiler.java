package bg.util.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class FileInLineCompiler {

	private static HashMap<File, FileInLineCompiler> hMap = new HashMap<File, FileInLineCompiler>();

	public static FileInLineCompiler getInstance(String dirPath) {
		File dirRoot = new File(dirPath);
		return getInstance(dirRoot);
	}

	public static FileInLineCompiler getInstance(File dirRoot) {
		FileInLineCompiler f = hMap.get(dirRoot);
		if (f == null) {
			f = new FileInLineCompiler(dirRoot);
		}
		return f;
	}

	public URLClassLoader classLoader;

	private FileInLineCompiler(File dir) {
		hMap.put(dir, this);
		try {
			classLoader = new URLClassLoader(new URL[] { dir.toURI().toURL() });
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public boolean compileFile(File fileJava) {
		try {

			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

			// This sets up the class path that the compiler will use.
			// I've added the .jar file that contains the DoStuff interface within in it...
			List<String> optionList = new ArrayList<String>();
			optionList.add("-classpath");
			optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/InlineCompiler.jar");

			Iterable<? extends JavaFileObject> compilationUnit = fileManager
					.getJavaFileObjectsFromFiles(Arrays.asList(fileJava));
			JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnostics, optionList,
					null, compilationUnit);
			boolean compilationPerformed = compilationTask.call();
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(),
						diagnostic.getSource().toUri());
			}
			fileManager.close();
			return compilationPerformed;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
