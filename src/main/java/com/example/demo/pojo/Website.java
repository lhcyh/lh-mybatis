package com.example.demo.pojo;

public class Website{
    private Integer id;
    private String url;
    private Status status;
    private Boolean isPublic;
    private Boolean isSensitive;
    private String name;
    private String note;
    private Integer typeId;
    private Integer userId;

    public Integer getId(){
      return id;
    }

    public void setId(Integer id){
      this.id=id;
    }

    public String getUrl(){
      return url;
    }

    public void setUrl(String url){
      this.url=url;
    }

    public Status getStatus(){
      return status;
    }

    public void setStatus(Status status){
      this.status=status;
    }

    public Boolean isIsPublic(){
      return isPublic;
    }

    public void setIsPublic(Boolean isPublic){
      this.isPublic=isPublic;
    }

    public Boolean isIsSensitive(){
      return isSensitive;
    }

    public void setIsSensitive(Boolean isSensitive){
      this.isSensitive=isSensitive;
    }

    public String getName(){
      return name;
    }

    public void setName(String name){
      this.name=name;
    }

    public String getNote(){
      return note;
    }

    public void setNote(String note){
      this.note=note;
    }

    public Integer getTypeId(){
      return typeId;
    }

    public void setTypeId(Integer typeId){
      this.typeId=typeId;
    }

    public Integer getUserId(){
      return userId;
    }

    public void setUserId(Integer userId){
      this.userId=userId;
    }

}