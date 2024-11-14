package com.unir.cajerovirtual.modelo.dao;

import java.util.List;

import com.unir.cajerovirtual.modelo.entidades.Cuenta;
import com.unir.cajerovirtual.modelo.entidades.Movimiento;

public interface CuentaDao {

	/// Login de la cuenta
	Cuenta findByidCuenta(int idCuenta);

	// Actualizar saldo de la cuenta
	Cuenta updateCuenta(Cuenta idCuenta);

	// Movimientos de la cuenta
	List<Movimiento> ListFirst20MovimientosFromCuenta(int idCuenta);

	// Movimientos de la cuenta
	List<Movimiento> ListMovimientosFromCuenta(int idCuenta);

	// Movimientos de la cuenta por tipo de movimiento
	List<Movimiento> ListFromCuentaByTipoMovimiento(int idCuenta, String tipoMovimiento);

	// Total ingresos o gastos de la cuenta
	Double TotalFromCuentaByTipoMovimiento(int idCuenta, String tipoMovimiento);

	// Todas las cuentas menos la de la sesión
	List<Cuenta> getAllCuentasNotme(int idCuenta);

}
