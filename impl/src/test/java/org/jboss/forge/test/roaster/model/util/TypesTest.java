/*
 * Copyright 2012-2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.test.roaster.model.util;

import static org.junit.Assert.*;

import java.util.Vector;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.util.Types;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class TypesTest
{
   @Test
   public void testIsBasicType()
   {
      assertTrue(Types.isBasicType("String"));
      assertTrue(Types.isBasicType("Short"));
      assertTrue(Types.isBasicType("int"));
      assertTrue(Types.isBasicType("boolean"));
      assertTrue(Types.isBasicType("Boolean"));
      assertTrue(Types.isBasicType("long"));
      assertTrue(Types.isBasicType("Float"));
      assertTrue(Types.isBasicType("Double"));
   }

   @Test
   public void testAreEquivalent() throws Exception
   {
      assertTrue(Types.areEquivalent("com.example.Domain", "com.example.Domain"));
      assertTrue(Types.areEquivalent("Domain", "com.example.Domain"));
      assertTrue(Types.areEquivalent("com.example.Domain", "Domain"));
      assertTrue(Types.areEquivalent("Domain", "Domain"));
      assertFalse(Types.areEquivalent("com.example.Domain", "com.other.Domain"));
      assertTrue(Types.areEquivalent("com.example.Domain<T>", "Domain"));
      assertTrue(Types.areEquivalent("Domain<T>", "com.example.Domain"));
   }

   @Test
   public void testIsQualified() throws Exception
   {
      assertTrue(Types.isQualified("org.jboss.forge.roaster.JavaParser"));
      assertFalse(Types.isQualified("JavaParser"));
   }

   @Test
   public void testGetPackage() throws Exception
   {
      assertEquals("com.example", Types.getPackage("com.example.Domain"));
      assertEquals("", Types.getPackage("Domain"));
   }

   @Test
   public void testIsSimpleName() throws Exception
   {
      assertTrue(Types.isSimpleName("Domain$"));
      assertFalse(Types.isSimpleName("9Domain$"));
      assertFalse(Types.isSimpleName("com.Domain$"));
      assertFalse(Types.isSimpleName("99"));
      assertFalse(Types.isSimpleName(""));
      assertFalse(Types.isSimpleName("Foo-bar"));
   }

   @Test
   public void testArray()
   {
      assertTrue(Types.isArray("byte[]"));
      assertTrue(Types.isArray("java.lang.Boolean[]"));
      assertTrue(Types.isArray("java.util.Vector[]"));

      assertTrue(Types.isArray(byte[].class.getName()));
      assertTrue(Types.isArray(Boolean[].class.getName()));
      assertTrue(Types.isArray(Types[].class.getName()));

      assertEquals("byte", Types.stripArray(byte[].class.getSimpleName()));
      assertEquals("Boolean", Types.stripArray(Boolean[].class.getSimpleName()));
      assertEquals("Vector", Types.stripArray(Vector[].class.getSimpleName()));

      assertEquals("byte", Types.stripArray(byte[].class.getName()));
      assertEquals("java.lang.Boolean", Types.stripArray(Boolean[].class.getName()));
      assertEquals("java.util.Vector", Types.stripArray(Vector[].class.getName()));

      assertEquals("int", Types.stripArray(int[][][][][].class.getName()));

      assertEquals("int", Types.stripArray(int[][][][][].class.getSimpleName()));
      assertEquals("List<Long>", Types.stripArray("List<Long>[]"));
      assertEquals("java.lang.Class<?>", Types.stripArray("java.lang.Class<?>[]"));
      assertEquals("java.lang.Class<T>", Types.stripArray("java.lang.Class<T>[]"));
      assertEquals("java.lang.Class<LONG_TYPE_VARIABLE_NAME>",
               Types.stripArray("java.lang.Class<LONG_TYPE_VARIABLE_NAME>[]"));
      assertEquals("java.lang.Class<? extends Number>", Types.stripArray("java.lang.Class<? extends Number>[]"));
      assertEquals("java.lang.Class<E extends Enum<E>>", Types.stripArray("java.lang.Class<E extends Enum<E>>[]"));
   }

   @Test
   public void testArrayDimensions()
   {
      assertEquals(0, Types.getArrayDimension(Boolean.class.getName()));
      assertEquals(1, Types.getArrayDimension(int[].class.getName()));
      assertEquals(2, Types.getArrayDimension(int[][].class.getName()));
      assertEquals(3, Types.getArrayDimension(int[][][].class.getName()));

   }

   @Test
   public void testGenerics()
   {
      assertEquals("byte", Types.stripGenerics("byte"));
      assertEquals("byte[]", Types.stripGenerics("byte[]"));
      assertEquals("java.lang.Class", Types.stripGenerics("java.lang.Class"));
      assertEquals("java.lang.Class", Types.stripGenerics("java.lang.Class<?>"));
      assertEquals("java.lang.Class", Types.stripGenerics("java.lang.Class<? extends Number>"));
      assertEquals("java.lang.Class", Types.stripGenerics("java.lang.Class<E extends Enum<E>>"));
      assertEquals("java.lang.Class[]", Types.stripGenerics("java.lang.Class[]"));
      assertEquals("java.lang.Class[]", Types.stripGenerics("java.lang.Class<?>[]"));
      assertEquals("java.lang.Class[]", Types.stripGenerics("java.lang.Class<T>[]"));
      assertEquals("java.lang.Class[]", Types.stripGenerics("java.lang.Class<LONG_TYPE_VARIABLE_NAME>[]"));
      assertEquals("java.lang.Class[]", Types.stripGenerics("java.lang.Class<? extends Number>[]"));
      assertEquals("java.lang.Class[]", Types.stripGenerics("java.lang.Class<E extends Enum<E>>[]"));
      assertEquals("int[]", Types.stripGenerics(int[].class.getName()));
      assertEquals("java.util.List", Types.stripGenerics("java.util.List<String>"));
      assertEquals("java.util.List", Types.stripGenerics("java.util.List<java.lang.String>"));
      assertEquals("java.util.List", Types.stripGenerics("java.util.List<List<String>>"));
      assertEquals("java.util.List", Types.stripGenerics("java.util.List<List<java.lang.String>>"));
   }

   @Test
   public void testStringIsJavaLang()
   {
      assertTrue(Types.isJavaLang("String"));
   }

   @Test
   public void testAssertClassIsNotJavaLang()
   {
      assertFalse(Types.isJavaLang("AssertClass"));
   }

   @Test
   public void testAssertToSimpleName() throws Exception
   {
      assertEquals("List<String>", Types.toSimpleName("java.util.List<java.lang.String>"));
      assertEquals("List<String>", Types.toSimpleName("java.util.List<String>"));
      assertEquals("List<List<String>>", Types.toSimpleName("java.util.List<java.util.List<String>>"));
      assertEquals("List<? extends List<String>>",
               Types.toSimpleName("java.util.List<? extends java.util.List<String>>"));
   }

   @Test
   public void testGetGenericsTypeParameter()
   {
      assertEquals("java.lang.String", Types.getGenericsTypeParameter("java.util.List<java.lang.String>"));
      assertEquals("java.lang.String, java.util.List<java.lang.String>",
               Types.getGenericsTypeParameter("java.util.Map<java.lang.String, java.util.List<java.lang.String>>"));
   }

   @Test
   public void testRecursiveImport()
   {
      {
         final JavaClassSource source = Roaster.create(JavaClassSource.class);
         assertTrue(source.getImports().isEmpty());
         Types.recursiveImport("java.util.Map<com.acme.Foo, java.util.List<? extends com.acme.Bar<com.acme.Baz>>>",
                  source);
         assertEquals(5, source.getImports().size());
         assertEquals("java.util.Map", source.resolveType("Map"));
         assertEquals("java.util.List", source.resolveType("List"));
         assertEquals("com.acme.Foo", source.resolveType("Foo"));
         assertEquals("com.acme.Bar", source.resolveType("Bar"));
         assertEquals("com.acme.Baz", source.resolveType("Baz"));
      }
   }

   @Test
   public void testRecursiveImportWithTypeVariables()
   {
      final JavaClassSource source = Roaster.create(JavaClassSource.class);
      source.addTypeVariable("X");
      source.addTypeVariable("Y");
      Types.recursiveImport("java.util.Map<? extends X, java.util.List<? super Y>>", source);
      assertEquals(2, source.getImports().size());
      assertEquals("java.util.Map", source.resolveType("Map"));
      assertEquals("java.util.List", source.resolveType("List"));
   }

}
