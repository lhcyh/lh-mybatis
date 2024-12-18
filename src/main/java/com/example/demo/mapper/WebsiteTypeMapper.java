package com.example.demo.mapper;

import io.github.lhcyh.lhmybatis.Example;
import com.example.demo.pojo.WebsiteType;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WebsiteTypeMapper{
    public int addWebsiteType(WebsiteType websiteType);
    public int deleteWebsiteTypeById(Integer id);
    public int updateWebsiteType(WebsiteType websiteType);
    public WebsiteType getWebsiteTypeById(Integer id);
    public List<WebsiteType> getWebsiteTypeListByExample(Example<WebsiteType> example);
}
