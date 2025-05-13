package com.dam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dam.entity.Desayuno;



@Repository
public interface DesayunoRepository extends JpaRepository<Desayuno, Long> {
}
