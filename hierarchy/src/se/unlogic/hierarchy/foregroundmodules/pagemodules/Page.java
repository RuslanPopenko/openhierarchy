/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.pagemodules;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.SearchableItem;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.OrderBy;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.annotations.URLRewrite;

@Table(name = "pages")
public class Page implements AccessInterface, SearchableItem {

	private static final Pattern PERCENT_PATTERN = Pattern.compile("%(?![0-9a-fA-F]{2})");
	public static final String RELATIVE_PATH_MARKER = "/@";

	@DAOManaged(autoGenerated=true)
	@Key
	private Integer pageID;

	@DAOManaged
	@OrderBy
	private String name;

	@DAOManaged
	private String description;

	@DAOManaged
	@URLRewrite
	private String text;

	@DAOManaged
	private boolean enabled;

	@DAOManaged
	private boolean visibleInMenu;

	@DAOManaged
	private boolean breadCrumb;

	@DAOManaged
	private boolean anonymousAccess;

	@DAOManaged
	private boolean userAccess;

	@DAOManaged
	private boolean adminAccess;

	@DAOManaged
	private String alias;

	@DAOManaged
	private Integer sectionID;

	@DAOManaged
	@OneToMany(autoAdd = true, autoGet = true, autoUpdate = true)
	@SimplifiedRelation(table = "page_groups", remoteValueColumnName = "groupID")
	private List<Integer> allowedGroupIDs;

	@DAOManaged
	@OneToMany(autoAdd = true, autoGet = true, autoUpdate = true)
	@SimplifiedRelation(table = "page_users", remoteValueColumnName = "userID")
	private List<Integer> allowedUserIDs;

	private String unescapedText;

	public Integer getSectionID() {

		return sectionID;
	}

	public void setSectionID(Integer sectionID) {

		this.sectionID = sectionID;
	}

	public Integer getPageID() {

		return pageID;
	}

	public void setPageID(Integer pageID) {

		this.pageID = pageID;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}

	@Override
	public boolean allowsAnonymousAccess() {

		return anonymousAccess;
	}

	public void setAnonymousAccess(boolean anonymousAccess) {

		this.anonymousAccess = anonymousAccess;
	}

	@Override
	public boolean allowsUserAccess() {

		return userAccess;
	}

	public void setUserAccess(boolean userAccess) {

		this.userAccess = userAccess;
	}

	@Override
	public boolean allowsAdminAccess() {

		return adminAccess;
	}

	public void setAdminAccess(boolean adminAccess) {

		this.adminAccess = adminAccess;
	}

	public boolean isVisibleInMenu() {

		return visibleInMenu;
	}

	public void setVisibleInMenu(boolean visibleInMenu) {

		this.visibleInMenu = visibleInMenu;
	}

	public boolean isEnabled() {

		return enabled;
	}

	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	@Override
	public String toString() {

		return this.getName() + " (pageID: " + this.getPageID() + ")";
	}

	public Element toXML(Document doc) {

		return toXML(doc, null);
	}

	public Element toXML(Document doc, String text) {

		Element page = doc.createElement("page");

		if (this.pageID != null) {
			page.appendChild(XMLUtils.createElement("pageID", Integer.toString(this.pageID), doc));
		}

		if (this.sectionID != null) {
			page.appendChild(XMLUtils.createElement("sectionID", Integer.toString(this.sectionID), doc));
		}

		if (this.name != null) {
			page.appendChild(XMLUtils.createCDATAElement("name", name, doc));
		}

		if (text != null) {

			page.appendChild(XMLUtils.createCDATAElement("text", text, doc));

		} else {

			page.appendChild(XMLUtils.createCDATAElement("text", this.text, doc));
		}

		if (this.description != null) {
			page.appendChild(XMLUtils.createCDATAElement("description", description, doc));
		}

		if (this.alias != null) {
			page.appendChild(XMLUtils.createCDATAElement("alias", alias, doc));
		}

		page.appendChild(XMLUtils.createElement("anonymousAccess", Boolean.toString(this.anonymousAccess), doc));
		page.appendChild(XMLUtils.createElement("userAccess", Boolean.toString(this.userAccess), doc));
		page.appendChild(XMLUtils.createElement("adminAccess", Boolean.toString(this.adminAccess), doc));

		page.appendChild(XMLUtils.createElement("enabled", Boolean.toString(this.enabled), doc));
		page.appendChild(XMLUtils.createElement("visibleInMenu", Boolean.toString(this.visibleInMenu), doc));
		page.appendChild(XMLUtils.createElement("breadCrumb", Boolean.toString(this.breadCrumb), doc));

		AccessUtils.appendAllowedGroupAndUserIDs(doc, page, this);

		return page;
	}

	@Override
	public String getAlias() {

		return alias;
	}

	public void setAlias(String alias) {

		this.alias = alias;
	}

	@Override
	public List<Integer> getAllowedGroupIDs() {

		return allowedGroupIDs;
	}

	public void setAllowedGroupIDs(List<Integer> allowedGroupIDs) {

		this.allowedGroupIDs = allowedGroupIDs;
	}

	@Override
	public List<Integer> getAllowedUserIDs() {

		return allowedUserIDs;
	}

	public void setAllowedUserIDs(List<Integer> allowedUserIDs) {

		this.allowedUserIDs = allowedUserIDs;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((pageID == null) ? 0 : pageID.hashCode());
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
		Page other = (Page) obj;
		if (pageID == null) {
			if (other.pageID != null) {
				return false;
			}
		} else if (!pageID.equals(other.pageID)) {
			return false;
		}
		return true;
	}

	public String getUnescapedText() {

		if (this.unescapedText == null) {

			unescapedText = PERCENT_PATTERN.matcher(text).replaceAll("%25");
			try {
				unescapedText = URLDecoder.decode(unescapedText, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		return unescapedText;
	}

	public boolean hasBreadCrumb() {

		return breadCrumb;
	}

	public void setBreadCrumb(boolean breadCrumb) {

		this.breadCrumb = breadCrumb;
	}

	@Override
	public String getID() {

		return pageID.toString();
	}

	@Override
	public String getTitle() {

		return name;
	}

	@Override
	public String getContentType() {

		return "text/html";
	}

	@Override
	public InputStream getData() {

		return StringUtils.getInputStream(text);
	}

	@Override
	public long getLastModified() {

		return 0;
	}

	@Override
	public AccessInterface getAccessInterface() {

		return this;
	}
}
