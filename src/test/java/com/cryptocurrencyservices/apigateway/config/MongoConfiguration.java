package com.cryptocurrencyservices.apigateway.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class })
public class MongoConfiguration {
    @Autowired
    private Environment environment;

    @Autowired
    private MongoProperties properties;

    @Autowired(required = false)
    private MongoClientOptions options;

    @Autowired
    private IMongodConfig mongoConfig;

    @Bean
    public MongoClient mongo() throws Exception {
        properties.setPort(mongoConfig.net().getPort());
        return properties.createMongoClient(this.options, this.environment);
    }
}
