/******************************************************************
 *
 *    
 *
 *    Copyright (c) 2016-forever 
 *    http://www.fzqblog.top
 *
 *    Package:     com.qiton.controller
 *
 *    Filename:    UserCotroller.java
 *
 *    Description: TODO(用一句话描述该文件做什么)
 *
 *    Copyright:   Copyright (c) 2001-2014
 *
 *    Company:     fzqblog
 *
 *    @author:     抽离
 *
 *    @version:    1.0.0
 *
 *    Create at:   2016年10月21日 下午5:10:05
 *
 *    Revision:
 *
 *    2016年10月21日 下午5:10:05
 *        - first revision
 *
 *****************************************************************/
package com.qiton.controller;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qiton.exception.BussinessException;
import com.qiton.model.User;
import com.qiton.service.ISmsService;
import com.qiton.service.IUserService;
import com.qiton.utils.StringUtils;

/**
 * @ClassName UserCotroller
 * @Description TODO(这里用一句话描述这个类的作用)
 * @author 抽离
 * @Date 2016年10月21日 下午5:10:05
 * @version 1.0.0
 */
@Controller
@RequestMapping("/user")
public class UserCotroller extends BaseController{
	
    private static final Logger LOGGER = LogManager.getLogger(UserCotroller.class);
	
	@Autowired
	private ISmsService smsService;
	
	@Autowired
	private IUserService userService;
	
	@ResponseBody
	@RequestMapping("sendSms")
	public Object sendSms(String phoneNumber, HttpSession session){
		String validateCode = StringUtils.getRandomCode();
		session.setAttribute("validateCode", validateCode);
		try{
			smsService.sendSms(phoneNumber, validateCode);
			session.setAttribute("rightValidateCode", validateCode);
		}catch(BussinessException e){
			LOGGER.info(phoneNumber + "----" + e.getLocalizedMessage());
			return renderError(e.getLocalizedMessage());
		}
		return renderSuccess();
	}
	
	
	@ResponseBody
	@RequestMapping("regist")
	public Object regist(User user, HttpSession session){
		try{
			this.userService.regist(user, (String) session.getAttribute("rightValidateCode"));
		}catch(BussinessException e){
			LOGGER.info(e.getLocalizedMessage());
			return renderError(user.getPhone() + "----" + e.getLocalizedMessage());
		}catch (Exception e) {
			LOGGER.info(user.getPhone() + "----" + e.getLocalizedMessage());
			return renderError("注册失败请重试");
		}
		return renderSuccess();
	}
}