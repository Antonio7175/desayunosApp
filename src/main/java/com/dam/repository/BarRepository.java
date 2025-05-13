package com.dam.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dam.entity.Bar;

@Repository
public interface BarRepository extends JpaRepository<Bar, Long> {
	 @EntityGraph(attributePaths = "desayunos") // Para traer los desayunos junto con el bar
	    Optional<Bar> findById(Long id);
}

