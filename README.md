# Plaza Órbita — App móvil (Kotlin) + Backend (Spring Boot/Kotlin) + MySQL

## ⚠️ Nota importante sobre la arquitectura

El acta de constitución que subiste describe Plaza Órbita como una **plataforma web**
("paneles web", API REST, base de datos relacional). Tú pediste construirlo como
**app móvil nativa en Kotlin**, así que este proyecto respeta esa decisión pero
mantiene la arquitectura de 3 capas del acta:

```
[ App Android (Kotlin + Jetpack Compose) ]
              |  HTTP/JSON (Retrofit)
              v
[ Backend REST API (Spring Boot + Kotlin) ]
              |  JDBC
              v
[ MySQL ]
```

**Por qué hay un backend en medio y la app no se conecta directo a MySQL:**
Un Android NO debe conectarse directamente a una base de datos MySQL. Requeriría
que la contraseña de la base de datos viajara dentro del APK (cualquiera puede
descompilarlo y leerla) y expondría el puerto 3306 de tu base de datos a
internet. El RNF-03 del acta ("HTTPS/TLS, hash de contraseñas, RBAC") solo es
alcanzable con una API intermedia. Esta es la forma estándar en la industria
de conectar apps móviles a bases de datos relacionales.

Como todo el stack (app + backend) está en Kotlin, aprendes el lenguaje en
ambos lados sin cambiar de sintaxis.

## Estructura de carpetas

```
PlazaOrbita/
├── database/
│   └── schema.sql              # Esquema completo de MySQL (6 tablas, todos los módulos)
├── backend/                    # API REST en Spring Boot + Kotlin
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/plazaorbita/backend/
│       ├── model/              # Entidades JPA (User, Business, Product, Appointment, Order, Notification)
│       ├── repository/         # Interfaces Spring Data JPA
│       ├── dto/                # Objetos de petición/respuesta
│       ├── security/           # JWT (generación y validación de tokens)
│       ├── config/             # Configuración de Spring Security
│       ├── service/            # Lógica de negocio de cada módulo
│       └── controller/         # Endpoints REST (/api/auth, /api/businesses, ...)
└── android-app/                # App nativa en Kotlin + Jetpack Compose
    └── app/src/main/java/com/plazaorbita/app/
        ├── data/model/         # Data classes (equivalentes a los DTOs del backend)
        ├── data/remote/        # Retrofit (ApiService + cliente HTTP)
        ├── ui/auth/            # Login y Registro (funcionales, conectados al backend)
        ├── ui/admin/           # Panel del administrador de la plaza
        ├── ui/business/        # Panel del dueño de negocio
        ├── ui/customer/        # Panel del cliente
        ├── ui/navigation/      # Navegación (redirige según el rol tras login)
        └── util/SessionManager.kt  # Guarda el token JWT en el dispositivo
```

## Qué está 100% funcional ahora mismo

- **Registro y login** (app ↔ backend ↔ MySQL) con contraseñas hasheadas (BCrypt) y JWT.
- **Redirección automática por rol** tras iniciar sesión (admin / dueño / cliente).
- **Negocios**: crear y listar (`/api/businesses`).
- **Inventario**: crear productos, listar, y bandera automática de "stock bajo" (Historia 1).
- **Citas**: reservar con bloqueo de horario duplicado a nivel de base de datos y de código (Historia 2).
- **Pedidos**: crear pedido con descuento de stock, cambio de estatus, notificación al cliente cuando está listo (Historia 3 y 4).
- **Notificaciones**: se generan automáticamente al crear cita/pedido; endpoint para listarlas.
- **Reportes**: conteo de pedidos/citas por negocio (Historia 5), listo para ampliarse.

Las pantallas de Admin/Dueño/Cliente ya cargan datos reales del backend; los
formularios de "crear cita", "crear pedido" y "dar de alta negocio" quedan
marcados con `// TODO siguiente sprint` en el código — siguen exactamente el
mismo patrón que Login/Register, así que decirme "hazme la pantalla de X" te
la genero igual de completa.

## Cómo correrlo en LOCAL (desarrollo)

### 1. Base de datos
```bash
mysql -u root -p < database/schema.sql
```

