/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.ear.controller;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Named;

import org.jboss.as.quickstarts.ear.ejb.Baz;
import org.jboss.as.quickstarts.interapp.shared.Foo;

import java.io.Serializable;

/**
 * A simple managed bean that is used to invoke the GreeterEJB and store the response. The response is obtained by
 * invoking getMessage().
 *
 * @author paul.robinson@redhat.com, 2011-12-21
 */
@Named("departer")
@Stateless
@Local(DeparterService.class)
public class Departer implements Serializable, DeparterService
{
	/** Default value included to remove warning. **/
	private static final long serialVersionUID = 1L;
	@EJB
	// (lookup = "Person/FooBean/local")
	private Foo fooEJB;
	/**
	 * Stores the response from the call to greeterEJB.sayHello(...)
	 */
	private String message;

	/**
	 * Invoke greeterEJB.sayHello(...) and store the message
	 *
	 * @param name
	 *            The name of the person to be greeted
	 */
	@Override
	public void setName(String name)
	{
		message = "You are " + getAge() + " years old. Goodbye " + name;
	}

	/**
	 * Get the greeting message, customized with the name of the person to be greeted.
	 *
	 * @return message. The greeting message.
	 */
	@Override
	public String getMessage()
	{
		return message;
	}

	public int getAge()
	{
		return fooEJB.getAge();
	}

	public String getName()
	{
		// TODO Auto-generated method stub
		return fooEJB.getName();
	}
}
