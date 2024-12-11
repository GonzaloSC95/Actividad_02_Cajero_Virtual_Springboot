package com.unir.cajerovirtual.modelo.entidades;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//Anotaciones de Lombok
@NoArgsConstructor
@AllArgsConstructor
@Data // Esta anotación genera los métodos getter, setter, toString, equals y hashCode
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Esta anotación genera los métodos equals y hashCode solo para el atributo especificado
//Builder: Sirve para construir objetos de una clase sin necesidad de usar el constructor, con los atributos que se deseen
@Builder
// Anotaciones de JPA
@Entity
@Table(name = "prestamos")
public class Prestamo {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
   @Column(name = "id_prestamo")
   private int idPrestamo;

   private String descripcion;

   @Column(name = "cantidad_prestamo")
   private double cantidadPrestamo;

   @Column(name = "fecha_prestamo")
   private Date fechaPrestamo;

   @Column(name = "tasa_interes_anual")
   private double tasaInteresAnual;

   @Column(name = "plazo_meses")
   private int plazoMeses;

   @Column(name = "tipo_cuota")
   private String tipoCuota;

   @ManyToOne
   @JoinColumn(name = "id_cuenta")
   private Cuenta cuenta;

   @Column(name = "estado_cd")
   @Builder.Default // Esta anotación indica que el atributo se inicializa con el valor por defecto
   private String estadoCd = "TRAMITANDO";
}
