package bg.graphql.tool;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import org.hibernate.cfg.annotations.ListBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import bg.graphql.tool.util.BeanMethod;
import bg.graphql.tool.util.JavaPoetHelper;

import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.Type;

public class GeneratorDataFetcher {

	public enum TYPE {
		QUERY("Query"), MUTATION("Mutation");

		String name;

		TYPE(String name_) {
			name = name_;
		};
	}

	List<BeanMethod> lisBeanMethod = new ArrayList();
	List<ObjectTypeDefinition> objectTypeDefinitions;
	List<JavaFile> listJavaFiles = new ArrayList();
	
	String packageName = "bg.generated.graphql";
	GeneratorClassesFromGraphQL generator;
	
	public GeneratorDataFetcher(List<ObjectTypeDefinition> listDefinition, GeneratorClassesFromGraphQL generator) {

		this.generator = generator;
		objectTypeDefinitions = listDefinition;
		AnnotationSpec.Builder annotation = AnnotationSpec.builder(com.netflix.graphql.dgs.DgsComponent.class);

		for (ObjectTypeDefinition objectTypeDefinition : listDefinition) {
			for (FieldDefinition fieldDefinition : objectTypeDefinition.getFieldDefinitions()) {
				System.err.println("objectTypeDefinition name ----" + objectTypeDefinition.getName());
				BeanMethod umb = generateMethod_(getType(objectTypeDefinition.getName()), fieldDefinition);
				lisBeanMethod.add(umb);
			}
		}
		generateClasses();

	}

	private static TYPE getType(String typeStr) {
		if (typeStr.trim().equalsIgnoreCase("query")) {
			return TYPE.QUERY;
		} else if (typeStr.trim().equalsIgnoreCase("mutation")) {
			return TYPE.MUTATION;
		} else {
			throw new RuntimeException("No Type for " + typeStr);
		}

	}

	private void generateClasses() {
		Set<TypeName> setTypes = new HashSet<TypeName>();
		for (BeanMethod bm : lisBeanMethod) {
			setTypes.add(bm.getRetourTypeNameSpan());
		}
		for (TypeName typeName : setTypes) {
			String s = "graphSql_" + JavaPoetHelper.getSimpleName(typeName) + "_DataFetcher2";
			AnnotationSpec.Builder annotation = AnnotationSpec.builder(com.netflix.graphql.dgs.DgsComponent.class);
			TypeSpec.Builder classBuilder = TypeSpec.classBuilder(s).addModifiers(Modifier.PUBLIC);
			classBuilder.addAnnotation(annotation.build());
			addMethodFetcher(classBuilder, typeName);
			String nameTypeStr = JavaPoetHelper.getSimpleName(typeName);
			/////

			/////

			ClassName typeRepository = ClassName.get(Common.getSpringBaseRepository() + ".repository",
					nameTypeStr + "Repository");
			FieldSpec.Builder fieldRepository = FieldSpec.builder(typeRepository, "repository")
					.addAnnotation(Autowired.class);
			classBuilder.addField(fieldRepository.build());
			JavaFile jf = getJavaFileGenerator(classBuilder);
			listJavaFiles.add(jf);
		}

	}

	private void addMethodFetcher(Builder classBuilder2, TypeName typeName) {
		for (BeanMethod beanMethod : this.lisBeanMethod) {
			if (beanMethod.getRetourTypeNameSpan().equals(typeName)) {
				classBuilder2.addMethod(beanMethod.getMethodBuilder().build());

			}
		}

	}

	private static TypeName getReturnTypeSpan(Type typeReturn, String packageName) {

		if (typeReturn instanceof graphql.language.ListType) {
			ListType listType = (ListType) typeReturn;
			String typeNameList = ((graphql.language.TypeName) listType.getType()).getName();
			ClassName typeName1 = ClassName.get(List.class);
			TypeName typeName2 = ClassName.get(packageName, typeNameList);
			return typeName2;
		} else if (typeReturn instanceof graphql.language.TypeName) {
			graphql.language.TypeName typeName = (graphql.language.TypeName) typeReturn;
			TypeName typeName3 = ClassName.get(packageName, typeName.getName());
			return typeName3;
		} else if (typeReturn instanceof NonNullType) {
			NonNullType lType = (NonNullType) typeReturn;
			Type typeEmbeded = lType.getType();
			return getReturnTypeSpan(typeEmbeded, packageName);
		}
		throw new RuntimeException("No typeName for " + typeReturn);
	}

