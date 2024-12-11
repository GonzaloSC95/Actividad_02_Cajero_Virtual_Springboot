package com.unir.cajerovirtual.modelo.entidades;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//Anotaciones de Lombok
@NoArgsConstructor
@AllArgsConstructor
@Data // Esta anotación genera los métodos getter, setter, toString, equals y hashCode
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Esta anotación genera los métodos equals y hashCode solo para el
																	// atributo idFamilia)
// Anotaciones de JPA
@Entity
@Table(name = "cuentas")
public class Cuenta implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@EqualsAndHashCode.Include
	@Column(name = "id_cuenta")
	private int idCuenta;
	private double saldo;
	@Column(name = "tipo_cuenta")
	private String tipoCuenta;

	// Métodos
	public void reCalcularSaldo(Movimiento objMovimiento) {
		if ("INGRESO".equals(objMovimiento.getOperacion())) {
			this.saldo += objMovimiento.getCantidad();
		} else if ("EXTRACCION".equals(objMovimiento.getOperacion())) {
			this.saldo -= objMovimiento.getCantidad();
		}
	}

	public boolean isPrestamoConcedido(Prestamo objPrestamo, Movimiento objMovimiento) {
		// Obtener la fecha actual
		LocalDate fechaActual = LocalDate.now();
		// Convertir la fecha de bjPrestamo a LocalDate
		Date fechaPrestamo = objPrestamo.getFechaPrestamo();
		LocalDate fechaPrestamoLocalDate = fechaPrestamo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		// Si la fecha actual es mayor que la fecha del préstamo
		if (fechaActual.isAfter(fechaPrestamoLocalDate)) {
			objMovimiento.setCuenta(objPrestamo.getCuenta());
			objMovimiento.setCantidad(objPrestamo.getCantidadPrestamo());
			objMovimiento.setOperacion("INGRESO");
			reCalcularSaldo(objMovimiento);
			return true;
		}
		return false;
	}
}
