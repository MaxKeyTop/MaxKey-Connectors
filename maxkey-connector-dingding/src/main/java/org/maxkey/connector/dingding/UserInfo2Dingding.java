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

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.maxkey.connector.UserInfoConnector;
import org.maxkey.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiV2UserCreateRequest;
import com.dingtalk.api.request.OapiV2UserCreateRequest.DeptOrder;
import com.dingtalk.api.request.OapiV2UserCreateRequest.DeptTitle;
import com.dingtalk.api.request.OapiV2UserDeleteRequest;
import com.dingtalk.api.request.OapiV2UserUpdateRequest;
import com.dingtalk.api.response.OapiV2UserCreateResponse;
import com.dingtalk.api.response.OapiV2UserDeleteResponse;
import com.dingtalk.api.response.OapiV2UserUpdateResponse;

@Component(value = "userInfoConnector")
public class UserInfo2Dingding  extends UserInfoConnector{
	private final static Logger logger = LoggerFactory.getLogger(UserInfo2Dingding.class);

	@Autowired
	AccessTokenService accessTokenService;
	
	public UserInfo2Dingding() {
		
	}

	@Override
	public boolean create(UserInfo userInfo) throws Exception{
		logger.info("create");
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/create");
		OapiV2UserCreateRequest req = new OapiV2UserCreateRequest();
		req.setUserid(userInfo.getUsername());
		req.setName(userInfo.getDisplayName());
		req.setMobile(userInfo.getMobile());
		req.setHideMobile(false);
		req.setTelephone(userInfo.getWorkPhoneNumber());
		req.setJobNumber(userInfo.getEmployeeNumber());
		req.setTitle(userInfo.getJobTitle());
		req.setEmail(userInfo.getEmail());
		req.setOrgEmail(userInfo.getWorkEmail());
		req.setWorkPlace(userInfo.getWorkOfficeName());
		req.setRemark(userInfo.getDescription());
		
		if(userInfo.getDepartmentId() !=null && !userInfo.getDescription().equals("")) {
			req.setDeptIdList(userInfo.getDepartmentId());
		}
		
		/*
		List<DeptOrder> list2 = new ArrayList<DeptOrder>();
		DeptOrder obj3 = new DeptOrder();
		list2.add(obj3);
		obj3.setDeptId(2L);
		obj3.setOrder(1L);
		req.setDeptOrderList(list2);
		*/
		/*
		List<DeptTitle> list5 = new ArrayList<DeptTitle>();
		DeptTitle obj6 = new DeptTitle();
		list5.add(obj6);
		obj6.setDeptId(2L);
		obj6.setTitle("资深产品经理");
		req.setDeptTitleList(list5);
		*/
		//req.setExtension("{\"爱好\":\"旅游\"}");
		
		req.setSeniorMode(false);
		if(userInfo.getEntryDate() !=null && !userInfo.getEntryDate().equals("")) {
			req.setHiredDate(new DateTime(userInfo.getEntryDate()).getMillis());
		}
		OapiV2UserCreateResponse rsp = client.execute(req, "access_token");
		System.out.println(rsp.getBody());
			super.create(userInfo);

		return true;
	}
	
	@Override
	public boolean update(UserInfo userInfo) throws Exception{
		logger.info("update");
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/update");
		OapiV2UserUpdateRequest req = new OapiV2UserUpdateRequest();
		req.setUserid(userInfo.getUsername());
		req.setName(userInfo.getDisplayName());
		req.setMobile(userInfo.getMobile());
		req.setHideMobile(false);
		req.setTelephone(userInfo.getWorkPhoneNumber());
		req.setJobNumber(userInfo.getEmployeeNumber());
		req.setTitle(userInfo.getJobTitle());
		req.setEmail(userInfo.getEmail());
		req.setOrgEmail(userInfo.getWorkEmail());
		req.setWorkPlace(userInfo.getWorkOfficeName());
		req.setRemark(userInfo.getDescription());
		if(userInfo.getDepartmentId() !=null && !userInfo.getDescription().equals("")) {
			req.setDeptIdList(userInfo.getDepartmentId());
		}
		/*
		List<com.dingtalk.api.request.OapiV2UserUpdateRequest.DeptOrder> list2 = 
				new ArrayList<com.dingtalk.api.request.OapiV2UserUpdateRequest.DeptOrder>();
		com.dingtalk.api.request.OapiV2UserUpdateRequest.DeptOrder obj3 = 
				new com.dingtalk.api.request.OapiV2UserUpdateRequest.DeptOrder();
		list2.add(obj3);
		obj3.setDeptId(Long.parseLong(userInfo.getDepartmentId()));
		obj3.setOrder(1L);
		req.setDeptOrderList(list2);
		List<com.dingtalk.api.request.OapiV2UserUpdateRequest.DeptTitle> list5 = 
				new ArrayList<com.dingtalk.api.request.OapiV2UserUpdateRequest.DeptTitle>();
		com.dingtalk.api.request.OapiV2UserUpdateRequest.DeptTitle obj6 = 
				new com.dingtalk.api.request.OapiV2UserUpdateRequest.DeptTitle();
		list5.add(obj6);
		obj6.setDeptId(2L);
		obj6.setTitle("资深产品经理");
		req.setDeptTitleList(list5);
		*/
		//req.setExtension("{\"爱好\":\"旅游\",\"年龄\":\"24\"}");
		req.setSeniorMode(false);
		if(userInfo.getEntryDate() !=null && !userInfo.getEntryDate().equals("")) {
			req.setHiredDate(new DateTime(userInfo.getEntryDate()).getMillis());
		}
		req.setLanguage("zh_CN");
		OapiV2UserUpdateResponse rsp = client.execute(req, "access_token");
		System.out.println(rsp.getBody());
		return true;
		
	}
	
	@Override
	public boolean delete(UserInfo userInfo) throws Exception{
		logger.info("delete");
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/delete");
		OapiV2UserDeleteRequest req = new OapiV2UserDeleteRequest();
		req.setUserid(userInfo.getUsername());
		OapiV2UserDeleteResponse rsp = client.execute(req, "access_token");
		System.out.println(rsp.getBody());
		return true;
	}

	public UserInfo loadUser(UserInfo UserInfo) {
		return null;
	}

}
