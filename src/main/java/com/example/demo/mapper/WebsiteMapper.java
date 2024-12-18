package com.example.demo.mapper;

import io.github.lhcyh.lhmybatis.Example;
import com.example.demo.pojo.Website;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WebsiteMapper{
    public int addWebsite(Website website);
    public int deleteWebsiteById(Integer id);
    public int updateWebsite(Website website);
    public Website getWebsiteById(Integer id);
    public List<Website> getWebsiteListByUserId(Integer userId);
    public List<Website> getWebsiteListByExample(Example<Website> example);
}
