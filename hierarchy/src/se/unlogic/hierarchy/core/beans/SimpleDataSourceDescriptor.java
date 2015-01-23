/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.DataSourceType;
import se.unlogic.standardutils.annotations.RequiredIfSet;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;

@Table(name="openhierarchy_data_sources")
public class SimpleDataSourceDescriptor implements Elementable, DataSourceDescriptor{

	@DAOManaged(autoGenerated=true)
	@Key
	private Integer dataSourceID;

	@WebPopulate(required=true)
	@DAOManaged
	private DataSourceType type;

	@DAOManaged
	@WebPopulate(required=true,maxLength=255)
	private String url;

	@DAOManaged
	@WebPopulate(maxLength=255)
	private String defaultCatalog;

	@DAOManaged
	@WebPopulate(maxLength=255)
	@RequiredIfSet(paramName="type",value="SystemManaged")
	private String driver;

	@DAOManaged
	@WebPopulate(required=true,maxLength=255)
	private String name;

	@DAOManaged
	@WebPopulate(maxLength=255)
	@RequiredIfSet(paramName="type",value="SystemManaged")
	private String username;

	@DAOManaged
	@WebPopulate(maxLength=255)
	@RequiredIfSet(paramName="type",value="SystemManaged")
	private String password;

	@DAOManaged
	@WebPopulate
	private boolean enabled;

	@DAOManaged
	@WebPopulate
	private Boolean logAbandoned;

	@DAOManaged
	@WebPopulate
	private Boolean removeAbandoned;

	@DAOManaged
	@WebPopulate
	@RequiredIfSet(paramName="removeAbandoned")
	private Integer removeTimeout;

	@DAOManaged
	@WebPopulate
	private Boolean testOnBorrow;

	@DAOManaged
	@WebPopulate(maxLength=255)
	@RequiredIfSet(paramName="testOnBorrow")
	private String validationQuery;

	@DAOManaged
	@WebPopulate
	@RequiredIfSet(paramName="type",value="SystemManaged")
	private Integer maxActive;

	@DAOManaged
	@WebPopulate
	@RequiredIfSet(paramName="type",value="SystemManaged")
	private Integer maxIdle;

	@DAOManaged
	@WebPopulate
	@RequiredIfSet(paramName="type",value="SystemManaged")
	private Integer minIdle;

	@DAOManaged
	@WebPopulate
	@RequiredIfSet(paramName="type",value="SystemManaged")
	private Integer maxWait;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SimpleDataSourceDescriptor() {}

	public SimpleDataSourceDescriptor(String url) {
		super();
		this.url = url;
		this.type = DataSourceType.ContainerManaged;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSourceID == null) ? 0 : dataSourceID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SimpleDataSourceDescriptor other = (SimpleDataSourceDescriptor) obj;
		if (dataSourceID == null) {
			if (other.dataSourceID != null) {
				return false;
			}
		} else if (!dataSourceID.equals(other.dataSourceID)) {
			return false;
		}
		return true;
	}

	public Integer getDataSourceID() {
		return dataSourceID;
	}

	public void setDataSourceID(Integer datasourceID) {
		this.dataSourceID = datasourceID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public DataSourceType getType() {
		return type;
	}

	public void setType(DataSourceType type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return this.url + " (ID: " + this.dataSourceID + ", " + this.type + ")";
	}

	public boolean removeAbandoned() {

		if(removeAbandoned == null){
			return false;
		}

		return removeAbandoned;
	}


	public void setRemoveAbandoned(boolean removeAbandoned) {
		this.removeAbandoned = removeAbandoned;
	}


	public Integer getRemoveTimeout() {
		return removeTimeout;
	}


	public void setRemoveTimeout(Integer removeTimeout) {
		this.removeTimeout = removeTimeout;
	}


	public boolean testOnBorrow() {

		if(testOnBorrow == null){

			return false;
		}

		return testOnBorrow;
	}


	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}


	public String getValidationQuery() {
		return validationQuery;
	}


	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}


	public Integer getMaxActive() {
		return maxActive;
	}


	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}


	public Integer getMaxIdle() {
		return maxIdle;
	}


	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}


	public Integer getMaxWait() {
		return maxWait;
	}


	public void setMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}

	public boolean logAbandoned() {

		if(logAbandoned == null){
			return false;
		}

		return logAbandoned;
	}

	public void setLogAbandoned(boolean logAbandoned) {
		this.logAbandoned = logAbandoned;
	}

	public Integer getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}

	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}

	public String getDefaultCatalog() {
		return defaultCatalog;
	}

	public Element toXML(Document doc) {

		Element datasource = doc.createElement("datasource");

		datasource.appendChild(XMLUtils.createElement("dataSourceID", this.dataSourceID.toString(), doc));

		datasource.appendChild(XMLUtils.createCDATAElement("url", this.url.toString(), doc));

		datasource.appendChild(XMLUtils.createElement("type", this.type.toString(), doc));

		datasource.appendChild(XMLUtils.createElement("enabled", Boolean.toString(this.enabled), doc));

		XMLUtils.appendNewElement(doc, datasource, "name", this.name);
		XMLUtils.appendNewElement(doc, datasource, "driver", this.driver);
		XMLUtils.appendNewElement(doc, datasource, "username", this.username);
		XMLUtils.appendNewElement(doc, datasource, "password", this.password);
		XMLUtils.appendNewElement(doc, datasource, "defaultCatalog", this.defaultCatalog);
		XMLUtils.appendNewElement(doc, datasource, "logAbandoned", this.logAbandoned);
		XMLUtils.appendNewElement(doc, datasource, "removeAbandoned", this.removeAbandoned);
		XMLUtils.appendNewElement(doc, datasource, "removeTimeout", this.removeTimeout);
		XMLUtils.appendNewElement(doc, datasource, "testOnBorrow", this.testOnBorrow);
		XMLUtils.appendNewElement(doc, datasource, "validationQuery", this.validationQuery);
		XMLUtils.appendNewElement(doc, datasource, "maxActive", this.maxActive);
		XMLUtils.appendNewElement(doc, datasource, "maxIdle", this.maxIdle);
		XMLUtils.appendNewElement(doc, datasource, "minIdle", this.minIdle);
		XMLUtils.appendNewElement(doc, datasource, "maxWait", this.maxWait);

		return datasource;
	}

}
