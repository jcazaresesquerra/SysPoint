SELECT v.venta, c.cuenta, c.nombre_comercial, pr.articulo, pr.descripcion, p.precio, 
SUM(p.cantidad) As Cantidad, 
SUM(p.precio * p.cantidad) As Total, 
e.identificador, e.nombre, v.latitud, v.longitud  
FROM partidas As p 
INNER JOIN ventas As v ON p.venta = v.venta  
INNER JOIN clientes As c ON c.id = v.clientes_id
INNER JOIN productos As pr ON pr.id = p.productos_id
INNER JOIN empleados As e on e.id = v.empleados_id
GROUP BY v.venta, c.cuenta, c.nombre_comercial, pr.articulo, pr.descripcion, p.precio, e.identificador, e.nombre, v.latitud, v.longitud 

