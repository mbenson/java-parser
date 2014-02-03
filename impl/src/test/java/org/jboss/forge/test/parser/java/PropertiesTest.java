/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.test.parser.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.source.FieldSource;
import org.jboss.forge.parser.java.source.JavaClassSource;
import org.jboss.forge.parser.java.source.JavaEnumSource;
import org.jboss.forge.parser.java.source.JavaInterfaceSource;
import org.jboss.forge.parser.java.source.JavaSource;
import org.jboss.forge.parser.java.source.MethodSource;
import org.jboss.forge.parser.java.source.ParameterSource;
import org.jboss.forge.parser.java.source.PropertyHolderSource;
import org.jboss.forge.parser.java.source.PropertySource;
import org.jboss.forge.parser.java.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PropertiesTest<O extends JavaSource<O> & PropertyHolderSource<O>>
{
   public enum PropertyComponent
   {
      FIELD
      {
         @Override
         String format(Class<?> type, String s)
         {
            return s;
         }
      },
      ACCESSOR
      {
         @Override
         String format(Class<?> type, String s)
         {
            return (boolean.class.equals(type) ? "is" : "get") + Strings.capitalize(s);
         }
      },
      MUTATOR
      {
         @Override
         String format(Class<?> type, String s)
         {
            return "set" + Strings.capitalize(s);
         }
      };

      abstract String format(Class<?> type, String s);
   }

   @Parameters(name = "{2} {1}.{3}")
   public static List<Object[]> createParameters()
   {
      final List<Object[]> parameters = new ArrayList<Object[]>();
      parameters.add(new Object[] { JavaClassSource.class, "MockClass", String.class, "field",
               EnumSet.of(PropertyComponent.FIELD) });
      parameters.add(new Object[] { JavaEnumSource.class, "MockEnum", String.class, "field",
               EnumSet.of(PropertyComponent.FIELD) });
      parameters.add(new Object[] { JavaInterfaceSource.class, "MockInterface", int.class, "count",
               EnumSet.of(PropertyComponent.ACCESSOR) });
      parameters.add(new Object[] { JavaInterfaceSource.class, "BigInterface", boolean.class, "verbose",
               EnumSet.of(PropertyComponent.ACCESSOR, PropertyComponent.MUTATOR) });
      return parameters;
   }

   @Parameter(0)
   public Class<O> sourceType;

   @Parameter(1)
   public String resourceName;

   @Parameter(2)
   public Class<?> type;

   @Parameter(3)
   public String name;

   @Parameter(4)
   public Set<PropertyComponent> existingItems;

   private O source;

   @Before
   public void reset()
   {
      final InputStream stream = JavaClassTest.class.getResourceAsStream(String.format(
               "/org/jboss/forge/grammar/java/%s.java", resourceName));
      source = JavaParser.parse(sourceType, stream);
   }

   @Test
   public void testHasProperty()
   {
      assertTrue(source.hasProperty(name));
      assertFalse(source.hasProperty("noSuchProperty"));
   }

   @Test
   public void testGetPropertyByName()
   {
      PropertySource<O> property = source.getProperty(name);
      assertEquals(name, property.getName());
      assertTrue(property.getType().isType(type));
   }

   @Test
   public void testIsReadable()
   {
      assertEquals(existingItems.contains(PropertyComponent.ACCESSOR), source.getProperty(name).isReadable());
   }

   @Test
   public void testIsWritable()
   {
      assertEquals(existingItems.contains(PropertyComponent.MUTATOR), source.getProperty(name).isWritable());
   }

   @Test
   public void testHasField()
   {
      assertEquals(existingItems.contains(PropertyComponent.FIELD), source.getProperty(name).hasField());
   }

   @Test
   public void testGetField()
   {
      final FieldSource<O> field = source.getProperty(name).getField();

      if (!existingItems.contains(PropertyComponent.FIELD))
      {
         assertNull(field);
         return;
      }

      assertNotNull(field);
      assertEquals(name, field.getName());
      assertTrue(field.getType().isType(type));
   }

   @Test
   public void testGetAccessor()
   {
      final MethodSource<O> accessor = source.getProperty(name).getAccessor();

      if (!existingItems.contains(PropertyComponent.ACCESSOR))
      {
         assertNull(accessor);
         return;
      }
      assertNotNull(accessor);

      assertEquals(PropertyComponent.ACCESSOR.format(type, name), accessor.getName());
      assertTrue(accessor.getReturnType().isType(type));
   }

   @Test
   public void testGetMutator()
   {
      final MethodSource<O> mutator = source.getProperty(name).getMutator();

      if (!existingItems.contains(PropertyComponent.MUTATOR))
      {
         assertNull(mutator);
         return;
      }
      assertNotNull(mutator);

      assertEquals(PropertyComponent.MUTATOR.format(type, name), mutator.getName());
      assertTrue(mutator.isReturnTypeVoid());
      assertEquals(1, mutator.getParameters().size());
      assertTrue(mutator.getParameters().get(0).getType().isType(type));
   }

   @Test
   public void testCreateAccessor()
   {
      assumeFalse(existingItems.contains(PropertyComponent.ACCESSOR));

      final MethodSource<O> accessor = source.getProperty(name).createAccessor();
      assertTrue(source.hasMethod(accessor));

      assertTrue(source.isInterface() || accessor.isPublic());
      assertTrue(accessor.getReturnType().isType(type));
      assertEquals(PropertyComponent.ACCESSOR.format(type, name), accessor.getName());
      assertTrue(accessor.getParameters().isEmpty());
      assertTrue(!existingItems.contains(PropertyComponent.FIELD)
               || accessor.getBody().contains(String.format("return %s;", name)));
   }

   @Test
   public void testCreateMutator()
   {
      assumeFalse(existingItems.contains(PropertyComponent.MUTATOR));

      final MethodSource<O> mutator = source.getProperty(name).createMutator();
      assertTrue(source.hasMethod(mutator));

      assertTrue(source.isInterface() || mutator.isPublic());
      assertTrue(mutator.isReturnTypeVoid());
      assertEquals(PropertyComponent.MUTATOR.format(type, name), mutator.getName());
      assertEquals(1, mutator.getParameters().size());
      final ParameterSource<O> parameter = mutator.getParameters().get(0);
      assertTrue(parameter.getType().isType(type));
      assertEquals(name, parameter.getName());
      assertTrue(!existingItems.contains(PropertyComponent.FIELD)
               || mutator.getBody().contains(String.format("this.%1$s=%1$s;", name)));
   }

   @Test(expected = IllegalStateException.class)
   public void testCreateFieldAgain()
   {
      source.getProperty(name).createField();
      assertFalse(existingItems.contains(PropertyComponent.FIELD));
      source.getProperty(name).createField();
   }

   @Test(expected = IllegalStateException.class)
   public void testCreateAccessorAgain()
   {
      source.getProperty(name).createAccessor();
      assertFalse(existingItems.contains(PropertyComponent.ACCESSOR));
      source.getProperty(name).createAccessor();
   }

   @Test(expected = IllegalStateException.class)
   public void testCreateMutatorAgain()
   {
      source.getProperty(name).createAccessor();
      assertFalse(existingItems.contains(PropertyComponent.MUTATOR));
      source.getProperty(name).createAccessor();
   }

   @Test
   public void testRemoveField()
   {
      assumeTrue(existingItems.contains(PropertyComponent.FIELD));

      final FieldSource<O> field = source.getProperty(name).getField();
      assertTrue(source.hasField(field));
      source.getProperty(name).removeField();
      assertFalse(source.hasField(field));
   }

   @Test
   public void testRemoveAccessor()
   {
      assumeTrue(existingItems.contains(PropertyComponent.ACCESSOR));

      final MethodSource<O> accessor = source.getProperty(name).getAccessor();
      assertTrue(source.hasMethod(accessor));
      source.getProperty(name).removeAccessor();
      assertFalse(source.hasMethod(accessor));
   }

   @Test
   public void testRemoveMutator()
   {
      assumeTrue(existingItems.contains(PropertyComponent.MUTATOR));

      final MethodSource<O> mutator = source.getProperty(name).getMutator();
      assertTrue(source.hasMethod(mutator));
      source.getProperty(name).removeMutator();
      assertFalse(source.hasMethod(mutator));
   }

   @Test
   public void testAddProperty()
   {
      final PropertySource<O> property = source.addProperty("Whatever", "blah");
      assertEquals(property, source.getProperty("blah"));

      if (!source.isInterface())
      {
         assertTrue(source.hasField("blah"));
         assertEquals("Whatever", source.getField("blah").getType().getName());
         if (source.isEnum())
         {
            assertTrue(source.getField("blah").isFinal());
         }
      }
      final String accessorName = "getBlah";
      assertTrue(source.hasMethodSignature(accessorName));
      final MethodSource<O> accessor = source.getMethod(accessorName);
      assertEquals("Whatever", accessor.getReturnType().getName());
      assertTrue(source.isInterface() || accessor.isPublic());

      if (source.isEnum())
      {
         return;
      }

      final String mutatorName = "setBlah";
      assertTrue(source.hasMethodSignature(mutatorName, "Whatever"));
      final MethodSource<O> mutator = source.getMethod(mutatorName, "Whatever");
      assertEquals("Whatever", mutator.getParameters().get(0).getType().getName());
      assertTrue(source.isInterface() || mutator.isPublic());
   }

   @Test
   public void testAddPropertyThenChangeType()
   {
      final PropertySource<O> property = source.addProperty("int", "something");
      assertTrue(source.hasMethodSignature("getSomething"));
      assertTrue(source.getMethod("getSomething").getReturnType().isType(int.class));
      property.setType(boolean.class);
      assertTrue(property.getType().isType(boolean.class));
      assertFalse(source.hasMethodSignature("getSomething"));
      assertTrue(source.hasMethodSignature("isSomething"));
      assertTrue(source.getMethod("isSomething").getReturnType().isType(boolean.class));
   }

   @Test
   public void testSetName()
   {
      assumeFalse("foo".equals(name));

      assertEquals(existingItems.contains(PropertyComponent.FIELD), sourceHasPropertyField(name));
      assertEquals(existingItems.contains(PropertyComponent.ACCESSOR),
               source.getMethod(PropertyComponent.ACCESSOR.format(type, name)) != null);
      assertEquals(existingItems.contains(PropertyComponent.MUTATOR),
               source.getMethod(PropertyComponent.MUTATOR.format(type, name), type) != null);

      final PropertySource<O> property = source.getProperty(name);

      if (!source.isInterface() && !property.hasField())
      {
         property.createField();
      }
      if (!property.isReadable())
      {
         property.createAccessor();
      }
      if (!source.isEnum() && !property.isWritable())
      {
         property.createMutator();
      }

      property.setName("foo");
      assertEquals("foo", property.getName());

      // make sure none of the original items remain:
      assertFalse(existingItems.contains(PropertyComponent.FIELD) && sourceHasPropertyField(name));
      assertFalse(existingItems.contains(PropertyComponent.ACCESSOR)
               && source.getMethod(PropertyComponent.ACCESSOR.format(type, name)) != null);
      assertFalse(existingItems.contains(PropertyComponent.MUTATOR)
               && source.getMethod(PropertyComponent.MUTATOR.format(type, name), type) != null);

      assertTrue(source.isInterface() || sourceHasPropertyField("foo"));
      assertTrue(source.getMethod(PropertyComponent.ACCESSOR.format(type, "foo")) != null);
      assertTrue(source.isEnum() || source.getMethod(PropertyComponent.MUTATOR.format(type, "foo"), type) != null);

      if (property.hasField())
      {
         assertTrue(property.getAccessor().getBody().contains("return foo;"));
         assertTrue(!property.isWritable() || property.getMutator().getBody().contains("this.foo=foo;"));
      }
   }

   @Test
   public void testSetTypeClass()
   {
      assumeFalse(CharSequence.class.equals(type));
      final PropertySource<O> property = source.getProperty(name);
      property.setType(CharSequence.class);
      assertTrue(property.getType().isType(CharSequence.class));
      assertTrue(!existingItems.contains(PropertyComponent.FIELD)
               || property.getField().getType().isType(CharSequence.class));
      assertTrue(!existingItems.contains(PropertyComponent.ACCESSOR)
               || property.getAccessor().getReturnType().isType(CharSequence.class));
      assertTrue(!existingItems.contains(PropertyComponent.MUTATOR)
               || property.getMutator().getParameters().get(0).getType().isType(CharSequence.class));
   }

   @Test
   public void testSetTypeString()
   {
      assumeFalse(CharSequence.class.equals(type));
      final PropertySource<O> property = source.getProperty(name);
      property.setType("CharSequence");
      assertEquals("CharSequence", property.getType().getName());
      assertTrue(!existingItems.contains(PropertyComponent.FIELD)
               || "CharSequence".equals(property.getField().getType().getName()));
      assertTrue(!existingItems.contains(PropertyComponent.ACCESSOR)
               || "CharSequence".equals(property.getAccessor().getReturnType().getName()));
      assertTrue(!existingItems.contains(PropertyComponent.MUTATOR)
               || "CharSequence".equals(property.getMutator().getParameters().get(0).getType().getName()));
   }

   @Test
   public void testSetTypeJavaType()
   {
      final PropertySource<O> property = source.getProperty(name);
      property.setType(source);
      assertEquals(source.getQualifiedName(), property.getType().getQualifiedName());
      assertTrue(!existingItems.contains(PropertyComponent.FIELD)
               || Strings.areEqual(source.getQualifiedName(), property.getField().getType().getQualifiedName()));
      assertTrue(!existingItems.contains(PropertyComponent.ACCESSOR)
               || Strings
                        .areEqual(source.getQualifiedName(), property.getAccessor().getReturnType().getQualifiedName()));
      assertTrue(!existingItems.contains(PropertyComponent.MUTATOR)
               || Strings.areEqual(source.getQualifiedName(), property.getMutator().getParameters().get(0).getType()
                        .getQualifiedName()));
   }

   @Test
   public void testPropertySeesChangedAccessor()
   {
      assumeTrue(existingItems.contains(PropertyComponent.ACCESSOR));

      final PropertySource<O> property = source.getProperty(name);
      assertTrue(property.isReadable());
      property.getAccessor().setName("foo");

      assertFalse(property.isReadable());
   }

   @Test
   public void testPropertySeesChangedMutator()
   {
      assumeTrue(existingItems.contains(PropertyComponent.MUTATOR));

      final PropertySource<O> property = source.getProperty(name);
      assertTrue(property.isWritable());
      property.getMutator().setName("foo");

      assertFalse(property.isWritable());
   }

   private boolean sourceHasPropertyField(String fieldName)
   {
      if (source.isInterface())
      {
         return false;
      }
      final FieldSource<O> field = source.getField(fieldName);
      return !(field == null || field.isStatic());
   }
}
