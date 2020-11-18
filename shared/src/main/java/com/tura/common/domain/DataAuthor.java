package com.tura.common.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity(name = "DataAuthor")
@Indexed
@Audited
@Table(name = "persons")
@NamedQueries({
		@NamedQuery(name = "DataAuthor.findByUsername", query = "select p from DataAuthor p where p.username=:username", hints = {
				@QueryHint(name = "org.hibernate.cacheable", value = "true"),
				@QueryHint(name = "org.hibernate.flushMode", value = "MANUAL") }) })
/**
 * This is designed as a view into the persons table specifically tailor to the needs of the createdBy and updatedBy
 * fields. It is designed as a read only entity which can be serialized with entities in the createdBy and updatedBy
 * fields without pulling all the password and role data along with it which could be a security problem or at the very
 * least create more data in the pipe than is needed.
 * 
 * This is not meant to be a sub class of person.
 * 
 * If you need the person object you can use the id of the DataAuthor to look up the corresponding person.
 * 
 * This class purposely does not extend entity. We don't want to see created and updated by information for this object.
 * 
 *
 */
public class DataAuthor implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(updatable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlElement
	@DocumentId
	protected Long id;
	@Version
	@Column(name = "lock_version", updatable = false)
	@XmlElement
	protected Integer lockVersion = 0;
	// attributes
	@Column(updatable = false)
	@Field(index = Index.YES, store = Store.NO)
	protected String username;
	@Column(name = "first_name", updatable = false)
	@XmlElement
	@Field(index = Index.YES, store = Store.NO)
	protected String firstName;
	@Column(name = "last_name", updatable = false)
	@XmlElement
	@Field(index = Index.YES, store = Store.NO)
	protected String lastName;
	@Column(name = "email", updatable = false)
	@XmlElement
	@Field(index = Index.YES, store = Store.NO)
	protected String email;

	public DataAuthor()
	{
		super();
	}

	public String getUsername()
	{
		return username;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public Long getId()
	{
		return id;
	}

	public Integer getLockVersion()
	{
		return lockVersion;
	}

	public String getFullName()
	{
		String lastName = getLastName() == null ? "" : getLastName();
		String firstName = getFirstName() == null ? "" : getFirstName();
		return new StringBuffer(lastName).append(", ").append(firstName).toString();
	}
}
