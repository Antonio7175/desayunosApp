package com.dam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.dam.dto.PedidoRequest;
import com.dam.entity.Pedido;
import com.dam.entity.Usuario;
import com.dam.service.PedidoService;
import com.dam.service.UsuarioService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
	private final PedidoService pedidoService;
	@Autowired
	private UsuarioService usuarioService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}
	
	// PedidoController.java - Agregando endpoint para obtener pedidos del usuario
	@GetMapping("/mis-pedidos")
	public ResponseEntity<List<Pedido>> obtenerMisPedidos(Authentication authentication) {
	    Usuario usuario = usuarioService.findByEmail(authentication.getName());
	    if (usuario == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }

	    List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(usuario);
	    return ResponseEntity.ok(pedidos);
	}
	
	// PedidoController.java - Agregando endpoint para cancelar pedido si está en "PENDIENTE"
	@PutMapping("/{id}/cancelar")
	public ResponseEntity<String> cancelarPedido(@PathVariable Long id, Authentication authentication) {
	    Usuario usuario = usuarioService.findByEmail(authentication.getName());
	    if (usuario == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	    }

	    boolean cancelado = pedidoService.cancelarPedido(id, usuario);
	    if (cancelado) {
	        return ResponseEntity.ok("Pedido cancelado correctamente");
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se puede cancelar el pedido");
	    }
	}

	@PostMapping
	public ResponseEntity<?> createPedido(@RequestBody PedidoRequest pedidoRequest) {
		pedidoService.createPedido(pedidoRequest.getUsuarioId(), pedidoRequest.getBarId(),
				pedidoRequest.getDesayunoId());
		return ResponseEntity.ok("Pedido creado");
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Pedido>> getAllPedidos() {
	    return ResponseEntity.ok(pedidoService.getAllPedidos());
	}


	@PostMapping("/{pedidoId}/accept")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> acceptPedido(@PathVariable Long pedidoId) {
		pedidoService.acceptPedido(pedidoId);
		return ResponseEntity.ok("Pedido aceptado");
	}
}
