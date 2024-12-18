package com.example.demo.mapper;

import io.github.lhcyh.lhmybatis.Example;
import com.example.demo.pojo.User;
import com.example.demo.entity.UserEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper{
    public int addUser(User user);
    public int deleteUserById(Integer id);
    public int updateUser(User user);
    public UserEntity getUserById(Integer id);
    public List<UserEntity> getUserListByExample(Example<User> example);
}
