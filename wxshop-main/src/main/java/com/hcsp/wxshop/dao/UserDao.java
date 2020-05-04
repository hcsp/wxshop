package com.hcsp.wxshop.dao;

import com.hcsp.wxshop.generate.User;
import com.hcsp.wxshop.generate.UserExample;
import com.hcsp.wxshop.generate.UserMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("ALL")
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
@Service
public class UserDao {
    private final UserMapper userMapper;

    @Autowired
    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void insertUser(User user) {
        userMapper.insert(user);
    }

    public User getUserByTel(String tel) {
        UserExample example = new UserExample();
        example.createCriteria().andTelEqualTo(tel);
        List<User> users = userMapper.selectByExample(example);
        return users.isEmpty() ? null : users.get(0);
    }
}
