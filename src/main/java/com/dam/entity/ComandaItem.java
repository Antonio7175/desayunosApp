package com.dam.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ComandaItem {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Comanda comanda;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Desayuno desayuno;

    @Column(nullable = false)
    private String nombreInvitado;

    private boolean autorizado = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Comanda getComanda() {
		return comanda;
	}

	public void setComanda(Comanda comanda) {
		this.comanda = comanda;
	}

	public Desayuno getDesayuno() {
		return desayuno;
	}

	public void setDesayuno(Desayuno desayuno) {
		this.desayuno = desayuno;
	}

	public String getNombreInvitado() {
		return nombreInvitado;
	}

	public void setNombreInvitado(String nombreInvitado) {
		this.nombreInvitado = nombreInvitado;
	}

	public boolean isAutorizado() {
		return autorizado;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

    
}
