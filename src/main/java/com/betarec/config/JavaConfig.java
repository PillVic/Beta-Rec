package com.betarec.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.betarec")
@Import({DataSourceConfig.class, MybatisConfig.class})
public class JavaConfig {
}
