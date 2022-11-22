package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xkcoding.justauth.AuthRequestFactory;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.thirdapp.ThirdAppConfig;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysThirdAccount;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.ThirdLoginModel;
import org.jeecg.modules.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 *  OAuth2登录
 * 1、ThirdLoginController有些不规范和不满足需要（未实现wechat_mp等），故此新建本class
 * 2、为避免引起模块循环依赖，未放在独立模块
 * 3、参考：justauth使用说明 https://justauth.wiki/guide/quickstart/how-to-use/
 * @Author cfm
 * @since 20211025
 */
@RestController
@RequestMapping("/sys/oauth2")
@Api(tags="OAuth2登录")
@Slf4j
public class OAuth2Controller {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysThirdAccountService sysThirdAccountService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private AuthRequestFactory factory;
	@Autowired
    private ISysDepartService sysDepartService;
	@Autowired
	private ISysTenantService sysTenantService;
	@Autowired
    private ISysDictService sysDictService;

	/**
	 * 获取第三方平台的授权链接
	 * @param source 第三方授权平台
	 */
	@RequestMapping("/render/{source}")
	public void renderAuth(@PathVariable("source") String source, @RequestParam("from") String from, HttpServletResponse response) throws IOException {
		AuthRequest authRequest = factory.get(source);
		String state = AuthStateUtils.createState();
		String authorizeUrl = authRequest.authorize(state);
		if (ThirdAppConfig.DINGTALK.equalsIgnoreCase(source)) {
			//justAuth V1.15.7只实现了qrconnect
			authorizeUrl = authorizeUrl.replace("/connect/qrconnect?", "/connect/oauth2/sns_authorize?");
			authorizeUrl = authorizeUrl.replace("scope=snsapi_login", "scope=snsapi_auth");
		}

		redisUtil.set(state, from,60*30);
		log.info("OAuth2: {}  授权链接:{}", new Object[]{source, authorizeUrl});
		response.sendRedirect(authorizeUrl);
	}

	/**
	 * 访问授权链接后第三方平台的回调处理
	 * @param source 第三方授权平台
	 */
	@RequestMapping("/{source}/callback")
	public void thirdCallbackLogin(@PathVariable("source") String source, AuthCallback callback, HttpServletResponse response) throws IOException {
		log.info("OAuth2: {}  回调登录：{}", new Object[]{source, callback});
		Result<JSONObject> result = new Result<JSONObject>();
		String redirectUrl = (String) redisUtil.get(callback.getState());
		redirectUrl += (redirectUrl.indexOf('?') == -1) ? "?" : "&";

		AuthRequest authRequest = factory.get(source);
		AuthResponse authResponse = authRequest.login(callback);
		log.info(JSONObject.toJSONString(authResponse));
		if (authResponse.getCode() != 2000) {
			int code = 500;
			String message = URLEncoder.encode("获取第三方平台用户信息异常，请联系管理员", "UTF-8");
			redirectUrl = String.format("%s?code=%d&message=%s", redirectUrl, code, message);
			response.sendRedirect(redirectUrl);
			return;
		}

		JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(authResponse.getData()));
		String username = object.getString("username");
		String avatar = object.getString("avatar");
		String uuid = object.getString("uuid");

