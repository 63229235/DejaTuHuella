# Guía de Contribución para DejaTuHuella

¡Gracias por tu interés en contribuir a DejaTuHuella! Este documento proporciona directrices para contribuir al proyecto.

## Código de Conducta

Al participar en este proyecto, se espera que todos los contribuyentes respeten a los demás participantes y mantengan un ambiente colaborativo positivo.

## Cómo Contribuir

### Reportar Bugs

Si encuentras un bug, por favor crea un issue en GitHub con la siguiente información:

- Título claro y descriptivo
- Pasos detallados para reproducir el problema
- Comportamiento esperado vs. comportamiento actual
- Capturas de pantalla si aplica
- Información del entorno (navegador, sistema operativo, etc.)

### Sugerir Mejoras

Para sugerir mejoras o nuevas funcionalidades:

- Usa un título claro y descriptivo
- Proporciona una descripción detallada de la mejora
- Explica por qué esta mejora sería útil para el proyecto
- Incluye mockups o ejemplos si es posible

### Pull Requests

1. Haz fork del repositorio
2. Crea una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios siguiendo las convenciones de código
4. Asegúrate de que las pruebas pasan
5. Haz commit de tus cambios (`git commit -m 'Añadir nueva funcionalidad'`)
6. Haz push a la rama (`git push origin feature/nueva-funcionalidad`)
7. Crea un Pull Request

## Convenciones de Código

### Java

- Sigue las convenciones de nomenclatura de Java
- Usa 4 espacios para la indentación
- Incluye comentarios JavaDoc para clases y métodos públicos
- Escribe pruebas unitarias para nuevas funcionalidades

### HTML/CSS/JavaScript

- Usa 2 espacios para la indentación
- Sigue las convenciones de BEM para CSS
- Mantén el JavaScript modular y documentado

### Commits

- Usa mensajes de commit claros y descriptivos
- Sigue el formato: `tipo(ámbito): descripción`
  - Tipos: feat, fix, docs, style, refactor, test, chore
  - Ejemplo: `feat(payment): implementar validación de tarjetas de crédito`

## Estructura del Proyecto

Familiarízate con la estructura del proyecto antes de contribuir:

```
src/
├── main/
│   ├── java/
│   │   └── com/proyecto/dejatuhuella/
│   │       ├── config/          # Configuraciones de la aplicación
│   │       ├── controller/      # Controladores REST y MVC
│   │       ├── dto/             # Objetos de transferencia de datos
│   │       ├── model/           # Entidades de la base de datos
│   │       ├── repository/      # Repositorios JPA
│   │       ├── service/         # Servicios de negocio
│   │       └── DejatuHuellaApplication.java  # Punto de entrada
│   └── resources/
│       ├── static/              # Recursos estáticos (CSS, JS, imágenes)
│       ├── templates/           # Plantillas Thymeleaf
│       └── application.properties  # Configuración de la aplicación
└── test/                        # Pruebas unitarias e integración
```

## Proceso de Revisión

Cada Pull Request será revisado por al menos un mantenedor del proyecto. Durante la revisión, se pueden solicitar cambios o mejoras. Una vez aprobado, el PR será fusionado con la rama principal.

## Recursos Adicionales

- [Documentación de Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Guía de Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Convenciones de Código de Java](https://www.oracle.com/java/technologies/javase/codeconventions-introduction.html)

¡Gracias por contribuir a DejaTuHuella!