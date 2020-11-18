/*
 * Created on Feb 1, 2004 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.tura.common.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

/**
 * A role on the system
 * 
 * @author gdunkle
 */
@Entity(name = "Role")
@Table(name = "roles")
@NamedQueries({ @NamedQuery(name = "Role.findByName", hints = {
		@QueryHint(name = "org.hibernate.cacheable", value = "true") }, query = "select r from Role r where r.name=:name"),
		@NamedQuery(name = "Role.findByNames", query = "select r from Role r  where r.name in (:names)"),
		@NamedQuery(name = "Role.findIdsByNames", query = "select r.id from Role r  where r.name in (:names)"),
		@NamedQuery(name = "Role.countAll", query = "select count(r) from Role r"),
		@NamedQuery(name = "Role.findAll", query = "select r from Role r order by r.name asc"),
		@NamedQuery(name = "Role.findMembers", query = "select r.members from Role r where r.name=:roleName"),
		@NamedQuery(name = "Role.getInsightRoles", query = "select role from Role as role ,IN(role.parentRoles) parentRole where parentRole.name='purchase_order_system'"),
		@NamedQuery(name = "Role.getMembers", query = "select members from Role as r JOIN r.members as members where r.id=:id"),
		@NamedQuery(name = "Role.getChildRoles", query = "select role from Role as role ,IN(role.parentRoles) parentRole where parentRole.id=:pid") })
@XmlType(name = "role")
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.NONE)
@Audited
@Indexed
public class Role extends com.tura.common.domain.Entity
{
	/**
	 * 
	 */
	@Id
	@XmlElement
	@DocumentId
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	private static final long serialVersionUID = 3256728381147396404L;
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, targetEntity = Role.class)
	@JoinTable(name = "roles_to_roles", inverseJoinColumns = {
			@JoinColumn(name = "parent_role_id", referencedColumnName = "id") }, joinColumns = {
					@JoinColumn(name = "child_role_id", referencedColumnName = "id") })
	@NotAudited
	private List<Role> parentRoles;
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, targetEntity = Role.class)
	@JoinTable(name = "roles_to_roles", inverseJoinColumns = {
			@JoinColumn(name = "child_role_id", referencedColumnName = "id") }, joinColumns = {
					@JoinColumn(name = "parent_role_id", referencedColumnName = "id") })
	@NotAudited
	private List<Role> childRoles;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "persons_to_roles", inverseJoinColumns = {
			@JoinColumn(name = "person_id", referencedColumnName = "id") }, joinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	@NotAudited
	private List<Person> members;
	// attributes
	@Field(index = Index.YES, store = Store.NO)
	@Column(unique = true, nullable = false)
	protected String name;
	@Column(name = "description")
	@Field(index = Index.YES, store = Store.NO)
	protected String description;

	/**
	 * 
	 */
	public Role()
	{
		super();
	}

	public Role(String name, String description)
	{
		super();
		this.name = name;
		this.description = description;
	}

	public Role(Long id, String name)
	{
		super();
		setId(id);
		setName(name);
	}

	@com.tura.common.annotations.NamedConstructor("parentRolesConstructor")
	public Role(Long id, String name, String description, List<Role> parentRoles)
	{
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.parentRoles = parentRoles;
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

	public Set<String> getRoleNames()
	{
		Set<String> roleNames = new HashSet<String>();
		roleNames.add(getName());
		List<Role> parentRoles = getParentRoles();
		for (Role parentRole : parentRoles)
		{
			roleNames.add(parentRole.getName());
		}
		return roleNames;
	}

	@XmlElementWrapper(name = "parentRoles")
	@XmlElement(name = "role")
	public List<Role> getParentRoles()
	{
		return parentRoles;
	}

	public void setParentRoles(List<Role> groups)
	{
		this.parentRoles = groups;
	}

	public void setMembers(List<Person> members)
	{
		this.members = members;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tura.common.domain.Role#getName()
	 */
	@XmlElement(name = "name")
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tura.common.domain.Role#setName(java.lang.String)
	 */
	public void setName(String string)
	{
		name = string;
	}

	public boolean isMember(Person member)
	{
		return getMembers().contains(member);
	}

	public boolean removeMember(Person user)
	{
		Object prev = getMembers().remove(user);
		return prev != null;
	}

	public boolean removeMembers(List<Person> persons)
	{
		if (getMembers().removeAll(persons))
		{
			return true;
		}
		return false;
	}

	public boolean addMember(Person user)
	{
		boolean isMember = getMembers().contains(user);
		if (isMember == false)
		{
			getMembers().add(user);
			return true;
		}
		return false;
	}

	public boolean addChildRoles(List<Role> childRoles)
	{
		return getChildRoles().addAll(childRoles);
	}

	public boolean removeChildRoles(List<Role> childRoles)
	{
		return getChildRoles().removeAll(childRoles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tura.common.domain.Role#getDescription()
	 */
	public String getDescription()
	{
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tura.common.domain.Role#setDescription(java.lang.String)
	 */
	public void setDescription(String name)
	{
		this.description = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public List<Person> getMembers()
	{
		if (members == null)
		{
			members = new ArrayList<Person>();
		}
		return members;
	}

	public List<Role> getChildRoles()
	{
		if (childRoles == null)
		{
			childRoles = new ArrayList<Role>();
		}
		return childRoles;
	}

	public void setChildRoles(List<Role> childRoles)
	{
		this.childRoles = childRoles;
	}

	public Role frontLoadParentRoles()
	{
		if (!getParentRoles().isEmpty())
		{
			getParentRoles().get(0);
		}
		return this;
	}
}