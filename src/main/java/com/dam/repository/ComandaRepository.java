package com.dam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.entity.Comanda;
import com.dam.entity.EstadoComanda;
import com.dam.entity.Usuario;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    Optional<Comanda> findByCodigoUnico(String codigoUnico);

	List<Comanda> findByBarPropietarioAndEstado(Usuario admin, EstadoComanda enviada);
	
	List<Comanda> findAllByOrderByFechaCreacionDesc(); // Para el ADMIN GENERAL
	List<Comanda> findByAdminOrderByFechaCreacionDesc(Usuario admin);

}
