/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.parser.java.source;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;

/**
 * Represents a Java {@code class} source file as an in-memory modifiable element. See {@link JavaParser} for various
 * options in generating {@link JavaClassSource} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface JavaClassSource extends JavaClass<JavaClassSource>,
         JavaSource<JavaClassSource>,
         InterfaceCapableSource<JavaClassSource>,
         FieldHolderSource<JavaClassSource>,
         MethodHolderSource<JavaClassSource>,
         GenericCapableSource<JavaClassSource, JavaClassSource>,
         ExtendableSource<JavaClassSource>,
         AbstractableSource<JavaClassSource>,
         PropertyHolderSource<JavaClassSource>
{
}