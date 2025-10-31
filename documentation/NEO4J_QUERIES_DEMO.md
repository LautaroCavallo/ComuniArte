# 🔵 Queries de Neo4j para Demostración

## 🎯 Queries Básicas para Mostrar en Presentación

### 1. Ver Todos los Nodos
```cypher
MATCH (n) 
RETURN n 
LIMIT 100
```

**Uso:** Mostrar la estructura general del grafo

---

### 2. Ver Solo Usuarios
```cypher
MATCH (u:Usuario) 
RETURN u.nombre as Nombre, u.email as Email, u.region as Región
LIMIT 20
```

**Uso:** Listar usuarios registrados

---

### 3. Ver Red de Seguidores
```cypher
MATCH (follower:Usuario)-[s:SIGUE]->(creator:Usuario)
RETURN follower.nombre as Seguidor, 
       creator.nombre as Creador,
       s.fechaSeguimiento as Fecha
LIMIT 50
```

**Uso:** Visualizar relaciones de seguimiento

---

### 4. Ver Contenidos con Likes
```cypher
MATCH (u:Usuario)-[g:GUSTA]->(c:Contenido)
RETURN u.nombre as Usuario, 
       c.titulo as Contenido,
       g.timestamp as Fecha
ORDER BY g.timestamp DESC
LIMIT 20
```

**Uso:** Mostrar qué contenidos gustan a los usuarios

---

### 5. Visualización de Red Social
```cypher
MATCH (u:Usuario)-[s:SIGUE]->(c:Usuario)
RETURN u, s, c
LIMIT 100
```

**Uso:** Grafo visual de la red social completa

---

## 🚀 Queries Avanzadas para Impresionar

### 6. Top Creadores por Seguidores
```cypher
MATCH (follower:Usuario)-[:SIGUE]->(creator:Usuario)
WITH creator, COUNT(follower) as totalSeguidores
WHERE totalSeguidores > 0
RETURN creator.nombre as Creador,
       creator.email as Email,
       totalSeguidores as Seguidores
ORDER BY totalSeguidores DESC
LIMIT 10
```

**Uso:** Ranking de popularidad de creadores

---

### 7. Contenidos Más Populares
```cypher
MATCH (u:Usuario)-[:GUSTA]->(c:Contenido)
WITH c, COUNT(u) as totalLikes
WHERE totalLikes > 0
RETURN c.titulo as Contenido,
       c.categoria as Categoría,
       totalLikes as Likes
ORDER BY totalLikes DESC
LIMIT 10
```

**Uso:** Ranking de contenidos con más likes

---

### 8. Recomendaciones: "A quién seguir"
```cypher
// Usuarios que siguen a gente que yo sigo, pero que yo no sigo
MATCH (yo:Usuario {userId: "USER_ID_AQUI"})
MATCH (yo)-[:SIGUE]->(mutuo:Usuario)
MATCH (mutuo)-[:SIGUE]->(recomendado:Usuario)
WHERE NOT (yo)-[:SIGUE]->(recomendado)
  AND recomendado <> yo
WITH DISTINCT recomendado, COUNT(mutuo) as conexiones
RETURN recomendado.nombre as Recomendado,
       recomendado.region as Región,
       conexiones as ConexionesComunes
ORDER BY conexiones DESC
LIMIT 5
```

**Uso:** Algoritmo de recomendación básico

---

### 9. Contenido Recomendado por Gustos Similares
```cypher
// Contenidos que les gustaron a usuarios con gustos similares
MATCH (yo:Usuario {userId: "USER_ID_AQUI"})
MATCH (yo)-[:GUSTA]->(contenidoQueGusta:Contenido)
MATCH (similares:Usuario)-[:GUSTA]->(contenidoQueGusta)
MATCH (similares)-[:GUSTA]->(recomendacion:Contenido)
WHERE NOT (yo)-[:GUSTA]->(recomendacion)
WITH DISTINCT recomendacion, COUNT(similares) as coincidencias
RETURN recomendacion.titulo as Contenido,
       recomendacion.categoria as Categoría,
       coincidencias as UsuariosSimilares
ORDER BY coincidencias DESC
LIMIT 10
```

**Uso:** Sistema de recomendación de contenidos

---

### 10. Análisis de Influencia (PageRank simulado)
```cypher
MATCH (creator:Usuario)
OPTIONAL MATCH (follower:Usuario)-[:SIGUE]->(creator)
WITH creator, COUNT(follower) as seguidores
OPTIONAL MATCH (creator)-[:SIGUE]->(following:Usuario)
WITH creator, seguidores, COUNT(following) as siguiendo
RETURN creator.nombre as Creador,
       seguidores as Seguidores,
       siguiendo as Siguiendo,
       toFloat(seguidores) / (siguiendo + 1) as InfluenciaRatio
ORDER BY InfluenciaRatio DESC
LIMIT 10
```

**Uso:** Identificar usuarios más influyentes

---

### 11. Comunidades: Clusters de Usuarios
```cypher
MATCH (u1:Usuario)-[:SIGUE]->(u2:Usuario)-[:SIGUE]->(u3:Usuario)
WHERE (u3)-[:SIGUE]->(u1)
  AND u1 <> u2 
  AND u2 <> u3 
  AND u1 <> u3
RETURN u1.nombre as Usuario1, 
       u2.nombre as Usuario2, 
       u3.nombre as Usuario3
LIMIT 20
```

