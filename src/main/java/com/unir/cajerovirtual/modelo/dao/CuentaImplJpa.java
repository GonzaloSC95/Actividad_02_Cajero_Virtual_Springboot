package com.unir.cajerovirtual.modelo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.unir.cajerovirtual.modelo.entidades.Cuenta;
import com.unir.cajerovirtual.modelo.entidades.Movimiento;
import com.unir.cajerovirtual.modelo.repository.CuentaRepository;

@Repository
public class CuentaImplJpa implements CuentaDao {

	@Autowired
	private CuentaRepository cuentaRepository;

	@Override
	public Cuenta findByidCuenta(int idCuenta) {
		return cuentaRepository.findById(idCuenta).orElse(null);
	}

	@Override
	public List<Movimiento> ListMovimientosFromCuenta(int idCuenta) {
		return cuentaRepository.listMovimientosFromCuenta(idCuenta, Pageable.unpaged());
	}

	@Override
	public List<Movimiento> ListFirst20MovimientosFromCuenta(int idCuenta) {
		return cuentaRepository.listMovimientosFromCuenta(idCuenta, PageRequest.of(0, 20));
	}

	@Override
	public List<Movimiento> ListFromCuentaByTipoMovimiento(int idCuenta, String tipoMovimiento) {
		return cuentaRepository.listFromCuentaByTipoMovimiento(idCuenta, tipoMovimiento);
	}

	@Override
	public Double TotalFromCuentaByTipoMovimiento(int idCuenta, String tipoMovimiento) {
		return cuentaRepository.totalMovimientosFromCuentaByTipoMovimiento(idCuenta, tipoMovimiento);
	}

	@Override
	public Cuenta updateCuenta(Cuenta objCuenta) {
		try {
			Cuenta cuenta = cuentaRepository.findById(objCuenta.getIdCuenta()).orElse(null);
			if (cuenta != null) {
				cuenta.setSaldo(objCuenta.getSaldo());
				return cuentaRepository.save(cuenta);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}
