package bg.graphql.tool.util;

import java.util.Date;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec.Builder;

import bg.graphql.tool.GeneratorClassType;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.Type;

public class JavaPoetHelper {

	public static void addGetterAndSetter(FieldSpec fieldSpec, Builder classBuilder) {
		addGetter(fieldSpec, classBuilder);
		addSetter(fieldSpec, classBuilder);
	}
	
	
	private static void addSetter(FieldSpec fieldSpec, Builder classBuilder) {
		String setterName  = "set"+capitalizeFirstLetter(fieldSpec.name);
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(setterName).addModifiers(Modifier.PUBLIC);
		methodBuilder.addParameter(fieldSpec.type, fieldSpec.name);
		methodBuilder.addStatement("this."+fieldSpec.name+"="+fieldSpec.name);
		classBuilder.addMethod(methodBuilder.build());	
	}


	public static void addGetter(FieldSpec fieldSpec, Builder classBuilder) {
		String getterName  = "get"+capitalizeFirstLetter(fieldSpec.name);
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(getterName).returns(fieldSpec.type).addModifiers(Modifier.PUBLIC);
		methodBuilder.addStatement("return this."+fieldSpec.name);
		classBuilder.addMethod(methodBuilder.build());	
	}
	
	

	public static String capitalizeFirstLetter(final String str) {
		final String s = str.substring(0, 1).toUpperCase() + str.substring(1);
		return s;
	}


	public static String getSimpleName(TypeName typeName) {
		String s = typeName.toString();
		int i =s.lastIndexOf(".");
		String sLast = s.substring(i+1);
		return sLast;
	}
	

	public static  com.squareup.javapoet.TypeName getClassNameFromGraphQlType(Type type,String packageName) {
		if (type instanceof graphql.language.TypeName) {
			String typeStr= ((graphql.language.TypeName) type).getName();
			return getClassNameFromNameStr(typeStr,packageName);
		}else if (type instanceof NonNullType) {
			Type type2 =  ((NonNullType) type).getType();
			return getClassNameFromGraphQlType(type2,packageName);
		}else if (type instanceof ListType) {
			ListType lType = (ListType) type;
			com.squareup.javapoet.TypeName boxed = getClassNameFromGraphQlType(lType.getType(),packageName);
			ParameterizedTypeName typeName = ParameterizedTypeName.get(ClassName.get(List.class),boxed);
			
			return typeName;
			
		}
		throw new RuntimeException("No class name  for :"+type);
	}
	
	public static ClassName getClassNameFromNameStr(String name,String packageName){
		if (name.equalsIgnoreCase("String")) {
			return ClassName.get(String.class);
		} else if (name.equalsIgnoreCase("Int")) {
			return ClassName.get(Integer.class);
		} else if (name.equalsIgnoreCase("Float")) {
			return ClassName.get(Float.class);
		} else if (name.equalsIgnoreCase("Boolean")) {
			return ClassName.get(Boolean.class);
		} else if (name.equalsIgnoreCase("ID")) {
			return ClassName.get(String.class);
		} else if (name.equalsIgnoreCase("Date")) {
			return ClassName.get(Long.class);
		} else if (name.equalsIgnoreCase("DateTime")) {
			return ClassName.get(Date.class);
		}else {
			return ClassName.get(packageName,name);
		}		
	}
	
	public  static boolean getReturnAsList(Type typeReturn) {
		if (typeReturn instanceof graphql.language.ListType) {
			return true;
		} else if (typeReturn instanceof graphql.language.TypeName) {
			return false;
		}else if (typeReturn instanceof NonNullType) {
			NonNullType lType = (NonNullType) typeReturn;
			return getReturnAsList(lType.getType());
		}
		throw new RuntimeException("No determined if isList "+typeReturn);
	}


}
