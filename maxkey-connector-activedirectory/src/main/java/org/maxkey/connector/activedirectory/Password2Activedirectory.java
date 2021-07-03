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
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.maxkey.connector.PasswordConnector;
import org.maxkey.constants.ldap.ActiveDirectoryUser;
import org.maxkey.crypto.ReciprocalUtils;
import org.maxkey.domain.UserInfo;
import org.maxkey.persistence.ldap.ActiveDirectoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value = "passwordConnector")
public class Password2Activedirectory  extends PasswordConnector{
	private final static Logger logger = LoggerFactory.getLogger(Password2Activedirectory.class);
	ActiveDirectoryUtils  ldapUtils;
	
	public Password2Activedirectory() {
		
	}
	
	@Override
	public boolean sync(UserInfo userInfo) throws Exception{
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
				ModificationItem[] modificationItems = new ModificationItem[1];
				logger.info("decipherable : "+userInfo.getDecipherable());
				String password=ReciprocalUtils.decoder(userInfo.getDecipherable());
				//modificationItems[0]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("userPassword",password));
				modificationItems[0]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(ActiveDirectoryUser.UNICODEPWD,("\"" + password + "\"").getBytes("UTF-16LE")));
				
				ldapUtils.getCtx().modifyAttributes(dn, modificationItems);
			}
			
			ldapUtils.close();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return true;
	}

	
}
