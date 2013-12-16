/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.java;

import java.util.List;

import org.jboss.forge.parser.Origin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface Type<O extends JavaType<O>> extends Origin<O>
{
   abstract List<Type<O>> getTypeArguments();

   abstract String getName();

   abstract String getQualifiedName();

   abstract Type<O> getParentType();

   abstract boolean isArray();

   abstract int getArrayDimensions();

   abstract boolean isParameterized();

   abstract boolean isPrimitive();

   abstract boolean isQualified();

   abstract boolean isWildcard();

   boolean isType(Class<?> type);

   boolean isType(String name);

}
