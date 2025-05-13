package com.dam.dto;

import com.dam.entity.Bar;

public class BarDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String imagenUrl;

    public BarDTO(Bar bar) {
        this.id = bar.getId();
        this.nombre = bar.getNombre();
        this.direccion = bar.getDireccion();
        this.imagenUrl = bar.getImagenUrl();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}


}