package com.unir.cajerovirtual.modelo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.unir.cajerovirtual.modelo.entidades.Prestamo;
import com.unir.cajerovirtual.modelo.repository.PrestamoRepository;

@Repository
public class PrestamoImpJpa implements PrestamoDao {

	@Autowired
	private PrestamoRepository prestamoRepository;

	@Override
	public int insertPrestamo(Prestamo prestamo) {
		try {
			prestamoRepository.save(prestamo);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int deletePrestamo(Prestamo prestamo) {
		try {
			prestamoRepository.delete(prestamo);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public Prestamo findByIdPrestamo(int idPrestamo) {
		return prestamoRepository.findById(idPrestamo).orElse(null);
	}

	@Override
	public Prestamo updatePrestamo(Prestamo prestamo) {
		try {
			Prestamo objPrestamo = prestamoRepository.findById(prestamo.getIdPrestamo()).orElse(null);
			if (objPrestamo != null) {
				return prestamoRepository.save(objPrestamo);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}
