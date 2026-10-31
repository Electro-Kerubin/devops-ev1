# syntax=docker/dockerfile:1
# ---------------------------------------------------------------------------
# Multi-stage build: dependencies -> test -> compile -> prod
#  - dependencies: descarga y cachea las dependencias de Maven (capa reutilizable)
#  - test:         ejecuta las pruebas unitarias; si fallan, la imagen NO se construye
#  - compile:      empaqueta el JAR a partir del código ya probado
#  - prod:         imagen final mínima (solo JRE + JAR), sin Maven ni código fuente
# ---------------------------------------------------------------------------

# ---- Etapa 1: dependencias (cache) ----
FROM maven:3.9.9-eclipse-temurin-21 AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

# ---- Etapa 2: pruebas unitarias ----
FROM maven:3.9.9-eclipse-temurin-21 AS test
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY pom.xml lombok.config ./
COPY src ./src
RUN mvn -B -ntp test

# ---- Etapa 3: compilación (depende de que "test" haya pasado) ----
FROM maven:3.9.9-eclipse-temurin-21 AS compile
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=test /app/pom.xml /app/lombok.config ./
COPY --from=test /app/src ./src
RUN mvn -B -ntp package -DskipTests

# ---- Etapa 4: producción ----
FROM eclipse-temurin:21-jre-alpine AS prod

# Metadatos OCI: trazabilidad de la imagen hacia el commit que la originó
ARG GIT_SHA=unknown
ARG BUILD_DATE=unknown
ARG VERSION=dev
LABEL org.opencontainers.image.title="sanosysalvos-geolocation-service" \
      org.opencontainers.image.description="Microservicio de geolocalización de Sanosysalvos" \
      org.opencontainers.image.source="https://github.com/Electro-Kerubin/devops-ev1" \
      org.opencontainers.image.revision="${GIT_SHA}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.version="${VERSION}"

# Parches de seguridad del sistema operativo: se aplican en cada build sin esperar una imagen base nueva
RUN apk upgrade --no-cache

# Seguridad: el proceso corre como usuario sin privilegios.
# UID/GID numéricos fijos: Kubernetes los necesita para verificar runAsNonRoot
RUN addgroup -S -g 10001 app && adduser -S -u 10001 -G app -H -s /sbin/nologin app
WORKDIR /app
COPY --from=compile --chown=10001:10001 /app/target/*.jar app.jar
USER 10001:10001

ENV GIT_SHA=${GIT_SHA} \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8082

HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=5 \
  CMD wget -qO- http://localhost:8082/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
