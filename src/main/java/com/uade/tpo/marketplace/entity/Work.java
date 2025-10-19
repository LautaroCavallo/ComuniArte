package com.uade.tpo.marketplace.entity;

import org.springframework.data.neo4j.core.schema.*;

@Node
public class Work {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @Relationship(type = "CREATED_BY")
    private Artist artist;

    public Work(String title, Artist artist) {
        this.title = title;
        this.artist = artist;
    }

    // getters y setters
}
