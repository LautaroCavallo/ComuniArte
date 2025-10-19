package com.uade.tpo.marketplace.entity;

import org.springframework.data.neo4j.core.schema.*;

@Node
public class Artist {

    @Id @GeneratedValue
    private Long id;

    private String name;

    public Artist(String name) {
        this.name = name;
    }

    // getters y setters
}
