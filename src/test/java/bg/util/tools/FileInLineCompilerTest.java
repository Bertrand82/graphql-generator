package bg.util.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.Assert;
import org.junit.Test;






public class FileInLineCompilerTest {

	File dirTestTemp = new File("./generatedTempTest");
	@Test
	public void test1() throws Exception {
		
		File helloWorldJava = createFileTest();
		
		boolean isCompilationOk = FileInLineCompiler.getInstance(dirTestTemp).compileFile(helloWorldJava.getAbsoluteFile());
		Assert.assertTrue(isCompilationOk);
		String sRetour = processClassTest();
		Assert.assertEquals("Hello", sRetour);
	}
	
	
	private  File createFileTest() {
		StringBuilder sb = new StringBuilder(64);
		sb.append("package testcompile;\n");
		sb.append("public class HelloWorld implements "+DoStuff.class.getName()+" {\n");
		sb.append("    public String doStuff() {\n");
		sb.append("        System.out.println(\"Hello world\");\n");
		sb.append("        return(\"Hello\");\n");
		sb.append("    }\n");
		sb.append("}\n");
		System.out.println("" + sb);
		File helloWorldJava = new File(dirTestTemp,"testcompile/HelloWorld.java");
		System.out.println("helloWorldJava getName() : " + helloWorldJava.getName());
		if (helloWorldJava.getParentFile().exists() || helloWorldJava.getParentFile().mkdirs()) {

			Writer writer = null;
			try {
				writer = new FileWriter(helloWorldJava);
				writer.write(sb.toString());
				writer.flush();
			} catch (IOException e) {

				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
				}
			}
		}
		return helloWorldJava;
	}
	
	private  String processClassTest() throws Exception{
		/**
		 * Load and execute
		 *************************************************************************************************/
		System.out.println("processClassTest start");
		// Create a new custom class loader, pointing to the directory that contains the
		// compiled
		// classes, this should point to the top of the package structure!
		// Load the class from the classloader by name....
		Class<?> loadedClass = FileInLineCompiler.getInstance(dirTestTemp).classLoader.loadClass("testcompile.HelloWorld");
		// Create a new instance...
		Object obj = loadedClass.getConstructors()[0].newInstance();
		// Santity check
		if (obj instanceof DoStuff) {
			// Cast to the DoStuff interface
			DoStuff stuffToDo = (DoStuff) obj;
			// Run it baby
			return stuffToDo.doStuff();
		}
		return null;
	}

	
	

}
