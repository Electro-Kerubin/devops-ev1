# 📸 Sanosysalvos - Geolocation Service

Microservicio especializado geolocalización para la plataforma Sanosysalvos.

## 🚀 Quick Start

### Requisitos Previos
- Java 21 (JDK Temurin)
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 16+

### Instalación
```bash
# Clonar repositorio
git clone https://github.com/Electro-Kerubin/devops-ev1
cd Geolocation-service

# Compilar proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Empaquetar aplicación
mvn package
```

### Ejecutar Localmente
```bash
# Opción 1: Con Maven
mvn spring-boot:run

# Opción 2: Con Docker
docker build -t geolocation-service:latest .
docker run -p 8082:8082 geolocation-service:latest
```

---

## 📋 Estrategia de Versionado: GitFlow

### ¿Por qué GitFlow?

Adoptamos **GitFlow** como estrategia de branching por las siguientes razones:

#### ✅ **Control y Estabilidad**
- **`main`**: Rama de producción. Solo contiene versiones **estables y testeadas**
- **`develop`**: Rama de integración continua. Reúne todas las características completadas
- Garantiza que `main` siempre esté en estado deployable

#### ✅ **Flujo de Trabajo Organizado**
- **Feature branches** (`feature/*`): Desarrollo de nuevas funcionalidades
- **Bugfix branches** (`bugfix/*`): Corrección de errores en desarrollo
- **Release branches** (`release/*`): Preparación de versiones para producción
- **Hotfix branches** (`hotfix/*`): Parches críticos directamente desde `main`

#### ✅ **Integración Continua**
- Cada push a `develop` **ejecuta automáticamente** los tests y build
- Las Pull Requests permiten **revisión de código** antes de merge
- Reduce bugs en producción gracias al CI/CD

#### ✅ **Ambiente de Staging**
- `develop` actúa como ambiente **pre-producción**
- Los cambios se validan automáticamente antes de llegar a `main`
- Facilita testing en un entorno similar al de producción

#### ✅ **Gestión de Versiones Claras**
- Cada release tiene **tags y versiones** claramente identificables
- Permite rollback rápido si es necesario
- Historial de cambios bien documentado

---

## 📐 Guía de Buenas Prácticas

### Naming de ramas
- `feature/<nombre-descriptivo>` — ej: feature/geocoding-endpoint
- `hotfix/<nombre-descriptivo>` — ej: hotfix/fix-null-pointer-coordenada
- `bugfix/<nombre-descriptivo>`
- `release/<version>` — ej: release/1.0.0

### Convención de mensajes de commit
Usamos Conventional Commits:
- `feat:` nueva funcionalidad
- `fix:` corrección de bug
- `docs:` cambios en documentación
- `refactor:` refactorización sin cambio de comportamiento
- `test:` agregar o modificar tests
- `chore:` tareas de mantenimiento (configs, dependencias)

Ejemplo: `feat: agregar endpoint de geocodificación inversa`

---

### Estructura de carpetas

\`\`\`
src/
├── main/
│   ├── java/org/sanosysalvos/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── security/
│   └── resources/
└── test/
    └── java/org/sanosysalvos/
\`\`\`

---

### Control de versiones (Semantic Versioning)
- **MAJOR** (1.x.x → 2.0.0): cambios incompatibles en la API
- **MINOR** (1.0.x → 1.1.0): nueva funcionalidad compatible
- **PATCH** (1.0.0 → 1.0.1): corrección de bugs

---


## 🔄 Pipeline CI/CD

Nuestro pipeline está automatizado con **GitHub Actions** y se ejecuta en cada push según la rama:

### 📊 Diagrama del Flujo

```
Push a rama
    ↓
┌─────────────────────────────────────┐
│  1️⃣  build-and-test (Siempre)      │
│  └─ Compile + Test + Package       │
└─────────────────────────────────────┘
    ↓ (Si es develop + es push)
┌─────────────────────────────────────┐
│  2️⃣  docker-build-push              │
│  └─ Build imagen Docker + Push Hub  │
└─────────────────────────────────────┘
    ↓ (Si es develop + es push)
┌─────────────────────────────────────┐
│  3️⃣  deploy-simulado                │
│  └─ Simula deploy con PostgreSQL    │
└─────────────────────────────────────┘
```

---

## 🎯 Descripción de Jobs del Pipeline

### 1️⃣ **Job: `build-and-test`**

**Cuándo se ejecuta:** En todos los pushes y Pull Requests

**¿Qué hace?**
- ✅ Descarga el código fuente
- ✅ Instala JDK 21 (Temurin)
- ✅ Compila el proyecto con Maven
- ✅ Ejecuta suite de tests unitarios
- ✅ Empaqueta la aplicación en JAR

**Por qué es importante:**
- Valida que el código compila correctamente
- Detecta errores lógicos mediante tests
- Genera el artefacto (JAR) necesario para siguiente etapa
- Falla si algún test no pasa → previene regresiones

**Ejemplo de salida exitosa:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2:45 min
[INFO] Generated JAR: target/imagenes-service-1.0.0.jar
```

---

### 2️⃣ **Job: `docker-build-push`**

**Cuándo se ejecuta:** Solo cuando hay push a `develop` (automático)

**Requisitos previos:** `build-and-test` debe completarse exitosamente

**¿Qué hace?**
- ✅ Descarga código y credenciales de DockerHub
- ✅ Construye imagen Docker basada en el `Dockerfile`
- ✅ Sube la imagen a DockerHub con tag `latest`
- ✅ Registra la imagen en el repositorio público

