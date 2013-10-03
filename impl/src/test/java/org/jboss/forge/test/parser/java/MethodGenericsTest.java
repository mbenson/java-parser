/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.test.parser.java;

import java.util.List;
import java.util.regex.Pattern;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.source.JavaClassSource;
import org.jboss.forge.parser.java.source.MethodSource;
import org.jboss.forge.parser.java.source.TypeVariableSource;
import org.junit.Assert;
import org.junit.Test;

public class MethodGenericsTest
{

   @Test
   public void addAndRemoveGenericType() throws ClassNotFoundException
   {
      JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      
      MethodSource<JavaClassSource> method = javaClass.addMethod();
      method.addTypeVariable().setName("T");
      
      Assert.assertTrue(method.toString().contains("<T>"));
      Assert.assertTrue(method.getTypeVariables().get(0).getBounds().isEmpty());
      method.removeTypeVariable("T");
      Assert.assertFalse(method.toString().contains("<T>"));
   }

   @Test
   public void addMultipleGenerics() throws ClassNotFoundException
   {
      JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      MethodSource<JavaClassSource> method = javaClass.addMethod();

      method.addTypeVariable().setName("I");
      method.addTypeVariable().setName("O");
      Assert.assertTrue(Pattern.compile("<I, *O>").matcher(method.toString()).find());
      method.removeTypeVariable("I");
      Assert.assertTrue(method.toString().contains("<O>"));
   }

   @Test
   public void getMethodGenerics() throws ClassNotFoundException
   {
      JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
      MethodSource<JavaClassSource> method = javaClass.addMethod();

      method.addTypeVariable().setName("I");
      method.addTypeVariable().setName("O");
      List<TypeVariableSource<JavaClassSource>> typeVariables = method.getTypeVariables();
      Assert.assertNotNull(typeVariables);
      Assert.assertEquals(2, typeVariables.size());
      Assert.assertEquals("I", typeVariables.get(0).getName());
      Assert.assertTrue(typeVariables.get(0).getBounds().isEmpty());
      Assert.assertEquals("O", typeVariables.get(1).getName());
      Assert.assertTrue(typeVariables.get(1).getBounds().isEmpty());
   }

}
