/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package org.maxkey.connector.activedirectory;

import java.io.UnsupportedEncodingException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.maxkey.connector.UserInfoConnector;
import org.maxkey.constants.ldap.ActiveDirectoryUser;
import org.maxkey.crypto.ReciprocalUtils;
import org.maxkey.domain.UserInfo;
import org.maxkey.persistence.ldap.ActiveDirectoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "userInfoConnector")
public class UserInfo2Activedirectory  extends UserInfoConnector{
	private final static Logger logger = LoggerFactory.getLogger(UserInfo2Activedirectory.class);
	ActiveDirectoryUtils  ldapUtils;

	public UserInfo2Activedirectory() {
		
	}

	@Override
	public boolean create(UserInfo userInfo) throws Exception{
		try {
			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("top");
			objectClass.add("person");
			objectClass.add("organizationalPerson");
			objectClass.add("user");
			attributes.put(objectClass);
			attributes.put(new BasicAttribute(ActiveDirectoryUser.SAMACCOUNTNAME,userInfo.getUsername()));
			logger.debug("decipherable : "+userInfo.getDecipherable());
			String password=ReciprocalUtils.decoder(userInfo.getDecipherable());
			
			attributes.put(new BasicAttribute(ActiveDirectoryUser.UNICODEPWD,("\"" + password + "\"").getBytes("UTF-16LE")));
			//attributes.put(new BasicAttribute("cn",userInfo.getDisplayName()));
			attributes.put(new BasicAttribute(ActiveDirectoryUser.CN,userInfo.getUsername()));
			attributes.put(new BasicAttribute(ActiveDirectoryUser.DISPLAYNAME,userInfo.getDisplayName()));
			attributes.put(new BasicAttribute(ActiveDirectoryUser.GIVENNAME,userInfo.getGivenName()));
			attributes.put(new BasicAttribute(ActiveDirectoryUser.SN,userInfo.getFamilyName()));

			attributes.put(new BasicAttribute(ActiveDirectoryUser.MOBILE,userInfo.getWorkPhoneNumber()==null?"00000000000":userInfo.getWorkPhoneNumber()));
			attributes.put(new BasicAttribute(ActiveDirectoryUser.MAIL,userInfo.getWorkEmail()==null?"email@default.com":userInfo.getWorkEmail()));
		
			attributes.put(new BasicAttribute("employeeNumber",userInfo.getEmployeeNumber()==null?"0":userInfo.getEmployeeNumber()));
			attributes.put(new BasicAttribute("ou",userInfo.getDepartment()==null?"default":userInfo.getDepartment()));
			attributes.put(new BasicAttribute(ActiveDirectoryUser.DEPARTMENT,userInfo.getDepartment()==null?"default":userInfo.getDepartment()));
			String managerDn="CN=dummy,"+ldapUtils.getBaseDN();
			if(userInfo.getManagerId()==null||userInfo.getManagerId().equals("")){
				logger.debug("manager is null.");
			}else{
				UserInfo queryManager=new UserInfo();
				queryManager.setId(userInfo.getManagerId());
				UserInfo manager=loadUser(queryManager);
				SearchControls managerSearchControls = new SearchControls();
				managerSearchControls.setSearchScope(ldapUtils.getSearchScope());
				logger.debug("managerResults : "+ldapUtils.getBaseDN());
				logger.debug("filter : "+"(sAMAccountName="+manager.getUsername()+")");
				logger.debug("managerSearchControls : "+managerSearchControls);
				NamingEnumeration<SearchResult> managerResults = ldapUtils.getConnection()
						.search(ldapUtils.getBaseDN(), "("+ActiveDirectoryUser.SAMACCOUNTNAME+"="+manager.getUsername()+")", managerSearchControls);
				if (managerResults == null || !managerResults.hasMore()) {
					
				}else{
					SearchResult managerSr = (SearchResult) managerResults.next();
					managerDn =managerSr.getNameInNamespace();
				}
			}
			
			attributes.put(new BasicAttribute("manager",managerDn));
			
			attributes.put(new BasicAttribute(ActiveDirectoryUser.DEPARTMENT,userInfo.getDepartment()==null?"default":userInfo.getDepartment()));
			attributes.put(new BasicAttribute("departmentNumber",userInfo.getDepartmentId()==null?"default":userInfo.getDepartmentId()));
			attributes.put(new BasicAttribute(ActiveDirectoryUser.TITLE,userInfo.getJobTitle()==null?"default":userInfo.getJobTitle()));
			
			//for kerberos login
			attributes.put(new BasicAttribute(ActiveDirectoryUser.USERPRINCIPALNAME,this.properties.getProperty("servicePrincipalName")));
			attributes.put(new BasicAttribute(ActiveDirectoryUser.USERPRINCIPALNAME,userInfo.getUsername()+"@"+this.properties.getProperty("domain")));
			
			attributes.put(new BasicAttribute(ActiveDirectoryUser.USERACCOUNTCONTROL,Integer.toString(66048)));
			String rdn="";
			if(userInfo.getDepartmentId()!=null&&
					!userInfo.getDepartmentId().equals("")){
				//get organization dn
				SearchControls searchControls = new SearchControls();
				searchControls.setSearchScope(ldapUtils.getSearchScope());
				logger.debug("managerResults : "+ldapUtils.getBaseDN());
				logger.debug("filter  : "+"(&(objectClass=organizationalUnit)(description="+userInfo.getDepartmentId()+"))");
				logger.debug("searchControls : "+searchControls);
				NamingEnumeration<SearchResult> results = ldapUtils.getConnection()
						.search(ldapUtils.getBaseDN(), "(&(objectClass=organizationalUnit)(description="+userInfo.getDepartmentId()+"))", searchControls);
				
				if (results == null || !results.hasMore()) {
					rdn=ldapUtils.getBaseDN();
				}else{
					SearchResult sr = (SearchResult) results.next();
					rdn =sr.getNameInNamespace();
				}
			}else{
				rdn=ldapUtils.getBaseDN();
			}
			
			//String dn="CN="+userInfo.getDisplayName()+","+rdn;
			String dn="CN="+userInfo.getUsername()+","+rdn;
		
			logger.debug("dn : "+dn);
			ldapUtils.getCtx().createSubcontext(dn, attributes);
			ldapUtils.close();
			super.create(userInfo);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean update(UserInfo userInfo) throws Exception{
		try {
			String dn=null;
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(ldapUtils.getSearchScope());
			NamingEnumeration<SearchResult> results = ldapUtils.getConnection()
					.search(ldapUtils.getBaseDN(), "(sAMAccountName="+userInfo.getUsername()+")", searchControls);
			if (results == null || !results.hasMore()) {
				return create(loadUser(userInfo));
			}
			
			SearchResult sr = (SearchResult) results.next();
			dn =sr.getNameInNamespace();
			
			ModificationItem[] modificationItems = new ModificationItem[8];
			//modificationItems[0]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("displayName",userInfo.getDisplayName()));
			//modificationItems[1]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("cn",userInfo.getDisplayName()));
			//modificationItems[2]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("givenName",userInfo.getGivenName()));
			//modificationItems[3]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("sn",userInfo.getFamilyName()));
			
			modificationItems[0]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(ActiveDirectoryUser.MOBILE,userInfo.getWorkPhoneNumber()==null?"00000000000":userInfo.getWorkPhoneNumber()));
			modificationItems[1]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(ActiveDirectoryUser.MAIL,userInfo.getWorkEmail()==null?"email@default.com":userInfo.getWorkEmail()));
			
			modificationItems[2]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("employeeNumber",userInfo.getEmployeeNumber()==null?"default":userInfo.getEmployeeNumber()));
			modificationItems[3]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("ou",userInfo.getDepartment()==null?"default":userInfo.getDepartment()));
			
