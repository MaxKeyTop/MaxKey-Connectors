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
 

package org.maxkey.connector;

import org.maxkey.constants.ConstantsProperties;
import org.maxkey.persistence.ldap.LdapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(ConstantsProperties.applicationPropertySource)
public class WelinkConfig {
	
	//@Bean(name = "ldapUtils")
	public LdapUtils getLdapConnection(
			@Value("${config.connector.ldap.providerUrl}") String providerUrl,
			@Value("${config.connector.ldap.principal}") String principal,
			@Value("${config.connector.ldap.credentials}") String credentials,
			@Value("${config.connector.ldap.baseDN}") String baseDn
			)throws Exception{
		
		LdapUtils ldapUtils=new LdapUtils(
				providerUrl,
				principal,
				credentials,
				baseDn);
		if(ldapUtils.openConnection()==null){
			 throw new Exception("connection to Ldap Error.");   
		}
		return ldapUtils;
	}

}