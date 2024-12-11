package com.unir.cajerovirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/")
public class CuentaController {

	@Autowired
	private CuentaDao cuentaDao;

	@Autowired
	private MovimientoDao movimientoDao;

	@Autowired
	private PrestamoDao prestamoDao;

	@GetMapping("")
	public String login() {
		return "login";
	}

	@GetMapping("/index")
	public String index(HttpSession session, Model model) {
		Cuenta objCuenta = (Cuenta) session.getAttribute("objCuenta");
		if (objCuenta == null) {
			return "forward:/";
		}
		model.addAttribute("objCuenta", objCuenta);
		List<Movimiento> listaMovimientos = cuentaDao.ListFirst20MovimientosFromCuenta(objCuenta.getIdCuenta());
		model.addAttribute("listaMovimientos", listaMovimientos);
		// Evaluar concesión de préstamos
		evaluarConcesionPrestamos(objCuenta);
		return "index";
	}

	@PostMapping("/index")
	public String index(@RequestParam int IdCuenta, RedirectAttributes ratt, HttpSession session, Model model) {
		Cuenta objCuenta = cuentaDao.findByidCuenta(IdCuenta);
		if (objCuenta != null) {
			// Guardamos la cuenta en la sesión
			session.setAttribute("objCuenta", objCuenta);
			model.addAttribute("objCuenta", objCuenta);
			// Evaluar concesión de préstamos
			evaluarConcesionPrestamos(objCuenta);
			// Recuperamos los movimientos de la cuenta
			List<Movimiento> listaMovimientos = cuentaDao.ListFirst20MovimientosFromCuenta(IdCuenta);
			model.addAttribute("listaMovimientos", listaMovimientos);
			return "index";
		} else {
			ratt.addFlashAttribute("mensajeKo", "Nº de cuenta incorrecto");
			return "redirect:";
		}
	}

	@GetMapping("/exit")
	public String salir(HttpSession session) {
		session.removeAttribute("objCuenta");
		session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/verMovimientos")
	public String verMovimientos(HttpSession session, Model model) {
		Cuenta objCuenta = (Cuenta) session.getAttribute("objCuenta");
		if (objCuenta == null) {
			return "forward:/";
		}
		List<Movimiento> listaMovimientos = cuentaDao.ListMovimientosFromCuenta(objCuenta.getIdCuenta());
		model.addAttribute("objCuenta", objCuenta);
		model.addAttribute("listaMovimientos", listaMovimientos);
		return "movimientos";
	}

	@GetMapping("/verPrestamos")
	public String verPrestamos(HttpSession session, Model model) {
		Cuenta objCuenta = (Cuenta) session.getAttribute("objCuenta");
		if (objCuenta == null) {
			return "forward:/";
		}
		List<Prestamo> listaPrestamos = cuentaDao.ListPrestamosFromCuenta(objCuenta.getIdCuenta());
		model.addAttribute("objCuenta", objCuenta);
		model.addAttribute("listaPrestamos", listaPrestamos);
		return "prestamos";
	}

	// Métodos de negocio
	private void evaluarConcesionPrestamos(Cuenta objCuenta) {
		List<Prestamo> listaPrestamos = cuentaDao.ListPrestamosFromCuenta(objCuenta.getIdCuenta());
		for (Prestamo objPrestamo : listaPrestamos) {
			Movimiento objMovimiento = new Movimiento();
			if (!objPrestamo.getEstadoCd().equals("CONCEDIDO") && !objPrestamo.getEstadoCd().equals("DENEGADO")
					&& objCuenta.isPrestamoConcedido(objPrestamo, objMovimiento)) {
				// Actualizamos el saldo de la cuenta
				if (movimientoDao.insertMovimiento(objMovimiento) == 0) {
					cuentaDao.updateCuenta(objCuenta);
					objPrestamo.setEstadoCd("CONCEDIDO");
					prestamoDao.updatePrestamo(objPrestamo);
				}
			}
		}
	}
}
