package com.uade.tpo.marketplace.service;

// Importamos las clases necesarias de Neo4j
import org.neo4j.driver.Driver; 
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    /**
     * Define el bean del gestor de transacciones para MongoDB.
     * Este es el gestor por defecto (@Primary).
     */
    @Bean(name = "transactionManager")
    @Primary
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    /**
     * --- ¡NUEVO Y CORREGIDO! ---
     * * Define manualmente el bean del gestor de transacciones para Neo4j.
     * * Spring Boot auto-configura un bean 'Driver' cuando ve las propiedades
     * 'spring.neo4j.uri' en tu application.properties.
     * * Nosotros simplemente tomamos ese 'Driver' y lo usamos para
     * construir el 'Neo4jTransactionManager' con el nombre exacto
     * que tu 'Neo4jUserService' está buscando.
     */
    @Bean(name = "neo4jTransactionManager")
    public PlatformTransactionManager neo4jTransactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}