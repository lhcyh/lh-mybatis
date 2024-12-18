package com.example.demo.pojo;

public class User{
    private Integer id;
    private String account;
    private String password;
    private Role role;

    public Integer getId(){
      return id;
    }

    public void setId(Integer id){
      this.id=id;
    }

    public String getAccount(){
      return account;
    }

    public void setAccount(String account){
      this.account=account;
    }

    public String getPassword(){
      return password;
    }

    public void setPassword(String password){
      this.password=password;
    }

    public Role getRole(){
      return role;
    }

    public void setRole(Role role){
      this.role=role;
    }

}