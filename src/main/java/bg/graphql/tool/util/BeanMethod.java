package bg.graphql.tool.util;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

public class BeanMethod {

	
	private final TypeName retourTypeNameSpan ;
	private final MethodSpec.Builder methodBuilder;
	
	public BeanMethod(TypeName retourTypeNameSpan, Builder methodBuilder) {
		super();
		this.retourTypeNameSpan = retourTypeNameSpan;
		this.methodBuilder = methodBuilder;
	}

	public TypeName getRetourTypeNameSpan() {
		return retourTypeNameSpan;
	}

	public MethodSpec.Builder getMethodBuilder() {
		return methodBuilder;
	}
	
}
