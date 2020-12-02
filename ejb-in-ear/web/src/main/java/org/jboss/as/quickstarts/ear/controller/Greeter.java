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

import org.jboss.as.quickstarts.ear.ejb.GreeterEJB;
import org.jboss.as.quickstarts.interapp.shared.Foo;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import java.io.Serializable;

/**
 * A simple managed bean that is used to invoke the GreeterEJB and store the response. The response is obtained by
 * invoking getMessage().
 *
 * @author paul.robinson@redhat.com, 2011-12-21
 */
@Named("greeter")
@SessionScoped
@Local(GreeterProvider.class)
public class Greeter implements Serializable, GreeterProvider
{
	/** Default value included to remove warning. **/
	private static final long serialVersionUID = 1L;
	/**
	 * Injected GreeterEJB client
	 */
	@EJB
	private GreeterEJB greeterEJB;
	@EJB
	private Foo departerEJB;
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
		// message = greeterEJB.sayHello(name);
		departerEJB.setName(name);
	}

	/**
	 * Get the greeting message, customized with the name of the person to be greeted.
	 *
	 * @return message. The greeting message.
	 */
	@Override
	public String getMessage()
	{
		return "Hello. " + getName() + ". You are " + getAge() + " years old.";
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return departerEJB.getName();
	}

	@Override
	public int getAge()
	{
		// TODO Auto-generated method stub
		return departerEJB.getAge();
	}

	@Override
	public String getEMStatus() {
		
		return departerEJB.getEMStatus();
	}
}
