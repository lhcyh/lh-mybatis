package io.github.lhcyh.lhmybatis.pojo;

import java.util.ArrayList;
import java.util.List;

public class ProjectCode {
    private List<CodeFile> pojo;
    private List<CodeFile> entity;
    private List<CodeFile> mapper;
    private List<CodeFile> mapperXml;
    private List<CodeFile> service;
    private List<CodeFile> controller;

    public ProjectCode(){
        this.pojo=new ArrayList<>();
        this.entity=new ArrayList<>();
        this.mapper=new ArrayList<>();
        this.mapperXml=new ArrayList<>();
        this.service=new ArrayList<>();
        this.controller=new ArrayList<>();
    }

    public List<CodeFile> getPojo() {
        return pojo;
    }

    public void setPojo(List<CodeFile> pojo) {
        this.pojo = pojo;
    }

    public List<CodeFile> getEntity() {
        return entity;
    }

    public void setEntity(List<CodeFile> entity) {
        this.entity = entity;
    }

    public List<CodeFile> getMapper() {
        return mapper;
    }

    public void setMapper(List<CodeFile> mapper) {
        this.mapper = mapper;
    }

    public List<CodeFile> getMapperXml() {
        return mapperXml;
    }

    public void setMapperXml(List<CodeFile> mapperXml) {
        this.mapperXml = mapperXml;
    }

    public List<CodeFile> getService() {
        return service;
    }

    public void setService(List<CodeFile> service) {
        this.service = service;
    }

    public List<CodeFile> getController() {
        return controller;
    }

    public void setController(List<CodeFile> controller) {
        this.controller = controller;
    }
}
