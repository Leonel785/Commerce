package com.ecommerce.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configura Jackson para evitar el error:
 *   "Type definition error: [simple type, class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor]"
 *
 * Estrategia:
 *  - Globalmente ignorar el campo "hibernateLazyInitializer" (que añade Hibernate a cada proxy).
 *  - Soporte para LocalDateTime (Java 8 time).
 *  - No incluir campos nulos en la salida.
 *
 * Adicionalmente, en cada entidad se usa @JsonIgnore / @JsonIgnoreProperties sobre
 * las relaciones que no se quieren exponer, para evitar ciclos y carga innecesaria.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Ignora el atributo interno que Hibernate añade a los proxies lazy.
        mapper.addMixIn(Object.class, HibernateProxyMixIn.class);

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    /**
     * A nivel global, cualquier objeto que se serialice omitirá el campo
     * hibernateLazyInitializer (también conocido como "handler"), el cual es el
     * ByteBuddyInterceptor que rompe la serialización.
     */
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private static final class HibernateProxyMixIn {
    }
}