### 2. Backend
1. Abre la carpeta `backend/` en IntelliJ IDEA o Android Studio (con plugin de Kotlin).
2. `application.properties` ahora lee todo de variables de entorno con valores por
   defecto para local (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`...). Para
   correr en tu máquina sin configurar nada, no necesitas tocar el archivo — usa
   `root` sin password. Si tu MySQL local sí tiene password, expórtalo como variable
   de entorno antes de correr, o edita el valor por defecto entre `${...:...}`.
3. Ejecuta `PlazaOrbitaApplication.kt` (o `./gradlew bootRun`).
4. Debe quedar escuchando en `http://localhost:8080`.

### 3. App Android
1. Copia `android-app/local.properties.example` a `android-app/local.properties`
   (este archivo está en `.gitignore`, nunca se sube a GitHub).
2. Abre `android-app/` en **Android Studio** y deja que Gradle sincronice.
3. Corre el build **debug** en el emulador: ya apunta a `http://10.0.2.2:8080/`
   (así ve el emulador el `localhost` de tu PC). Si usas celular físico por USB,
   cambia `DEBUG_BASE_URL` en `local.properties` por la IP local de tu PC
   (ej. `http://192.168.1.50:8080/`).
4. Prueba: Registrarme → elige un rol → deberías caer en el panel correspondiente.

---

## Cómo mudarlo a la NUBE (gratis)

Arquitectura final: **App Android (APK firmado, minificado con R8, HTTPS-only)** →
**Backend Spring Boot en Render (Docker, gratis)** → **MySQL en Aiven (gratis, para siempre)**.

### Paso 1 — Base de datos: Aiven for MySQL (gratis, sin tarjeta)
1. Crea cuenta en [aiven.io](https://aiven.io) → "Create service" → MySQL → plan Free.
2. Cuando esté listo, en la pestaña **Overview** copia: host, puerto, usuario, password
   y el nombre por defecto de la base (o crea la base `plaza_orbita`).
3. Carga el esquema apuntando a Aiven en vez de a tu MySQL local:
   ```bash
   mysql --host=TU_HOST --port=TU_PUERTO --user=TU_USUARIO -p < database/schema.sql
   ```
   (Aiven exige SSL; el cliente `mysql` moderno lo negocia solo.)
4. Anota la URL JDBC que vas a usar en el Paso 2, con el formato:
   `jdbc:mysql://TU_HOST:TU_PUERTO/plaza_orbita?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true`

> Alternativas gratuitas si prefieres: Railway (crédito de prueba, no permanente) o
> tu propio MySQL en un VPS. PlanetScale ya **no** tiene plan gratis.

### Paso 2 — Backend: Render (Docker, gratis)
El proyecto ya trae `backend/Dockerfile` y `render.yaml` listos.
1. Sube el proyecto a un repositorio de GitHub.
2. En [render.com](https://render.com) → "New +" → "Blueprint" → conecta el repo:
   Render detecta `render.yaml` solo y crea el servicio `plaza-orbita-backend`.
   (Si prefieres hacerlo a mano: "New +" → "Web Service" → runtime **Docker** →
   root directory `backend`.)
3. En la pestaña **Environment** del servicio, define:
   - `DB_URL` → la URL JDBC de Aiven del Paso 1
   - `DB_USERNAME`, `DB_PASSWORD` → los de Aiven
   - `JWT_SECRET` → Render puede generarla sola (con `render.yaml` ya está configurado así)
4. Deploy. Cuando termine, Render te da una URL pública HTTPS, ej.
   `https://plaza-orbita-backend.onrender.com`.

> Nota: el plan free de Render "duerme" el servicio tras ~15 min sin tráfico; la
> primera petición después de dormir tarda unos segundos en responder (cold start).
> Es normal y no cuesta nada; si te molesta, el siguiente escalón de pago lo quita.

### Paso 3 — Seguridad y build de producción de la APK
1. En `android-app/local.properties`, pon:
   ```
   RELEASE_BASE_URL=https://plaza-orbita-backend.onrender.com/
   ```
2. En Android Studio: **Build → Generate Signed Bundle / APK** → elige **APK**,
   crea (o usa) tu keystore de firma, y selecciona el build type **release**.
3. Ese build ya sale con:
   - HTTPS obligatorio (el manifest de producción ya no permite tráfico sin cifrar;
     solo el build debug lo permite, para hablar con el emulador).
   - Token JWT guardado con `EncryptedSharedPreferences` (Android Keystore), no en
     texto plano.
   - Código ofuscado y recursos no usados eliminados (R8 `isMinifyEnabled = true`).
   - La URL del backend sale de `BuildConfig`, no como texto plano en el código fuente.

### Resumen de arquitectura final (gratis)
| Capa | Dónde | Costo |
|---|---|---|
| App Android | APK firmado, R8, HTTPS-only, token encriptado | — |
| Backend (API REST) | Render, Docker, Spring Boot | Gratis (con cold start) |
| Base de datos | Aiven for MySQL | Gratis para siempre (1 GB) |

## Próximos pasos sugeridos (Sprint 2 en adelante, según tu cronograma)

- Formulario de alta de producto con validación de umbral mínimo.
- Pantalla de reserva de cita con selector de fecha/hora (`DatePicker`/`TimePicker` de Compose).
- Pantalla de carrito de compra para pedidos con pickup.
- Pantalla de notificaciones (ya hay endpoint listo: `/api/notifications/user/{id}`).
- Restringir cada endpoint por rol en `SecurityConfig.kt` (ahora solo exige estar autenticado; falta `hasRole(...)` por módulo).
