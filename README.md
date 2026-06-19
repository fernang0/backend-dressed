# Dressed — Backend

> API REST del sistema de moda inteligente Dressed. Gestiona autenticación, perfiles de usuario, catálogo de prendas, outfits generados por IA y administración del sistema.

---

## Tabla de contenidos

- [Descripción general](#descripción-general)
- [Equipo](#equipo)
- [Arquitectura del sistema](#arquitectura-del-sistema)
- [Stack tecnológico](#stack-tecnológico)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Módulos y endpoints](#módulos-y-endpoints)
- [Instalación y desarrollo local](#instalación-y-desarrollo-local)
- [Variables de entorno](#variables-de-entorno)
- [Testing](#testing)
- [Seguridad](#seguridad)

---

## Descripción general

**Dressed** es una plataforma web full-stack orientada a la moda personalizada. El backend (este repositorio) es una API REST construida con **Spring Boot 3 + Java 21 + Gradle**. Expone los servicios de autenticación, perfil de usuario, catálogo de prendas, outfits generados por IA y administración del sistema. Es consumido exclusivamente a través del **BFF (Backend for Frontend)** — nunca de forma directa desde el frontend. El sistema está preparado para despliegue en cloud (Railway, Render, AWS) como escalabilidad futura.

---

## Equipo

| Integrante | Rol |
|---|---|
| **Fernando Henríquez** | Líder Técnico · IA · Scraping |
| **Joaquín Beltrán** | Frontend · QA / Testing |
| **Lucas Soto** | Backend · DevOps |

---

## Arquitectura del sistema

```
┌──────────────────────────────────────────────────────────────────┐
│                        Navegador Web                              │
│                  Chrome · Firefox · Edge                          │
└─────────────────────────┬────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                         Frontend                                  │
│              React 19 + TailwindCSS — Vercel (SaaS)              │
│                    repo: front-deressed                           │
└─────────────────────────┬────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                    BFF (Spring Boot / Java)                       │
│               JWT · Cookies HttpOnly · OAuth                      │
│                      repo: dressed-bff                            │
└─────────────────────────┬────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│               ★  Backend (este repositorio)  ★                   │
│           Spring Boot 3 · Java 21 · Gradle · Puerto 8081         │
│                                                                   │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐  ┌──────────────┐   │
│  │   auth   │  │ profile  │  │  catalog  │  │    admin     │   │
│  │Login·JWT │  │Tallas·   │  │Prendas H&M│  │Usuarios·     │   │
│  │  OAuth   │  │ estilos  │  │           │  │  métricas    │   │
│  └──────────┘  └──────────┘  └───────────┘  └──────────────┘   │
│                                                                   │
│  ┌──────────┐  ┌──────────────────────────────────────────────┐ │
│  │  outfit  │  │                  shared                       │ │
│  │Outfits IA│  │  ContactController · GlobalExceptionHandler   │ │
│  │          │  │  SwaggerConfig (config/) · EmailService       │ │
│  └──────────┘  └──────────────────────────────────────────────┘ │
└─────────────────────────┬────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                      Base de Datos                                │
│               PostgreSQL — NeonDB (PaaS)                          │
└──────────────────────────────────────────────────────────────────┘

          Servicios externos
          ┌─────────────────┐
          │  OAuth Google   │  Autenticación con Google
          │  Gmail SMTP     │  Emails de recuperación de contraseña
          │  Anthropic API  │  Claude Haiku Vision (via Baltasar)
          │  H&M Chile      │  cl.hm.com (via Spideron)
          └─────────────────┘
```

---

## Stack tecnológico

| Categoría | Tecnología | Versión |
|---|---|---|
| Framework | Spring Boot | 3.5.x |
| Lenguaje | Java | 21 |
| Build tool | Gradle | Wrapper incluido |
| Base de datos | PostgreSQL (NeonDB) | — |
| ORM | Spring Data JPA / Hibernate | — |
| Seguridad | Spring Security + JWT | — |
| Email | Spring Mail (Gmail SMTP) | — |
| OAuth | Google Identity (token verification) | — |
| Documentación API | SpringDoc OpenAPI (Swagger UI) | 2.8.x |
| Reducción boilerplate | Lombok | — |
| Testing | JUnit 5 + Mockito + Spring Security Test | — |

---

## Estructura del proyecto

```
backend-dressed/
├── src/
│   ├── main/
│   │   ├── java/cl/dressed/backend/
│   │   │   ├── BackendApplication.java
│   │   │   ├── module/
│   │   │   │   ├── auth/               # Autenticación y seguridad
│   │   │   │   │   ├── config/         # AuthConfig
│   │   │   │   │   ├── controller/     # AuthController
│   │   │   │   │   ├── dto/            # AuthDto, ForgotPasswordRequest,
│   │   │   │   │   │                   # GoogleAuthDto, ResetPasswordRequest
│   │   │   │   │   ├── entity/         # User, Role, PasswordRecovery
│   │   │   │   │   ├── exception/      # AuthException
│   │   │   │   │   ├── repository/     # UserRepository, PasswordRecoveryRepository
│   │   │   │   │   ├── security/       # JwtFilter, JwtService
│   │   │   │   │   └── service/        # AuthService, GoogleAuthService,
│   │   │   │   │                       # GoogleTokenVerifier
│   │   │   │   ├── catalog/            # Catálogo de prendas
│   │   │   │   │   ├── controller/     # CatalogController
│   │   │   │   │   ├── dto/            # GarmentRequestDTO, GarmentResponseDTO
│   │   │   │   │   ├── entity/         # Garment
│   │   │   │   │   ├── repository/     # GarmentRepository
│   │   │   │   │   ├── security/       # ScraperApiKeyService
│   │   │   │   │   └── service/        # GarmentService
│   │   │   │   ├── outfit/             # Outfits generados por IA
│   │   │   │   │   ├── controller/     # OutfitController
│   │   │   │   │   ├── dto/            # OutfitResponseDTO, OutfitGarmentItemDTO
│   │   │   │   │   ├── entity/         # Outfit, OutfitGarment
│   │   │   │   │   ├── repository/     # OutfitRepository, OutfitGarmentRepository
│   │   │   │   │   └── service/        # OutfitGeneratorService
│   │   │   │   ├── profile/            # Perfil de usuario
│   │   │   │   │   ├── controller/     # ProfileController, UserSizeController,
│   │   │   │   │   │                   # UserMeasurementController
│   │   │   │   │   ├── dto/            # ProfileDto, ProfileCompletenessResponse,
│   │   │   │   │   │                   # ProfileStyleRequest, ProfileStyleResponse,
│   │   │   │   │   │                   # UserSizeDto, UserMeasurementDto
│   │   │   │   │   ├── entity/         # Profile, UserSize, UserMeasurement, UserStyle
│   │   │   │   │   ├── repository/     # ProfileRepository, UserSizeRepository,
│   │   │   │   │   │                   # UserMeasurementRepository, UserStyleRepository
│   │   │   │   │   └── service/        # ProfileService, ProfileCompletenessService,
│   │   │   │   │                       # UserSizeService, UserMeasurementService,
│   │   │   │   │                       # UserStyleService
│   │   │   │   └── admin/              # Panel de administración
│   │   │   │       ├── controller/     # AdminController
│   │   │   │       ├── dto/            # AdminMetricsDTO, AdminUserDTO
│   │   │   │       ├── entity/         # ScrapingRunEntity
│   │   │   │       ├── repository/     # ScrapingRunRepository
│   │   │   │       └── service/        # AdminService
│   │   │   └── shared/                 # Componentes transversales
│   │   │       ├── config/             # SwaggerConfig
│   │   │       ├── dto/
│   │   │       ├── exception/          # GlobalExceptionHandler
│   │   │       ├── service/            # EmailService
│   │   │       └── ContactController.java
│   │   └── resources/
│   │       └── application.properties.example
│   └── test/                           # Tests unitarios e integración (15 clases)
│       └── java/cl/dressed/backend/
│           ├── module/auth/
│           ├── module/catalog/
│           └── module/profile/
├── build.gradle
├── gradlew
├── gradlew.bat
└── settings.gradle
```

---

## Módulos y endpoints

### `auth` — Autenticación

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Registro de usuario | Pública |
| `POST` | `/api/auth/login` | Inicio de sesión, retorna JWT en cookie | Pública |
| `POST` | `/api/auth/logout` | Cierre de sesión, limpia cookie | Pública |
| `GET` | `/api/auth/me` | Retorna datos del usuario autenticado | JWT |
| `POST` | `/api/auth/forgot-password` | Envía email de recuperación | Pública |
| `POST` | `/api/auth/reset-password` | Restablece contraseña con token | Pública |
| `POST` | `/api/auth/google` | Login / registro con Google OAuth | Pública |

### `catalog` — Catálogo de prendas

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `GET` | `/api/catalog/garments` | Lista prendas con filtros | Pública |
| `GET` | `/api/catalog/garments/{id}` | Detalle de una prenda | Pública |
| `POST` | `/api/catalog/garments` | Upsert de prenda (solo Spideron) | API Key |

### `outfit` — Outfits generados por IA

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `GET` | `/api/outfits` | Lista outfits disponibles | JWT |
| `GET` | `/api/outfits/{id}` | Detalle de un outfit | JWT |

### `profile` — Perfil de usuario

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `GET` | `/api/users/profile` | Retorna perfil completo | JWT |
| `PUT` | `/api/users/profile` | Actualiza datos del perfil | JWT |
| `PUT` | `/api/users/profile/skin` | Actualiza tono de piel | JWT |
| `GET` | `/api/users/profile/style` | Retorna estilos guardados | JWT |
| `POST` | `/api/users/profile/style` | Guarda preferencias de estilo | JWT |
| `GET` | `/api/users/sizes` | Retorna tallas del usuario | JWT |
| `PUT` | `/api/users/sizes` | Actualiza tallas | JWT |
| `GET` | `/api/users/measurements` | Retorna medidas corporales | JWT |
| `PUT` | `/api/users/measurements` | Actualiza medidas | JWT |
| `GET` | `/api/users/profile/completeness` | Porcentaje de completitud del perfil | JWT |

### `admin` — Administración

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `GET` | `/api/admin/metrics` | Métricas generales del sistema | JWT + ADMIN |
| `GET` | `/api/admin/users` | Lista de usuarios | JWT + ADMIN |

> La documentación interactiva completa está disponible en `http://localhost:8081/swagger-ui.html` cuando el servidor está corriendo.

---

## Instalación y desarrollo local

### Requisitos previos

- Java 21+
- Gradle (o usar el wrapper incluido `./gradlew`)
- PostgreSQL accesible (se recomienda NeonDB)
- Cuenta Gmail con App Password habilitada (para emails)

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/<org>/backend-dressed.git
cd backend-dressed

# 2. Crear el archivo de configuración
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Editar application.properties con las credenciales reales

# 3. Compilar el proyecto
./gradlew build

# 4. Iniciar el servidor
./gradlew bootRun
```

El servidor estará disponible en `http://localhost:8081`.

---

## Variables de entorno

| Variable | Descripción | Ejemplo |
|---|---|---|
| `DB_URL` | URL de conexión a PostgreSQL | `jdbc:postgresql://host/dbname?sslmode=require` |
| `DB_USERNAME` | Usuario de la base de datos | `db_user` |
| `DB_PASSWORD` | Contraseña de la base de datos | `db_password` |
| `JWT_SECRET` | Secret para firmar tokens JWT (mín. 32 chars) | `mi-secret-seguro-minimo-32-caracteres` |
| `MAIL_USERNAME` | Correo remitente para emails | `correo@gmail.com` |
| `MAIL_PASSWORD` | App Password de Gmail (16 caracteres) | `abcdefghijklmnop` |
| `SCRAPER_API_KEY` | Clave de acceso para Spideron | `scraper-api-key` |

> **Nota:** El archivo `application.properties` con credenciales reales está excluido del repositorio mediante `.gitignore`. Usar `application.properties.example` como referencia.

---

## Testing

El proyecto incluye **15 clases de test** con cobertura sobre los módulos principales.

```bash
# Ejecutar todos los tests
./gradlew test

# El reporte HTML de resultados queda en:
# build/reports/tests/test/index.html
```

### Cobertura de tests

| Módulo | Tipo | Clases |
|---|---|---|
| `auth` | Unitario e integración | `AuthServiceTest`, `AuthControllerIntegrationTest`, `AuthControllerValidationTest`, `GoogleAuthServiceTest` |
| `catalog` | Unitario | `GarmentServiceTest`, `CatalogControllerTest` |
| `profile` | Unitario | `ProfileServiceTest`, `ProfileCompletenessServiceTest`, `UserSizeServiceTest`, `UserMeasurementServiceTest`, `UserStyleServiceTest`, `ProfileControllerTest`, `UserSizeControllerTest`, `UserMeasurementControllerTest` |

---

## Seguridad

- **Contraseñas hasheadas con BCrypt** — nunca se almacenan en texto plano.
- **JWT stateless** — el servidor no guarda sesiones; el token contiene toda la información firmada criptográficamente.
- **Cookie HttpOnly** — el JWT viaja en cookie no accesible desde JavaScript, protegiéndolo de ataques XSS.
- **JwtFilter** — intercepta y valida el token en cada request antes de llegar al controlador.
- **Rutas públicas acotadas** — solo login, register, forgot/reset-password y catalog son accesibles sin autenticación.
- **ScraperApiKeyService** — el endpoint de upsert del catálogo requiere una API key específica para Spideron.
- **GlobalExceptionHandler** — centraliza el manejo de errores y nunca expone detalles internos del servidor al cliente.
- **Recuperación de contraseña segura** — tokens UUID de un solo uso con expiración de 30 minutos.
- **Google OAuth** — verificación del token de Google en el servidor antes de crear o autenticar al usuario.

---

## Licencia

Proyecto académico — uso interno del equipo.