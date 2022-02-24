package com.xxxx.security;

import com.xxxx.entity.po.User;
import com.xxxx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        // todo 获取权限
        List<GrantedAuthority> grantedAuthority = getGrantedAuthority(user.getId());

        return new CustomizeUser(user.getId(), username, user.getPassword(), grantedAuthority);
    }

    // 获取权限（角色，菜单）
    public List<GrantedAuthority> getGrantedAuthority(Long userId) {
        String authority = userService.getAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}
