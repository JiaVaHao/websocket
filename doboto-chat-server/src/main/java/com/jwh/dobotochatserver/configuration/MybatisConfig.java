package com.jwh.dobotochatserver.configuration;

import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan("com.jwh.dobotochatserver.dao")
public class MybatisConfig {
    @Value("${mapper.xml.config.path}")
    private String mapperXMLConfigPath;
    @Value("${mapper.entity.path}")
    private String mapperPackagePath;
    @Autowired
    private DataSource dataSource;

    //配置PageHelper插件
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        pageHelper.setProperties(p);
        return pageHelper;
    }

    //配置mybatis sqlSessionFactoryBean
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //拼接mapper.xml路径
        String xmlConfigPath = PathMatchingResourcePatternResolver.CLASSPATH_URL_PREFIX + mapperXMLConfigPath;
        //factory设置Mapper.xml
        factoryBean.setMapperLocations(resolver.getResources(xmlConfigPath));
        //或者用这条，效果一样factoryBean.setMapperLocations(resolver.getResources("classpath:/mapper/*Mapper.xml"));
        //设置数据源
        factoryBean.setDataSource(dataSource);
        //设置实体包以用于别名（entity包）
        factoryBean.setTypeAliasesPackage(mapperPackagePath);
        //最终会绑定与mapperScan所指定的mapper包的mapper接口中
        //注册pagehelper插件到mybatis bean中
        Interceptor[] plugins = new Interceptor[]{pageHelper()};
        factoryBean.setPlugins(plugins);

        factoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);

        return factoryBean.getObject();
    }
}
