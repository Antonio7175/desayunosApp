package com.dam.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dam.entity.Desayuno;
import com.dam.entity.EstadoPedido;
import com.dam.entity.Pedido;
import com.dam.entity.Usuario;
import com.dam.repository.DesayunoRepository;
import com.dam.repository.PedidoRepository;
import com.dam.repository.UsuarioRepository;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DesayunoRepository desayunoRepository;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, DesayunoRepository desayunoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.desayunoRepository = desayunoRepository;
    }

	public List<Pedido> getAllPedidos() {
	    return pedidoRepository.findByEstado(EstadoPedido.PENDIENTE);
	}


    public void acceptPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(EstadoPedido.ACEPTADO); // Usamos el enum aquí
        pedidoRepository.save(pedido);
    }
    
    public void createPedido(Long usuarioId, Long barId, Long desayunoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Desayuno desayuno = desayunoRepository.findById(desayunoId)
                .orElseThrow(() -> new RuntimeException("Desayuno no encontrado"));

        // Se crea el pedido con estado "PENDIENTE" por defecto
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDesayuno(desayuno);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        pedidoRepository.save(pedido);
    }
    
 // PedidoService.java - Método para obtener pedidos de un usuario
    public List<Pedido> obtenerPedidosPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }
    
 // PedidoService.java - Método para cancelar pedido si está "PENDIENTE"
    public boolean cancelarPedido(Long id, Usuario usuario) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            if (pedido.getUsuario().equals(usuario) && pedido.getEstado() == EstadoPedido.PENDIENTE) {
                pedido.setEstado(EstadoPedido.CANCELADO);
                pedido.setCanceladoPorAdmin(false); // ⬅️ MUY IMPORTANTE
                pedidoRepository.save(pedido);
                return true;
            }
        }
        return false;
    }
    public void rechazarPedidoComoAdmin(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() == EstadoPedido.PENDIENTE) {
            pedido.setEstado(EstadoPedido.CANCELADO);
            pedido.setCanceladoPorAdmin(true); // ⬅️ IMPORTANTE
            pedidoRepository.save(pedido);
        } else {
            throw new IllegalStateException("El pedido no está en estado PENDIENTE.");
        }
    }
    
    
    public void eliminarPedidosDeUsuario(Usuario usuario) {
        List<Pedido> pedidos = pedidoRepository.findByUsuario(usuario);

        pedidos.stream()
            .filter(p -> p.getEstado() == EstadoPedido.CANCELADO || p.getEstado() == EstadoPedido.ACEPTADO)
            .forEach(pedidoRepository::delete);
    }



}
