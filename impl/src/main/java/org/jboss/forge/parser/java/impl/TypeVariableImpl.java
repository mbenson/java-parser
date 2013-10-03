/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.java.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaType;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.source.JavaClassSource;
import org.jboss.forge.parser.java.source.JavaSource;
import org.jboss.forge.parser.java.source.TypeVariableSource;
import org.jboss.forge.parser.java.util.Assert;

/**
 * 
 * @author mbenson
 * 
 */
public class TypeVariableImpl<O extends JavaSource<O>> implements TypeVariableSource<O>
{
   private final O origin;
   private final TypeParameter internal;

   TypeVariableImpl(O origin, TypeParameter internal)
   {
      super();
      Assert.notNull(origin, "null origin");
      this.origin = origin;
      Assert.notNull(internal, "null internal representation");
      this.internal = internal;
   }

   @Override
   public List<Type<O>> getBounds()
   {
      @SuppressWarnings("unchecked")
      List<org.eclipse.jdt.core.dom.Type> typeBounds = internal.typeBounds();
      final List<Type<O>> result = new ArrayList<Type<O>>(typeBounds.size());

      for (org.eclipse.jdt.core.dom.Type type : typeBounds)
      {
         result.add(new TypeImpl<O>(origin, type));
      }
      return result;
   }

   @Override
   public String getName()
   {
      return internal.getName().getIdentifier();
   }

   @Override
   public Object getInternal()
   {
      return internal;
   }

   @Override
   public O getOrigin()
   {
      return origin;
   }

   @Override
   public TypeVariableSource<O> setName(String name)
   {
      internal.setName(internal.getAST().newSimpleName(name));
      return this;
   }

   @Override
   public TypeVariableSource<O> setBounds(JavaType<?>... bounds)
   {
      final String[] names;
      if (bounds == null)
      {
         names = new String[0];
      }
      else
      {
         names = new String[bounds.length];
         int i = 0;
         for (JavaType<?> t : bounds)
         {
            names[i++] = t.getQualifiedName();
         }
      }
      return setBounds(names);
   }

   @Override
   public TypeVariableSource<O> setBounds(Class<?>... bounds)
   {
      final String[] names;
      if (bounds == null)
      {
         names = new String[0];
      }
      else
      {
         names = new String[bounds.length];
         int i = 0;
         for (Class<?> cls : bounds)
         {
            names[i++] = cls.getName();
         }
      }
      return setBounds(names);
   }

   @SuppressWarnings("unchecked")
   @Override
   public TypeVariableSource<O> setBounds(String... bounds)
   {
      internal.typeBounds().clear();
      for (String s : bounds)
      {
         org.eclipse.jdt.core.dom.Type copy = (org.eclipse.jdt.core.dom.Type) ASTNode.copySubtree(internal.getAST(),
                  parseTypeBound(s));
         internal.typeBounds().add(copy);
      }
      return this;
   }

   private org.eclipse.jdt.core.dom.Type parseTypeBound(String bound)
   {
      String stub = "public class Stub<T extends " + bound + "> {}";
      JavaClassSource temp = JavaParser.parse(JavaClassSource.class, stub);
      TypeParameter v = (TypeParameter) temp.getTypeVariables().get(0).getInternal();
      return (org.eclipse.jdt.core.dom.Type) v.typeBounds().get(0);
   }
}
