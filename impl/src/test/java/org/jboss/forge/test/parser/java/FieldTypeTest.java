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
import org.jboss.forge.parser.java.source.FieldSource;
import org.jboss.forge.parser.java.source.JavaClassSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FieldTypeTest
{
   @Test
   public void testGetReturnTypeReturnsFullTypeForJavaLang() throws Exception
   {
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class).addField("public Long l;");
      Assert.assertEquals("java.lang.Long", field.getType().getQualifiedName());
      Assert.assertEquals("Long", field.getType().getName());
   }

   @Test
   public void testGetReturnTypeReturnsFullTypeForJavaLangGeneric() throws Exception
   {
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public List<Long> list;");
      field.getOrigin().addImport(List.class);
      Assert.assertEquals("java.util.List", field.getType().getQualifiedName());
      Assert.assertEquals("List", field.getType().getName());
   }

   @Test
   public void testGetReturnTypeObjectArray() throws Exception
   {
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public List[] field;");
      field.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = field.getType();
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
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public List<Long>[] list;");
      field.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = field.getType();
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
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public List list;");
      field.getOrigin().addImport(List.class);
      Assert.assertEquals("java.util.List", field.getType().getQualifiedName());
      Assert.assertFalse(field.getType().isParameterized());
   }

   @Test
   public void testGetReturnTypeObjectParameterized() throws Exception
   {
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public List<Long> list;");
      field.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = field.getType();
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
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public List<?> list;");
      field.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = field.getType();
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
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public Map<String, Long> map;");
      field.getOrigin().addImport(Map.class);
      Type<JavaClassSource> type = field.getType();
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
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public List<List<Long>> map;");
      field.getOrigin().addImport(List.class);
      Type<JavaClassSource> type = field.getType();
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
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public Map<String, List<Long>> map;");
      field.getOrigin().addImport(List.class);
      field.getOrigin().addImport(Map.class);
      Type<JavaClassSource> type = field.getType();
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
      FieldSource<JavaClassSource> field = JavaParser.create(JavaClassSource.class)
               .addField("public Map<String, List<Long>>[] maps;");
      field.getOrigin().addImport(List.class);
      field.getOrigin().addImport(Map.class);
      Type<JavaClassSource> type = field.getType();
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
   public void testFieldTypeByteArrayTest()
   {
      final JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      final FieldSource<JavaClassSource> field = javaClass.addField();
      field.setName("content");
      field.setType(byte[].class);
      Assert.assertEquals("byte", field.getType().getQualifiedName());
      Assert.assertTrue(field.getType().isArray());
      Assert.assertEquals(1, field.getType().getArrayDimensions());
   }

   @Test
   public void testFieldMultidimensionalArray()
   {
      final JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      final FieldSource<JavaClassSource> field = javaClass.addField();
      field.setName("content");
      field.setType(byte[][][].class);
      Assert.assertEquals("byte", field.getType().getQualifiedName());
      Type<JavaClassSource> type = field.getType();
      Assert.assertTrue(type.isArray());
      Assert.assertEquals(3, type.getArrayDimensions());
   }


   @Test
   public void testFieldMultidimensionalArray2()
   {
      final JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      final FieldSource<JavaClassSource> field = javaClass.addField();
      field.setName("content");
      field.setType(java.util.Vector[][][].class);
      Assert.assertEquals("java.util.Vector", field.getType().getQualifiedName());
      Type<JavaClassSource> type = field.getType();
      Assert.assertTrue(type.isArray());
      Assert.assertEquals(3, type.getArrayDimensions());
      Assert.assertEquals("Vector[][][]", field.getType().getName());
   }
   
   @Test
   public void testFieldTypeByteArrayAlternativeDeclarationTest()
   {
      final JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      final FieldSource<JavaClassSource> field = javaClass.addField("public byte content[];");
      Assert.assertEquals("byte[]", field.getType().getName());
      Assert.assertEquals("byte", field.getType().getQualifiedName());
      Assert.assertTrue(field.getType().isArray());
      Assert.assertEquals(1, field.getType().getArrayDimensions());
   }
   
   @Test
   public void testFieldTypeObjectArrayAlternativeDeclarationTest()
   {
      final JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      final FieldSource<JavaClassSource> field = javaClass.addField("public Long content[];");
      Assert.assertEquals("Long[]", field.getType().getName());
      Assert.assertEquals("java.lang.Long", field.getType().getQualifiedName());
      Assert.assertTrue(field.getType().isArray());
      Assert.assertEquals(1, field.getType().getArrayDimensions());
   }
   
   @Test
   public void testFieldTypeObjectArrayMixedDimensionTest()
   {
      final JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      final FieldSource<JavaClassSource> field = javaClass.addField("public Long[] content[];");
      Assert.assertEquals("Long[][]", field.getType().getName());
      Assert.assertEquals("java.lang.Long", field.getType().getQualifiedName());
      Assert.assertTrue(field.getType().isArray());
      Assert.assertEquals(2, field.getType().getArrayDimensions());
   }
}
