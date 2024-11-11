package com.unir.cajerovirtual.modelo.entidades;

import java.io.Serializable;

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

	public void reCalcularSaldo(Movimiento objMovimiento) {
		if ("INGRESO".equals(objMovimiento.getOperacion())) {
			this.saldo += objMovimiento.getCantidad();
		} else if ("EXTRACCION".equals(objMovimiento.getOperacion())) {
			this.saldo -= objMovimiento.getCantidad();
		}
	}

}