**Por qué es importante:**
- Containeriza la aplicación para portabilidad
- Permite desploying consistente en cualquier ambiente
- Facilita escalado horizontal
- Genera artefacto deployable (imagen Docker)

**Imagen generada:**
```
dockerhub_user/imagenes-service:latest
```

**Requisitos en GitHub Secrets:**
```
DOCKERHUB_USERNAME  → usuario DockerHub
DOCKERHUB_TOKEN     → token de autenticación
DOCKERHUB_REPONAME  → nombre del repo
```

---

### 3️⃣ **Job: `deploy-simulado`**

**Cuándo se ejecuta:** Solo cuando hay push a `develop` (automático)

**Requisitos previos:** `docker-build-push` debe completarse exitosamente

**¿Qué hace?**
- ✅ Inicia un contenedor PostgreSQL 16 (base de datos de prueba)
- ✅ Descarga la imagen Docker construida en el paso anterior
- ✅ Ejecuta el contenedor de la aplicación en puerto 8082
- ✅ Espera conexión a PostgreSQL
- ✅ Verifica salud del servicio con endpoint `/actuator/health`
- ✅ Realiza hasta 12 intentos de verificación (60 segundos total)

**Por qué es importante:**
- Valida que el contenedor **corre correctamente** en un ambiente
- Verifica conectividad con base de datos
- Detecta problemas de configuración antes de producción
- **Simula** un despliegue real sin afectar AWS
- Proporciona logs si algo falla

**Configuración de base de datos:**
```
POSTGRES_DB: geolocation
POSTGRES_USER: test_user
POSTGRES_PASSWORD: test_pass
Puerto: 5432
```

**Verificación de salud:**
```bash
curl http://localhost:8082/actuator/health
# Respuesta esperada:
# {"status":"UP","components":{...}}
```

---

## 🔑 Configuración de Secretos en GitHub

Para que el pipeline funcione, configura estos secretos en:
**Settings → Secrets and variables → Actions**

| Secreto | Descripción | Ejemplo |
|---------|-------------|---------|
| `DOCKERHUB_USERNAME` | Usuario de tu cuenta DockerHub | `tu_usuario` |
| `DOCKERHUB_TOKEN` | Token PAT de DockerHub | `dckr_pat_xxx...` |
| `DOCKERHUB_REPONAME` | Nombre del repositorio Docker | `imagenes-service` |

---

## 📱 Ramas y Flujo de Trabajo

### Estructura de Ramas

```
main (producción)
└─ release/1.0.0
    └─ hotfix/patch-bug-crítico
    
develop (integración)
└─ feature/nueva-funcionalidad
└─ feature/optimizar-rendimiento
└─ bugfix/corregir-geocodificación
```

### Workflow Típico

#### **Crear nueva funcionalidad:**
```bash
# Desde develop
git checkout -b feature/nueva-funcionalidad develop

# Hacer cambios...
git add .
git commit -m "feat: nueva funcionalidad"

# Subir e ir a Pull Request
git push origin feature/nueva-funcionalidad
```

#### **Preparar release (desde develop a main):**
```bash
git checkout -b release/1.0.0 develop
# Actualizar versión en pom.xml
git tag -a v1.0.0
git push origin release/1.0.0 --tags
# Pull Request a main
```

#### **Hotfix de emergencia (desde main):**
```bash
git checkout -b hotfix/patch-critico main
# Corregir bug...
git push origin hotfix/patch-critico
# Pull Request a main Y a develop
```

---

## 🧪 Testing

### Ejecutar Tests Localmente
```bash
# Todos los tests
mvn test

# Test específico
mvn test -Dtest=NombreDeTestClass

# Con cobertura
mvn test jacoco:report
```

### Convención de Nombres
- `*Test.java` → Tests unitarios (ejecutan en CI)
- `*IntegrationTest.java` → Tests de integración (incluidos en `mvn test`)
- `*IT.java` → Tests de integración adicionales

---

## 📊 Monitoreo del Pipeline

### Verificar estado en GitHub
1. Ve a **Actions** en el repositorio
2. Selecciona el último workflow run
3. Expande cada job para ver logs detallados

### Troubleshooting

**❌ Build falla en tests:**
```bash
# Revisar logs localmente
mvn test -X
```

**❌ Docker build falla:**
```bash
# Revisar Dockerfile
docker build -t imagenes-service:test .
docker run imagenes-service:test
```

**❌ Deploy simulado falla:**
```bash
# Verificar conectividad a PostgreSQL
docker logs microservicio-simulado
curl -v http://localhost:8082/actuator/health
```

---

## 🛠️ Configuración Adicional

### Environment Variables
```properties
# application.yml / application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/geolocation
spring.datasource.username=test_user
spring.datasource.password=test_pass
server.port=8082
```

### Dockerfile
El proyecto incluye un `Dockerfile` optimizado para:
- Multi-stage build (reduce tamaño)
- JDK 21 Temurin
- Healthcheck integrado

---

## 📚 Referencias

- [GitFlow Guide](https://nvie.com/posts/a-successful-git-branching-model/)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

---

## 👥 Equipo

- **Desenvolvedor:** Rodrigo Baeza
- **Estudios:** DUOC - Ingeniería DevOps

---

**Última actualización:** 2026-09-05  
**Versión:** 1.0.0


## 🤖 Uso de Inteligencia Artificial

Se utilizó Claude (Anthropic) como apoyo para:
- Debugging del pipeline de GitHub Actions (resolución de errores de configuración)
- Redacción y estructuración de la documentación técnica (README)
