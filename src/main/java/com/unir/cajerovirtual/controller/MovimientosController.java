package com.unir.cajerovirtual.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unir.cajerovirtual.modelo.dao.CuentaDao;
import com.unir.cajerovirtual.modelo.dao.MovimientoDao;
import com.unir.cajerovirtual.modelo.entidades.Cuenta;
import com.unir.cajerovirtual.modelo.entidades.Movimiento;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/movimientos")
public class MovimientosController {

	@Autowired
	private MovimientoDao movimientoDao;
	@Autowired
	private CuentaDao cuentaDao;

	@GetMapping("/ingresar")
	public String ingresar(Model model) {
		Movimiento objMovimiento = new Movimiento();
		objMovimiento.setOperacion("INGRESO");
		model.addAttribute("objMovimiento", objMovimiento);
		return "alta_movimiento_ingresar";
	}

	@PostMapping("/ingresar")
	public String ingresar(@ModelAttribute Movimiento objMovimiento, RedirectAttributes ratt, HttpSession session) {
		// Comprobamos que la cantidad sea mayor que 0
		if (objMovimiento.getCantidad() <= 0) {
			ratt.addFlashAttribute("mensajeKo", "El importe debe ser mayor que 0");
			return "redirect:/movimientos/ingresar";
		}
		// Recuperamos la cuenta del usuario logueado
		Cuenta objCuenta = (Cuenta) session.getAttribute("objCuenta");
		if (objCuenta == null) {
			ratt.addFlashAttribute("mensajeKo", "No se ha podido recuperar la cuenta");
			return "redirect:/movimientos/ingresar";
		} else {

			// Creamos el objeto movimiento y lo añadimos a la cuenta
			objMovimiento.setCuenta(objCuenta);
			int result = movimientoDao.insertMovimiento(objMovimiento);
			if (result == 0) {
				ratt.addFlashAttribute("mensajeKo", "No se ha podido realizar el ingreso");
				return "redirect:/movimientos/ingresar";
			}

			// Actualizamos el saldo de la cuenta
			objCuenta.reCalcularSaldo(objMovimiento);
			Cuenta updateObjCuenta = cuentaDao.updateCuenta(objCuenta);
			if (updateObjCuenta == null) {
				ratt.addFlashAttribute("mensajeKo", "No se ha podido actualizar la cuenta");
				return "redirect:/movimientos/ingresar";
			}
			session.setAttribute("objCuenta", updateObjCuenta);

			// Mostramos un mensaje de éxito
			ratt.addFlashAttribute("mensajeOk", "Ingreso realizado correctamente");
		}
		return "redirect:/movimientos/ingresar";
	}

	@GetMapping("/extraer")
	public String extraer(Model model) {
		Movimiento objMovimiento = new Movimiento();
		objMovimiento.setOperacion("EXTRACCION");
		model.addAttribute("objMovimiento", objMovimiento);
		return "alta_movimiento_extraer";
	}

	@PostMapping("/extraer")
	public String extraer(@ModelAttribute Movimiento objMovimiento, RedirectAttributes ratt, HttpSession session,
			Model model) {
		// Comprobamos que la cantidad sea mayor que 0
		if (objMovimiento.getCantidad() <= 0) {
			ratt.addFlashAttribute("mensajeKo", "El importe debe ser mayor que 0");
			return "redirect:/movimientos/extraer";
		}
		// Recuperamos la cuenta del usuario logueado
		Cuenta objCuenta = (Cuenta) session.getAttribute("objCuenta");
		if (objCuenta == null) {
			ratt.addFlashAttribute("mensajeKo", "No se ha podido recuperar la cuenta");
			return "redirect:/movimientos/extraer";
		} else {
			// Comprobamos que hay saldo suficiente
			double backUpsaldoActual = objCuenta.getSaldo();
			objCuenta.reCalcularSaldo(objMovimiento);
			if (objCuenta.getSaldo() < 0) {
				objCuenta.setSaldo(backUpsaldoActual);
				ratt.addFlashAttribute("mensajeKo", "No hay suficiente saldo en la cuenta");
				return "redirect:/movimientos/extraer";
			}

			// Creamos el objeto movimiento y lo añadimos a la cuenta
			objMovimiento.setCuenta(objCuenta);
			int result = movimientoDao.insertMovimiento(objMovimiento);
			if (result == 0) {
				ratt.addFlashAttribute("mensajeKo", "No se ha podido realizar la extraccion");
				return "redirect:/movimientos/extraer";
			}

			// Actualizamos el saldo de la cuenta
			Cuenta updateObjCuenta = cuentaDao.updateCuenta(objCuenta);
			if (updateObjCuenta == null) {
				ratt.addFlashAttribute("mensajeKo", "No se ha podido actualizar la cuenta");
				return "redirect:/movimientos/extraer";
			}
			session.setAttribute("objCuenta", updateObjCuenta);

			// Mostramos un mensaje de éxito
			ratt.addFlashAttribute("mensajeOk", "Extraccion realizada correctamente");
		}
		return "redirect:/movimientos/extraer";
	}

