package com.hcsp.wxshop.service;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service()
public class ShiroRealm extends AuthorizingRealm {

    private final VerificationCodeCheckService verificationCodeCheckService;

    @Autowired
    public ShiroRealm(VerificationCodeCheckService verificationCodeCheckService) {
        this.verificationCodeCheckService = verificationCodeCheckService;
        this.setCredentialsMatcher((token, info) -> new String((char[]) token.getCredentials()).equals(info.getCredentials()));
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String tel = (String) token.getPrincipal();
        String correctCode = verificationCodeCheckService.getCorrectCode(tel);

        return new SimpleAuthenticationInfo(tel, correctCode, getName());
    }
}
