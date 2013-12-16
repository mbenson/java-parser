/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.parser.java.impl;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaType;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.ast.AnnotationAccessor;
import org.jboss.forge.parser.java.ast.ModifierAccessor;
import org.jboss.forge.parser.java.source.AnnotationSource;
import org.jboss.forge.parser.java.source.FieldSource;
import org.jboss.forge.parser.java.source.JavaSource;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.java.util.Types;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FieldImpl<O extends JavaSource<O>> implements FieldSource<O>
{
   private final AnnotationAccessor<O, FieldSource<O>> annotations = new AnnotationAccessor<O, FieldSource<O>>();
   private final ModifierAccessor modifiers = new ModifierAccessor();

   private O parent;
   private AST ast;
   private final FieldDeclaration field;
   private final VariableDeclarationFragment fragment;

   private void init(final O parent)
   {
      this.parent = parent;
      ast = ((ASTNode) parent.getInternal()).getAST();
   }

   public FieldImpl(final O parent)
   {
      init(parent);
      this.fragment = ast.newVariableDeclarationFragment();
      this.field = ast.newFieldDeclaration(this.fragment);
   }

   public FieldImpl(final O parent, final Object internal)
   {
      this(parent, internal, false);
   }

   @SuppressWarnings("unchecked")
   public FieldImpl(final O parent, final Object internal, boolean copy)
   {
      init(parent);
      if (copy)
      {
         VariableDeclarationFragment newFieldFragment = (VariableDeclarationFragment) internal;
         FieldDeclaration newFieldDeclaration = (FieldDeclaration) newFieldFragment.getParent();
         this.field = (FieldDeclaration) ASTNode.copySubtree(ast, newFieldDeclaration);
         Iterator<VariableDeclarationFragment> fragmentsIterator = this.field.fragments().iterator();
         VariableDeclarationFragment temp = null;
         while (fragmentsIterator.hasNext())
         {
            VariableDeclarationFragment variableDeclarationFragment = fragmentsIterator.next();
            if (newFieldFragment.getName().getFullyQualifiedName()
                     .equals(variableDeclarationFragment.getName().getFullyQualifiedName()))
            {
               temp = variableDeclarationFragment;
            }
            else
            {
               fragmentsIterator.remove();
            }
         }
         this.fragment = temp;
      }
      else
      {
         this.fragment = (VariableDeclarationFragment) internal;
         this.field = (FieldDeclaration) this.fragment.getParent();
      }
   }

   @Override
   public O getOrigin()
   {
      return parent.getOrigin();
   }

   @Override
   public Object getInternal()
   {
      return fragment;
   }

   /*
    * Annotation<O> Modifiers
    */
   @Override
   public AnnotationSource<O> addAnnotation()
   {
      return annotations.addAnnotation(this, field);
   }

   @Override
   public AnnotationSource<O> addAnnotation(final Class<? extends java.lang.annotation.Annotation> clazz)
   {
      if (parent.requiresImport(clazz))
      {
         parent.addImport(clazz);
      }
      return annotations.addAnnotation(this, field, clazz.getSimpleName());
   }

   @Override
   public AnnotationSource<O> addAnnotation(final String className)
   {
      return annotations.addAnnotation(this, field, className);
   }

   @Override
   public List<AnnotationSource<O>> getAnnotations()
   {
      return annotations.getAnnotations(this, field);
   }

   @Override
   public boolean hasAnnotation(final Class<? extends java.lang.annotation.Annotation> type)
   {
      return annotations.hasAnnotation(this, field, type.getName());
   }

   @Override
   public boolean hasAnnotation(final String type)
   {
      return annotations.hasAnnotation(this, field, type);
   }

   @Override
   public AnnotationSource<O> getAnnotation(final Class<? extends java.lang.annotation.Annotation> type)
   {
      return annotations.getAnnotation(this, field, type);
   }

   @Override
   public AnnotationSource<O> getAnnotation(final String type)
   {
      return annotations.getAnnotation(this, field, type);
   }

   @Override
   public FieldSource<O> removeAnnotation(final Annotation<O> annotation)
   {
      return annotations.removeAnnotation(this, field, annotation);
   }

   @Override
   public String toString()
   {
      return field.toString();
   }

   /*
    * Visibility Modifiers
    */

   @Override
   public boolean isFinal()
   {
      return modifiers.hasModifier(field, ModifierKeyword.FINAL_KEYWORD);
   }

   @Override
   public FieldSource<O> setFinal(final boolean finl)
   {
      if (finl)
         modifiers.addModifier(field, ModifierKeyword.FINAL_KEYWORD);
      else
         modifiers.removeModifier(field, ModifierKeyword.FINAL_KEYWORD);
      return this;
   }

   @Override
   public boolean isStatic()
   {
      return modifiers.hasModifier(field, ModifierKeyword.STATIC_KEYWORD);
   }

   @Override
   public FieldSource<O> setStatic(final boolean statc)
   {
      if (statc)
         modifiers.addModifier(field, ModifierKeyword.STATIC_KEYWORD);
      else
         modifiers.removeModifier(field, ModifierKeyword.STATIC_KEYWORD);
      return this;
   }

   @Override
   public boolean isPackagePrivate()
   {
      return (!isPublic() && !isPrivate() && !isProtected());
   }

   @Override
   public FieldSource<O> setPackagePrivate()
   {
      modifiers.clearVisibility(field);
      return this;
   }

   @Override
   public boolean isPublic()
   {
      return modifiers.hasModifier(field, ModifierKeyword.PUBLIC_KEYWORD);
   }

   @Override
   public FieldSource<O> setPublic()
   {
      modifiers.clearVisibility(field);
      modifiers.addModifier(field, ModifierKeyword.PUBLIC_KEYWORD);
      return this;
   }

   @Override
   public boolean isPrivate()
   {
      return modifiers.hasModifier(field, ModifierKeyword.PRIVATE_KEYWORD);
   }

   @Override
   public FieldSource<O> setPrivate()
   {
      modifiers.clearVisibility(field);
      modifiers.addModifier(field, ModifierKeyword.PRIVATE_KEYWORD);
      return this;
   }

   @Override
   public boolean isProtected()
   {
      return modifiers.hasModifier(field, ModifierKeyword.PROTECTED_KEYWORD);
   }

   @Override
   public FieldSource<O> setProtected()
   {
      modifiers.clearVisibility(field);
      modifiers.addModifier(field, ModifierKeyword.PROTECTED_KEYWORD);
      return this;
   }

   @Override
   public Visibility getVisibility()
   {
      return Visibility.getFrom(this);
   }

   @Override
   public FieldSource<O> setVisibility(final Visibility scope)
   {
      return Visibility.set(this, scope);
   }

   /*
    * Field<O> methods
    */

   @Override
   public String getName()
   {
      String result = null;
      for (Object f : field.fragments())
      {
         if (f instanceof VariableDeclarationFragment)
         {
            VariableDeclarationFragment frag = (VariableDeclarationFragment) f;
            result = frag.getName().getFullyQualifiedName();
            break;
         }
      }
      return result;
   }

   @Override
   public FieldSource<O> setName(final String name)
   {
      fragment.setName(ast.newSimpleName(name));
      return this;
   }

   @Override
   public Type<O> getType()
   {
      return new TypeImpl<O>(parent, field.getStructuralProperty(FieldDeclaration.TYPE_PROPERTY));
   }

   @Override
   public FieldSource<O> setType(final Class<?> clazz)
   {
      if (parent.requiresImport(clazz))
      {
         parent.addImport(clazz);
      }
      return setType(clazz.getSimpleName());
   }

   @Override
   public FieldSource<O> setType(final JavaType<?> source)
   {
      return setType(source.getQualifiedName());
   }

   @Override
   public FieldSource<O> setType(final String typeName)
   {
      String simpleName = Types.toSimpleName(typeName);

      O origin = getOrigin();
      if (!Strings.areEqual(typeName, simpleName) && origin.requiresImport(typeName))
      {
         origin.addImport(typeName);
      }

      Code primitive = PrimitiveType.toCode(typeName);

      org.eclipse.jdt.core.dom.Type type = null;
      if (primitive != null)
      {
         type = ast.newPrimitiveType(primitive);
      }
      else
      {
         if (!origin.requiresImport(typeName))
         {
            if (Types.isArray(typeName))
            {
               String arrayType = Types.stripArray(typeName);
               int arrayDimension = Types.getArrayDimension(typeName);
               if (Types.isPrimitive(arrayType))
               {
                  type = ast.newArrayType(ast.newPrimitiveType(PrimitiveType.toCode(arrayType)), arrayDimension);
               }
               else
               {
                  type = ast.newArrayType(ast.newSimpleType(ast.newSimpleName(arrayType)), arrayDimension);
               }
            }
            else
            {
               type = ast.newSimpleType(ast.newSimpleName(simpleName));
            }
         }
         else
         {
            String[] className = Types.tokenizeClassName(typeName);
            Name name = ast.newName(className);
            type = ast.newSimpleType(name);
         }
      }
      field.setType(type);
      return this;
   }

   @Override
   public String getLiteralInitializer()
   {
      String result = fragment.getInitializer().toString();
      return result;
   }

   @Override
   public String getStringInitializer()
   {
      String result = Strings.unquote(fragment.getInitializer().toString());
      return result;
   }

   @Override
   public FieldSource<O> setLiteralInitializer(final String value)
   {
      String stub = "public class Stub { private String stub = " + value + " }";
      JavaClass<?> temp = JavaParser.parse(JavaClass.class, stub);
      VariableDeclarationFragment tempFrag = (VariableDeclarationFragment) temp.getFields().get(0).getInternal();
      fragment.setInitializer((Expression) ASTNode.copySubtree(ast, tempFrag.getInitializer()));
      return this;
   }

   @Override
   public FieldSource<O> setStringInitializer(final String value)
   {
      return setLiteralInitializer(Strings.enquote(value));
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((field == null) ? 0 : field.hashCode());
      result = prime * result + ((fragment == null) ? 0 : fragment.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      FieldImpl<?> other = (FieldImpl<?>) obj;
      if (field == null)
      {
         if (other.field != null)
            return false;
      }
      else if (!field.equals(other.field ))
         return false;
      if (fragment == null)
      {
         if (other.fragment != null)
            return false;
      }
      else if (!fragment.equals(other.fragment))
         return false;
      return true;
   }

   @Override
   public boolean isTransient()
   {
      return modifiers.hasModifier(field, ModifierKeyword.TRANSIENT_KEYWORD);
   }

   @Override
   public boolean isVolatile()
   {
      return modifiers.hasModifier(field, ModifierKeyword.VOLATILE_KEYWORD);
   }

}
