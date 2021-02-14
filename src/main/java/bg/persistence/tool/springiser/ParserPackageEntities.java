package bg.persistence.tool.springiser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.squareup.javapoet.JavaFile;

import bg.persistence.tool.hibernate.HibernateClass;
import bg.persistence.tool.hibernate.HibernateMapping;



/**
 *
 * Scan a java package and process entities.
 *
 * @JsonTypeInfo and @JsonSubTypes in case of heritage.
 */

public class ParserPackageEntities {
	/* Nom du package recherché dans le classpath et traité */
	private String packageName;
	private Set<String> packageNameVersions = new HashSet();
	private static Logger logger = Logger.getLogger(ParserPackageEntities.class.getName());
	public ParserPackageEntities(String packageName) throws Exception {
		logger.info("//  Parse package : >" + packageName + "<");
		this.packageName = packageName;
		List<ClassGeneratorItem> listAll = parsePackageAllClasses();
		extractPackage(listAll);
		ManagerClassEntities.instance.setListAllClasses(listAll);
		ManagerClassEntities.instance.processBasePojo(packageName);
		int nbClasses = ManagerClassEntities.instance.getListBaseClassEntityItems().size();
		logger.info("Nb Classes : " + nbClasses);
	}

	private void extractPackage(List<ClassGeneratorItem> listAll) {
		for(ClassGeneratorItem ce : listAll) {
			String pName = ce.getClazz().getPackage().getName();
			if(!pName.equals(packageName)) {
				packageNameVersions.add(pName);
			}
		}
	}

	public void generateHibernateXMLMapping(File dirDest) {

		HibernateMapping hibernateMapping = new HibernateMapping();
		hibernateMapping.setPackageStr(this.packageName);
		for (ClassGeneratorItem cei : ManagerClassEntities.instance.getListBaseClassEntityItems()) {
			if (cei.getClazz().isEnum()) {

			} else {
				hibernateMapping.getListClass().add(new HibernateClass(cei));
			}
		}
		generateXmlHibernateFile(hibernateMapping, dirDest);
	}

