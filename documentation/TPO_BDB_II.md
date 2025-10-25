Plataforma de Streaming Cultural y Comunitario
Contexto General 
ComuniArte es una organización sin fines de lucro que trabaja con artistas independientes, comunidades rurales y colectivos culturales de América Latina. Su misión es crear una plataforma digital donde estos actores puedan compartir contenido en video, audio y texto, conectarse con el público y generar nuevas formas de participación e interacción comunitaria. 
Después de dos años de transmisiones por redes sociales, ComuniArte recibió un subsidio internacional para desarrollar su propia plataforma de streaming, con herramientas que fomenten la creación, descubrimiento, colaboración y análisis de impacto social. 
Tu equipo tiene la responsabilidad de diseñar e implementar la arquitectura de datos de esta nueva plataforma, asegurando su escalabilidad, resiliencia, flexibilidad estructural y enfoque social. Están colaborando con ComuniArte, una red de festivales, radios comunitarias, músicos barriales, poetas urbanos y activistas digitales. 
El objetivo no es solo transmitir contenido, sino crear un ecosistema cultural digital donde los vínculos entre personas y contenidos cobren sentido. 
Para eso, necesitan una plataforma que permita:
●	Subir contenidos audiovisuales con metadatos enriquecidos. 
●	Crear perfiles de usuario (espectadores y creadores), y seguir sus trayectorias. • 
●	Relacionar creadores entre sí y con su audiencia mediante grafos. 
●	Gestionar comentarios, donaciones, transmisiones en vivo y notificaciones. 
●	Obtener estadísticas de uso, impacto y preferencias culturales por región.
●	Personalizar la experiencia del usuario según su red, intereses e historial. 
Como grupo, deberán elegir cómo almacenar y consultar cada tipo de información usando las tecnologías trabajadas. La solución tiene que soportar tanto la dinámica de streaming como la persistencia de largo plazo, con posibilidad de visualización analítica y recomendaciones automatizadas. 
Consejos al grupo:  El foco está en cómo relacionar personas, arte, impacto e interacción. Pensalo como una red creativa con datos vivos. 
Requerimientos:
1.	Gestión de Contenidos: o Videos, audios, textos, transmisiones en vivo, organizados por categorías y etiquetas. o Comentarios, likes y visualizaciones.
2.	Perfil de Usuarios: o Historial de reproducción, intereses, listas personalizadas. o Segmentación de usuarios por consumo.
3.	Red de Creadores y Seguidores: o Seguimiento de creadores, recomendaciones de contenido por cercanía de red. o Influencia y propagación de contenido.
4.	Eventos en Tiempo Real: o Comentarios en vivo, donaciones, preguntas al presentador. o Redis Streams o pub/sub.
5.	Análisis de Métricas y Ranking de Popularidad: o Vistas, duración media, interacción. o Persistencia de análisis y perfiles para reporting.
6.	Infraestructura Distribuida: o Perfiles y contenidos. o Red social. o Estado de usuarios en vivo y sesiones. o Almacenar configuraciones y métricas históricas.
Desarrollar una propuesta integral que conecte los modelos de BD elegidos y presentar la propuesta a ComuniArte































