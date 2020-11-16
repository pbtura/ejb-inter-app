package org.jboss.as.quickstarts.ear.controller;

import org.jboss.as.quickstarts.interapp.shared.Foo;

public interface DeparterServiceProvider extends Foo
{
	/**
	 * Get the greeting message, customized with the name of the person to be greeted.
	 *
	 * @return message. The greeting message.
	 */
	String getMessage();
}