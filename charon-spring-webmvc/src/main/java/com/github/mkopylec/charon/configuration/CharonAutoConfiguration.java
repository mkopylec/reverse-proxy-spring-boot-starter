package com.github.mkopylec.charon.configuration;

import com.github.mkopylec.charon.CharonProxyFilter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(CharonConfigurer.class)
class CharonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    CharonProxyFilter charonProxyFilter(CharonConfigurer configurer) {
        CharonConfiguration configuration = configurer.configure();
        configuration.validate(); // TODO Remove after extracting Valid interface
        return new CharonProxyFilter(configuration);
    }
}
