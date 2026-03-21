# PONTIFICIA UNIVERSIDAD CATÓLICA MADRE Y MAESTRA

## FACULTAD DE CIENCIAS E INGENIERÍA

### ESCUELA DE INGENIERÍA EN COMPUTACIÓN Y TELECOMUNICACIONES

**Programación Web Avanzada**

**Juan Alfonso Alvarado Batista**
**ID:1014-1321**

## Implementando sistemas basados en Bróker de Mensajería

---

### Objetivos

- Implementar un bróker de mensajería.
- Crear una aplicación basada en el intercambio de mensajes asíncronos.
- Implementar colas de suscripción y publicación.
- Uso de Docker Compose para la orquestación de servicios.

### Desarrollo de la Práctica

La práctica consistió en diseñar y desarrollar un sistema completo de mensajería asíncrona que permita la comunicación eficiente de dispositivos simulados (clientes) con un servidor central que procesa y visualiza los datos en tiempo real. Se implementaron los siguientes componentes:

### Estructura del Proyecto

El proyecto se desarrolló en la carpeta `practica-5`, que contiene los siguientes directorios y archivos principales:

1. **apache-activemq-5.18.2-bin**:

   - Contiene la distribución de Apache ActiveMQ, utilizada como bróker de mensajería. Proporciona la infraestructura para la gestión de mensajes mediante el estándar JMS y soporta protocolos como OpenWire.

2. **Cliente1** y **Cliente2**:

   - Directorios que contienen el código fuente de los clientes simuladores. Estos clientes generan de forma aleatoria valores de temperatura y humedad y los envían a la cola `notificacion_sensores` en formato JSON.

3. **Receptor**:

   - Directorio que incluye la implementación del servicio de recepción, que consume los mensajes de la cola, los procesa, los persiste en una base de datos y los envía a la aplicación web para la visualización.

4. **docker-compose.yaml**:
   - Archivo de configuración para orquestar la ejecución de todos los servicios involucrados en el proyecto, incluyendo ActiveMQ, los clientes y el receptor.

### Implementación de Modelos de Cliente

Se desarrollaron los siguientes modelos para facilitar el manejo de los datos y la comunicación con el bróker de mensajería:

- **ClienteRest.java**: Gestiona la comunicación con servicios REST externos para la obtención de datos de sensores.
- **DataWeather.java**: Modelo de datos para encapsular la información de temperatura y humedad en formato JSON.
- **Productor.java**: Se encarga de la conexión y publicación de mensajes al bróker de ActiveMQ, utilizando la API JMS.
- **Respuesta.java**: Modelo que maneja las respuestas del servidor, verificando la recepción de los mensajes y gestionando errores.
- **TipoCola.java**: Enumerador que define los tipos de colas utilizadas, facilitando la gestión y expansión futura del sistema.

### Flujo de Trabajo del Proyecto

1. **Bróker de Mensajería (Apache ActiveMQ)**:

   - Se configura y despliega utilizando Docker para actuar como intermediario entre los clientes y el receptor.

2. **Clientes Simuladores (Cliente1 y Cliente2)**:

   - Generan mensajes cada minuto, formateados en JSON con la siguiente estructura:
     ```json
     {
       "fechaGeneración": "DD/MM/YYYY HH:mm:ss",
       "IdDispositivo": 1,
       "temperatura": 25.3,
       "humedad": 60.5
     }
     ```
   - Publican estos mensajes en la cola `notificacion_sensores`.

3. **Receptor**:

   - Consume los mensajes de la cola, los procesa y los persiste en una base de datos local. Utiliza WebSockets para enviar los datos a la aplicación web y actualizar los gráficos en tiempo real.

4. **Aplicación Web**:
   - Muestra gráficos en tiempo real de temperatura y humedad utilizando Chart.js y WebSockets. La visualización se realiza con una paleta de colores en escala de grises, proporcionando un diseño limpio y uniforme.

### Orquestación con Docker Compose

El archivo `docker-compose.yaml` se configuró para orquestar los servicios, permitiendo una integración fluida y la ejecución simultánea de todos los componentes:

```yaml
version: "3"
services:
  active-mq:
    image: apache/activemq-classic
    ports:
      - "1883:1883"
      - "61616:61616"
      - "8161:8161"
    networks:
      - red-receptor

  receptor:
    image: armandogl14/practica5-receptor-app
    ports:
      - "8083:8083"
    depends_on:
      - active-mq
    networks:
      - red-receptor

  cliente1:
    image: armandogl14/webavanzada:practica5-cliente1-app
    depends_on:
      - active-mq
    networks:
      - red-receptor

  cliente2:
    image: armandogl14/webavanzada:practica5-cliente2-app
    depends_on:
      - active-mq
    networks:
      - red-receptor

networks:
  red-receptor:
```

# Conlsión

La práctica fue completada con éxito, cumpliendo los objetivos de implementar un sistema de mensajería asíncrona con Apache ActiveMQ, clientes simuladores, un servidor receptor y una aplicación web para la visualización en tiempo real. La solución se empaquetó y desplegó utilizando Docker y Docker Compose, facilitando la replicación y escalabilidad del entorno.
