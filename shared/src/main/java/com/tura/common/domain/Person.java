package com.tura.common.domain;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.tura.common.annotations.NamedConstructor;;

/**
 * A user account on the system
 * 
 * @author gdunkle
 */
@Entity(name = "Person")
@Table(name = "persons")
@NamedQueries({
		@NamedQuery(name = "Person.findByUsername", query = "select p from Person p where p.username=:username", hints = {
				@QueryHint(name = "org.hibernate.cacheable", value = "true") }),
		@NamedQuery(name = "Person.findByUsernameIgnoreSelf", query = "select p from Person p where p.username=:username and p.id!=:id", hints = {
				@QueryHint(name = "org.hibernate.cacheable", value = "true") }),
		@NamedQuery(name = "Person.findByUsernameAndPassword", query = "select p from Person p where p.username=:username and p.password=:password and p.enabled=true"),
		@NamedQuery(name = "Person.findAll", query = "select p from Person p order by p.username asc"),
		@NamedQuery(name = "Person.findByUsernameLike", query = "select p from Person p where p.username like ':username%' order by p.username asc"),
		@NamedQuery(name = "Person.countAll", query = "select count(p) from Person p"),
		@NamedQuery(name = "Person.findByEmail", query = "select p from Person p where p.email=:email"),
		@NamedQuery(name = "Person.findByRoleFilterByUsername", query = "select p from Person p,IN(p.roles) roles  where roles.name=:roleName and p.username like ':username%'"),
		@NamedQuery(name = "Person.findByRole", query = "select p from Person p,IN(p.roles) roles  where roles.name=:roleName"),
		@NamedQuery(name = "Person.findByRoles", query = "select p from Person as p join p.roles as r where r.name in (:ROLES)"),
		@NamedQuery(name = "Person.isInRole", query = "select roles.name from Person p,IN(p.roles) roles  where p.id=:id and roles.name=:roleName"),
		@NamedQuery(name = "Person.getRoles", query = "select roles from Person  p JOIN p.roles as roles  where p.id=:id"),
		@NamedQuery(name = "Person.getAllUsernames", query = "select p.username from Person  p ") })