	private BeanMethod generateMethod_(TYPE type_, FieldDefinition fieldDefinition) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(fieldDefinition.getName())
				.addModifiers(Modifier.PUBLIC);

		List<ArgumentHelper> listArgument = new ArrayList<ArgumentHelper>();
		for (InputValueDefinition input : fieldDefinition.getInputValueDefinitions()) {
			String argumentName = input.getName();
			
			String pojoPackageName = Common.getSpringBaseRepository() + ".pojo";
			com.squareup.javapoet.TypeName argumentClassName = JavaPoetHelper
					.getClassNameFromGraphQlType(input.getType(), pojoPackageName);
			AnnotationSpec.Builder argumentAnnotation = AnnotationSpec
					.builder(com.netflix.graphql.dgs.InputArgument.class);
			argumentAnnotation.addMember("value", "\"" + argumentName + "\"", "");
			TypeName argumentClassNameAnnoted = argumentClassName.annotated(argumentAnnotation.build());

			methodBuilder.addParameter(argumentClassNameAnnoted, argumentName);
			listArgument.add(new ArgumentHelper(argumentName,argumentClassName));
		}

		AnnotationSpec.Builder annotationMethod = AnnotationSpec.builder(com.netflix.graphql.dgs.DgsData.class);
		// parentType = "Query"
		annotationMethod.addMember("parentType", "\"" + type_.name + "\"");
		annotationMethod.addMember("field", "\"" + fieldDefinition.getName() + "\"");

		methodBuilder.addAnnotation(annotationMethod.build());
		TypeName retourTypeName_ = getReturn(fieldDefinition.getType());
		String pojoPackageName = Common.getSpringBaseRepository() + ".pojo";
		TypeName retourTypeNameSpan = getReturnTypeSpan(fieldDefinition.getType(), pojoPackageName);
		boolean isRetourAsList = JavaPoetHelper.getReturnAsList(fieldDefinition.getType());
		
		if (type_ == TYPE.QUERY) {
			methodBuilder.addJavadoc(CodeBlock.builder().add("", "QUERY").build());
			processQueryMethod(methodBuilder, retourTypeNameSpan, isRetourAsList, listArgument,
					retourTypeName_);
		} else if (type_ == TYPE.MUTATION) {
			processMutationMethod(methodBuilder, retourTypeNameSpan,  isRetourAsList, listArgument,
					retourTypeName_);
		}

