package com.github.yuanmomo.mybatis.mbg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

/**
 *  Merge java class files generated by MBG, like java bean, mapper ,example and sql provider classes.
 *
 * Created by Hongbin.Yuan on 2017-06-11 18:52.
 */

public class JavaFilesMergeUtil extends DefaultShellCallback {

    private boolean overwrite;
    private boolean isMergeSupported;

    public JavaFilesMergeUtil(boolean overwrite) {
        super(overwrite);
    }

    public JavaFilesMergeUtil(boolean overwrite,  boolean isMergeSupported) {
        super(overwrite);
        this.isMergeSupported = isMergeSupported;
    }

    @Override
    public void refreshProject(String project) { }

    @Override
    public boolean isMergeSupported() {
        return isMergeSupported;
    }

    @Override
    public boolean isOverwriteEnabled() {
        return overwrite;
    }


    @Override
    public String mergeJavaFile(String newFileSource,
                                File existingFile,
                                String[] javadocTags,
                                String fileEncoding) throws ShellException {
        // parse the files
        FileInputStream existingFileInputStream = null;
        try {
            existingFileInputStream = new FileInputStream(existingFile);
        } catch (FileNotFoundException e) {
            return null;
        }
        CompilationUnit newFileCU = JavaParser.parse(newFileSource);
        CompilationUnit existingFileCU = JavaParser.parse(existingFileInputStream);

        // merger imports
        NodeList<ImportDeclaration> newImportList =  newFileCU.getImports();
        NodeList<ImportDeclaration> existingImportList =  existingFileCU.getImports();

        // check null
        if(existingImportList == null){
            existingImportList = new NodeList<>();
        }
        if(newImportList != null && newImportList.size() > 0) {
            for (ImportDeclaration newImportDeclaration : newImportList) {
                // check if exists in existing file
                boolean exists =false;
                for (ImportDeclaration existImportDeclaration : existingImportList) {
                    if(existImportDeclaration.getNameAsString().equals(newImportDeclaration.getNameAsString())){
                        exists = true;
                    }
                }
                if(!exists){
                    existingImportList.add(newImportDeclaration);
                }
            }
        }

        // delete mbg generated fields,constructor and inner class
        // Go through all the fields in the file
        NodeList<TypeDeclaration<?>> existingTypes = existingFileCU.getTypes();
        for (TypeDeclaration<?> type : existingTypes) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();

            Iterator<BodyDeclaration<?>> iterator = members.iterator();

            while(iterator.hasNext()){
                boolean isMBGMethod = false;

                BodyDeclaration<?> member = iterator.next();
                if (member instanceof FieldDeclaration   // field
                        || member instanceof MethodDeclaration // method
                        || member instanceof ClassOrInterfaceDeclaration // inner class in example
                        || member instanceof ConstructorDeclaration ) { //  constructor
                    NodeWithJavadoc field = (NodeWithJavadoc) member;
                    Optional<Javadoc> javadocOptional = field.getJavadoc();
                    if(javadocOptional != null &&  javadocOptional.isPresent()  ) {
                        Javadoc javadoc = javadocOptional.get();
                        if(javadoc != null
                                && javadoc.getBlockTags() != null
                                && javadoc.getBlockTags().size() > 0){
                            for (JavadocBlockTag javadocBlockTag : javadoc.getBlockTags()) {
                                if(javadocBlockTag.toText().contains(MergeConstants.NEW_ELEMENT_TAG)){
                                    if(member instanceof MethodDeclaration) {
                                        isMBGMethod = true;
                                    }
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }

                // if the custom methods in mapper files with @Select(...,ToDoSqlProvider.ALL_COLUMN_FIELDS,...) annotations, then update the new @Results annotation.
                if( ! isMBGMethod){ // is custom method
                    // check if there is a @Select(...,ToDoSqlProvider.ALL_COLUMN_FIELDS,...)
                    if(member instanceof MethodDeclaration) {
                        Optional<AnnotationExpr> selectAnnotation = member.getAnnotationByName(Select.class.getSimpleName());
                        Optional<AnnotationExpr> resultsAnnotation = member.getAnnotationByName(Results.class.getSimpleName());
                        if (selectAnnotation.isPresent()
                                && resultsAnnotation.isPresent()
                                && isContainsAnnotation(selectAnnotation.get().getChildNodes(), "ALL_COLUMN_FIELDS")) {
                            resultsAnnotation.get().remove();

                            // add new Results
                            BodyDeclaration selectByExample = findMBGMethod(newFileCU, "selectByPrimaryKey");

                            Optional<AnnotationExpr> newResultsAnnotation = selectByExample.getAnnotationByName(Results.class.getSimpleName());
                            if(newResultsAnnotation.isPresent()){
                                member.addAnnotation(newResultsAnnotation.get());
                            }
                        }
                    }
                }
            }
        }

        // add new data into  exists
        NodeList<TypeDeclaration<?>> newTypes = newFileCU.getTypes();
        for (int i = 0; i < newTypes.size(); i++) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = newTypes.get(i).getMembers();
            NodeList<BodyDeclaration<?>> existMembers = existingTypes.get(i).getMembers();

            Iterator<BodyDeclaration<?>> iterator = members.iterator();
            while(iterator.hasNext()){
                BodyDeclaration<?> member = iterator.next();
                if (member instanceof FieldDeclaration   // field
                        || member instanceof MethodDeclaration // method
                        || member instanceof ClassOrInterfaceDeclaration // inner class in example
                        || member instanceof ConstructorDeclaration ) { //  constructor
                    NodeWithJavadoc field = (NodeWithJavadoc) member;
                    Optional<Javadoc> javadocOptional = field.getJavadoc();
                    if(javadocOptional.isPresent()) {
                        Javadoc javadoc = javadocOptional.get();
                        if(javadoc != null
                                && javadoc.getBlockTags() != null
                                && javadoc.getBlockTags().size() > 0){
                            for (JavadocBlockTag javadocBlockTag : javadoc.getBlockTags()) {
                                if(javadocBlockTag.toText().contains(MergeConstants.NEW_ELEMENT_TAG)){
                                    existMembers.add(member);
                                }
                            }
                        }
                    }
                }
            }
        }




        // return merger result
        return existingFileCU.toString();
    }

    private static boolean isContainsAnnotation(List<Node> childNodes,String target){
        if(StringUtils.isBlank(target)){
            return false;
        }

        if(childNodes != null && childNodes.size() > 0){
            for (Node childNode : childNodes) {
                if(childNode.getChildNodes() != null
                        && childNode.getChildNodes().size() > 1){
                    return isContainsAnnotation(childNode.getChildNodes(),target);
                }else{
//                    System.out.println(childNode);
                    if(childNode.toString().equals(target)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static BodyDeclaration findMBGMethod(CompilationUnit fileCU,String target){
        NodeList<TypeDeclaration<?>> newTypes = fileCU.getTypes();
        for (int i = 0; i < newTypes.size(); i++) {
            // Go through  methods
            List<MethodDeclaration> methods = newTypes.get(i).getMethodsByName(target);
            if(methods != null && methods.size() > 0){
                for (MethodDeclaration method : methods) {
                    NodeWithJavadoc field = (NodeWithJavadoc) method;
                    Optional<Javadoc> javadocOptional = field.getJavadoc();
                    if(javadocOptional != null &&  javadocOptional.isPresent()  ) {
                        Javadoc javadoc = javadocOptional.get();
                        if(javadoc != null
                                && javadoc.getBlockTags() != null
                                && javadoc.getBlockTags().size() > 0){
                            for (JavadocBlockTag javadocBlockTag : javadoc.getBlockTags()) {
                                if(javadocBlockTag.toText().contains(MergeConstants.NEW_ELEMENT_TAG)){
                                    return method;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;

    }
}
