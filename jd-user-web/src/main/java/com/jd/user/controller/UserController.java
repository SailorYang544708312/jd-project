package com.jd.user.controller;
import java.util.List;

import com.jd.common.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.jd.common.pojo.JdResult;
import com.jd.common.pojo.PageResult;
import com.jd.pojo.TbUser;
import com.jd.user.service.UserService;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference(timeout=5000)
	private UserService userService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){
		return userService.findAll();
	}


	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){
		return userService.findPage(page, rows);
	}

	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public JdResult add(@RequestBody TbUser user,@RequestParam(value="smscode",defaultValue="") String smscode){
		//做验证码的验证
		JdResult result = userService.checkSmsCode(user.getPhone(), smscode);
		if(result.isSuccess() == false){
			return result;
		}
		try {
			userService.add(user);
			return new JdResult(true, "增加成功",null);
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "增加失败",null);
		}
	}

	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public JdResult update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new JdResult(true, "修改成功",null);
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "修改失败",null);
		}
	}

	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public JdResult delete(Long [] ids){
		try {
			userService.delete(ids);
			return new JdResult(true, "删除成功" ,null);
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "删除失败" ,null);
		}
	}

	/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);
	}

	@RequestMapping("/sendCode")
	public JdResult sendCode(@RequestParam("phone") String phone){
		//判断用户的手机号是否正确(实际开发中，重要的数据应该前端验证+后端验证)
		if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
			return new JdResult(false, "手机号格式不正确", null);
		}
		try {
			userService.createSmsCode(phone);
			return JdResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return new JdResult(false, "验证码创建失败", null);
		}
	}

}
