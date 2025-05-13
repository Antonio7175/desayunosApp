package com.dam.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dam.entity.EstadoPedido;
import com.dam.entity.Pedido;
import com.dam.entity.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findByEstado(EstadoPedido estado);
	 List<Pedido> findByUsuario(Usuario usuario);
   
}

