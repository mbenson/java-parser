/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.test.parser.java;

import java.util.List;
import java.util.Map;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.source.JavaClassSource;
import org.jboss.forge.parser.java.source.MethodSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MethodReturnTypeTest
{
   @Test
   public void testGetReturnTypeReturnsFullTypeForJavaLang() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public Long getLong()");
      Assert.assertEquals("java.lang.Long", method.getReturnType().getQualifiedName());
   }

   @Test
   public void testGetReturnTypeReturnsFullTypeForJavaLangGeneric() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public List<Long> getLong(return null;)");
      method.getOrigin().addImport(List.class);
      Assert.assertEquals("java.util.List", method.getReturnType().getQualifiedName());
   }

   @Test
   public void testGetQualifiedReturnTypePrimitiveArray() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public long[] getLongArray()");
      Assert.assertEquals("long", method.getReturnType().getQualifiedName());
      Assert.assertTrue(method.getReturnType().isArray());
      Assert.assertEquals(1, method.getReturnType().getArrayDimensions());
   }

   @Test
   public void testGetQualifiedReturnTypeObjectArray() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public Long[] getLongArray()");
      Assert.assertEquals("java.lang.Long", method.getReturnType().getQualifiedName());
      Assert.assertTrue(method.getReturnType().isArray());
      Assert.assertEquals(1, method.getReturnType().getArrayDimensions());
   }

   @Test
   public void testGetQualifiedReturnTypeNDimensionObjectArray() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public Long[][] getLongArray()");
      Assert.assertEquals("java.lang.Long", method.getReturnType().getQualifiedName());
      Assert.assertTrue(method.getReturnType().isArray());
      Assert.assertEquals(2, method.getReturnType().getArrayDimensions());
   }

   @Test
   public void testGetQualifiedReturnTypeObjectArrayOfImportedType() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public List[] getListArray()");
      method.getOrigin().addImport(List.class);
      Assert.assertEquals("java.util.List", method.getReturnType().getQualifiedName());
      Assert.assertTrue(method.getReturnType().isArray());
      Assert.assertEquals(1, method.getReturnType().getArrayDimensions());
   }

   @Test
   public void testGetQualifiedReturnTypeImportedObjectArrayParameterizedImportedType() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public List<Long>[] getListArray()");
      method.getOrigin().addImport(List.class);
      Assert.assertEquals("java.util.List", method.getReturnType().getQualifiedName());
      Assert.assertTrue(method.getReturnType().isArray());
      Assert.assertEquals(1, method.getReturnType().getArrayDimensions());
   }

   @Test
   public void testGetReturnTypePrimitiveObjectArray() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public long[] getList(return null;)");
      method.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("long", type.getQualifiedName());
      Assert.assertFalse(type.isParameterized());
      Assert.assertFalse(type.isWildcard());
      Assert.assertTrue(type.isPrimitive());
      Assert.assertFalse(type.isQualified());
      Assert.assertTrue(type.isArray());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(0, arguments.size());
   }

   @Test
   public void testGetReturnTypeBoxedObjectArray() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public Long[] getList(return null;)");
      method.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.lang.Long", type.getQualifiedName());
      Assert.assertFalse(type.isParameterized());
      Assert.assertFalse(type.isWildcard());
      Assert.assertFalse(type.isPrimitive());
      Assert.assertFalse(type.isQualified());
      Assert.assertTrue(type.isArray());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(0, arguments.size());
   }

   @Test
   public void testGetReturnTypeObjectArray() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public List[] getList(return null;)");
      method.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertFalse(type.isParameterized());
      Assert.assertFalse(type.isWildcard());
      Assert.assertFalse(type.isPrimitive());
      Assert.assertFalse(type.isQualified());
      Assert.assertTrue(type.isArray());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(0, arguments.size());
   }

   @Test
   public void testGetReturnTypeObjectArrayParameterized() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public List<Long>[] getList(return null;)");
      method.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());
      Assert.assertFalse(type.isWildcard());
      Assert.assertFalse(type.isPrimitive());
      Assert.assertFalse(type.isQualified());
      Assert.assertTrue(type.isArray());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(1, arguments.size());
   }

   @Test
   public void testGetReturnTypeObjectUnparameterized() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public List getLong(return null;)");
      method.getOrigin().addImport(List.class);
      Assert.assertEquals("java.util.List", method.getReturnType().getQualifiedName());
      Assert.assertFalse(method.getReturnType().isParameterized());
   }

   @Test
   public void testGetReturnTypeObjectParameterized() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public List<Long> getList(return null;)");
      method.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(1, arguments.size());
      Assert.assertEquals("Long", arguments.get(0).getName());
      Assert.assertEquals("java.lang.Long", arguments.get(0).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectWildcard() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public List<?> getList(return null;)");
      method.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(1, arguments.size());
      Assert.assertEquals("?", arguments.get(0).getName());
      Assert.assertEquals("?", arguments.get(0).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectParameterizedMultiple() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public Map<String, Long> getMap(return null;)");
      method.getOrigin().addImport(Map.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.util.Map", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(2, arguments.size());
      Assert.assertEquals("String", arguments.get(0).getName());
      Assert.assertEquals("java.lang.String", arguments.get(0).getQualifiedName());

      Assert.assertEquals("Long", arguments.get(1).getName());
      Assert.assertEquals("java.lang.Long", arguments.get(1).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectParameterizedNested() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public List<List<Long>> getLists(return null;)");
      method.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.util.List", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(1, arguments.size());
      Assert.assertEquals("List", arguments.get(0).getName());
      Assert.assertEquals("java.util.List", arguments.get(0).getQualifiedName());

      Assert.assertEquals(1, arguments.size());
      Assert.assertEquals("Long", arguments.get(0).getTypeArguments().get(0).getName());
      Assert.assertEquals("java.lang.Long", arguments.get(0).getTypeArguments().get(0).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectParameterizedMultipleNested() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public Map<String, List<Long>> getMap(return null;)");
      method.getOrigin().addImport(List.class);
      method.getOrigin().addImport(Map.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.util.Map", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(2, arguments.size());
      Assert.assertEquals("String", arguments.get(0).getName());
      Assert.assertEquals("java.lang.String", arguments.get(0).getQualifiedName());

      Assert.assertEquals("List", arguments.get(1).getName());
      Assert.assertEquals("java.util.List", arguments.get(1).getQualifiedName());
   }

   @Test
   public void testGetReturnTypeObjectParameterizedArrayMultipleNested() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class)
               .addMethod("public Map<String, List<Long>>[] getMaps(return null;)");
      method.getOrigin().addImport(List.class);
      method.getOrigin().addImport(Map.class);
      Type<JavaClassSource> type = method.getReturnType();
      Assert.assertEquals("java.util.Map", type.getQualifiedName());
      Assert.assertTrue(type.isParameterized());

      List<Type<JavaClassSource>> arguments = type.getTypeArguments();

      Assert.assertEquals(2, arguments.size());
      Assert.assertEquals("String", arguments.get(0).getName());
      Assert.assertEquals("java.lang.String", arguments.get(0).getQualifiedName());

      Assert.assertEquals("List", arguments.get(1).getName());
      Assert.assertEquals("java.util.List", arguments.get(1).getQualifiedName());
   }

}
