package com.dam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dam.entity.ComandaItem;

public interface ComandaItemRepository extends JpaRepository<ComandaItem, Long> {
    List<ComandaItem> findByComandaCodigoUnico(String codigoUnico);
}
