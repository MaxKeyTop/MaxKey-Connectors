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

import org.maxkey.connector.UserInfoConnector;
import org.maxkey.domain.UserInfo;
import org.maxkey.workweixin.domain.WorkWeixinUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component(value = "userInfoConnector")
public class UserInfo2Weixin  extends UserInfoConnector{
	private final static Logger logger = LoggerFactory.getLogger(UserInfo2Weixin.class);
	static String CREATE_USER_URL 	=	"https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=%s";
	static String UPDATE_USER_URL 	=	"https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=%s";
	static String DELETE_USER_URL 	=	"https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=%s&userid=%s";
	static String GET_USER_URL		= 	"https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";
	
	@Autowired
	AccessTokenService accessTokenService;
	
	
	public UserInfo2Weixin() {
		
	}

	@Override
	public boolean create(UserInfo userInfo) throws Exception{
		logger.info("create");
		
		return true;
	}
	
	@Override
	public boolean update(UserInfo userInfo) throws Exception{
		logger.info("update");
		
		return true;
		
	}
	
	@Override
	public boolean delete(UserInfo userInfo) throws Exception{
		logger.info("delete");
		
		return true;
	}

	public UserInfo loadUser(UserInfo UserInfo) {
		return null;
	}
	
	
	public WorkWeixinUsers  buildUserInfo( UserInfo userInfo) {
		WorkWeixinUsers user = new  WorkWeixinUsers();
		
		user.setUserid(userInfo.getUsername());//账号
		user.setName(userInfo.getDisplayName());//名字
		user.setAlias(userInfo.getNickName());//名字
		
		user.setMobile(userInfo.getMobile());//手机
		user.setEmail(userInfo.getEmail());
		user.setGender(userInfo.getGender()+"");
		
		user.setTelephone(userInfo.getWorkPhoneNumber());//工作电话
		if(userInfo.getDepartmentId()!= null && !userInfo.getDepartmentId().equals("")) {
			user.setMain_department(Long.parseLong(userInfo.getDepartmentId()));
		}
		user.setPosition(userInfo.getJobTitle());//职务
		user.setAddress(userInfo.getWorkAddressFormatted());//工作地点

		//激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。
		user.setStatus(userInfo.getStatus());
		return user;
	}
}
