# Microservicio de Gestión de Imágenes - SanoSysalvos

Microservicio responsable de la gestión, almacenamiento y procesamiento de imágenes para la plataforma SanoSysalvos.

---

## 📋 Justificación del Uso de Gitflow

Se ha adoptado **Gitflow** como estrategia de control de versiones para este proyecto por las siguientes razones:

### 1. **Separación Clara de Ambientes de Desarrollo**
Gitflow establece ramas dedicadas para cada fase del ciclo de vida del software:
- **`main`**: Código en producción estable y listo para desplegar
- **`develop`**: Integración continua de características completadas
- **`feature/*`**: Desarrollo aislado de nuevas funcionalidades
- **`release/*`**: Preparación y ajustes previos a la producción
- **`hotfix/*`**: Corrección urgente de errores en producción

Esta separación permite que múltiples desarrolladores trabajen en paralelo sin interferencias.

### 2. **Facilita la Colaboración en Equipo**
- Cada desarrollador trabaja en su propia rama de feature, evitando conflictos directos con el código de otros
- Los cambios se integran de forma controlada mediante pull requests
- Se establecen puntos de revisión antes de fusionar código a `develop` o `main`

### 3. **Versionado Controlado y Predecible**
- Las ramas `release` permiten preparar versiones de forma ordenada
- Se pueden aplicar etiquetas (tags) para marcar versiones específicas
- Facilita la generación de changelogs y documentación de cambios

### 4. **Gestión de Hotfixes en Producción**
- Si surge un error crítico en producción, se crea una rama `hotfix` desde `main`
- La corrección se prueba y se fusiona directamente a `main` sin esperar a `develop`
- Luego se sincroniza con `develop` para evitar regresiones
- Esto minimiza el tiempo de respuesta ante incidentes críticos

### 5. **Mayor Estabilidad de la Rama Principal**
- La rama `main` representa siempre código productivo
- Solo se integra código que ha sido validado y probado completamente
- Reduce significativamente el riesgo de despliegues fallidos

### 6. **Escalabilidad**
- A medida que el equipo crece, Gitflow proporciona estructura clara
- Las convenciones de nombres de ramas hacen que el flujo sea intuitivo
- Facilita la onboarding de nuevos miembros del equipo

---

## 🔄 Estructura de Ramas en Gitflow

```
main (producción)
  ↑
  └─← release/v1.0.0 (preparación para producción)
  └─← hotfix/bug-crítico (correcciones urgentes)

develop (rama de integración)
  ↑
  └─← feature/nueva-funcionalidad (desarrollo de features)
  └─← feature/mejora-rendimiento
  └─← ...
```

---

## 🚀 Cómo Usar Gitflow en Este Proyecto

### Iniciar una Nueva Funcionalidad
```bash
git checkout -b feature/descripcion-feature develop
# Realizar cambios
git commit -m "descripción del cambio"
git push origin feature/descripcion-feature
```

### Preparar una Release
```bash
git checkout -b release/v1.0.0 develop
# Realizar ajustes finales y bump de versión
git commit -m "Bump version to 1.0.0"
git push origin release/v1.0.0
```

### Crear un Hotfix (Corrección Urgente)
```bash
git checkout -b hotfix/descripcion-hotfix main
# Corregir el error
git commit -m "Fix: descripción del fix"
git push origin hotfix/descripcion-hotfix
```

---

## 📦 Instalación y Configuración

[Agregar instrucciones de instalación específicas del proyecto]

## 🧪 Testing

[Agregar guía de pruebas]

## 📝 Convenciones de Commits

Se recomienda seguir estas convenciones:
- `feat:` Nuevas funcionalidades
- `fix:` Corrección de bugs
- `docs:` Cambios en documentación
- `refactor:` Refactorización de código
- `test:` Cambios en tests

Ejemplo: `feat: agregar validación de tipo de imagen`

---

## 👥 Contribución

Consulta la documentación de Gitflow antes de contribuir. Todos los cambios deben pasar por pull request y revisión de código.

---

**Autor:** Rodrigo Baeza  
**Última actualización:** 2026-09-05