			modificationItems[4]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(ActiveDirectoryUser.DEPARTMENT,userInfo.getDepartmentId()==null?"default":userInfo.getDepartment()));
			modificationItems[5]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("departmentNumber",userInfo.getDepartmentId()==null?"default":userInfo.getDepartmentId()));
			modificationItems[6]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(ActiveDirectoryUser.TITLE,userInfo.getJobTitle()==null?"default":userInfo.getJobTitle()));
			
			String managerDn="CN=dummy,"+ldapUtils.getBaseDN();
			if(userInfo.getManagerId()==null||userInfo.getManagerId().equals("")){

			}else{
				UserInfo queryManager=new UserInfo();
				queryManager.setId(userInfo.getManagerId());
				UserInfo manager=loadUser(queryManager);
				SearchControls managerSearchControls = new SearchControls();
				managerSearchControls.setSearchScope(ldapUtils.getSearchScope());
				NamingEnumeration<SearchResult> managerResults = ldapUtils.getConnection()
						.search(ldapUtils.getBaseDN(), "(sAMAccountName="+manager.getUsername()+")", managerSearchControls);
				if (managerResults == null || !managerResults.hasMore()) {
					
				}else{
					SearchResult managerSr = (SearchResult) managerResults.next();
					managerDn =managerSr.getNameInNamespace();
				}
			}
			
			modificationItems[7]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("manager",managerDn));
			
			
			ldapUtils.getCtx().modifyAttributes(dn, modificationItems);
			
			if(userInfo.getDepartmentId()!=null&&
					!userInfo.getDepartmentId().equals("")){
				//get organization dn
				SearchControls orgSearchControls = new SearchControls();
				orgSearchControls.setSearchScope(ldapUtils.getSearchScope());
				NamingEnumeration<SearchResult> orgResults = ldapUtils.getConnection()
						.search(ldapUtils.getBaseDN(), "(&(objectClass=organizationalUnit)(description="+userInfo.getDepartmentId()+"))", orgSearchControls);
				String orgRdn="";
				if (orgResults == null || !orgResults.hasMore()) {
					orgRdn=ldapUtils.getBaseDN();
				}else{
					SearchResult orgSearchResult = (SearchResult) orgResults.next();
					orgRdn =orgSearchResult.getNameInNamespace();
				}
				
				//String newDn="CN="+userInfo.getDisplayName()+","+orgRdn;
				String newDn="CN="+userInfo.getUsername()+","+orgRdn;
				
				if(!dn.equals(newDn)){
					logger.debug("oldDn : "+dn);
					logger.debug("newDn : "+newDn);
					ldapUtils.getCtx().rename(dn, newDn);
				}
			}
			
			ldapUtils.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean delete(UserInfo userInfo) throws Exception{
		try {
			String dn=null;
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(ldapUtils.getSearchScope());
			NamingEnumeration<SearchResult> results = ldapUtils.getConnection()
					.search(ldapUtils.getBaseDN(), "(sAMAccountName="+userInfo.getUsername()+")", searchControls);
			if (results == null || !results.hasMore()) {
				
			}else{
				SearchResult sr = (SearchResult) results.next();
				dn =sr.getNameInNamespace();
				logger.debug("delete dn : "+dn);
				ldapUtils.getCtx().destroySubcontext(dn);
			}
			
			ldapUtils.close();
			super.delete(userInfo);
		} catch (NamingException e) {
			e.printStackTrace();
		} 
		return true;
	}
	

	public UserInfo loadUser(UserInfo UserInfo) {
		return null;
	}
	
	
}
