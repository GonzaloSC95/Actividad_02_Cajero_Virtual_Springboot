package com.unir.cajerovirtual.modelo.dao;

import com.unir.cajerovirtual.modelo.entidades.Movimiento;

public interface MovimientoDao {

	// Insertar un movimiento
	int insertMovimiento(Movimiento movimiento);

	// Eliminar un movimiento
	int deleteMovimiento(Movimiento movimiento);

}
