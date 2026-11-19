# RIATIGA Audio

RIATIGA Audio es una aplicación móvil Android desarrollada en Kotlin como proyecto académico para la asignatura Desarrollo de Aplicaciones Móviles Nativas.

La aplicación representa una tienda virtual de productos de audio y permite trabajar con tres tipos de usuario: comprador, vendedor y administrador.

## Funcionalidades principales

### Autenticación

La aplicación utiliza Firebase Authentication para:

- Registro de nuevos compradores.
- Inicio de sesión mediante correo y contraseña.
- Recuperación de contraseña.
- Cierre de sesión.
- Acceso según el rol del usuario.

También se implementó autenticación biométrica mediante las funciones biométricas de Android. Para utilizar esta función se requiere un dispositivo Android compatible y con biometría configurada.

## Roles

### Comprador

El comprador puede:

- Iniciar sesión.
- Consultar el catálogo de productos.
- Ver los detalles de cada producto.
- Seleccionar cantidad.
- Agregar productos al flujo de compra.
- Utilizar su ubicación para obtener una dirección de entrega.
- Editar manualmente la dirección antes de realizar el pedido.
- Realizar una compra.
- Cerrar sesión.

### Vendedor

El vendedor puede:

- Consultar los productos registrados.
- Crear productos.
- Editar productos.
- Eliminar productos.
- Consultar los pedidos realizados por los compradores.
- Actualizar el estado de un pedido.
- Marcar pedidos como enviados o entregados.
- Eliminar pedidos.
- Consultar indicadores de productos y pedidos.
- Cerrar sesión.

### Administrador

El administrador puede:

- Consultar usuarios.
- Crear y editar información de usuarios en Firestore.
- Eliminar usuarios de Firestore.
- Gestionar productos.
- Consultar indicadores generales.
- Consultar el reporte de ventas.
- Visualizar ingresos totales.
- Consultar número de pedidos y unidades vendidas.
- Consultar ventas por producto.
- Cerrar sesión.

## Firebase

El proyecto utiliza Firebase para las principales funciones de persistencia y autenticación.

### Firebase Authentication

Se utiliza para:

- Registro.
- Inicio de sesión.
- Recuperación de contraseña.
- Gestión de sesión.

### Cloud Firestore

Se utilizan las siguientes colecciones:

- `usuarios`
- `productos`
- `pedidos`

Firestore permite almacenar y actualizar la información utilizada por la aplicación.

Los paneles del vendedor y administrador, así como el reporte de ventas, consultan directamente estos datos.

## Geolocalización

La aplicación utiliza los servicios de ubicación de Android para obtener la ubicación actual del comprador.

La latitud y longitud obtenidas se convierten en una dirección legible mediante geocodificación inversa.

La dirección obtenida puede ser modificada manualmente por el comprador antes de confirmar el pedido.

En un dispositivo físico, la ubicación corresponde a la ubicación proporcionada por el propio teléfono, siempre que el usuario conceda los permisos correspondientes.

## Productos

Actualmente se utilizan como productos principales:

- RIATIGA Air Pro
- RIATIGA One

Los datos comerciales de los productos se almacenan en Firestore.

Las imágenes utilizadas por la aplicación se encuentran almacenadas localmente dentro del proyecto Android.

## Pedidos

Los pedidos se almacenan en Firestore e incluyen información como:

- Producto.
- Cantidad.
- Total.
- Comprador.
- Dirección de entrega.
- Estado.
- Fecha.

El vendedor puede consultar, actualizar y eliminar pedidos.

Los estados utilizados son:

- Preparando.
- Enviado.
- Entregado.

## Reporte de ventas

El administrador dispone de un reporte generado a partir de los pedidos almacenados en Firestore.

El reporte calcula automáticamente:

- Ingresos totales.
- Número de pedidos.
- Unidades vendidas.
- Unidades vendidas por producto.
- Valor de ventas por producto.

## Pago

El proyecto incluye el flujo completo de compra y registro del pedido.

La interfaz de pago actualmente funciona como una simulación académica y no realiza transacciones monetarias reales mediante una pasarela externa.

## Usuarios de prueba

### Comprador

Correo:

`comprador@riatiga.com`

Contraseña:

`123456`

### Vendedor

Correo:

`vendedor@riatiga.com`

Contraseña:

`123456`

### Administrador

Correo:

`admin@riatiga.com`

Contraseña:

`123456`

## Tecnologías utilizadas

- Android Studio.
- Kotlin.
- XML.
- Firebase Authentication.
- Cloud Firestore.
- Google Play Services Location.
- Android Biometric.
- Git.
- GitHub.

## Requisitos

Para ejecutar el proyecto se recomienda:

- Android Studio.
- Dispositivo o emulador Android compatible.
- Conexión a Internet para las funciones de Firebase.
- Permisos de ubicación para utilizar la geolocalización.
- Dispositivo Android compatible con biometría para probar el acceso biométrico.

## Estado del proyecto

Actualmente se encuentran implementados:

- Autenticación con Firebase.
- Registro de compradores.
- Recuperación de contraseña.
- Tres roles de usuario.
- CRUD de usuarios en Firestore.
- CRUD de productos.
- CRUD de pedidos.
- Flujo de compra.
- Geolocalización.
- Panel del vendedor con información de Firestore.
- Panel del administrador con información de Firestore.
- Reporte de ventas dinámico.
- Autenticación biométrica.

La pasarela de pago se mantiene como simulación y las imágenes de productos se administran localmente.

## Repositorio

Proyecto desarrollado con fines académicos para la asignatura Desarrollo de Aplicaciones Móviles Nativas.