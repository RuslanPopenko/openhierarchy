/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.handlers.SimpleSettingHandler;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.OrderBy;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLParserPopulateable;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.standardutils.xml.XMLValidationUtils;

public abstract class BaseModuleDescriptor implements ModuleDescriptor, XMLParserPopulateable{

	private static final long serialVersionUID = -420170467834032478L;

	@DAOManaged(autoGenerated=true)
	@Key
	protected Integer moduleID;

	@DAOManaged
	@WebPopulate(required = true, maxLength = 255)
	protected String classname;

	@DAOManaged
	@OrderBy
	@WebPopulate(required = true, maxLength = 255)
	protected String name;

	@DAOManaged
	@WebPopulate
	protected boolean anonymousAccess;

	@DAOManaged
	@WebPopulate
	protected boolean userAccess;

	@DAOManaged
	@WebPopulate
	protected boolean adminAccess;

	@DAOManaged
	@WebPopulate
	protected boolean enabled;

	@DAOManaged
	@WebPopulate
	protected Integer dataSourceID;

	@DAOManaged
	@OneToMany(autoAdd=true,autoUpdate=true,autoGet=true)
	@SimplifiedRelation(addTablePrefix=true,deplurifyTablePrefix=true,table="_groups",remoteKeyColumnName="moduleID",remoteValueColumnName="groupID")
	@WebPopulate(paramName = "group")
	protected List<Integer> allowedGroupIDs;

	@DAOManaged
	@OneToMany(autoAdd=true,autoUpdate=true,autoGet=true)
	@SimplifiedRelation(addTablePrefix=true,deplurifyTablePrefix=true,table="_users",remoteKeyColumnName="moduleID",remoteValueColumnName="userID")
	@WebPopulate(paramName = "user")
	protected List<Integer> allowedUserIDs;

	protected MutableSettingHandler mutableSettingHandler;

	public MutableSettingHandler getMutableSettingHandler() {

		return mutableSettingHandler;
	}

	public void setMutableSettingHandler(MutableSettingHandler mutableSettingHandler) {

		this.mutableSettingHandler = mutableSettingHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#isEnabled()
	 */
	public boolean isEnabled() {

		return enabled;
	}

	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#getModuleID()
	 */
	public Integer getModuleID() {

		return moduleID;
	}

	public void setModuleID(Integer moduleID) {

		this.moduleID = moduleID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#getDataSourceID()
	 */
	public Integer getDataSourceID() {

		return dataSourceID;
	}

	public void setDataSourceID(Integer dataSourceID) {

		this.dataSourceID = dataSourceID;
	}

	public List<Integer> getAllowedGroupIDs() {

		return allowedGroupIDs;
	}

	public void setAllowedGroupIDs(ArrayList<Integer> allowedGroupIDs) {

		this.allowedGroupIDs = allowedGroupIDs;
	}

	public List<Integer> getAllowedUserIDs() {

		return allowedUserIDs;
	}

	public void setAllowedUserIDs(ArrayList<Integer> allowedUserIDs) {

		this.allowedUserIDs = allowedUserIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#getName()
	 */
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#isUserAccess()
	 */
	public boolean allowsUserAccess() {

		return userAccess;
	}

	public void setUserAccess(boolean userAccess) {

		this.userAccess = userAccess;
	}

	@Override
	public int hashCode() {

		Integer moduleID = this.moduleID;

		if (moduleID == null) {

			return super.hashCode();
		}

		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleID == null) ? 0 : moduleID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		Integer moduleID = this.moduleID;

		if (moduleID == null) {

			return super.equals(obj);
		}

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BaseModuleDescriptor other = (BaseModuleDescriptor) obj;

		if (!moduleID.equals(other.moduleID)) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#isAnonymousAccess()
	 */
	public boolean allowsAnonymousAccess() {

		return anonymousAccess;
	}

	public void setAnonymousAccess(boolean anonymousAccess) {

		this.anonymousAccess = anonymousAccess;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#getClassname()
	 */
	public String getClassname() {

		return classname;
	}

	public void setClassname(String classname) {

		this.classname = classname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.ModuleDescriptor#isAdminAccess()
	 */
	public boolean allowsAdminAccess() {

		return adminAccess;
	}

	public void setAdminAccess(boolean adminAccess) {

		this.adminAccess = adminAccess;
	}

	@Override
	public String toString() {

		return name + " (moduleID: " + this.moduleID + ")";
	}

	public Element toXML(Document doc) {

		Element moduleElement = doc.createElement("module");

		if(moduleID != null){
			moduleElement.appendChild(XMLUtils.createElement("moduleID", this.moduleID.toString(), doc));
		}

		moduleElement.appendChild(XMLUtils.createCDATAElement("name", this.name, doc));
		moduleElement.appendChild(XMLUtils.createElement("classname", this.classname, doc));

		if (this.dataSourceID != null) {
			moduleElement.appendChild(XMLUtils.createElement("dataSourceID", this.dataSourceID.toString(), doc));
		}

		moduleElement.appendChild(XMLUtils.createElement("adminAccess", Boolean.toString(this.adminAccess), doc));
		moduleElement.appendChild(XMLUtils.createElement("userAccess", Boolean.toString(this.userAccess), doc));
		moduleElement.appendChild(XMLUtils.createElement("anonymousAccess", Boolean.toString(this.anonymousAccess), doc));
		moduleElement.appendChild(XMLUtils.createElement("enabled", Boolean.toString(this.enabled), doc));

		AccessUtils.appendAllowedGroupAndUserIDs(doc, moduleElement, this);

		return moduleElement;
	}

	public Element toXML(Document doc, boolean includeSettings) {

		Element moduleElement = toXML(doc);

		if(mutableSettingHandler != null){
			moduleElement.appendChild(mutableSettingHandler.toXML(doc));
		}

		return moduleElement;
	}

	public void populate(XMLParser xmlParser) throws ValidationException {

		List<ValidationError> errors = new ArrayList<ValidationError>();

		this.name = XMLValidationUtils.validateParameter("name", xmlParser, true, 1, 255, StringPopulator.getPopulator(), errors);
		this.classname = XMLValidationUtils.validateParameter("classname", xmlParser, true, 1, 255, StringPopulator.getPopulator(), errors);
		this.adminAccess = xmlParser.getBoolean("adminAccess");
		this.userAccess = xmlParser.getBoolean("userAccess");
		this.anonymousAccess = xmlParser.getBoolean("anonymousAccess");
		this.enabled = xmlParser.getBoolean("enabled");
		this.dataSourceID = xmlParser.getInteger("dataSourceID");
		this.moduleID = xmlParser.getInteger("moduleID");
		
		this.allowedGroupIDs = xmlParser.getIntegers("allowedGroupIDs/groupID");
		this.allowedUserIDs = xmlParser.getIntegers("allowedUserIDs/userID");
		
		XMLParser settingsParser = xmlParser.getNode("settings"); 
		
		if(settingsParser != null){
			
			this.mutableSettingHandler = new SimpleSettingHandler(settingsParser);
		}

		if(!errors.isEmpty()){

			throw new ValidationException(errors);
		}
	}
}