package com.unir.cajerovirtual.modelo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.unir.cajerovirtual.modelo.entidades.Movimiento;
import com.unir.cajerovirtual.modelo.repository.MovimientoRepository;

@Repository
public class MovimientoImpJpa implements MovimientoDao{

	@Autowired
	private MovimientoRepository movimientoRepository;
	
	@Override
	public int insertMovimiento(Movimiento movimiento) {
		try {
			movimientoRepository.save(movimiento);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}

}
