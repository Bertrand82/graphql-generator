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
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import bg.generated.graphql.model.Post;
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
	String  pathSchemagraphQl ="";
	String packageName = "bg.generated.graphql";
	TYPE type;

	public GeneratorDataFetcher(TYPE type, List<ObjectTypeDefinition> listDefinition, String pathSourceSchema) {
		this.type = type;
		this.pathSchemagraphQl = pathSourceSchema;
		objectTypeDefinitions = listDefinition;
		AnnotationSpec.Builder annotation = AnnotationSpec.builder(com.netflix.graphql.dgs.DgsComponent.class);
		
		for (ObjectTypeDefinition objectTypeDefinition : listDefinition) {
			for (FieldDefinition fieldDefinition : objectTypeDefinition.getFieldDefinitions()) {
				BeanMethod umb  = generateMethod_(fieldDefinition);
				lisBeanMethod.add(umb) ;
			}
		}
		generateClasses();
		
	}

	private void generateClasses() {
		Set<TypeName> setTypes= new HashSet<TypeName>();
		for(BeanMethod bm:lisBeanMethod) {
			setTypes.add(bm.getRetourTypeNameSpan());
		}
		for (TypeName typeName :setTypes) {
			String s = "graphSql_"+JavaPoetHelper.getSimpleName(typeName) + "_DataFetcher2";
			System.err.println("TypeSpec name ->"+s+"<-");
			AnnotationSpec.Builder annotation = AnnotationSpec.builder(com.netflix.graphql.dgs.DgsComponent.class);
		    TypeSpec.Builder classBuilder = TypeSpec.classBuilder(s).addModifiers(Modifier.PUBLIC);
			classBuilder.addAnnotation(annotation.build());
			addMethodFetcher(classBuilder,typeName);
			// @Autowired
		    //private ShowRepository repository;
			String nameTypeStr = JavaPoetHelper.getSimpleName(typeName);
			/////
			
			/////
			
			ClassName typeRepository =  ClassName.get(Common.getSpringBaseRepository()+".repository", nameTypeStr+"Repository");
			FieldSpec.Builder fieldRepository= FieldSpec.builder(typeRepository, "repository").addAnnotation(Autowired.class);
			classBuilder.addField(fieldRepository.build());
			JavaFile  jf  = getJavaFileGenerator(classBuilder);
			listJavaFiles.add(jf);
		}
		 
	}
	
	

	

	private void addMethodFetcher(Builder classBuilder2,TypeName typeName) {
		for (BeanMethod beanMethod : this.lisBeanMethod) {
			if(beanMethod.getRetourTypeNameSpan().equals(typeName)) {
				classBuilder2.addMethod(beanMethod.getMethodBuilder().build());
				
			}
		}
		
	}
	
	private static TypeName getReturnTypeSpan(Type typeReturn) {

		if (typeReturn instanceof graphql.language.ListType) {
			ListType listType = (ListType) typeReturn;
			String typeNameList = ((graphql.language.TypeName) listType.getType()).getName();
			ClassName typeName1 = ClassName.get(List.class);
			TypeName typeName2 = ClassName.get(GeneratorClassType.packageName, typeNameList);
			return typeName2;			
		} else if (typeReturn instanceof graphql.language.TypeName) {
			graphql.language.TypeName typeName = (graphql.language.TypeName) typeReturn;
			TypeName typeName3 = ClassName.get(GeneratorClassType.packageName, typeName.getName());
			return typeName3;
		}else if (typeReturn instanceof NonNullType) {
			NonNullType lType = (NonNullType) typeReturn;
			Type typeEmbeded  = lType.getType();
			return getReturnTypeSpan(typeEmbeded);
		}
		throw new RuntimeException("No typeName for "+typeReturn);
	}


	private BeanMethod generateMethod_(FieldDefinition fieldDefinition) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(fieldDefinition.getName())
				.addModifiers(Modifier.PUBLIC);
		
		 List<String> listArgument = new ArrayList<String>();
		for (InputValueDefinition input : fieldDefinition.getInputValueDefinitions()) {
			String argumentName = input.getName() ;
			listArgument.add(argumentName);
			com.squareup.javapoet.TypeName argumentClassName = JavaPoetHelper.getClassNameFromGraphQlType(input.getType());
			AnnotationSpec.Builder argumentAnnotation = AnnotationSpec.builder(com.netflix.graphql.dgs.InputArgument.class);
			argumentAnnotation.addMember("value", "\""+argumentName+"\"","");// field = "pouet"
			TypeName argumentClassNameAnnoted = argumentClassName.annotated(argumentAnnotation.build());
			
			methodBuilder.addParameter(argumentClassNameAnnoted,argumentName);
		}

		AnnotationSpec.Builder annotationMethod = AnnotationSpec.builder(com.netflix.graphql.dgs.DgsData.class);
		// parentType = "Query"
		annotationMethod.addMember("parentType", "\"" + type.name + "\"");
		annotationMethod.addMember("field", "\"" + fieldDefinition.getName() + "\"");
		
		methodBuilder.addAnnotation(annotationMethod.build());
		TypeName retourTypeName_ = getReturn(fieldDefinition.getType());
		TypeName retourTypeNameSpan = getReturnTypeSpan(fieldDefinition.getType());
		boolean isRetourAsList = JavaPoetHelper.getReturnAsList(fieldDefinition.getType());
		boolean isPageable=false;
		methodBuilder.addStatement("$T oAsProbe  = new $T()",retourTypeNameSpan,retourTypeNameSpan);
		for(String argumentName :listArgument) {
			if (argumentName.contentEquals("offset")) {
				isPageable = true;
			}else if (argumentName.contentEquals("count")) {
				isPageable = true;
			}else {
			String methodSetter = "set"+JavaPoetHelper.capitalizeFirstLetter(argumentName);
			methodBuilder.addStatement("oAsProbe."+methodSetter+"("+argumentName+")",retourTypeNameSpan,retourTypeNameSpan);
			}
		}
		if (isPageable) {
			methodBuilder.addStatement("int pageNb = offset/count");
			methodBuilder.addStatement("$T pageable = $T.of(pageNb,count)",Pageable.class, PageRequest.class);
		}
		// 	ExampleMatcher showMatcher = ExampleMatcher.matchingAny().withIgnoreCase("releaseYear").withNullHandler(ExampleMatcher.NullHandler.INCLUDE)	.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
		String statement ="$T exampleMatcher = ExampleMatcher.matchingAny()";
		for(String argumentName :listArgument) {
			statement+=".withIgnoreCase(\""+argumentName+"\")";
		}
		methodBuilder.addStatement(statement, ExampleMatcher.class);
		methodBuilder.addStatement("$T ex  = (Example.of(oAsProbe, exampleMatcher))", Example.class);
		if (isRetourAsList) {
			if (isPageable) {
				methodBuilder.addStatement(" $T<$T> page = repository.findAll(ex,pageable)", Page.class,retourTypeNameSpan);
				methodBuilder.addStatement("return page.getContent()");
			}else {
			methodBuilder.addStatement("return repository.findAll(ex)");
			}
		}else {
			methodBuilder.addStatement("return ($T) repository.findOne(ex).get()",retourTypeName_);
		}
		methodBuilder.returns(retourTypeName_);
		BeanMethod umb = new BeanMethod(retourTypeNameSpan,methodBuilder);
		return umb;
	}


/**
 * TODO Mutaliser avec 
 * @param typeReturn
 * @return
 */
	private static TypeName getReturn(Type typeReturn) {
		return JavaPoetHelper.getClassNameFromGraphQlType(typeReturn);		
	}
	

	public JavaFile getJavaFileGenerator(TypeSpec.Builder classBuilder) {
		String comment = " ";
		comment += " doc :"+this.pathSchemagraphQl+"\n";
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

}