	private void generateXmlHibernateFile(HibernateMapping hibernateMapping, File dirDest) {
		try {
			String fileName = "orm-mapping.xml";
			dirDest.mkdirs();

			File file = new File(dirDest, fileName);
			File dirCustom = getDirCustomFromDirDest(dirDest);
			File fileCustom = new File(dirCustom, fileName);
			logger.info("File custom exists : " + fileCustom.exists());
			if (fileCustom.exists()) {
				logger.info("File custom exists  NO GENERATION !!   "+fileCustom.getName());
			} else {
				
				JAXBContext jaxbContext = JAXBContext.newInstance(HibernateMapping.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				jaxbMarshaller.marshal(hibernateMapping, file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] q) {
		File d = new File("src/generated/resource");
	}

	private static File getDirCustomFromDirDest(File dirDest) {

		try {
			String dirName = dirDest.getAbsoluteFile().getName();
			File origin = dirDest.getCanonicalFile().getParentFile().getParentFile();
			File dirCustomParent = new File(origin, "custom");
			File dirCustom = new File(dirCustomParent, dirName);
			return dirCustom;
		} catch (IOException e) {
			e.printStackTrace();
			return new File("");
		}
	}

	public void generateJavaSources(File dirDest) throws Exception {
		this.generatePojoEnhanced(dirDest);
		this.generateSpringSources_(dirDest);
		//this.generateHistorySpringSources_(dirDest);
		//this.generateVersions_(dirDest);
	}

	

	private void generatePojoEnhanced(File dirDest) throws Exception {
		
		for (ClassGeneratorItem cei : ManagerClassEntities.instance.getListBaseClassEntityItems()) {
			String packageDest =cei.getPackagePojo();
			JavaFile javaSrcSpringRepository = cei.getSourcePojo(packageDest);
			writePoetTo(dirDest, javaSrcSpringRepository);
		}
	}

	private void generateSpringSources_(File dirDest) throws Exception {
		for (ClassGeneratorItem cei : ManagerClassEntities.instance.getListBaseClassEntityItems()) {
			String packNAmeController = cei.getPackageController();
			if (cei.isVersion()) {

			} else {
				if (cei.getClazz().isEnum()) {

					JavaFile javaSrcSpringRepository = cei.getSpringSourceControllerForEnum(packNAmeController);
					writePoetTo(dirDest, javaSrcSpringRepository);
				} else {
					JavaFile javaSrcSpringRepository = cei.getSpringSourceRepository();
					writePoetTo(dirDest, javaSrcSpringRepository);
					JavaFile javaSrcSpringController = cei.getSpringSourceController_();
					writePoetTo(dirDest, javaSrcSpringController);
				}
			}
		}
		logger.info("Writted to " + dirDest.getAbsolutePath());
	}

	private void generateHistorySpringSources_(File dirDest) throws Exception {
		for (ClassGeneratorItem cei : ManagerClassEntities.instance.getListBaseClassEntityItems()) {
			if (cei.isHistorisable()) {
				String generatedPackageNameRepository_ = cei.getPackagePojo() + ".history.repository";
				JavaFile javaSrcSpringRepository = cei
						.getSpringSourceHistoryRepository(generatedPackageNameRepository_);
				writePoetTo(dirDest, javaSrcSpringRepository);
				String generatedPackageNameController = cei.getPackagePojo() + ".history.controller";
				JavaFile javaSrcSpringControler = cei.getSpringSourceHistoryControler(generatedPackageNameController,
						javaSrcSpringRepository);
				writePoetTo(dirDest, javaSrcSpringControler);
			}
		}
	}

	private void writePoetTo(File dirDest, JavaFile javaFile) throws Exception {

		File dirCustomJava = getDirCustomFromDirDest(dirDest);
		dirCustomJava.mkdirs();
		File fCustom = getFile(dirCustomJava, javaFile);
		if (fCustom.exists()) {
			//
		} else {
			javaFile.writeTo(dirDest);
		}
	}

	/**
	 * Writes this to {@code directory} as UTF-8 using the standard directory
	 * structure.
	 */
	public File getFile(File directory, JavaFile javaFile) throws IOException {

		Path outputDirectory = directory.toPath();
		if (!javaFile.packageName.isEmpty()) {
			for (String packageComponent : javaFile.packageName.split("\\.")) {
				outputDirectory = outputDirectory.resolve(packageComponent);
			}

		}
		Path outputPath = outputDirectory.resolve(javaFile.typeSpec.name + ".java");
		return new File(outputPath.toUri());
	}

	private List<ClassGeneratorItem> parsePackageAllClasses() throws Exception {
		Set<Class<?>> classes = getClassesFromFolders(packageName);
		logger.info("parsePackage ClassLoader : " + this.getClass().getClassLoader() + "   classes from folder: "
				+ classes.size());
		if (classes.size() == 0) {
			classes = getAllClassesFromJars();
		}
		List<ClassGeneratorItem> listClassEntityItems  = new ArrayList<>();
		for (Class<?> clazz : classes) {
			String newPAckageName="generaed2";
			ClassGeneratorItem classEntityItem = new ClassGeneratorItem(clazz,newPAckageName,"Non Utilisé");
			listClassEntityItems.add(classEntityItem);
		}
		return listClassEntityItems;
	}

	private Set<Class<?>> getAllClassesFromJars() {
		logger.info("getClassesFromJars start --------------------------- ");
		Reflections reflections = new Reflections(this.packageName, new SubTypesScanner(false));

		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
		@SuppressWarnings("rawtypes")
		Set<Class<? extends Enum>> enumClasses = reflections.getSubTypesOf(Enum.class);

		enumClasses.forEach((e) -> {
			Class<?> ee = (Class<?>) e;
			allClasses.add(ee);
		});

		
		return allClasses;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Set<Class<?>> getClassesFromFolders(String packageName) throws Exception {
		// ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		String path = packageName.replace('.', '/');
		logger.info("path: " + path);

		List<File> dirs = new ArrayList<>();
		dirs.addAll(getDirsFromPah(path));

		Set<Class<?>> classes = new HashSet<>();
		for (File directory : dirs) {
			classes.addAll(findClassesInDirectory(directory, packageName));
		}
		return classes;
	}

	private static Collection<? extends File> getDirsFromPah(String path) throws Exception {
		List<File> dirs = new ArrayList<>();
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		Enumeration<URL> resources = classLoader.getResources(path);
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			URI uri = new URI(resource.toString());
			logger.info("getDirsFromPah resource :" + resource.toString() + " | Scheme : " + uri.getScheme()
					+ " | UserInfo:  " + uri.getUserInfo() + " |  host: " + uri.getHost() + "  | uri:  " + uri);
			if (uri.getScheme() == null) {
			} else if (uri.getScheme().equals("jar")) {
				logger.info("jar :::: " + uri.getScheme());
			} else {
				dirs.add(new File(uri.getPath()));
			}
		}
		logger.info("dirs " + dirs.size() + "   " + path);
		return dirs;
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base
	 *                    directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClassesInDirectory(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(
						Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		logger.info("findClassesInDirectory | directory:  " + directory.getPath() + " | classes.size : "
				+ classes.size() + " | classes: " + classes);
		return classes;
	}
	/**
	 * 
	 * @param dirDest
	 * @throws Exception
	 */
	private void generateVersions_(File dirDest) throws Exception {
		for(String packageVersion : this.packageNameVersions ) {
			generateVersions(dirDest,packageVersion);
		}
		
	}

	private void generateVersions(File dirDest, String packageVersion) throws Exception{
		String version = getVersionFromPackage(packageVersion);
		List<ClassGeneratorItem> listClassEntityItem = ManagerClassEntities.instance.getListClassEntityItemsByPackage(packageVersion);
		for(ClassGeneratorItem cei :listClassEntityItem) {
			String packRoot = cei.getPackagePojo().replace("."+version, "");
			String packageDest =packRoot+"."+version;
			JavaFile javaSrcSpringRepository = cei.getSourcePojo(packageDest);
			writePoetTo(dirDest, javaSrcSpringRepository);
		}
	}

	private String getVersionFromPackage(String packageVersion) {
	    
		String version =  packageVersion.substring(packageVersion.lastIndexOf(".")+1);
		logger.info("verion : >"+version+"<");
		return version;
	}

}