🌐 Listado de Requests del Backend
________________________________________
1. Gestión de Contenidos
(Videos, audios, textos, transmisiones en vivo, categorías, etiquetas, comentarios y reacciones)
Endpoint	Método	Descripción
/api/content/upload	POST	Sube un nuevo contenido (video/audio/texto) junto con sus metadatos (título, autor, idioma, región, etiquetas, etc.).
/api/content/{id}	GET	Obtiene la información completa de un contenido, incluyendo estadísticas básicas.
/api/content/{id}	PUT	Actualiza los metadatos o estado de un contenido.
/api/content/{id}	DELETE	Elimina un contenido (solo permitido a creadores o admins).
/api/content/list	GET	Lista de contenidos con filtros por categoría, etiqueta, creador o tipo.
/api/content/{id}/comments	GET	Devuelve los comentarios asociados a un contenido.
/api/content/{id}/comments	POST	Agrega un comentario a un contenido.
/api/content/{id}/like	POST	Registra un “me gusta” o reacción en un contenido.
/api/live/start	POST	Inicia una transmisión en vivo.
/api/live/{id}/end	POST	Finaliza la transmisión y almacena la grabación.
________________________________________
2. Perfil de Usuarios
(Perfiles, historial, intereses, listas personalizadas, segmentación)
Endpoint	Método	Descripción
/api/users/register	POST	Crea un nuevo usuario (creador o espectador).
/api/users/login	POST	Autenticación y generación de token JWT.
/api/users/{id}	GET	Obtiene el perfil completo del usuario.
/api/users/{id}	PUT	Actualiza información del perfil (bio, imagen, intereses, región).
/api/users/{id}/history	GET	Muestra el historial de reproducción o interacción del usuario.
/api/users/{id}/lists	GET	Devuelve las listas personalizadas del usuario.
/api/users/{id}/lists	POST	Crea una nueva lista personalizada.
/api/users/{id}/lists/{listId}	PUT	Agrega o elimina contenido de una lista.
/api/users/segment	GET	Permite segmentar usuarios según consumo, región o intereses (para análisis).
________________________________________
3. Red de Creadores y Seguidores
(Relaciones entre usuarios, grafos de influencia, recomendaciones)
Endpoint	Método	Descripción
/api/network/follow	POST	Un usuario comienza a seguir a un creador.
/api/network/unfollow	POST	Deja de seguir a un creador.
/api/network/followers/{id}	GET	Lista de seguidores de un usuario.
/api/network/following/{id}	GET	Lista de creadores seguidos por un usuario.
/api/network/recommendations/{id}	GET	Genera recomendaciones de creadores o contenidos según cercanía de red.
/api/network/graph	GET	Devuelve un grafo simplificado de relaciones para análisis social.
________________________________________
4. Eventos en Tiempo Real
(Interacciones en vivo, donaciones, chat, preguntas)
Endpoint	Método	Descripción
/api/live/{id}/chat	WS / GET	Canal WebSocket o Pub/Sub para chat en vivo.
/api/live/{id}/donate	POST	Registra una donación a un creador durante la transmisión.
/api/live/{id}/questions	POST	Envía una pregunta al presentador.
/api/live/{id}/events	STREAM	Canal de eventos en tiempo real (comentarios, reacciones, nuevos espectadores).
________________________________________
5. Análisis de Métricas y Ranking
(Datos estadísticos, popularidad, impacto cultural)
Endpoint	Método	Descripción
/api/analytics/views	POST	Registra una nueva vista o interacción.
/api/analytics/content/{id}	GET	Devuelve métricas detalladas de un contenido (vistas, duración media, engagement).
/api/analytics/creator/{id}	GET	Devuelve métricas del creador (seguidores, interacciones, impacto regional).
/api/analytics/ranking	GET	Genera rankings de popularidad por categoría, región o tipo de contenido.
/api/analytics/impact	GET	Mide el impacto social y cultural por región (usando datos agregados).
/api/reports/custom	POST	Genera reportes personalizados bajo demanda (extensible a futuro).
________________________________________
6. Infraestructura y Configuración
(Estado del sistema, gestión de sesiones, almacenamiento distribuido)
Endpoint	Método	Descripción
/api/system/status	GET	Verifica el estado general de los servicios.
/api/system/config	GET	Devuelve configuraciones de streaming, caché o límites del sistema.
/api/system/sessions	GET	Lista las sesiones de usuarios activos.
/api/system/logs	GET	Consulta de logs del sistema para monitoreo.
/api/system/metrics	GET	Métricas técnicas (uso de CPU, almacenamiento, red).


























1. MongoDB (Base Principal) 💾
MongoDB es ideal para la persistencia flexible de documentos (usuarios, contenidos, transacciones, datos analíticos enriquecidos).
Entidad	Campos Clave (Ejemplos)	Descripción
Usuario	_id, nombre, email, tipo_usuario (espectador/creador), region, intereses, perfil_redes, historial_reproduccion (referencias a Contenido), listas_personalizadas	Perfiles de espectadores y creadores.
Artista/Creador	_id (vinculado a Usuario si es creador), trayectoria, bio, enfoque_social, colectivo_id, contenido_creado (referencias a Contenido)	Perfiles extendidos para los creadores de la plataforma.
Contenido	_id, titulo, tipo (video, audio, texto, live), url_archivo, creador_id (referencia a Artista), metadatos_enriquecidos (locación, tema social, etc.), categoria, etiquetas, fecha_publicacion	Datos principales de los medios compartidos.
Transaccion	_id, usuario_id (donante), creador_id (receptor), tipo (donación), monto, fecha, metodo_pago	Gestión de donaciones y flujos económicos.
Comentario	_id, contenido_id, usuario_id, texto, fecha, es_live (booleano)	Comentarios persistentes en contenidos y aquellos de transmisiones en vivo que se desean guardar.
AnalisisHistorico	_id, region, fecha_analisis, metricas_resumidas (ej. vistas totales, interacciones medias), preferencias_culturales_detectadas	Resultados de análisis y reportes para persistencia a largo plazo.
________________________________________
2. Neo4j (Relaciones y Grafos) 🌐
Neo4j se utiliza para modelar las relaciones complejas entre usuarios y contenidos, esenciales para las recomendaciones y la red social.
Tipo de Nodo	Propiedades Clave (Ejemplos)	Descripción
(:Usuario)	user_id (Mismo _id de MongoDB)	Representa a cualquier usuario (espectador o creador).
(:Contenido)	contenido_id (Mismo _id de MongoDB), tipo, categoria	Representa la obra o publicación.
(:Colectivo)	nombre, enfoque	Representa agrupaciones de artistas/creadores.