**Uso:** Detectar triángulos (comunidades cerradas)

---

### 12. Camino Más Corto entre Usuarios
```cypher
MATCH p = shortestPath(
  (u1:Usuario {userId: "USER_ID_1"})-[:SIGUE*]-(u2:Usuario {userId: "USER_ID_2"})
)
RETURN p
```

**Uso:** "Grados de separación" entre usuarios

---

### 13. Contenidos Vistos pero No Gustados
```cypher
MATCH (u:Usuario {userId: "USER_ID_AQUI"})
MATCH (u)-[:VIO]->(c:Contenido)
WHERE NOT (u)-[:GUSTA]->(c)
RETURN c.titulo as Contenido,
       c.categoria as Categoría
LIMIT 10
```

**Uso:** Contenidos que vio pero no le gustaron

---

### 14. Estadísticas Generales del Grafo
```cypher
CALL {
  MATCH (u:Usuario) RETURN COUNT(u) as usuarios
  UNION
  MATCH (c:Contenido) RETURN COUNT(c) as contenidos
  UNION
  MATCH ()-[s:SIGUE]->() RETURN COUNT(s) as relaciones_sigue
  UNION
  MATCH ()-[g:GUSTA]->() RETURN COUNT(g) as relaciones_gusta
  UNION
  MATCH ()-[v:VIO]->() RETURN COUNT(v) as relaciones_vio
}
RETURN *
```

**Uso:** Vista general de las métricas del grafo

---

### 15. Limpiar Base de Datos (¡CUIDADO!)
```cypher
// SOLO PARA DEMOS - ELIMINA TODO
MATCH (n)
DETACH DELETE n
```

**⚠️ Uso:** Resetear Neo4j para empezar demo desde cero

---

## 🎨 Queries de Visualización

### 16. Visualizar Red Social Completa (Limitada)
```cypher
MATCH (u:Usuario)
OPTIONAL MATCH (u)-[r]->(n)
RETURN u, r, n
LIMIT 200
```

**Uso:** Ver el grafo completo de forma visual

---

### 17. Subgrafo de un Usuario Específico
```cypher
MATCH (centro:Usuario {userId: "USER_ID_AQUI"})
OPTIONAL MATCH (centro)-[r1]-(vecino1)
OPTIONAL MATCH (vecino1)-[r2]-(vecino2)
WHERE vecino2 <> centro
RETURN centro, r1, vecino1, r2, vecino2
LIMIT 100
```

**Uso:** Ver el "ecosistema" de un usuario

---

## 🔍 Queries de Debugging

### 18. Verificar Nodos Huérfanos
```cypher
MATCH (n)
WHERE NOT (n)--()
RETURN n
```

**Uso:** Encontrar nodos sin relaciones

---

### 19. Verificar Duplicados de Usuarios
```cypher
MATCH (u:Usuario)
WITH u.userId as userId, COUNT(u) as count
WHERE count > 1
RETURN userId, count
```

**Uso:** Detectar IDs duplicados

---

### 20. Ver Propiedades de Relaciones
```cypher
MATCH ()-[r]->()
RETURN type(r) as TipoRelacion, 
       COUNT(r) as Cantidad,
       keys(r) as Propiedades
```

**Uso:** Auditar qué tipos de relaciones existen

---

## 💡 Tips para la Presentación

### Antes de Mostrar Neo4j:
1. Ejecutar query de estadísticas (#14) para tener contexto
2. Tener abierto el grafo visual (#16) como inicio
3. Preparar IDs de usuarios reales en variables

### Durante la Demo:
1. **Empezar visual** → Query #16 (impresiona)
2. **Mostrar datos** → Query #3 o #4 (entendible)
3. **Feature avanzado** → Query #8 o #9 (recomendaciones)
4. **Cerrar con estadísticas** → Query #14 (resumen)

### Si Preguntan:
- **"¿Para qué Neo4j?"** → Mostrar Query #8 (recomendaciones en 1 query)
- **"¿Qué tan rápido es?"** → Ejecutar query compleja y mostrar tiempo
- **"¿Cómo escala?"** → Mencionar Neo4j Clustering + índices

---

## 🎯 Queries Por Sección de Presentación

| Sección | Query | Objetivo |
|---------|-------|----------|
| Intro Neo4j | #1, #2 | Mostrar estructura |
| Red Social | #3, #5, #6 | Visualizar relaciones |
| Likes | #4, #7 | Demostrar GUSTA |
| Recomendaciones | #8, #9 | Feature killer |
| Analytics | #10, #14 | Métricas avanzadas |

---

## 📝 Variables a Reemplazar

En las queries que usan `USER_ID_AQUI`:
1. Obtener desde MongoDB: `db.usuarios.findOne({}, {_id: 1})`
2. O desde Postman después de registrar
3. Reemplazar en Neo4j antes de ejecutar

**Ejemplo:**
```cypher
// Antes
MATCH (u:Usuario {userId: "USER_ID_AQUI"})

// Después (con ID real)
MATCH (u:Usuario {userId: "675eb1e8d2e16c4a2e8f9a12"})
```

---

**¡Estas queries te darán una demo impresionante de Neo4j!** 🔵🚀

