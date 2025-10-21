// MongoDB initialization script for ComuniArte
// Creates databases, collections and indexes

// Switch to the application database
db = db.getSiblingDB('comuniarte_db');

// Create collections with validation schemas
db.createCollection('usuarios', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['nombre', 'email', 'password', 'rol'],
      properties: {
        nombre: { bsonType: 'string' },
        email: { bsonType: 'string', pattern: '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$' },
        password: { bsonType: 'string', minLength: 6 },
        rol: { enum: ['ESPECTADOR', 'CREADOR', 'ADMIN'] },
        fecha_registro: { bsonType: 'date' }
      }
    }
  }
});

db.createCollection('contents', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['title', 'description', 'category', 'creatorId', 'mediaType'],
      properties: {
        title: { bsonType: 'string', minLength: 1 },
        description: { bsonType: 'string' },
        category: { bsonType: 'string' },
        creatorId: { bsonType: 'string' },
        mediaType: { enum: ['video', 'audio', 'text', 'live'] },
        mediaUrl: { bsonType: 'string' },
        tags: { bsonType: 'array', items: { bsonType: 'string' } },
        createdAt: { bsonType: 'date' },
        totalViews: { bsonType: 'long' }
      }
    }
  }
});

db.createCollection('interactions', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['userId', 'contentId', 'type'],
      properties: {
        userId: { bsonType: 'string' },
        contentId: { bsonType: 'string' },
        type: { enum: ['view', 'like', 'comment', 'share', 'donation'] },
        timestamp: { bsonType: 'date' },
        metadata: { bsonType: 'object' }
      }
    }
  }
});

db.createCollection('analytics', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['date', 'type'],
      properties: {
        date: { bsonType: 'date' },
        type: { enum: ['daily', 'weekly', 'monthly'] },
        contentId: { bsonType: 'string' },
        metrics: { bsonType: 'object' }
      }
    }
  }
});

// Create indexes for performance
db.usuarios.createIndex({ 'email': 1 }, { unique: true });
db.usuarios.createIndex({ 'rol': 1 });
db.usuarios.createIndex({ 'fecha_registro': 1 });

db.contents.createIndex({ 'creatorId': 1 });
db.contents.createIndex({ 'category': 1 });
db.contents.createIndex({ 'mediaType': 1 });
db.contents.createIndex({ 'tags': 1 });
db.contents.createIndex({ 'createdAt': -1 });
db.contents.createIndex({ 'totalViews': -1 });

db.interactions.createIndex({ 'userId': 1 });
db.interactions.createIndex({ 'contentId': 1 });
db.interactions.createIndex({ 'type': 1 });
db.interactions.createIndex({ 'timestamp': -1 });

db.analytics.createIndex({ 'date': -1 });
db.analytics.createIndex({ 'type': 1 });
db.analytics.createIndex({ 'contentId': 1 });

// Insert sample data for testing
db.usuarios.insertMany([
  {
    nombre: 'María González',
    email: 'maria@comuniarte.com',
    password: '$2a$10$encrypted_password_hash',
    rol: 'CREADOR',
    fecha_registro: new Date()
  },
  {
    nombre: 'Carlos Ruiz',
    email: 'carlos@comuniarte.com', 
    password: '$2a$10$encrypted_password_hash',
    rol: 'ESPECTADOR',
    fecha_registro: new Date()
  }
]);

db.contents.insertMany([
  {
    title: 'Poesía Urbana - Barrio Sur',
    description: 'Recital de poesía en el corazón del barrio',
    category: 'Poesía',
    creatorId: 'maria@comuniarte.com',
    mediaType: 'video',
    mediaUrl: 'https://minio.comuniarte.com/videos/poesia-urbana.mp4',
    tags: ['poesía', 'urbano', 'cultura'],
    createdAt: new Date(),
    totalViews: 0
  },
  {
    title: 'Música Folklórica Andina',
    description: 'Interpretación de música tradicional',
    category: 'Música',
    creatorId: 'maria@comuniarte.com',
    mediaType: 'audio',
    mediaUrl: 'https://minio.comuniarte.com/audio/folklor-andino.mp3',
    tags: ['folklore', 'andino', 'tradicional'],
    createdAt: new Date(),
    totalViews: 0
  }
]);

print('MongoDB initialization completed successfully!');
print('Collections created: usuarios, contents, interactions, analytics');
print('Indexes created for optimal performance');
print('Sample data inserted for testing');
