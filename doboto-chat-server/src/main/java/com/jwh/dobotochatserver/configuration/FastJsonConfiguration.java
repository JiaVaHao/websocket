package com.jwh.dobotochatserver.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FastJsonConfiguration {
    //主要用于RestController序列化对象
    @Bean
    public HttpMessageConverters fastjsonHttpMessageConverter() {
        //定义一个转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        //添加fastjson的配置信息 比如 ：是否要格式化返回的json数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        // 处理中文乱码问题
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);

        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

        //在转换器中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);

        HttpMessageConverter<?> converter = fastConverter;
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        return new HttpMessageConverters(converter);

    }
}
