package mmp.im.gate.configuration;


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


@EnableAsync
@Configuration
public class BeanConfig extends WebMvcConfigurerAdapter {

    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Value("${file.path}")
    private String path;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(staticAccessPath).addResourceLocations("file:" + path);
    }


    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("dialect", "mysql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }


    // 跨域
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Bean
    public ConversionService conversionService() {

        FormattingConversionServiceFactoryBean conversionService = new FormattingConversionServiceFactoryBean();

        Set<Converter> set = new HashSet<>();
        set.add(new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                Assert.hasLength(source, "text must be specified");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateFormat.setLenient(false);
                Date date = null;
                try {
                    date = dateFormat.parse(source);
                } catch (ParseException e) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(source);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return date;
            }
        });


        set.add(new Converter<Date, String>() {
            @Override
            public String convert(Date source) {
                return (source == null) ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(source);
            }
        });

        conversionService.setConverters(set);
        conversionService.afterPropertiesSet();

        return conversionService.getObject();
    }

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {

        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        fastConverter.setFastJsonConfig(fastJsonConfig);

        return new HttpMessageConverters(fastConverter);

    }
}
