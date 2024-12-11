package com.unir.cajerovirtual.configuration;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.unir.cajerovirtual.modelo.entidades.Prestamo;
import com.unir.cajerovirtual.modelo.entidades.TipoCuota;

@Configuration
public class BeansConfiguracionJava {

	/*
	 * Todos los Beans son Singleton por defecto, es decir, se crea una única instancia
	 * En el momento que se hace un @Autowired de un objeto, Spring busca la instancia de ese objeto
	 * en la configuracion de Spring y la inyecta en la clase que lo necesita
	 */
    @Bean
    Prestamo getCuenta() {
    	//Creación de un objeto de tipo Prestamo con el método builder de Lombok
		Prestamo prestamo = Prestamo.builder().descripcion("Prestamo 1").cantidadPrestamo(1000)
				.fechaPrestamo(new Date()).tasaInteresAnual(6).plazoMeses(34).tipoCuota(TipoCuota.FIJO).estadoCd("TRAMITANDO")
				.build();
    	//Creación de un objeto de tipo Prestamo
    	//Prestamo prestamo = new Prestamo(3,"Prestamo 1", 1000, new Date(), 6, 34, "FIJO", null, "TRAMITANDO");
    	//Retorno del objeto
		return prestamo;
	}

}
