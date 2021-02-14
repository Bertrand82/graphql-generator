package bg.persistence.tool.springiser;



import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

public class MethodGenerator {
   
    public static MethodSpec getGetMethodSpec(String name, TypeName typeName) {
      
        return MethodSpec.methodBuilder("get"+ name.substring(0, 1).toUpperCase() + name.substring(1) )
                .addModifiers(Modifier.PUBLIC)
                .returns(typeName)
                .addStatement("return $N", name)
                .build();
    }
    
    
    public static MethodSpec getSetMethodSpec(String name, TypeName typeName) {
        
        return MethodSpec.methodBuilder("set"+ name.substring(0, 1).toUpperCase() + name.substring(1) )
                .addModifiers(Modifier.PUBLIC)
                .addParameter(typeName, name)
                .addStatement("this.$N = $N", name, name)
                .build();
    }

  
}