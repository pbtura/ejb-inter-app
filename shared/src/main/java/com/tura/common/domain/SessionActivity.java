package com.tura.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.search.annotations.DocumentId;

@Entity(name = "SessionActivity")
@Table(name = "session_activity")
@NamedQueries({
		@NamedQuery(name = "SessionActivity.findActiveSessionForApp", query = "select sa from SessionActivity as sa where sa.active=true and sa.applicationName=:appName") })
public class SessionActivity extends com.tura.common.domain.Entity
{
	/**
	 * 
	 */
	@Id
	@XmlElement
	@DocumentId
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	private static final long serialVersionUID = -6629008228457622482L;
	@Column(name = "person_id", insertable = true, updatable = false)
	private Long personId;
	@Column(name = "application_name")
	private String applicationName;
	@Column(name = "active")
	private boolean active;
	@Column(name = "ip_address", nullable = true)
	private String ipAddress;

	public SessionActivity(Long personId, String applicationName, boolean active, String ipAddress)
	{
		super();
		this.personId = personId;
		this.applicationName = applicationName;
		this.active = active;
		this.ipAddress = ipAddress;
	}

	public SessionActivity()
	{
		super();
		// TODO Auto-generated constructor stub
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

	public Long getPersonId()
	{
		return personId;
	}

	public void setPersonId(Long personId)
	{
		this.personId = personId;
	}

	public String getApplicationName()
	{
		return applicationName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}
}