Tipo de Relación	Nodos Conectados	Propiedades Clave (Ejemplos)	Uso Principal
[:SIGUE]	(:Usuario) - [:SIGUE] -> (:Usuario)	fecha_inicio	Red de seguidores (Creadores y Espectadores).
[:CREO]	(:Usuario) - [:CREO] -> (:Contenido)	fecha_creacion	Vincula al creador con su obra.
[:VIO]	(:Usuario) - [:VIO] -> (:Contenido)	duracion_ms, fecha_ultima_vista	Historial de consumo y base para recomendaciones.
[:GUSTA]	(:Usuario) - [:GUSTA] -> (:Contenido)	fecha	Relación para modelar likes (preferencias).
[:COLABORA_EN]	(:Usuario) - [:COLABORA_EN] -> (:Contenido)	rol	Relaciones de colaboración entre creadores.
[:ES_MIEMBRO_DE]	(:Usuario) - [:ES_MIEMBRO_DE] -> (:Colectivo)	fecha_ingreso	Agrupación de creadores.
________________________________________

3. Redis (Cache y Tiempo Real) ⏱️
Redis se enfoca en la velocidad, el estado en tiempo real y el almacenamiento temporal de métricas.
Estructura de Datos	Clave (Patrón)	Datos Almacenados (Ejemplos)	Uso Principal
String/JSON	cache:contenido:{id}	Documento de Contenido de MongoDB	Cacheo de consultas pesadas (metadatos de contenido, perfiles de creador).
Hash/Sorted Set	ranking:vistas:global	{contenido_id}:{vistas}	Contadores en tiempo real (vistas, likes, duración media). Sorted Set para rankings.
Sorted Set	user:feed:{user_id}	{contenido_id}:{timestamp}	Feed personalizado y caché de recomendaciones generadas por Neo4j.
Stream/Pub/Sub	live:comentarios:{live_id}	Mensajes {user_id}, {texto}, {timestamp}	Eventos en tiempo real (comentarios en vivo, donaciones).
Hash	user:session:{token}	{user_id}, {estado_online}, {last_seen}	Estado de usuarios en vivo y sesiones (Infraestructura Distribuida).
Set	live:viewers:{live_id}	{user_id}	Contar espectadores activos en una transmisión en vivo.

La justificación de la estructura de datos distribuida se basa en la necesidad de escalabilidad, rendimiento y flexibilidad para soportar tanto la dinámica de streaming y eventos en tiempo real, como el análisis de impacto social y las recomendaciones personalizadas.
________________________________________














Justificación de la Estructura de Datos 🚀
1. MongoDB (Persistencia Central y Flexibilidad)
Entidad Asignada	Justificación	Beneficio Clave
Usuario, Artista/Creador, Contenido, Transaccion, Comentario, AnalisisHistorico	MongoDB es una base de datos orientada a documentos (NoSQL), ideal para datos que no tienen un esquema fijo (como los metadatos enriquecidos de contenido) o que se benefician de la desnormalización.	Flexibilidad Estructural: Permite agregar nuevos campos (ej. un nuevo tipo de metadato o métrica) sin alterar el esquema global. Escalabilidad Horizontal: Maneja grandes volúmenes de datos de usuarios y contenidos de forma distribuida.
Historial de Reproducción	Se almacena como un array o colección referenciada dentro del documento Usuario o en una colección separada.	Lectura Rápida: Almacenar referencias al historial junto al usuario facilita la personalización de la experiencia.
________________________________________
2. Neo4j (Relaciones, Recomendaciones y Red Social)
Entidad Asignada	Justificación	Beneficio Clave
Relaciones [:SIGUE], [:VIO], [:COLABORA_EN], [:GUSTA]	El modelo de grafo es el más eficiente para almacenar y consultar vínculos (conexiones entre personas y contenidos) a través de la red.	Recomendaciones Inteligentes: Permite consultas como "Recomendar contenido que tus seguidores han visto" o "Artistas que colaboraron con el artista que te gusta" de manera ultrarrápida (modelado de influencia y propagación).
Red de Creadores y Seguidores	El concepto de seguir a creadores y ver la influencia se traduce directamente en caminos y vecinos dentro del grafo.	Análisis de Impacto Social: Facilita medir la influencia de un artista o colectivo en la red y la propagación de un contenido específico.
________________________________________
3. Redis (Velocidad y Eventos en Tiempo Real)
Entidad Asignada	Justificación	Beneficio Clave
Contadores (Vistas, Likes, Popularidad), Cache de Contenido	Redis es una estructura de datos en memoria, lo que la hace extremadamente rápida. Es perfecto para gestionar datos que cambian constantemente o que requieren un acceso de baja latencia.	Rendimiento Máximo: Almacena los datos más consultados (contenido, perfiles) en caché para reducir la carga en MongoDB. Contadores Precisos: Maneja el alto tráfico de vistas y likes sin impactar la base de datos principal (evitando cuellos de botella).
Comentarios en Vivo, Donaciones, Estado de Sesión	Utilizando estructuras como Streams o Pub/Sub, Redis maneja la mensajería en tiempo real y el estado de la aplicación.	Eventos Instantáneos: Soporta la alta concurrencia de transmisiones en vivo, garantizando que los comentarios y donaciones aparezcan al instante.
Ranking de Popularidad	Usando la estructura Sorted Set (Conjunto Ordenado), Redis mantiene automáticamente una lista de contenidos ordenada por un score (ej. vistas o duración media).	Actualizaciones Dinámicas: Permite mostrar rankings y tendencias que se actualizan continuamente con latencia mínima.

