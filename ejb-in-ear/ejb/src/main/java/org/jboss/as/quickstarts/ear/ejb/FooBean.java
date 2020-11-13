package org.jboss.as.quickstarts.ear.ejb;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.jboss.as.quickstarts.interapp.shared.Foo;

@Stateless(mappedName = "Person/FooBean/local")
@Remote
public class FooBean implements Foo
{
	public FooBean()
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
		return "Jane Doe";
	}

	@Override
	public int getAge()
	{
		// TODO Auto-generated method stub
		return 75;
	}
}
