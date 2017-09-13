package me.aflak.filter_processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import me.aflak.utils.Checker;
import me.aflak.utils.Condition;
import me.aflak.utils.Equality;
import me.aflak.utils.Filter;
import me.aflak.filter_annotation.Filterable;
import me.aflak.utils.Operation;
import me.aflak.utils.CheckerSpec;
import me.aflak.utils.ClassData;
import me.aflak.utils.FieldInfo;
import me.aflak.utils.GeneratedPackage;

/**
 * Created by root on 13/08/17.
 */

@AutoService(Processor.class)
public class FilterProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Elements elements;
    private List<ClassData> classDataList;
    private List<Boolean> generated;
    private boolean filterBuilderGenerated;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        classDataList = new ArrayList<>();
        generated = new ArrayList<>();
        filterBuilderGenerated = false;
    }

    private ClassName getClassName(Element element){
        return ClassName.get(elements.getPackageOf(element).getQualifiedName().toString(),
                element.getSimpleName().toString());
    }

    private ClassName getClassNamePrimitive(TypeMirror typeMirror){
        switch (typeMirror.getKind()){
            case INT:
                return ClassName.get(Integer.class);
            case DOUBLE:
                return ClassName.get(Double.class);
            case FLOAT:
                return ClassName.get(Float.class);
            case LONG:
                return ClassName.get(Long.class);
            case SHORT:
                return ClassName.get(Short.class);
            case BOOLEAN:
                return ClassName.get(Boolean.class);
            case BYTE:
                return ClassName.get(Byte.class);
            case CHAR:
                return ClassName.get(Character.class);
        }
        return null;
    }

    private String getGetter(FieldInfo field){
        String name = field.getFieldName();
        String prefix;
        if(field.getFieldClass().equals(ClassName.get(Boolean.class))){
            if(field.isPrimitive()){
                if(name.startsWith("is")) {
                    prefix = "";
                }
                else{
                    prefix = "is";
                    name = name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : "");
                }
            }
            else{
                prefix = "get";
                if(name.startsWith("is")){
                    name = name.substring(2,1).toUpperCase() + (name.length() > 3 ? name.substring(3) : "");
                }
                else{
                    name = name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : "");
                }
            }
        }
        else{
            prefix = "get";
            name = name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : "");
        }

        return prefix+name;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Filterable.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR, "@Filterable can only be applied to classes.");
                return false;
            }

            ClassName className = getClassName(element);
            ClassData classData = new ClassData(className);

            for(Element field : element.getEnclosedElements()){
                if(field.getKind() == ElementKind.FIELD) {
                    boolean isPrimitive = field.asType().getKind().isPrimitive();
                    ClassName fieldClass = isPrimitive?getClassNamePrimitive(field.asType()):getClassName(field);
                    FieldInfo fieldInfo = new FieldInfo(fieldClass, field.getSimpleName().toString(), isPrimitive);
                    classData.add(fieldInfo);
                }
            }

            classDataList.add(classData);
            generated.add(false);
        }

        final ClassName filterBuilderGeneratedClass = ClassName.get(GeneratedPackage.PACKAGE, "FilterBuilder");

        int p=0;
        for(ClassData classData : classDataList){
            if(!generated.get(p)) {
                generated.set(p++, true);
                /**
                 *
                 *
                 *
                 *
                 *  Filter#Class
                 *
                 *
                 *
                 */

                final ClassName filterGeneratedClass = ClassName.get(GeneratedPackage.PACKAGE, classData.getClassName().simpleName() + "Filter");
                final ParameterizedTypeName paramFilterBuilderGeneratedClass = ParameterizedTypeName.get(filterBuilderGeneratedClass, filterGeneratedClass);
                final ParameterizedTypeName paramOperationClass = ParameterizedTypeName.get(ClassName.get(Operation.class), classData.getClassName());
                final ParameterizedTypeName paramConditionClass = ParameterizedTypeName.get(ClassName.get(Condition.class), classData.getClassName());
                final ParameterizedTypeName paramCollectionClass = ParameterizedTypeName.get(ClassName.get(Collection.class), classData.getClassName());
                final ParameterizedTypeName paramListClass = ParameterizedTypeName.get(ClassName.get(List.class), classData.getClassName());

                TypeSpec.Builder filterClass = TypeSpec
                        .classBuilder(filterGeneratedClass)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(paramFilterBuilderGeneratedClass, "builder", Modifier.PRIVATE)
                        .addField(Gson.class, "gson", Modifier.PRIVATE, Modifier.STATIC);

                MethodSpec constructor = MethodSpec
                        .constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("gson = new $T()", Gson.class)
                        .addStatement("builder = new $T(this)", filterBuilderGeneratedClass)
                        .build();

                MethodSpec builderMethod = MethodSpec
                        .methodBuilder("builder")
                        .returns(filterGeneratedClass)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addStatement("return new $T()", filterGeneratedClass)
                        .build();

                MethodSpec buildMethod = MethodSpec
                        .methodBuilder("build")
                        .returns(Filter.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return builder.getFilter()")
                        .build();

                MethodSpec notMethod = MethodSpec
                        .methodBuilder("not")
                        .returns(filterGeneratedClass)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("builder.not()")
                        .addStatement("return this")
                        .build();

                MethodSpec postOperationMethod = MethodSpec
                        .methodBuilder("postOperation")
                        .returns(filterGeneratedClass)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(paramOperationClass, "operation")
                        .addStatement("builder.postOperation(operation)")
                        .addStatement("return this")
                        .build();

                MethodSpec extraConditionMethod = MethodSpec
                        .methodBuilder("extraCondition")
                        .returns(filterGeneratedClass)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(paramConditionClass, "condition")
                        .addStatement("builder.extraCondition(condition)")
                        .addStatement("return this")
                        .build();

                MethodSpec copyMethod = MethodSpec
                        .methodBuilder("copy")
                        .returns(filterGeneratedClass)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("builder.copy()")
                        .addStatement("return this")
                        .build();

                MethodSpec onMethod = MethodSpec
                        .methodBuilder("on")
                        .returns(TypeName.BOOLEAN)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(classData.getClassName(), "object")
                        .addStatement("return check(object, builder.getFilter())")
                        .build();

                MethodSpec onMethodStatic = MethodSpec
                        .methodBuilder("on")
                        .returns(TypeName.BOOLEAN)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(classData.getClassName(), "object")
                        .addParameter(Filter.class, "filter")
                        .addStatement("return check(object, filter)")
                        .build();

                MethodSpec onMethodV2 = MethodSpec
                        .methodBuilder("on")
                        .returns(paramListClass)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(paramCollectionClass, "list")
                        .addStatement("return on(list, builder.getFilter())")
                        .build();

                MethodSpec onMethodV2Static = MethodSpec
                        .methodBuilder("on")
                        .returns(paramListClass)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(paramCollectionClass, "list")
                        .addParameter(Filter.class, "filter")
                        .addStatement("$T result = new $T<>()", paramListClass, ArrayList.class)
                        .beginControlFlow("for($T object : list)", classData.getClassName())
                        .beginControlFlow("if(check(object, filter))")
                        .addStatement("object = onSuccess(object, filter)")
                        .addStatement("result.add(object)")
                        .endControlFlow()
                        .endControlFlow()
                        .addStatement("return result")
                        .build();

                MethodSpec onMethodV3 = MethodSpec
                        .methodBuilder("onSingle")
                        .returns(classData.getClassName())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(paramCollectionClass, "list")
                        .addStatement("return onSingle(list, builder.getFilter())")
                        .build();

                MethodSpec onMethodV3Static = MethodSpec
                        .methodBuilder("onSingle")
                        .returns(classData.getClassName())
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(paramCollectionClass, "list")
                        .addParameter(Filter.class, "filter")
                        .addStatement("$T result = on(list, filter)", paramListClass)
                        .beginControlFlow("if(result.size()==1)")
                        .addStatement("return result.get(0)")
                        .endControlFlow()
                        .addStatement("throw new $T($S)", RuntimeException.class, "Found more than one match")
                        .build();

                MethodSpec onSuccessMethod = MethodSpec
                        .methodBuilder("onSuccess")
                        .returns(classData.getClassName())
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .addParameter(classData.getClassName(), "object")
                        .addParameter(Filter.class, "filter")
                        .beginControlFlow("if(filter.isCopy())")
                        .addStatement("object = gson.fromJson(gson.toJson(object), $T.class)", classData.getClassName())
                        .endControlFlow()
                        .beginControlFlow("if(filter.getPostOperation()!=null)")
                        .addStatement("(($T)filter.getPostOperation()).execute(object)", paramOperationClass)
                        .endControlFlow()
                        .addStatement("return object")
                        .build();

                MethodSpec.Builder checkPrivateMethod = MethodSpec
                        .methodBuilder("check")
                        .returns(TypeName.BOOLEAN)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .addParameter(classData.getClassName(), "object")
                        .addParameter(Filter.class, "filter")
                        .beginControlFlow("for($T eq : filter.getEqualities())", Equality.class);

                boolean firstIf1 = true, firstIf2 = true;
                for (FieldInfo field : classData.getFieldList()) {
                    String getter = getGetter(field);
                    checkPrivateMethod.beginControlFlow("$L(eq.getAttribute().equals($S))", firstIf1 ? "if" : "else if", field.getFieldName());

                    for (CheckerSpec spec : Checker.checkers) {
                        StringBuilder param = new StringBuilder();
                        Class[] types = spec.getCompared();
                        for (int i = 0; i < types.length; i++) {
                            param
                                    .append(", (")
                                    .append(types[i].getCanonicalName())
                                    .append(") eq.getCompared(")
                                    .append(i)
                                    .append(")");
                        }

                        checkPrivateMethod
                                .beginControlFlow("$L(eq.getComparator().equals($S))", firstIf2 ? "if" : "else if", spec.getComparator())
                                .beginControlFlow("if(eq.isNot() == $T.$L(object.$L()$L))", Checker.class, spec.getComparator(), getter, param.toString())
                                .addStatement("return false")
                                .endControlFlow()
                                .endControlFlow();

                        firstIf2 = false;
                    }
                    firstIf1 = false;
                    firstIf2 = true;
                    checkPrivateMethod.endControlFlow();
                }
                checkPrivateMethod
                        .endControlFlow()
                        .beginControlFlow("if(filter.getExtraCondition()!=null)")
                        .beginControlFlow("if(!filter.getExtraCondition().verify(object))")
                        .addStatement("return false")
                        .endControlFlow()
                        .endControlFlow()
                        .addStatement("return true");

                filterClass
                        .addMethod(constructor)
                        .addMethod(builderMethod)
                        .addMethod(buildMethod)
                        .addMethod(checkPrivateMethod.build())
                        .addMethod(onMethod)
                        .addMethod(onMethodStatic)
                        .addMethod(onMethodV2)
                        .addMethod(onMethodV2Static)
                        .addMethod(onMethodV3)
                        .addMethod(onMethodV3Static)
                        .addMethod(notMethod)
                        .addMethod(postOperationMethod)
                        .addMethod(extraConditionMethod)
                        .addMethod(copyMethod)
                        .addMethod(onSuccessMethod);

                for (FieldInfo fieldInfo : classData.getFieldList()) {
                    MethodSpec methodSpec = MethodSpec
                            .methodBuilder(fieldInfo.getFieldName())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(paramFilterBuilderGeneratedClass)
                            .addStatement("builder.setAttribute($S)", fieldInfo.getFieldName())
                            .addStatement("return builder")
                            .build();

                    filterClass.addMethod(methodSpec);
                }

                try {
                    JavaFile.builder(GeneratedPackage.PACKAGE, filterClass.build()).build().writeTo(filer);
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                }
            }
        }


        if(!filterBuilderGenerated) {
            filterBuilderGenerated = true;
            /**
             *
             *
             *
             *  FilterBuilder
             *
             *
             */

            TypeVariableName T = TypeVariableName.get("T");

            TypeSpec.Builder builderClass = TypeSpec
                    .classBuilder(filterBuilderGeneratedClass)
                    .addTypeVariable(T)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(Filter.class, "filter", Modifier.PRIVATE)
                    .addField(Equality.class, "equality", Modifier.PRIVATE)
                    .addField(T, "reference", Modifier.PRIVATE);

            MethodSpec builderConstructor = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(T, "reference")
                    .addStatement("this.filter = new $T()", Filter.class)
                    .addStatement("this.equality = new $T()", Equality.class)
                    .addStatement("this.reference = reference")
                    .build();

            MethodSpec addComparedMethod = MethodSpec
                    .methodBuilder("addCompared")
                    .returns(void.class)
                    .addModifiers(Modifier.PRIVATE)
                    .varargs().addParameter(Object[].class, "objects")
                    .addStatement("equality.addCompared(objects)")
                    .addStatement("filter.add(equality)")
                    .addStatement("equality = new $T()", Equality.class)
                    .build();

            MethodSpec addComparedV2Method = MethodSpec
                    .methodBuilder("addCompared")
                    .returns(void.class)
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("filter.add(equality)")
                    .addStatement("equality = new $T()", Equality.class)
                    .build();


            MethodSpec getFilterMethod = MethodSpec
                    .methodBuilder("getFilter")
                    .returns(Filter.class)
                    .addStatement("return filter")
                    .build();

            MethodSpec notMethod = MethodSpec
                    .methodBuilder("not")
                    .returns(void.class)
                    .addStatement("equality.setNot(true)")
                    .build();

            MethodSpec postOperationMethod = MethodSpec
                    .methodBuilder("postOperation")
                    .returns(void.class)
                    .addParameter(Operation.class, "operation")
                    .addStatement("this.filter.setPostOperation(operation)")
                    .build();

            MethodSpec extraConditionMethod = MethodSpec
                    .methodBuilder("extraCondition")
                    .returns(void.class)
                    .addParameter(Condition.class, "condition")
                    .addStatement("this.filter.setExtraCondition(condition)")
                    .build();

            MethodSpec copyMethod = MethodSpec
                    .methodBuilder("copy")
                    .returns(void.class)
                    .addStatement("this.filter.setCopy(true)")
                    .build();

            MethodSpec setAttributeMethod = MethodSpec
                    .methodBuilder("setAttribute")
                    .addParameter(String.class, "attribute")
                    .addStatement("equality.setAttribute(attribute)")
                    .build();

            List<MethodSpec> builderComparatorsMethods = new ArrayList<>();
            for (CheckerSpec spec : Checker.checkers) {

                MethodSpec.Builder builder = MethodSpec
                        .methodBuilder(spec.getComparator())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(T);

                StringBuilder param = new StringBuilder();
                Class[] types = spec.getCompared();
                for (int i = 0; i < types.length; i++) {
                    builder.addParameter(types[i], "o" + String.valueOf(i + 1));
                    param.append("o").append(i + 1);
                    if (i != types.length - 1) {
                        param.append(", ");
                    }
                }
                String paramStr = param.toString();
                if (paramStr.isEmpty()) {
                    paramStr = "";
                }

                builder.addStatement("equality.setComparator($S)", spec.getComparator())
                        .addStatement("addCompared($L)", paramStr)
                        .addStatement("return reference");

                builderClass.addMethod(builder.build());
            }

            builderClass
                    .addMethod(builderConstructor)
                    .addMethod(addComparedMethod)
                    .addMethod(addComparedV2Method)
                    .addMethod(getFilterMethod)
                    .addMethod(notMethod)
                    .addMethod(postOperationMethod)
                    .addMethod(extraConditionMethod)
                    .addMethod(copyMethod)
                    .addMethod(setAttributeMethod)
                    .addMethods(builderComparatorsMethods);

            try {
                JavaFile.builder(GeneratedPackage.PACKAGE, builderClass.build()).build().writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Filterable.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
