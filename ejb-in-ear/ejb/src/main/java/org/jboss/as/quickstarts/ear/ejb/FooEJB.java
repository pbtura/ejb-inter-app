package org.jboss.as.quickstarts.ear.ejb;

import javax.ejb.Stateless;

import org.jboss.as.quickstarts.interapp.shared.Foo;

@Stateless
public class FooEJB implements Foo
{
	public FooEJB()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAge()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
