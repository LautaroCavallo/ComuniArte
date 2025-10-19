# 🎭 ComuniArte – TPO Ingeniería de Datos II

Plataforma de *Streaming Cultural y Comunitario, desarrollada como Trabajo Práctico Obligatorio para la materia **Ingeniería de Datos II (UADE)*.

El objetivo es diseñar una *arquitectura multimodelo* sin depender de SQL, que soporte:
- Subida de contenidos audiovisuales con metadatos.
- Perfiles de usuarios (creadores y espectadores).
- Red social de creadores y seguidores.
- Interacciones en vivo (chat, donaciones, viewers).
- Métricas de popularidad y recomendaciones.

---

## 🚀 Stack Tecnológico

- *MongoDB* → Perfiles de usuarios, metadatos de contenidos, interacciones.  
- *Neo4j* → Grafo de creadores/seguidores y recomendaciones por red.  
- *Redis* → Chat en vivo, contadores de viewers, eventos en tiempo real.  
- *MinIO (S3 compatible)* → Almacenamiento de videos y audios.  
- *Node.js* → Backend que conecta todos los motores.  
- *Docker Compose* → Orquestación del entorno.  

---

## 📅 Roadmap e Hitos Semanales

### Semana 1 – Stack levantado ✅
- Levantar docker-compose.yml con MongoDB, Neo4j, Redis y MinIO.
- Verificar conexión y funcionamiento básico de cada motor.

### Semana 2 – Documentación mínima 📄
- Diagrama de arquitectura.  
- Justificación corta de cada motor (1–2 frases).  

### Semana 3 – CRUDs iniciales 🛠
- Endpoints en Node para users y contents.  
- Subida de archivos a MinIO + metadatos en Mongo.  
- Relaciones básicas en Neo4j (FOLLOWS, UPLOADED).  
- Query de recomendaciones simple.

### Semana 4 – Integración + Demo 🎥
- Chat y contadores en Redis.  
- Ranking diario con snapshot en Mongo.  
- Recomendaciones mejoradas con Neo4j.  
- Slides + demo grabada para la entrega.  

---

## 👥 Equipo

-Lautaro
-Nicolas
-Laura

---

## 📂 Estructura del Repo 

/tpo-comuniarte
├── backend/ # Node.js con endpoints
│ ├── src/
│ ├── package.json
│ └── README.md
├── docker-compose.yml # stack completo (Mongo, Neo4j, Redis, MinIO)
├── docs/ # diagramas, slides, informe
├── .gitignore
└── README.md # este archivo

---
## 📌 Notas

- Proyecto académico, no productivo.  
- Se prioriza la *simplicidad* y la *demostración funcional* sobre la completitud.  
- No se usan motores SQL, solo NoSQL y grafos.

---