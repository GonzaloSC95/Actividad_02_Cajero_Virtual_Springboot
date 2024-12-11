package com.unir.cajerovirtual.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unir.cajerovirtual.modelo.dao.CuentaDao;
import com.unir.cajerovirtual.modelo.dao.MovimientoDao;
import com.unir.cajerovirtual.modelo.dao.PrestamoDao;
import com.unir.cajerovirtual.modelo.entidades.Cuenta;
import com.unir.cajerovirtual.modelo.entidades.Movimiento;
import com.unir.cajerovirtual.modelo.entidades.Prestamo;

import jakarta.servlet.http.HttpSession;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/prestamos")
public class PrestamoController {

	@Autowired
	/* Esto me recoge el objeto prestamo inicializado en configuration/BeansConfiguracionJava.java*/
	private Prestamo beanPrestamo;
	
	@Autowired
	private PrestamoDao prestamoDao;

	@Autowired
	private MovimientoDao movimientoDao;

	@Autowired
	private CuentaDao cuentaDao;

	@GetMapping("/crear")
	public String crearPrestamoForm(Model model, HttpSession session) {
		// Testeo del bean
		System.out.println("beanPrestamo: " + beanPrestamo);
		//Codigo de la aplicación
		// Recuperamos la cuenta de la sesión
		Cuenta obCuenta = (Cuenta) session.getAttribute("objCuenta");
		// Creamos un objeto de tipo préstamo y le asignamos la cuenta
		Prestamo objprestamo = new Prestamo();
		objprestamo.setCuenta(obCuenta);
		// Añadimos el objeto al modelo
		model.addAttribute("objprestamo", objprestamo);
		return "alta_prestamo";
	}

	@PostMapping("/crear")
	public String crearPrestamo(@ModelAttribute Prestamo objprestamo, RedirectAttributes ratt,
			Model model, HttpSession session) {
		int result = prestamoDao.insertPrestamo(objprestamo);
		if (result == 0) {
			ratt.addFlashAttribute("mensajeKo", "No se ha podido crear el préstamo");
			ratt.addFlashAttribute("objprestamo", objprestamo); // Añadir el objeto al modelo en caso de error
			return "redirect:/prestamos/crear";
		} else {
			Cuenta objCuenta = (Cuenta) session.getAttribute("objCuenta");
			Movimiento objMovimiento = new Movimiento();
			System.out.println("objprestamo: " + objprestamo);
			if (objCuenta.isPrestamoConcedido(objprestamo, objMovimiento)) {
				// Actualizamos el saldo de la cuenta
				result = movimientoDao.insertMovimiento(objMovimiento);
				System.out.println("result: " + result);
				if (result == 1) {
					System.out.println("Actualizamos el saldo de la cuenta: " + objprestamo.getEstadoCd());
					cuentaDao.updateCuenta(objCuenta);
					objprestamo.setEstadoCd("CONCEDIDO");
					prestamoDao.updatePrestamo(objprestamo);
				}
			}
			ratt.addFlashAttribute("mensajeOk", "Préstamo solicitado correctamente");
			return "redirect:/prestamos/crear"; // Redirigir a la lista de préstamos después de la creación exitosa
		}
	}

	@GetMapping("/borrar/{idPrestamo}")
	public String borrarPrestamoForm(@PathVariable int idPrestamo, RedirectAttributes ratt, HttpSession session) {
		// Comprobamos que el préstamo existe
		Prestamo objprestamo = prestamoDao.findByIdPrestamo(idPrestamo);
		if (objprestamo == null) {
			ratt.addFlashAttribute("mensajeKo", "No se ha encontrado el préstamo");
			return "redirect:/verPrestamos";
		} else if (objprestamo.getEstadoCd().equals("CONCEDIDO")) {
			ratt.addFlashAttribute("mensajeKo", "No se ha podido cancelar el préstamo porque ya ha sido concedido");
			return "redirect:/verPrestamos";
		} else {
			int result = prestamoDao.deletePrestamo(objprestamo);
			if (result == 0) {
				ratt.addFlashAttribute("mensajeKo", "No se ha podido cancelar el préstamo");
			} else {
				ratt.addFlashAttribute("mensajeOk", "Préstamo cancelado correctamente");
			}
			return "redirect:/verPrestamos";
		}
	}

	/** Este método sirve para que Spring sepa cómo convertir los datos del form */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateformat, false));
	}

}
