package com.morning.taskapimain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.util.List;

@Configuration
public class R2dbcConfig {

/*    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        return new R2dbcCustomConversions(CustomConversions.StoreConversions.NONE, List.of(
                new JsonbToMapConverter(),
                new MapToJsonbConverter()
        ));
    }*/
}
