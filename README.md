# RIATIGA Audio

RIATIGA Audio es una aplicación móvil desarrollada en Android Studio con Kotlin para la asignatura Desarrollo de Aplicaciones Móviles Nativas.

La aplicación funciona como una tienda de audífonos y cuenta con tres tipos de usuario: comprador, vendedor y administrador.

## Usuarios de prueba

### Comprador

Correo: `comprador@riatiga.com`  
Contraseña: `123456`

### Vendedor

Correo: `vendedor@riatiga.com`  
Contraseña: `123456`

### Administrador

Correo: `admin@riatiga.com`  
Contraseña: `123456`

## ¿Qué puede hacer cada usuario?

El comprador puede ingresar a la aplicación, consultar los productos, ver sus detalles, seleccionar cantidades, realizar una compra y usar su ubicación para completar la dirección de entrega.

El vendedor puede gestionar los productos, revisar los pedidos realizados por los compradores y cambiar su estado entre Preparando, Enviado y Entregado.

El administrador puede gestionar usuarios y productos, además de consultar un reporte con los pedidos realizados, las unidades vendidas y los ingresos registrados.

## Funciones principales

La aplicación cuenta con inicio de sesión, registro de compradores, recuperación de contraseña y cierre de sesión mediante Firebase.

También permite crear, consultar, editar y eliminar usuarios, productos y pedidos.

La información se guarda en Cloud Firestore, por lo que los paneles del vendedor y del administrador se actualizan con los datos registrados en la aplicación.

El comprador también puede usar su ubicación para obtener una dirección de entrega y editarla si necesita hacer algún ajuste antes de realizar el pedido.

Además, se implementó acceso biométrico para dispositivos Android compatibles.

## Productos

Actualmente la aplicación trabaja con:

- RIATIGA Air Pro
- RIATIGA One

Las imágenes de los productos están guardadas localmente dentro de la aplicación.

## Pago

El flujo de compra funciona y los pedidos se guardan en Firebase.

El pago es simulado y no realiza cobros reales.

## Tecnologías utilizadas

Android Studio, Kotlin, XML, Firebase Authentication, Cloud Firestore, geolocalización de Android y autenticación biométrica.