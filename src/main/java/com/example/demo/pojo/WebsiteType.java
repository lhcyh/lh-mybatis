package com.example.demo.pojo;

public class WebsiteType{
    private Integer id;
    private String name;
    private Status status;
    private Integer userId;

    public Integer getId(){
      return id;
    }

    public void setId(Integer id){
      this.id=id;
    }

    public String getName(){
      return name;
    }

    public void setName(String name){
      this.name=name;
    }

    public Status getStatus(){
      return status;
    }

    public void setStatus(Status status){
      this.status=status;
    }

    public Integer getUserId(){
      return userId;
    }

    public void setUserId(Integer userId){
      this.userId=userId;
    }

}