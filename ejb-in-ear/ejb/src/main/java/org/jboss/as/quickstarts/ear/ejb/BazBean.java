package org.jboss.as.quickstarts.ear.ejb;

import javax.ejb.Local;
import javax.ejb.Stateless;

@Stateless(mappedName = "Person/BazBean/local")
@Local
public class BazBean implements Baz
{
	public BazBean()
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
		return "John Smith";
	}

	@Override
	public int getAge()
	{
		// TODO Auto-generated method stub
		return 18;
	}
}
