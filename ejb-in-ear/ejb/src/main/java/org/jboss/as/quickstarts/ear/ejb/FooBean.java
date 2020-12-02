package org.jboss.as.quickstarts.ear.ejb;


import org.jboss.as.quickstarts.interapp.shared.Foo;

//@Stateless(mappedName = "Person/FooBean/local")
//@Remote
public class FooBean implements Foo
{
	private String name = "Jane Doe";

	public FooBean()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public int getAge()
	{
		// TODO Auto-generated method stub
		return 75;
	}

	@Override
	public String getEMStatus() {
		// TODO Auto-generated method stub
		return "?";
	}
}
