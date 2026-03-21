# Reporte de Funcionalidad de la Aplicación

## Nombre del Proyecto: Mockup API Server con Spring Boot
**Curso**: Programación Web Avanzada  
**Descripción**:  
La aplicación desarrollada es un servidor Mockup API utilizando el framework Spring Boot, con las siguientes características: autenticación de usuarios mediante JWT, gestión de usuarios y roles con Spring Security, CRUD de endpoints Mockup para la simulación de APIs, y configuración de internacionalización (i18n) para soporte multilenguaje.

---

## 1. Objetivos de la Aplicación
1. **Autenticación y Autorización**: Implementar autenticación segura mediante tokens JWT, con control de acceso usando Spring Security.
2. **Gestión de Usuarios y Roles**: Permitir la creación de usuarios y asignación de roles (Administrador y Usuario).
3. **Creación y Gestión de Mockups API**: Desarrollar una interfaz sencilla para crear, modificar, eliminar y visualizar endpoints Mock que permitan simular APIs.
4. **Soporte para Internacionalización (i18n)**: Soportar múltiples idiomas en la aplicación mediante la configuración de i18n.

---

## 2. Funcionalidades Clave

### 2.1. Autenticación y Autorización
La aplicación utiliza **JWT (JSON Web Token)** para manejar la autenticación y autorización de los usuarios. El flujo de trabajo es el siguiente:
- Los usuarios pueden autenticarse enviando sus credenciales (nombre de usuario y contraseña) a un endpoint de autenticación `/api/auth/login`.
- Si las credenciales son válidas, la aplicación genera un **JWT** que se devuelve al usuario. Este token es necesario para acceder a los endpoints protegidos de la aplicación.
- El token se verifica en cada solicitud mediante un filtro (`JwtRequestFilter`), asegurándose de que el usuario esté autenticado para interactuar con la API.

**Tecnologías utilizadas**: Spring Security, JWT, BCryptPasswordEncoder para el cifrado de contraseñas.

### 2.2. Gestión de Usuarios y Roles
- **Creación de Usuarios**: Un usuario administrador puede registrar nuevos usuarios a través de un endpoint `/api/user/register`.
- **Asignación de Roles**: El administrador puede asignar diferentes roles a los usuarios, como `ROLE_ADMIN` y `ROLE_USER`, permitiendo gestionar los permisos de acceso a los recursos.
- **Roles en Seguridad**: Los endpoints se protegen en función del rol del usuario. Por ejemplo, solo un administrador (`ROLE_ADMIN`) puede crear o asignar roles a otros usuarios.

**Tecnologías utilizadas**: Spring Security, JPA para la persistencia de datos, BCryptPasswordEncoder para gestionar contraseñas seguras.

### 2.3. Gestión de Endpoints Mock (CRUD)
El sistema permite a los usuarios crear y gestionar endpoints mockup para simular APIs. Esta funcionalidad está disponible solo para usuarios autenticados y administradores.

- **Crear Mockups**: Los usuarios pueden crear endpoints mockup a través de un formulario que solicita la ruta, método HTTP, headers, código de respuesta, y otros atributos relacionados.
- **Listar y Detallar Endpoints**: Los usuarios pueden listar sus endpoints mockup creados, y ver detalles específicos de cada uno.
- **Actualizar y Eliminar Mockups**: Los usuarios pueden actualizar o eliminar los endpoints mockup que han creado.
- **Acceso Restringido**: Los usuarios solo pueden gestionar sus propios mockups, mientras que los administradores tienen acceso a todos los mockups creados en la plataforma.

**Tecnologías utilizadas**: Spring MVC, Spring Data JPA, H2 para la base de datos en memoria.

### 2.4. Internacionalización (i18n)
La aplicación soporta múltiples idiomas mediante la configuración de i18n. Los formularios, botones, y mensajes de error están disponibles en inglés y español.

- **Implementación de i18n**: Se han configurado archivos de recursos `messages_en.properties` y `messages_es.properties` para inglés y español, respectivamente.
- **Cambio de idioma**: Los usuarios pueden cambiar el idioma de la interfaz a través de un parámetro en la solicitud o configuración del navegador.

**Tecnologías utilizadas**: Spring i18n.

---

## 3. Estructura de la Aplicación

### 3.1. Paquetes Principales
- **`controller`**: Contiene los controladores REST para la autenticación de usuarios, la gestión de mockups API y la administración de roles.
    - **`AuthController`**: Controlador para la autenticación de usuarios y generación de tokens JWT.
    - **`MockApiController`**: Controlador para gestionar el CRUD de los endpoints mockup.
    - **`UserController`**: Controlador para la creación de usuarios y asignación de roles.

- **`service`**: Contiene la lógica de negocio, incluyendo la autenticación, la gestión de usuarios y roles, y la generación de tokens JWT.
    - **`UserService`**: Servicio para la gestión de usuarios y roles.
    - **`JwtService`**: Servicio para la generación y validación de tokens JWT.

- **`model`**: Contiene las clases de entidad que representan los datos persistentes.
    - **`User`**: Representa a los usuarios del sistema.
    - **`Role`**: Representa los roles asignados a los usuarios (Administrador y Usuario).
    - **`MockEndpoint`**: Representa los endpoints mockup creados por los usuarios.

- **`repository`**: Contiene las interfaces para interactuar con la base de datos utilizando Spring Data JPA.
    - **`UserRepository`**: Interfaz para acceder a los datos de los usuarios.
    - **`RoleRepository`**: Interfaz para acceder a los datos de los roles.
    - **`MockEndpointRepository`**: Interfaz para gestionar los endpoints mockup.

- **`filter`**: Contiene los filtros de seguridad como el `JwtRequestFilter` que se utiliza para interceptar las solicitudes HTTP y validar los tokens JWT.

---

## 4. Base de Datos
- **H2 In-Memory Database**: La aplicación utiliza una base de datos en memoria (H2) para almacenar usuarios, roles y mockups. Los datos se reinician cada vez que la aplicación se reinicia.
- **ORM con JPA**: Se utiliza **JPA (Java Persistence API)** para interactuar con la base de datos y persistir entidades como `User`, `Role`, y `MockEndpoint`.

---

## 5. Seguridad
- **Spring Security y JWT**: La aplicación utiliza Spring Security para gestionar la seguridad y la autenticación, con tokens JWT para manejar sesiones sin estado (stateless).
- **BCryptPasswordEncoder**: Las contraseñas de los usuarios se almacenan de manera segura utilizando el algoritmo de hashing BCrypt.

---

## 6. Internacionalización (i18n)
- **Idiomas soportados**: Inglés y Español.
- **Archivos de recursos**: Configurados en `src/main/resources/messages_en.properties` y `src/main/resources/messages_es.properties` para textos en los formularios y mensajes.

---

## 7. Conclusiones
La aplicación Mockup API Server ha sido desarrollada para cumplir con los objetivos planteados, proporcionando un sistema seguro para la gestión de usuarios, roles y endpoints mockup. El uso de JWT y Spring Security garantiza que la autenticación sea segura, mientras que la funcionalidad CRUD de mockups facilita la simulación de APIs para otros desarrolladores.

---