		//第三方账号是否已存在
		LambdaQueryWrapper<SysThirdAccount> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysThirdAccount::getThirdUserUuid, uuid);
		queryWrapper.eq(SysThirdAccount::getThirdType, source);
		SysThirdAccount thirdAccount = sysThirdAccountService.getOne(queryWrapper);
		if (thirdAccount == null) {
			ThirdLoginModel tlm = new ThirdLoginModel(source, uuid, username, avatar);
			thirdAccount = sysThirdAccountService.saveThirdUser(tlm);
		} else {
			if (thirdAccount.getStatus() == 2) {
				int code = 500;
				String message = URLEncoder.encode("第三方账号被冻结，请用其它方式登录", "UTF-8");
				redirectUrl += String.format("code=%d&message=%s", 500, message);
				response.sendRedirect(redirectUrl);
				return;
			}

			//更新信息
			thirdAccount.setDelFlag(CommonConstant.DEL_FLAG_0);
			thirdAccount.setAvatar(avatar);
			thirdAccount.setRealname(username);
			sysThirdAccountService.updateById(thirdAccount);
		}

		//第三方账号未绑定用户
		if (oConvertUtils.isEmpty(thirdAccount.getSysUserId()) || thirdAccount.getDelFlag() == 1) {
			int code = 206; //HTTP状态码206: 部分处理
			String message = URLEncoder.encode("未绑定用户，请绑定或用其它方式登录", "UTF-8");
			redirectUrl += String.format("code=%d&message=%s&uuid=%s", code, message, uuid);
			response.sendRedirect(redirectUrl);
			return;
		}

		SysUser sysUser = sysUserService.getById(thirdAccount.getSysUserId());
		if (sysUser == null) {
			int code = 500;
			String message = URLEncoder.encode("绑定的用户不存在，请联系管理员", "UTF-8");
			redirectUrl += String.format("code=%d&message=%s", code, message);
			response.sendRedirect(redirectUrl);
			return;
		}

		int code = 200;
		String token = generateToken(sysUser);
		redirectUrl += String.format("code=%d&token=%s", code, token);
		response.sendRedirect(redirectUrl);
	}

	/**
	 * 获取登录用户信息
	 * @param
	 */
	@RequestMapping("/userinfo/{source}/{token}")
	public Result<JSONObject> getUserInfo(@PathVariable("source") String source, @PathVariable("token") String token) {
		Result<JSONObject> result;
		String username = JwtUtil.getUsername(token);
		SysUser sysUser = sysUserService.getUserByName(username);

		result = this.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		result.setResult(this.buildUserInfo(token, source, sysUser));
		result.success("登录成功");
		log.info("用户: {}  {} OAuth2登录成功！", new Object[]{sysUser.getUsername(), source});
		return result;
	}

	/**
	 * 绑定并登录(获取用户信息)
	 * @param
	 * @return
	 */
	@RequestMapping("/binding/{source}")
	public Result<JSONObject> binding(@PathVariable("source") String source, @RequestBody JSONObject jsonObject) {
		Result<JSONObject> result;
		String username = jsonObject.getString("username");
		String password = jsonObject.getString("password");
		String uuid = jsonObject.getString("uuid");

		//用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = this.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//用户名或密码是否正确
		password = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		if (!password.equals(sysUser.getPassword())) {
			result.error500("用户名或密码错误");
			return result;
		}

		//绑定
		LambdaQueryWrapper<SysThirdAccount> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysThirdAccount::getThirdUserUuid, uuid);
		queryWrapper.eq(SysThirdAccount::getThirdType, source);
		SysThirdAccount thirdAccount = sysThirdAccountService.getOne(queryWrapper);
		thirdAccount.setSysUserId(sysUser.getId());
		sysThirdAccountService.updateById(thirdAccount);

		result.setResult(this.buildUserInfo(null, source, sysUser));
		result.success("绑定成功");
		log.info("用户: {}  {} OAuth2绑定并登录成功！", new Object[]{sysUser.getUsername(), source});
		return result;

	}

	private Result<JSONObject> checkUserIsEffective(SysUser sysUser) {
		//用户的关联租户
		String tenantIds = sysUser.getRelTenantIds();
		if (oConvertUtils.isNotEmpty(tenantIds)) {
			List<Integer> tenantIdList = new ArrayList<>();
			for(String id: tenantIds.split(",")){
				tenantIdList.add(Integer.valueOf(id));
			}
			List<SysTenant> tenantList = sysTenantService.queryEffectiveTenant(tenantIdList);
			if (tenantList.size() == 0) {
				return new Result<JSONObject>().error500("用户无有效的关联租户，无法登录！");
			}
		}

		//用户的部门
		String orgCode = sysUser.getOrgCode(); //登录部门
		if(oConvertUtils.isEmpty(orgCode)) {
			List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId()); //所属多部门
			if (departs == null || departs.size() == 0) {
				return new Result<JSONObject>().error500("用户无归属部门，不可登录!");
			}
		}

		return sysUserService.checkUserIsEffective(sysUser);
	}

	private JSONObject buildUserInfo(String token, String source, SysUser sysUser) {
		JSONObject result = new JSONObject();

		if(oConvertUtils.isEmpty(sysUser.getRealname()) || oConvertUtils.isEmpty(sysUser.getAvatar())) {
			LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<>();
			query.eq(SysThirdAccount::getSysUserId, sysUser.getId());
			query.eq(SysThirdAccount::getThirdType, source);
			SysThirdAccount account = sysThirdAccountService.getOne(query);
			if (oConvertUtils.isEmpty(sysUser.getRealname())) {
				sysUser.setRealname(account.getRealname());
			}
			if (oConvertUtils.isEmpty(sysUser.getAvatar())) {
				sysUser.setAvatar(account.getAvatar());
			}
		}
		result.put("userInfo", sysUser);

		// 用户部门信息
		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
		result.put("departs", departs);

		int multi_depart;
		if (departs == null || departs.size() == 0) {
			multi_depart = 0;
		} else if (departs.size() == 1) {
			sysUserService.updateUserDepart(sysUser.getUsername(), departs.get(0).getOrgCode());
			multi_depart = 1;
		} else {
			//查询当前是否有登录部门
			//如果用戶为多部门，数据库为存在上一次登录部门，则取一条存进去
			SysUser sysUserById = sysUserService.getById(sysUser.getId());
			if(oConvertUtils.isEmpty(sysUserById.getOrgCode())){
				sysUserService.updateUserDepart(sysUser.getUsername(), departs.get(0).getOrgCode());
			}
			multi_depart = 2;
		}
		result.put("multi_depart", multi_depart);

		// 用户租户信息
		String tenantIds = sysUser.getRelTenantIds();
		if (oConvertUtils.isNotEmpty(tenantIds)) {
			List<Integer> tenantIdList = new ArrayList<>();
			for(String id: tenantIds.split(",")){
				tenantIdList.add(Integer.valueOf(id));
			}
			List<SysTenant> tenantList = sysTenantService.queryEffectiveTenant(tenantIdList);
			result.put("tenantList", tenantList);
		}

		//代码字典
		result.put("sysAllDictItems", sysDictService.queryAllDictItems());

		//token
		if (token == null) {
			token = generateToken(sysUser);
		}
		result.put("token", token);

		return result;
	}

	private String generateToken(SysUser user) {
		// 生成token
		String token = JwtUtil.sign(user.getUsername(), user.getPassword());
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		// 设置超时时间
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);
		return token;
	}

}
