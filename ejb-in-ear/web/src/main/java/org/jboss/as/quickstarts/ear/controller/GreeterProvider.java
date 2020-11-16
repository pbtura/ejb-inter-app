package org.jboss.as.quickstarts.ear.controller;

import org.jboss.as.quickstarts.interapp.shared.Foo;

public interface GreeterProvider extends Foo
{
	/**
	 * Invoke greeterEJB.sayHello(...) and store the message
	 *
	 * @param name
	 *            The name of the person to be greeted
	 */
	void setName(String name);

	/**
	 * Get the greeting message, customized with the name of the person to be greeted.
	 *
	 * @return message. The greeting message.
	 */
	String getMessage();
}