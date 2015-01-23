/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.BackgroundModuleDAO;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.DBUtils;

public class MySQLBackgroundModuleDAO extends AnnotatedDAO<SimpleBackgroundModuleDescriptor> implements BackgroundModuleDAO {

	protected final MySQLBackgroundModuleSettingDAO moduleSettingDAO;
	
	protected final QueryParameterFactory<SimpleBackgroundModuleDescriptor, Boolean> enabledQueryParameterFactory;
	protected final QueryParameterFactory<SimpleBackgroundModuleDescriptor, Integer> moduleIDQueryParameterFactory;
	protected final QueryParameterFactory<SimpleBackgroundModuleDescriptor, Integer> sectionIDQueryParameterFactory;
		
	public MySQLBackgroundModuleDAO(DataSource dataSource, MySQLBackgroundModuleSettingDAO moduleSettingDAO) {

		super(dataSource, SimpleBackgroundModuleDescriptor.class, new SimpleAnnotatedDAOFactory());
		this.moduleSettingDAO = moduleSettingDAO;
		
		enabledQueryParameterFactory = this.getParamFactory("enabled", boolean.class);
		moduleIDQueryParameterFactory = this.getParamFactory("moduleID", Integer.class);
		sectionIDQueryParameterFactory = this.getParamFactory("sectionID", Integer.class);		
	}
	
	@Override
	public void add(SimpleBackgroundModuleDescriptor moduleDescriptor) throws SQLException {

		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = this.createTransaction();
		
			this.add(moduleDescriptor,transactionHandler,null);

			this.moduleSettingDAO.set(moduleDescriptor,transactionHandler);
			
			transactionHandler.commit();
			
		}finally{
			
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	@Override
	public void update(SimpleBackgroundModuleDescriptor moduleDescriptor) throws SQLException {

		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = this.createTransaction();
		
			this.update(moduleDescriptor,transactionHandler,null);

			this.moduleSettingDAO.set(moduleDescriptor,transactionHandler);
			
			transactionHandler.commit();
			
		}finally{
			
			TransactionHandler.autoClose(transactionHandler);
		}
	}
	
	public List<SimpleBackgroundModuleDescriptor> getEnabledModules(SectionDescriptor sectionDescriptor) throws SQLException {
		
		Connection connection = null;
		
		try{
			connection = this.dataSource.getConnection();

			HighLevelQuery<SimpleBackgroundModuleDescriptor> query = new HighLevelQuery<SimpleBackgroundModuleDescriptor>();
			
			query.addParameter(this.enabledQueryParameterFactory.getParameter(true));
			query.addParameter(this.sectionIDQueryParameterFactory.getParameter(sectionDescriptor.getSectionID()));			
			
			List<SimpleBackgroundModuleDescriptor> modules = this.getAll(query, connection);
			
			if(modules != null){
				
				getSettingHandlers(modules, connection);
			}
			
			return modules;
			
		}finally{
			
			DBUtils.closeConnection(connection);
		}		
	}

	public List<SimpleBackgroundModuleDescriptor> getModules(SectionDescriptor sectionDescriptor) throws SQLException {

		Connection connection = null;
		
		try{
			connection = this.dataSource.getConnection();
			
			HighLevelQuery<SimpleBackgroundModuleDescriptor> query = new HighLevelQuery<SimpleBackgroundModuleDescriptor>();
			
			query.addParameter(sectionIDQueryParameterFactory.getParameter(sectionDescriptor.getSectionID()));
			
			List<SimpleBackgroundModuleDescriptor> modules = this.getAll(query, connection);
			
			if(modules != null){
				
				getSettingHandlers(modules, connection);
			}
			
			return modules;
			
		}finally{
			
			DBUtils.closeConnection(connection);
		}		
	}

	public SimpleBackgroundModuleDescriptor getModule(Integer moduleID) throws SQLException {
		
		Connection connection = null;
		
		try{
			connection = this.dataSource.getConnection();
			
			HighLevelQuery<SimpleBackgroundModuleDescriptor> query = new HighLevelQuery<SimpleBackgroundModuleDescriptor>();
			
			query.addParameter(moduleIDQueryParameterFactory.getParameter(moduleID));
			
			SimpleBackgroundModuleDescriptor moduleDescriptor = this.get(query, connection);
			
			if(moduleDescriptor != null){
				
				getSettingHandlers(Collections.singletonList(moduleDescriptor), connection);
			}
			
			return moduleDescriptor;
			
		}finally{
			
			DBUtils.closeConnection(connection);
		}
	}

	protected void getSettingHandlers(List<SimpleBackgroundModuleDescriptor> modules, Connection connection) throws SQLException{
		
		for(SimpleBackgroundModuleDescriptor moduleDescriptor : modules){
			
			this.moduleSettingDAO.getSettingsHandler(moduleDescriptor, connection);
		}
	}	
}