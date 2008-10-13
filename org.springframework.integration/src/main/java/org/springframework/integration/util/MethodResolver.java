/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.util;

import java.lang.reflect.Method;

/**
 * Strategy interface for detecting a single Method on a Class.
 * 
 * @author Mark Fisher
 */
public interface MethodResolver {

	/**
	 * Find a single Method on the provided Class that matches this resolver's
	 * criteria.
	 * 
	 * @param clazz the Class on which to search for a Method
	 * 
	 * @return a single Method or <code>null</code> if no Method matching this
	 * resolver's criteria can be found.
	 * 
	 * @throws IllegalArgumentException if more than one Method defined on the
	 * given Class matches this resolver's criteria
	 */
	Method findMethod(Class<?> clazz) throws IllegalArgumentException;

}