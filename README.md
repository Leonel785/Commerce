# NOVA — Mini Sistema E-Commerce

Proyecto académico funcional basado en el modelo obligatorio:

`Usuario · Cliente · Producto · Carrito · DetalleCarrito · Pedido · DetallePedido`

Incluye Spring Boot, JPA, MySQL, HttpSession, API REST y frontend HTML/CSS/JavaScript con Fetch API.

## Requisitos

- Java 17+
- Maven 3.9+
- MySQL 8+

## Ejecución rápida

1. Crea la base de datos:

```bash
mysql -u root -p < database/schema.sql
```

2. Configura las variables opcionales si tu MySQL no usa los valores por defecto:

```bash
export DB_URL='jdbc:mysql://localhost:3306/ecommerce?createDatabaseIfNotExist=true&serverTimezone=UTC'
export DB_USERNAME='root'
export DB_PASSWORD='tu_clave'
```

3. Arranca:

```bash
mvn spring-boot:run
```

Abre http://localhost:8080

## Cuentas demo

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Cliente | `cliente` | `cliente123` |

Las contraseñas son simples únicamente para la demostración académica inicial solicitada. Para producción se debe incorporar un proveedor de autenticación y hashing.

## API principal

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`
- `GET /api/productos`
- `GET /api/productos/{id}`
- `POST /api/productos` — ADMIN
- `PUT /api/productos/{id}` — ADMIN
- `DELETE /api/productos/{id}` — ADMIN
- `GET /api/carrito` — CLIENTE
- `POST /api/carrito` — CLIENTE
- `PUT /api/carrito/detalle/{id}` — CLIENTE
- `DELETE /api/carrito/detalle/{id}` — CLIENTE
- `DELETE /api/carrito` — CLIENTE
- `POST /api/pedidos/confirmar` — CLIENTE
- `GET /api/pedidos` — CLIENTE

## Flujo para Postman

1. Ejecuta `POST /api/auth/login` con `{"username":"cliente","password":"cliente123"}` y conserva la cookie `JSESSIONID`.
2. Consulta productos.
3. Agrega un producto con `POST /api/carrito` y `{"productoId":1,"cantidad":1}`.
4. Consulta y actualiza el carrito.
5. Confirma con `POST /api/pedidos/confirmar`.
6. Consulta el pedido y verifica que el stock haya disminuido.

## Arquitectura

`Controller → Service → Repository → MySQL`

Los cálculos de subtotal, total y la validación de stock se realizan en el backend. El detalle de pedido conserva el precio unitario vigente al momento de confirmar la compra.