@Indexed
@XmlRootElement(name = "person")
@XmlType(name = "person")
@XmlAccessorType(XmlAccessType.NONE)
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Person extends com.tura.common.domain.Entity implements HasRole
{
	@Id
	@XmlElement
	@DocumentId
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	private static final long serialVersionUID = 3760846774507680304L;
	// relationships
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "persons_to_roles", inverseJoinColumns = {
			@JoinColumn(name = "role_id", referencedColumnName = "id") }, joinColumns = {
					@JoinColumn(name = "person_id", referencedColumnName = "id") })
	@OrderBy("name")
	@AuditJoinTable(inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") })
	protected List<Role> roles;
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id")
	@NotAudited
	protected List<SessionActivity> sessionActivity;
	// attributes
	@Column(nullable = true)
	@Field(index = Index.YES, store = Store.NO)
	protected String username;
	@Column(nullable = true)
	protected String password;
	@Column(name = "first_name")
	@XmlElement
	@Field(index = Index.YES, store = Store.NO)
	protected String firstName;
	@Column(name = "last_name")
	@XmlElement
	@Field(index = Index.YES, store = Store.NO)
	protected String lastName;
	@Column(name = "title")
	@XmlElement
	protected String title;
	@Column(name = "email")
	@XmlElement
	@Field(index = Index.YES, store = Store.NO)
	protected String email;
	@Column(name = "phone")
	@XmlElement
	protected String phone;
	@Column(name = "legacy_number")
	@XmlElement
	protected String legacy_number;
	@Column(name = "fax")
	@XmlElement
	protected String fax;
	@Column(name = "enabled")
	protected Boolean enabled = true;
	@Transient
	private String fullName;
	@Transient
	private List<String> roleNames;

	public Person()
	{
		super();
	}

	public Person(String name)
	{
		super();
		setUsername(name);
	}

	@NamedConstructor("firstAndlastName")
	public Person(String firstName, String lastName)
	{
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = getFullName();
	}

	public Person(String firstName, String lastName, String username, String email)
	{
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = getFullName();
		this.username = username;
		this.email = email;
	}

	public Person(Long id, String firstName, String lastName)
	{
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = getFullName();
	}

	public Person(Long id, String firstName, String lastName, String email)
	{
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = getFullName();
		this.email = email;
	}

	public Person(String firstName, String lastName, String userName, String email, String password, boolean enabled)
	{
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = getFullName();
		this.username = userName;
		this.email = email;
		this.password = password;
		this.setEnabled(enabled);
	}

	public Person(Long id, String firstName, String lastName, List<String> roleNames)
	{
		this(id, firstName, lastName);
		this.roleNames = roleNames;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public void setId(Long id)
	{
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setPassword(java.lang.String)
	 */
	// @RolesAllowed("administrator")
	// public void setPassword(String password)
	// {
	// this.password = password;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setUsername(java.lang.String)fgetf
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setUsername(java.lang.String)
	 */
	@RolesAllowed("administrator")
	public void setUsername(String username)
	{
		String previousValue = this.username;
		this.username = username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#addRoles(java.util.List)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#addRoles(java.util.List)
	 */
	public boolean addRoles(List<Role> roles)
	{
		return getRoles().addAll(roles);
	}

	public boolean addRole(Role role)
	{
		return getRoles().add(role);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#removeRoles(java.util.List)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#removeRoles(java.util.List)
	 */
	public boolean removeRoles(List<Role> roles)
	{
		if (getRoles().removeAll(roles))
		{
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setEmail(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setEmail(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setEmail(java.lang.String)
	 */
	@RolesAllowed("administrator")
	public void setEmail(String email)
	{
		String previousValue = this.email;
		this.email = email;
	}

	// private List<ValidationException> validateEmail()
	// {
	// List<ValidationException> errors=new ArrayList<ValidationException>();
	// if(hasPreviousValue("email"))
	// {
	// if (!email.matches(Globals.EMAIL_PATTERN))
	// {
	// errors.add(new ValidationException("Invalid email.", Severity.ERROR));
	// }
	// if(!errors.isEmpty())
	// {
	// rollBackFieldValue("email");
	// }
	// else
	// {
	// markFieldAsValid("email");
	// }
	// }
	// return errors;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setFirstName(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setFirstName(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setFirstName(java.lang.String)
	 */
	@RolesAllowed("administrator")
	public void setFirstName(String firstName)
	{
		String previousValue = this.firstName;
		this.firstName = firstName;
		// updateValue("firstName", previousValue, this.firstName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setLastName(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setLastName(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setLastName(java.lang.String)
	 */
	@RolesAllowed("administrator")
	public void setLastName(String lastName)
	{
		String previousValue = this.firstName;
		this.lastName = lastName;
		// updateValue("lastName", previousValue, this.lastName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setPhone(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setPhone(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setPhone(java.lang.String)
	 */
	@RolesAllowed("administrator")
	public void setPhone(String phone)
	{
		String previousValue = this.phone;
		this.phone = phone;
		// updateValue("phone", previousValue, this.phone);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#isInRole(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#isInRole(java.lang.String)
	 */
	@Override
	public boolean isInRole(String roleName)
	{
		return getRoleNames().contains(roleName);
	}

	public static Person createNew()
	{
		Person person = new Person();
		person.password = RandomStringUtils.randomAlphanumeric(6).toLowerCase();
		return person;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getPassword()
	 */
	public String getPassword()
	{
		return password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getUsername()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getUsername()
	 */
	@XmlElement(name = "username")
	public String getUsername()
	{
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getRoles()
	 */
	@XmlElementWrapper(name = "roles")
	@XmlElement(name = "role")
	public List<Role> getRoles()
	{
		if (roles == null)
		{
			roles = new ArrayList<Role>();
		}
		return roles;
	}

	public String getName()
	{
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getEmail()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getEmail()
	 */
	public String getEmail()
	{
		return email;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getFirstName()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getFirstName()
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getLastName()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getLastName()
	 */
	public String getLastName()
	{
		return lastName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getPhone()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getPhone()
	 */
	public String getPhone()
	{
		return phone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#getRoleNames()
	 */
	@XmlElementWrapper(name = "roleNames")
	@XmlElement(name = "roleName")
	public List<String> getRoleNames()
	{
		if (roleNames == null)
		{
			roleNames = new ArrayList<String>();
			List<Role> roles = getRoles();
			for (Role role : roles)
			{
				roleNames.addAll(role.getRoleNames());
			}
		}
		return roleNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#isEnabled()
	 */
	public Boolean isEnabled()
	{
		return enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#setEnabled(boolean)
	 */
	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Person#generateNewPassword(java.lang.Integer)
	 */
	public void generateNewPassword(Integer size)
	{
		password = RandomStringUtils.randomAlphanumeric(size).toLowerCase();
	}

	public String getFullName()
	{
		if (fullName == null)
		{
			String lastName = getLastName() == null ? "" : getLastName();
			String firstName = getFirstName() == null ? "" : getFirstName();
			fullName = new StringBuffer(lastName).append(", ").append(firstName).toString();
		}
		return fullName;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getFax()
	{
		return fax;
	}

	public void setFax(String fax)
	{
		this.fax = fax;
	}

	public static interface DefaultValidation
	{
	}

	public String getLegacy_number()
	{
		return legacy_number;
	}

	public void setLegacy_number(String legacy_number)
	{
		this.legacy_number = legacy_number;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public List<SessionActivity> getSessionActivity()
	{
		return sessionActivity;
	}

	public void setSessionActivity(List<SessionActivity> sessionActivity)
	{
		this.sessionActivity = sessionActivity;
	}

	public Person frontLoadRoles()
	{
		if (!getRoles().isEmpty())
		{
			for (Role r : getRoles())
			{
				r.frontLoadParentRoles();
			}
		}
		return this;
	}
}