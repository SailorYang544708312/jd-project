package com.jd.shop.service.impl;

import com.jd.pojo.TbSeller;
import com.jd.sellergoods.service.SellerService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现UserDetailsService的接口，用来连接数据的验证
 */
public class UserDetailsServiceImpl implements UserDetailsService {

   private SellerService sellerService;

   public void setSellerService(SellerService sellerService) {
      this.sellerService = sellerService;
   }

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      //写死，不走数据库，感受一波
      //角色的集合
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));//添加角色（授予角色）
      //通过商家id获取到商家对象
      TbSeller seller = sellerService.findOne(username);
      if (seller != null){
         //用户名是存在的
         if (seller.getStatus().equals("1")){
            //表示审核通过,再交给spring security去做密码的校验
            //参数一 要验证的用户名，  参数二 正确的密码(数据库里的密码), 参数三 如果认证成功，授予角色
            return new User(username,seller.getPassword(),authorities);
         }else {
            //表示用户名存在，但是运营商没有审核通过
            throw  new BadCredentialsException("您的账号还在审核中");
         }

      }else {
         //表示用户名不存在
         throw new BadCredentialsException("表示用户名不存在");
      }


   }
}
