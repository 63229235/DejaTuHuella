-- Script para insertar datos de ejemplo en las tablas del carrito

-- Insertar un carrito para el usuario con ID 1 (ajustar según los IDs de usuarios existentes)
INSERT INTO carritos (usuario_id) 
VALUES (1) 
ON DUPLICATE KEY UPDATE usuario_id = usuario_id;

-- Insertar un producto en el carrito (ajustar según los IDs de productos existentes)
-- Esto asume que el carrito con ID 1 existe y el producto con ID 1 existe
INSERT INTO carrito_items (carrito_id, producto_id, cantidad, precio_unitario) 
VALUES (1, 1, 2, 29.99) 
ON DUPLICATE KEY UPDATE cantidad = 2, precio_unitario = 29.99;