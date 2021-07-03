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
 

package org.maxkey.connector.workweixin;

import org.maxkey.connector.OrganizationConnector;
import org.maxkey.domain.Organizations;
import org.maxkey.workweixin.domain.WorkWeixinDepts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "organizationConnector")
public class Organization2Weixin  extends OrganizationConnector{
	private final static Logger logger = LoggerFactory.getLogger(Organization2Weixin.class);

	static String CREATE_ORG_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=%s";
	static String UPDATE_ORG_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=%s";
	static String DELETE_ORG_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=%s&id=%s";
	
	@Autowired
	AccessTokenService accessTokenService;
	
	
	public Organization2Weixin() {
		
	}
	
	@Override
	public boolean create(Organizations organization) throws Exception {
		logger.info("create");
		
		
		return super.create(organization);
	}

	@Override
	public boolean update(Organizations organization)  throws Exception{
		logger.info("update");
		WorkWeixinDepts dept= new WorkWeixinDepts();
		
		return super.update(organization);
	}

	@Override
	public boolean delete(Organizations organization)  throws Exception{
		logger.info("delete");
			
		
		return super.delete(organization);
	}
	
	public WorkWeixinDepts builderOrganization(Organizations organization) {
		WorkWeixinDepts dept= new WorkWeixinDepts();
		dept.setId(Long.parseLong(organization.getId()));	
		dept.setName(organization.getName());	
		dept.setParentid(Long.parseLong(organization.getParentId()));
		dept.setOrder(Long.parseLong(organization.getSortIndex()));
		return dept;
	}

}
