/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.test.parser.java;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.source.JavaClassSource;
import org.jboss.forge.parser.java.source.MethodSource;
import org.jboss.forge.parser.java.source.ParameterSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MethodSignatureTest
{
   @Test
   public void testEmptyMethodSignature() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public void hello()");
      String signature = method.toSignature();
      assertEquals("public hello() : void", signature);
   }

   @Test
   public void testMethodSignatureParams() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public void hello(String foo, int bar)");
      String signature = method.toSignature();
      assertEquals("public hello(String, int) : void", signature);
   }

   @Test
   public void testMethodParams() throws Exception
   {
      MethodSource<JavaClassSource> method = JavaParser.create(JavaClassSource.class).addMethod("public void hello(String foo, int bar)");
      List<ParameterSource<JavaClassSource>> parameters = method.getParameters();

      Assert.assertEquals("String", parameters.get(0).getType().toString());
      Assert.assertEquals("int", parameters.get(1).getType().toString());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testUnmodifiableMethodParams() throws Exception
   {
      JavaParser.create(JavaClassSource.class).addMethod("public void hello(String foo, int bar)").getParameters().add(null);
   }

   @Test
   public void testMethodVisibility() throws Exception {
       JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);

       MethodSource<JavaClassSource> method = javaClass.addMethod("public void hello()");
       assertVisibility(Visibility.PUBLIC, method);
       assertVisibility("public", method);

       method = javaClass.addMethod("protected void hello()");
       assertVisibility(Visibility.PROTECTED, method);
       assertVisibility("protected", method);

       method = javaClass.addMethod("private void hello()");
       assertVisibility(Visibility.PRIVATE, method);
       assertVisibility("private", method);

       method = javaClass.addMethod("void hello()");
       assertVisibility(Visibility.PACKAGE_PRIVATE, method);
       assertVisibility("", method);
   }

   @Test
   public void testMethodVisibilityWithSetter() throws Exception {
       JavaClassSource javaClass = JavaParser.create(JavaClassSource.class);
       MethodSource<JavaClassSource> method = javaClass.addMethod().setName("hello");
       assertVisibility("", method);

       method.setVisibility(Visibility.PUBLIC);
       assertVisibility("public", method);

       method.setVisibility(Visibility.PROTECTED);
       assertVisibility("protected", method);

       method.setVisibility(Visibility.PRIVATE);
       assertVisibility("private", method);

       method.setVisibility(Visibility.PACKAGE_PRIVATE);
       assertVisibility("", method);
   }

   private void assertVisibility(Visibility visibility, MethodSource<JavaClassSource> method) {
       Assert.assertEquals(visibility, method.getVisibility());
   }

   private void assertVisibility(String visibility, MethodSource<JavaClassSource> method) {
       Assert.assertEquals(visibility, method.getVisibility().toString());
   }
}
