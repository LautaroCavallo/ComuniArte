package com.uade.tpo.marketplace.entity;

import org.springframework.data.neo4j.core.schema.*;

import com.uade.tpo.marketplace.entity.mongodb.Artista;

@Node
public class Work {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @Relationship(type = "CREATED_BY")
    private Artista artist;

    public Work(String title, Artista artist) {
        this.title = title;
        this.artist = artist;
    }

    // getters y setters
}
