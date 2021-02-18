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

public class CompilerFileInLine {

	private static HashMap<File, CompilerFileInLine> hMap = new HashMap<File, CompilerFileInLine>();

	private File dirClasses = new File("classesGeneratedByBg");
	
	public static CompilerFileInLine getInstance(String dirPath) {
		File dirClassesRoot = new File(dirPath);
		return getInstance(dirClassesRoot);
	}

	public static CompilerFileInLine getInstance(File dirClassesRoot) {
		CompilerFileInLine f = hMap.get(dirClassesRoot);
		if (f == null) {
			f = new CompilerFileInLine(dirClassesRoot);
		}
		return f;
	}

	public URLClassLoader classLoader;

	private CompilerFileInLine(File dirClasses) {
		hMap.put(dirClasses, this);
		this.dirClasses=dirClasses;
		try {
			classLoader = new URLClassLoader(new URL[] { dirClasses.toURI().toURL() });
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public boolean compileFile(File ... fileJava) {
		try {
			dirClasses.mkdir();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

			// This sets up the class path that the compiler will use.
			// I've added the .jar file that contains the DoStuff interface within in it...
			List<String> optionList = new ArrayList<String>();
			optionList.add("-classpath");
			optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/InlineCompiler.jar");
			optionList.add("-d");
			optionList.add(dirClasses.getAbsolutePath());
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
	public  List<Class<?>> getClasses(File[] javasrces) {
		for(File f :javasrces) {
			//System.out.println("xxxx fileSrc:  exists:  "+f.exists()+"  | "+f.getName());
		}
		this.compileFile(javasrces);
		List<String> names = getJavaClassNames(this.dirClasses);
		List<Class<?>>  classes = new ArrayList<Class<?>>();
		for (int i = 0; i < names.size(); i++) {
			try {
				Class c = this.classLoader.loadClass(names.get(i));
				classes.add(c) ;
			} catch (ClassNotFoundException e) {
				System.err.println("Bloop "+e.getClass()+"  "+e.getMessage());;
			}
		}
		return classes;
	}

	private List<String>  getJavaClassNames( File dirClasses2) {
		return getJavaClassNames_("",dirClasses2);
	}
	private List<String>  getJavaClassNames_(String packageName, File dirClasses2) {
		List<String>	list = new ArrayList<String>() 	;
		for(File f : dirClasses2.listFiles()) {
			if (f.isDirectory()) {
				String name = f.getName();
				list.addAll(getJavaClassNames_(packageName+name+".",new File(dirClasses2,name)));
			}else if (f.getName().endsWith(".class")){
				String s = packageName+f.getName().replace(".class", "");
				list.add(s);
			}
		}
		return list;
	}

	public static CompilerFileInLine getInstance() {
		return CompilerFileInLine.getInstance("generatedDirClassCompiled");
	}


}
