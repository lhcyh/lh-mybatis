package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.example.demo.pojo.Website;
import com.example.demo.pojo.WebsiteType;

@JsonIgnoreProperties(value={"handler"})
public class WebsiteEntity extends Website{
     private WebsiteType websiteType;

     public WebsiteType getWebsiteType(){
          return websiteType;
     }

     public void setWebsiteType(WebsiteType websiteType){
          this.websiteType=websiteType;
     }

}
