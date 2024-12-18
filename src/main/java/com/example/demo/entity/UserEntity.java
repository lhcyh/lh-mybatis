package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.example.demo.pojo.User;
import com.example.demo.pojo.Website;
import java.util.List;

@JsonIgnoreProperties(value={"handler"})
public class UserEntity extends User{
    private List<Website>  websiteList;

    public List<Website> getWebsiteList(){
        return websiteList;
    }

    public void setWebsiteList(List<Website> websiteList){
        this.websiteList=websiteList;
    }

}
