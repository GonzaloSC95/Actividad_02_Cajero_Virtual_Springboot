package com.unir.cajerovirtual.modelo.dao;

import com.unir.cajerovirtual.modelo.entidades.Prestamo;

public interface PrestamoDao {

	// Insertar un movimiento
	int insertPrestamo(Prestamo prestamo);

	// Eliminar un movimiento
	int deletePrestamo(Prestamo prestamo);

	// Encontrar prestamo
	Prestamo findByIdPrestamo(int idPrestamo);

	// Actualizar prestamo
	Prestamo updatePrestamo(Prestamo prestamo);

}
