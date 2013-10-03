/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.java.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jface.text.Document;
import org.jboss.forge.parser.java.TypeVariable;
import org.jboss.forge.parser.java.source.GenericCapableSource;
import org.jboss.forge.parser.java.source.JavaSource;
import org.jboss.forge.parser.java.source.TypeVariableSource;
import org.jboss.forge.parser.java.util.Strings;

/**
 * 
 * @author mbenson
 * 
 * @param <O>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractGenericCapableJavaSource<O extends JavaSource<O>> extends AbstractJavaSourceMemberHolder<O>
         implements GenericCapableSource<O, O>
{

   public AbstractGenericCapableJavaSource(JavaSource<?> enclosingType, Document document, CompilationUnit unit,
            BodyDeclaration declaration)
   {
      super(enclosingType, document, unit, declaration);
   }

   @Override
   public List<TypeVariableSource<O>> getTypeVariables()
   {
      TypeDeclaration type = (TypeDeclaration) body;
      List<TypeParameter> typeParameters = type.typeParameters();
      List<TypeVariableSource<O>> result = new ArrayList<TypeVariableSource<O>>();
      for (TypeParameter typeParameter : typeParameters)
      {
         result.add(new TypeVariableImpl<O>((O) this, typeParameter));
      }
      return Collections.unmodifiableList(result);
   }

   @Override
   public TypeVariableSource<O> addTypeVariable()
   {
      TypeDeclaration type = (TypeDeclaration) body;
      TypeParameter tp2 = unit.getAST().newTypeParameter();
      type.typeParameters().add(tp2);
      return new TypeVariableImpl<O>((O) this, tp2);
   }

   @Override
   public O removeTypeVariable(String name)
   {
      TypeDeclaration type = (TypeDeclaration) body;
      List<TypeParameter> typeParameters = type.typeParameters();
      for (Iterator<TypeParameter> iter = typeParameters.iterator(); iter.hasNext();)
      {
         if (Strings.areEqual(name, iter.next().getName().getIdentifier()))
         {
            iter.remove();
            break;
         }
      }
      return (O) this;
   }

   @Override
   public O removeTypeVariable(TypeVariable<?> typeVariable)
   {
      return removeTypeVariable(typeVariable.getName());
   }

}
