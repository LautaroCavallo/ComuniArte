package com.uade.tpo.marketplace.controllers.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI comuniArteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ComuniArte API")
                        .description("""
                                ### 🎨 Plataforma de Streaming Cultural y Comunitario
                                
                                **ComuniArte** es una plataforma desarrollada para artistas independientes, 
                                comunidades rurales y colectivos culturales de América Latina.
                                
                                #### 📊 Características Principales:
                                - ✅ Gestión de Contenidos (Videos, Audios, Textos)
                                - ✅ Red Social con Neo4j (Seguidores, Recomendaciones)
                                - ✅ Transmisiones en Vivo con WebSocket
                                - ✅ Listas Personalizadas de Contenidos
                                - ✅ Analytics y Métricas en Tiempo Real
                                - ✅ Sistema de Likes y Comentarios
                                - ✅ Chat en Vivo y Donaciones
                                
                                #### 🗄️ Stack de Bases de Datos (Polyglot Persistence):
                                - **MongoDB**: Contenidos, usuarios, comentarios, listas
                                - **Neo4j**: Relaciones sociales, grafos de influencia
                                - **Redis**: Cache, contadores en tiempo real, Pub/Sub
                                
                                #### 🔐 Autenticación:
                                La mayoría de los endpoints requieren un token JWT.
                                Obtén tu token en `/api/users/login` o `/api/users/register`.
                                """)
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Equipo ComuniArte")
                                .email("contact@comuniarte.com")
                                .url("https://github.com/comuniarte"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("http://localhost:80")
                                .description("Nginx Proxy (Docker)")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa tu token JWT (sin 'Bearer' prefix)")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