	@GetMapping("/transferencia")
	public String transferencia(HttpSession session, Model model) {
		Movimiento objMovimiento = new Movimiento();
		Cuenta objCuenta = (Cuenta) session.getAttribute("objCuenta");
		List<Cuenta> listaCuentasDestino = cuentaDao.getAllCuentasNotme(objCuenta.getIdCuenta());
		model.addAttribute("listaCuentasDestino", listaCuentasDestino);
		model.addAttribute("objMovimiento", objMovimiento);
		return "alta_movimiento_transferencia";
	}

	@PostMapping("/transferencia")
	@Transactional
	public String transferencia(@ModelAttribute Movimiento objMovimiento, RedirectAttributes ratt, HttpSession session,
			Model model) {
		try {
			// Comprobamos que la cantidad sea mayor que 0
			if (objMovimiento.getCantidad() <= 0) {
				ratt.addFlashAttribute("mensajeKo", "El importe debe ser mayor que 0");
				return "redirect:/movimientos/transferencia";
			}
			// Recuperamos la cuenta de origen del usuario logueado, para la transferencia
			Cuenta objCuentaOrigen = (Cuenta) session.getAttribute("objCuenta");
			if (objCuentaOrigen == null) {
				ratt.addFlashAttribute("mensajeKo", "No se ha podido recuperar la cuenta");
				return "redirect:/movimientos/transferencia";
			}
			// Recuperamos la cuenta de destino para la trasnferencia
			Cuenta objCuentaDestino = cuentaDao.findByidCuenta(objMovimiento.getCuentaDestinoId());
			if (objCuentaDestino == null) {
				ratt.addFlashAttribute("mensajeKo", "No se ha podido recuperar la cuenta de destino");
				return "redirect:/movimientos/transferencia";
			} else {
				// Comprobamos que hay saldo suficiente en la cuenta de ORIGEN
				double backUpsaldoActual = objCuentaOrigen.getSaldo();
				objMovimiento.setOperacion("EXTRACCION");
				objCuentaOrigen.reCalcularSaldo(objMovimiento);
				if (objCuentaOrigen.getSaldo() < 0) {
					objCuentaOrigen.setSaldo(backUpsaldoActual);
					ratt.addFlashAttribute("mensajeKo", "No hay suficiente saldo en la cuenta");
					return "redirect:/movimientos/transferencia";
				}

				// Creamos el objeto movimiento y lo añadimos a la cuenta de ORIGEN
				Movimiento objMovimientoOrigen = new Movimiento();
				objMovimientoOrigen.setCuenta(objCuentaOrigen);
				objMovimientoOrigen.copyFromTransferenciaExtraccion(objMovimiento);
				// Creamos el objeto movimiento y lo añadimos a la cuenta de DESTINO
				Movimiento objMovimientoDestino = new Movimiento();
				objMovimiento.setOperacion("INGRESO");
				objCuentaDestino.reCalcularSaldo(objMovimiento);
				objMovimientoDestino.setCuenta(objCuentaDestino);
				objMovimientoDestino.copyFromTransferenciaIngreso(objMovimiento);

				// Creamos el objeto movimiento y lo añadimos a la cuenta
				int resultOrigen = movimientoDao.insertMovimiento(objMovimientoOrigen);
				int resultDestino = movimientoDao.insertMovimiento(objMovimientoDestino);
				if (resultOrigen == 0 || resultDestino == 0) {
					throw new RuntimeException("Error al insertar los movimientos");
				}

				// Actualizamos el saldo de laS cuentas
				Cuenta updateObjCuentaOrigen = cuentaDao.updateCuenta(objCuentaOrigen);
				if (updateObjCuentaOrigen == null || cuentaDao.updateCuenta(objCuentaDestino) == null) {
					objCuentaOrigen.setSaldo(backUpsaldoActual);
					throw new RuntimeException("Error al actualizar las cuentas");
				}
				session.setAttribute("objCuenta", updateObjCuentaOrigen);

				// Mostramos un mensaje de éxito
				ratt.addFlashAttribute("mensajeOk", "Transferencia realizada correctamente");
			}
		} catch (RuntimeException e) {
			ratt.addFlashAttribute("mensajeKo", "No se ha podido realizar la transferencia");
		}
		return "redirect:/movimientos/transferencia";
	}

	/** Este método sirve para que Spring sepa cómo convertir los datos del form */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateformat, false));
	}

}
