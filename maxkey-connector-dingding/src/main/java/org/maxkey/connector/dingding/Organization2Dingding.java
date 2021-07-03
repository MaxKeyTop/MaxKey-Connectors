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
 

package org.maxkey.connector.dingding;

import org.apache.commons.lang.StringUtils;
import org.maxkey.connector.OrganizationConnector;
import org.maxkey.domain.Organizations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2DepartmentCreateRequest;
import com.dingtalk.api.request.OapiV2DepartmentDeleteRequest;
import com.dingtalk.api.request.OapiV2DepartmentUpdateRequest;
import com.dingtalk.api.response.OapiV2DepartmentCreateResponse;
import com.dingtalk.api.response.OapiV2DepartmentDeleteResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse.DeptBaseResponse;
import com.dingtalk.api.response.OapiV2DepartmentUpdateResponse;

@Component(value = "organizationConnector")
public class Organization2Dingding  extends OrganizationConnector{
	private final static Logger logger = LoggerFactory.getLogger(Organization2Dingding.class);

	@Autowired
	AccessTokenService accessTokenService;
	
	public Organization2Dingding() {
		
	}

	@Override
	public boolean create(Organizations organization) throws Exception {
		logger.info("create");
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/create");
		OapiV2DepartmentCreateRequest req = new OapiV2DepartmentCreateRequest();
		//req.set(Long.parseLong(organization.getId()));
		req.setParentId(Long.parseLong(organization.getExtParentId()));
		req.setOuterDept(true);
		req.setHideDept(true);
		req.setCreateDeptGroup(true);
		if(StringUtils.isNotEmpty(organization.getSortIndex())) {
			req.setOrder(Long.parseLong(organization.getSortIndex()));
		}
		req.setName(organization.getName());
		req.setSourceIdentifier(organization.getFullName());
		/*
		req.setDeptPermits("3,4,5");
		req.setUserPermits("100,200");
		req.setOuterPermitUsers("500,600");
		req.setOuterPermitDepts("6,7,8");
		req.setOuterDeptOnlySelf(true);
		*/
		OapiV2DepartmentCreateResponse rsp = client.execute(req, "access_token");
		//dingding DeptId
		organization.setExtId(rsp.getResult().getDeptId()+"");
		System.out.println(rsp.getBody());
		
		return super.create(organization);
	}

	@Override
	public boolean update(Organizations organization)  throws Exception{
		logger.info("update");
			
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/update");
		OapiV2DepartmentUpdateRequest req = new OapiV2DepartmentUpdateRequest();
		req.setDeptId(Long.parseLong(organization.getExtId()));
		req.setParentId(Long.parseLong(organization.getExtParentId()));
		req.setOuterDept(true);
		req.setHideDept(true);
		req.setCreateDeptGroup(true);
		if(StringUtils.isNotEmpty(organization.getSortIndex())) {
			req.setOrder(Long.parseLong(organization.getSortIndex()));
		}
		req.setName(organization.getName());
		req.setSourceIdentifier(organization.getFullName());
		req.setAutoAddUser(false);
		req.setLanguage("zh_CN");
		/*
		req.setDeptPermits("123,456");
		req.setUserPermits("user123,manager222");
		req.setOuterPermitUsers("user100,user200");
		req.setOuterPermitDepts("123,456");
		req.setOuterDeptOnlySelf(true);
		
		
		req.setDeptManagerUseridList("manager200");
		req.setGroupContainSubDept(true);
		req.setGroupContainOuterDept(true);
		req.setGroupContainHiddenDept(true);
		req.setOrgDeptOwner("100");
		*/
		OapiV2DepartmentUpdateResponse rsp = client.execute(req, "access_token");
		System.out.println(rsp.getBody());
		return super.update(organization);
	}

	@Override
	public boolean delete(Organizations organization)  throws Exception{
		logger.info("delete");
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/delete");
		OapiV2DepartmentDeleteRequest req = new OapiV2DepartmentDeleteRequest();
		req.setDeptId(Long.parseLong(organization.getId()));
		OapiV2DepartmentDeleteResponse rsp = client.execute(req, "access_token");
		System.out.println(rsp.getBody());
		return super.delete(organization);
	}

}
