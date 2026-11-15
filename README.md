# Gestor de Torneos

API REST para organizar torneos de videojuegos: registro de usuarios, creación de equipos, inscripción en torneos, generación automática del calendario de liga, registro de resultados y clasificación en tiempo real.

**Stack:** Java 21 · Spring Boot 4 · Spring Security + JWT · Spring Data JPA · MySQL 8 · Maven · JUnit

**Estado:** en desarrollo activo (ver [Hoja de ruta](#hoja-de-ruta))

---

## Funcionalidades

### Implementado
- ✅ **Usuarios y autenticación:** registro y login con **JWT**; contraseñas cifradas con **BCrypt**; API sin sesiones (*stateless*).
- ✅ **Equipos:** creación con nombre y *tag* (2–5 caracteres), búsqueda por id o por tag.
- ✅ **Torneos:** creación con formato a elegir (`LIGA_RAPIDA` al mejor de 1, `LIGA_CLASICA` al mejor de 3).
- ✅ **Inscripciones** de equipos en torneos.
- ✅ **Generación automática del calendario:** liga "todos contra todos" repartida en jornadas.
- ✅ **Resultados por series:** cada enfrentamiento es una serie al mejor de N partidas; solo el organizador del torneo puede registrar resultados.
- ✅ **Clasificación** calculada a partir de los resultados.
- ✅ **Gestión de errores centralizada:** respuestas claras para recurso no encontrado, conflicto, operación no permitida o credenciales inválidas.
- ✅ **Pruebas unitarias** de la lógica principal (generador de liga, clasificación, series e inscripciones).

### Hoja de ruta
- ⬜ Formato **suizo** (ya modelado, pendiente de implementar).
- ⬜ Fases **eliminatorias** (playoffs) tras la liga.
- ⬜ Estadísticas de jugador (KDA, DPM, K/P) con datos de la API de Riot.
- ⬜ Interfaz web.

---

## Cómo ejecutarlo

### Requisitos
- Java 21
- MySQL 8 en marcha

### 1. Crear la base de datos
```sql
CREATE DATABASE gestor_torneos;
```
Las tablas se crean solas al arrancar (Hibernate `ddl-auto=update`).

### 2. Configurar las variables de entorno
La contraseña de la base de datos y la clave para firmar los tokens **no están en el código**: se leen de variables de entorno.

| Variable | Qué es |
|---|---|
| `DB_PASSWORD` | Contraseña del usuario `root` de MySQL |
| `JWT_SECRET` | Clave secreta para firmar los tokens (una cadena larga y aleatoria) |

En PowerShell (Windows):
```powershell
$env:DB_PASSWORD="tu_contraseña"
$env:JWT_SECRET="una_clave_larga_y_aleatoria_de_al_menos_32_caracteres"
```

### 3. Arrancar la API
```bash
./mvnw spring-boot:run
```
La API queda disponible en `http://localhost:8080`.

### Ejecutar los tests
```bash
./mvnw test
```

---

## Endpoints

🔓 = público · 🔒 = requiere token (`Authorization: Bearer <token>`)

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/usuarios` | 🔓 | Registrar usuario |
| POST | `/api/auth/login` | 🔓 | Iniciar sesión y obtener el token |
| GET | `/api/usuarios` | 🔒 | Listar usuarios |
| POST | `/api/equipos` | 🔒 | Crear equipo |
| GET | `/api/equipos` | 🔓 | Listar equipos |
| GET | `/api/equipos/{id}` | 🔓 | Ver un equipo |
| GET | `/api/equipos/tag/{tag}` | 🔓 | Buscar equipo por tag |
| POST | `/api/torneos` | 🔒 | Crear torneo |
| GET | `/api/torneos` | 🔓 | Listar torneos |
| GET | `/api/torneos/{id}` | 🔓 | Ver un torneo |
| POST | `/api/inscripciones` | 🔒 | Inscribir un equipo en un torneo |
| GET | `/api/torneos/{id}/inscripciones` | 🔓 | Equipos inscritos en un torneo |
| POST | `/api/torneos/{id}/iniciar` | 🔒 | Iniciar el torneo y generar el calendario (solo el organizador) |
| POST | `/api/series/{id}/partidos` | 🔒 | Registrar el ganador de una partida (solo el organizador) |
| GET | `/api/torneos/{id}/clasificacion` | 🔓 | Clasificación del torneo |

---

## Ejemplo de uso

**1. Registrarse**
```http
POST /api/usuarios
Content-Type: application/json

{ "email": "maxi@ejemplo.com", "password": "contraseña123", "nombre": "Maxi" }
```

**2. Iniciar sesión** → la respuesta incluye el token JWT
```http
POST /api/auth/login
Content-Type: application/json

{ "email": "maxi@ejemplo.com", "password": "contraseña123" }
```

**3. Crear un torneo** (con el token en la cabecera)
```http
POST /api/torneos
Authorization: Bearer <token>
Content-Type: application/json

{ "nombre": "Liga de Otoño", "formato": "LIGA_CLASICA" }
```

**4. Crear equipos e inscribirlos**
```http
POST /api/equipos
{ "nombre": "Los Invocadores", "tag": "INV" }

POST /api/inscripciones
{ "torneoId": 1, "equipoId": 1 }
```

**5. Iniciar el torneo** → se genera el calendario de jornadas
```http
POST /api/torneos/1/iniciar
```

**6. Registrar resultados y consultar la clasificación**
```http
POST /api/series/1/partidos
{ "ganadorId": 1 }

GET /api/torneos/1/clasificacion
```

---

## Estructura del proyecto
```
src/main/java/com/maxi/gestortorneos/
├── controller/     # Endpoints REST
├── service/        # Lógica de negocio
├── repository/     # Acceso a datos (Spring Data JPA)
├── model/          # Entidades: Torneo, Equipo, Serie, Partido...
├── dto/            # Objetos de entrada y salida de la API
├── generador/      # Generación del calendario de liga
├── clasificacion/  # Cálculo de la clasificación
├── security/       # Filtro y servicio JWT
├── config/         # Configuración de Spring Security
└── exception/      # Errores personalizados y manejador global
```

---

## Autor
**Maximiliano González Segura** · Desarrollador backend Java y Python · [GitHub](https://github.com/Maaxii00)
