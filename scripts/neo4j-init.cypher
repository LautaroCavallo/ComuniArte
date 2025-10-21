// Neo4j initialization script for ComuniArte
// Creates nodes, relationships and constraints

// Create constraints and indexes
CREATE CONSTRAINT user_email_unique IF NOT EXISTS FOR (u:User) REQUIRE u.email IS UNIQUE;
CREATE CONSTRAINT content_id_unique IF NOT EXISTS FOR (c:Content) REQUIRE c.id IS UNIQUE;
CREATE CONSTRAINT category_name_unique IF NOT EXISTS FOR (cat:Category) REQUIRE cat.name IS UNIQUE;

// Create indexes for performance
CREATE INDEX user_name_index IF NOT EXISTS FOR (u:User) ON (u.name);
CREATE INDEX content_title_index IF NOT EXISTS FOR (c:Content) ON (c.title);
CREATE INDEX content_category_index IF NOT EXISTS FOR (c:Content) ON (c.category);

// Create sample users
CREATE (u1:User {
    id: 'maria@comuniarte.com',
    name: 'María González',
    email: 'maria@comuniarte.com',
    role: 'CREADOR',
    createdAt: datetime(),
    bio: 'Poeta urbana y activista cultural',
    location: 'Buenos Aires, Argentina',
    followersCount: 0,
    followingCount: 0
});

CREATE (u2:User {
    id: 'carlos@comuniarte.com',
    name: 'Carlos Ruiz',
    email: 'carlos@comuniarte.com',
    role: 'ESPECTADOR',
    createdAt: datetime(),
    bio: 'Amante de la cultura latinoamericana',
    location: 'Córdoba, Argentina',
    followersCount: 0,
    followingCount: 0
});

CREATE (u3:User {
    id: 'ana@comuniarte.com',
    name: 'Ana Martínez',
    email: 'ana@comuniarte.com',
    role: 'CREADOR',
    createdAt: datetime(),
    bio: 'Música folklórica y compositora',
    location: 'Salta, Argentina',
    followersCount: 0,
    followingCount: 0
});

// Create categories
CREATE (cat1:Category {
    name: 'Poesía',
    description: 'Poesía urbana y tradicional',
    createdAt: datetime()
});

CREATE (cat2:Category {
    name: 'Música',
    description: 'Música folklórica y contemporánea',
    createdAt: datetime()
});

CREATE (cat3:Category {
    name: 'Arte Visual',
    description: 'Pintura, escultura y arte digital',
    createdAt: datetime()
});

// Create sample content
CREATE (c1:Content {
    id: 'content-001',
    title: 'Poesía Urbana - Barrio Sur',
    description: 'Recital de poesía en el corazón del barrio',
    category: 'Poesía',
    mediaType: 'video',
    createdAt: datetime(),
    views: 0,
    likes: 0,
    comments: 0
});

CREATE (c2:Content {
    id: 'content-002',
    title: 'Música Folklórica Andina',
    description: 'Interpretación de música tradicional',
    category: 'Música',
    mediaType: 'audio',
    createdAt: datetime(),
    views: 0,
    likes: 0,
    comments: 0
});

CREATE (c3:Content {
    id: 'content-003',
    title: 'Arte Callejero Digital',
    description: 'Fusión de arte tradicional y digital',
    category: 'Arte Visual',
    mediaType: 'video',
    createdAt: datetime(),
    views: 0,
    likes: 0,
    comments: 0
});

// Create relationships
// Users follow each other
MATCH (u1:User {email: 'maria@comuniarte.com'}), (u2:User {email: 'carlos@comuniarte.com'})
CREATE (u2)-[:FOLLOWS {createdAt: datetime()}]->(u1);

MATCH (u1:User {email: 'carlos@comuniarte.com'}), (u3:User {email: 'ana@comuniarte.com'})
CREATE (u1)-[:FOLLOWS {createdAt: datetime()}]->(u3);

MATCH (u1:User {email: 'maria@comuniarte.com'}), (u3:User {email: 'ana@comuniarte.com'})
CREATE (u1)-[:FOLLOWS {createdAt: datetime()}]->(u3);

// Users create content
MATCH (u:User {email: 'maria@comuniarte.com'}), (c:Content {id: 'content-001'})
CREATE (u)-[:CREATED {createdAt: datetime()}]->(c);

MATCH (u:User {email: 'maria@comuniarte.com'}), (c:Content {id: 'content-002'})
CREATE (u)-[:CREATED {createdAt: datetime()}]->(c);

MATCH (u:User {email: 'ana@comuniarte.com'}), (c:Content {id: 'content-003'})
CREATE (u)-[:CREATED {createdAt: datetime()}]->(c);

// Content belongs to categories
MATCH (c:Content {id: 'content-001'}), (cat:Category {name: 'Poesía'})
CREATE (c)-[:BELONGS_TO]->(cat);

MATCH (c:Content {id: 'content-002'}), (cat:Category {name: 'Música'})
CREATE (c)-[:BELONGS_TO]->(cat);

MATCH (c:Content {id: 'content-003'}), (cat:Category {name: 'Arte Visual'})
CREATE (c)-[:BELONGS_TO]->(cat);

// Users interact with content
MATCH (u:User {email: 'carlos@comuniarte.com'}), (c:Content {id: 'content-001'})
CREATE (u)-[:VIEWED {timestamp: datetime(), duration: 120}]->(c);

MATCH (u:User {email: 'carlos@comuniarte.com'}), (c:Content {id: 'content-002'})
CREATE (u)-[:LIKED {timestamp: datetime()}]->(c);

MATCH (u:User {email: 'maria@comuniarte.com'}), (c:Content {id: 'content-003'})
CREATE (u)-[:VIEWED {timestamp: datetime(), duration: 90}]->(c);

// Update follower counts
MATCH (u:User)
SET u.followersCount = size((u)<-[:FOLLOWS]-())
SET u.followingCount = size((u)-[:FOLLOWS]->());

// Update content interaction counts
MATCH (c:Content)
SET c.views = size((c)<-[:VIEWED]-())
SET c.likes = size((c)<-[:LIKED]-())
SET c.comments = size((c)<-[:COMMENTED]-());

// Create sample recommendations based on network
// Users who follow similar creators should see their content
MATCH (u1:User)-[:FOLLOWS]->(creator:User)-[:CREATED]->(content:Content)
WHERE NOT (u1)-[:VIEWED]->(content)
RETURN u1.name, content.title, creator.name
ORDER BY u1.name, content.title;

// Show the graph structure
MATCH (n)
RETURN n
LIMIT 50;