		methodBuilder.returns(retourTypeName_);
		BeanMethod umb = new BeanMethod(retourTypeNameSpan, methodBuilder);
		return umb;
	}

	private void processMutationMethod(MethodSpec.Builder methodBuilder, TypeName retourTypeNameSpan,
			 boolean isRetourAsList, List<ArgumentHelper> listArgument, TypeName retourTypeName_) {
		methodBuilder.addStatement("$T oNew  = new $T()", retourTypeNameSpan, retourTypeNameSpan);
		int i=0;
		for (ArgumentHelper helper : listArgument) {
			i++;
			String argumentName=helper.argumentName;
			
			String methodSetter = "set" + JavaPoetHelper.capitalizeFirstLetter(argumentName);
			boolean isFieldPrimitif = generator.isFieldPrimitif(retourTypeNameSpan,argumentName);
			if (isFieldPrimitif) {
				methodBuilder.addStatement("oNew."+methodSetter+"(" + argumentName + ")", retourTypeNameSpan, retourTypeNameSpan);
			}else {
				String cArgumentName = JavaPoetHelper.capitalizeFirstLetter(argumentName);
				ClassName cn = ClassName.get(PackageNameService.getPackagePojo(),cArgumentName);
				methodBuilder.addStatement( "$T o"+i+" = new $T()",cn,cn);
				methodBuilder.addStatement( "o"+i+".setId("+argumentName+")");
				
				methodBuilder.addStatement( "oNew.set"+cArgumentName+"( o"+i+")");
			}
		}
		methodBuilder.addStatement("return  repository.save(oNew)");
	}

	private void processQueryMethod(MethodSpec.Builder methodBuilder, TypeName retourTypeNameSpan,
			boolean isRetourAsList, List<ArgumentHelper> listArgument, TypeName retourTypeName_) {
		boolean isPageable = false;
		for (ArgumentHelper helper : listArgument) {
			String argumentName = helper.argumentName;
			if (argumentName.contentEquals("offset")) {
				isPageable = true;
			} else if (argumentName.contentEquals("count")) {
				isPageable = true;
			} else {
				String methodSetter = "set" + JavaPoetHelper.capitalizeFirstLetter(argumentName);
				methodBuilder.addStatement("oAsProbe." + methodSetter + "(" + argumentName + ")", retourTypeNameSpan,
						retourTypeNameSpan);
			}
		}
		methodBuilder.addStatement("$T oAsProbe  = new $T()", retourTypeNameSpan, retourTypeNameSpan);

		if (isPageable) {
			methodBuilder.addStatement("int pageNb = offset/count");
			methodBuilder.addStatement("$T pageable = $T.of(pageNb,count)", Pageable.class, PageRequest.class);
		}
		// ExampleMatcher showMatcher =
		// ExampleMatcher.matchingAny().withIgnoreCase("releaseYear").withNullHandler(ExampleMatcher.NullHandler.INCLUDE)
		// .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
		String statement = "$T exampleMatcher = ExampleMatcher.matchingAny()";
		for (ArgumentHelper helper : listArgument) {
			String argumentName=helper.argumentName;
			statement += ".withIgnoreCase(\"" + argumentName + "\")";
		}
		methodBuilder.addStatement(statement, ExampleMatcher.class);
		methodBuilder.addStatement("$T<$T> exampleRequest  = (Example.of(oAsProbe, exampleMatcher))", Example.class,
				retourTypeNameSpan);
		if (isRetourAsList) {
			if (isPageable) {
				methodBuilder.addStatement("$T<$T> page = repository.findAll(exampleRequest,pageable)", Page.class,
						retourTypeNameSpan);
				methodBuilder.addStatement("return page.getContent()");
			} else {
				methodBuilder.addStatement("return repository.findAll(exampleRequest)");
			}
		} else {
			methodBuilder.addStatement("return ($T) repository.findOne(exampleRequest).get()", retourTypeName_);
		}

	}

	/**
	 * 
	 * @param typeReturn
	 * @return
	 */
	private static TypeName getReturn(Type typeReturn) {
		String pojoPackageName = Common.getSpringBaseRepository() + ".pojo";
		return JavaPoetHelper.getClassNameFromGraphQlType(typeReturn, pojoPackageName);
	}

	public JavaFile getJavaFileGenerator(TypeSpec.Builder classBuilder) {
		String comment = " ";
		comment += " doc :" + this.generator.getPathSchemagraphQl() + "\n";
		comment += "\n";
		classBuilder.addJavadoc(comment);

		final JavaFile.Builder javaFileBuilder = JavaFile.builder(this.packageName, classBuilder.build())
				.indent("    ");

		javaFileBuilder.addFileComment("Generated by " + this.getClass().getName());
		final JavaFile javaFile = javaFileBuilder.build();
		return javaFile;
	}

	public List<BeanMethod> getLisBeanMethod() {
		return lisBeanMethod;
	}

	public List<JavaFile> getListJavaFiles() {
		return listJavaFiles;
	}
	
	static class ArgumentHelper{
		

		public ArgumentHelper(String argumentName, TypeName argumentClassName) {
			this.argumentName = argumentName;
			this.argumentClassName = argumentClassName;
		}
		String argumentName;
		TypeName argumentClassName;
	}

}
