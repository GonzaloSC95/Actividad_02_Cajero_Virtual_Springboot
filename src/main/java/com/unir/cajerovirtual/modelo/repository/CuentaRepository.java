package com.unir.cajerovirtual.modelo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.unir.cajerovirtual.modelo.entidades.Cuenta;
import com.unir.cajerovirtual.modelo.entidades.Movimiento;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

	// Login de la cuenta
	Cuenta findByidCuenta(int idCuenta);

	// Movimientos de la cuenta
	@Query("SELECT m FROM Movimiento m WHERE m.cuenta.idCuenta = :idCuenta ORDER BY m.fecha DESC")
	List<Movimiento> listMovimientosFromCuenta(@Param("idCuenta") int idCuenta, Pageable pageable);

	// Movimientos de la cuenta por tipo de movimiento
	@Query("SELECT m FROM Movimiento m WHERE m.cuenta.idCuenta = :idCuenta AND m.operacion = :tipoMovimiento")
	List<Movimiento> listFromCuentaByTipoMovimiento(@Param("idCuenta") int idCuenta,
			@Param("tipoMovimiento") String tipoMovimiento);

	// Total movimientos de la cuenta por tipo de movimiento
	@Query("SELECT COALESCE(SUM(m.cantidad), 0) FROM Movimiento m WHERE m.cuenta.idCuenta = :idCuenta AND m.operacion = :tipoMovimiento")
	Double totalMovimientosFromCuentaByTipoMovimiento(@Param("idCuenta") int idCuenta,
			@Param("tipoMovimiento") String tipoMovimiento);
}
