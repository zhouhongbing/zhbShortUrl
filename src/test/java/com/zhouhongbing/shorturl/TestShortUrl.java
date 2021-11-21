package com.zhouhongbing.shorturl;

import com.alibaba.fastjson.JSON;
import com.zhouhongbing.shorturl.dto.UrlBeanDto;
import com.zhouhongbing.shorturl.entity.ShorterUrl;
import com.zhouhongbing.shorturl.utils.LRUCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @version 1.0
 * @Author 海纳百川zhb
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TestShortUrl {

    private String longUrlPrefix = "https://blog.csdn.net/";

    @Autowired
    private LRUCache map;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(
                webApplicationContext).build();

    }


    @Test
    public void testGetShortUrt() throws Exception {


        UrlBeanDto urlBeanDto = new UrlBeanDto();


        urlBeanDto.setLongUrl("https://blog.csdn.net/java_zhangshuai/article/details/10694278?utm_medium=distribute.wap_aggpage_search_result.none-task-blog-2~aggregatepage~first_rank_ecpm_v1~rank_aggregation-1-106942758.wap_agg_rank_aggregation&utm_term=%E7%9F%AD%E5%9F%9F%E5%90%8D%E7%94%9F%E6%88%90java");

        log.info("添加长域名数据：" + JSON.toJSONString(urlBeanDto));

        MvcResult result = mockMvc.perform(
                post("/shortUrl/url/getShortUrl").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(JSON.toJSONString(urlBeanDto)))
                .andReturn();
        System.out.println("测试结果:" + result.getResponse().getContentAsString());
    }

    @Test
    public void testGetLongUrl() throws Exception {

        //先根据某个长域名,生成某个短域名并存储在缓存中,才能测试短域名转化长域名
        UrlBeanDto urlBeanDto = new UrlBeanDto();


        urlBeanDto.setLongUrl("https://blog.csdn.net/java_zhangshuai/article/details/10694278?utm_medium=distribute.wap_aggpage_search_result.none-task-blog-2~aggregatepage~first_rank_ecpm_v1~rank_aggregation-1-106942758.wap_agg_rank_aggregation&utm_term=%E7%9F%AD%E5%9F%9F%E5%90%8D%E7%94%9F%E6%88%90java");

        log.info("添加长域名数据：" + JSON.toJSONString(urlBeanDto));

        MvcResult result = mockMvc.perform(
                post("/shortUrl/url/getShortUrl").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(JSON.toJSONString(urlBeanDto)))
                .andReturn();
        System.out.println("存入的短域名结果:" + result.getResponse().getContentAsString());

        String shortUrlbeanStr = result.getResponse().getContentAsString();
        map.put("https://blog.csdn.net/java_zhangshuai/article/details/10694278?utm_medium=distribute.wap_aggpage_search_result.none-task-blog-2~aggregatepage~first_rank_ecpm_v1~rank_aggregation-1-106942758.wap_agg_rank_aggregation&utm_term=%E7%9F%AD%E5%9F%9F%E5%90%8D%E7%94%9F%E6%88%90java", shortUrlbeanStr);


        // 以下是根据短域名转化为长域名
        ShorterUrl shorterUrl = JSON.parseObject(shortUrlbeanStr, ShorterUrl.class);
        String shorterUrlStr = shorterUrl.getShorterUrl();

        System.out.println("本次查询用的短域名为:" + shorterUrlStr);
        UrlBeanDto urlBeanDto1 = new UrlBeanDto();
        urlBeanDto1.setShortUrl(shorterUrlStr);

        MvcResult resultLong = mockMvc.perform(
                post("/shortUrl/url/getLongUrl").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(JSON.toJSONString(urlBeanDto1)))
                .andReturn();
        System.out.println("查询的长域名结果:" + resultLong.getResponse().getContentAsString());

    }


}
