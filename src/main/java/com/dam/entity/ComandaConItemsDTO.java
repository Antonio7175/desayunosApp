package com.dam.entity;


import com.dam.entity.Comanda;
import com.dam.entity.ComandaItem;

import java.util.List;

public class ComandaConItemsDTO {
    private Comanda comanda;
    private List<ComandaItem> items;

    public ComandaConItemsDTO(Comanda comanda, List<ComandaItem> items) {
        this.comanda = comanda;
        this.items = items;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public List<ComandaItem> getItems() {
        return items;
    }

    public void setItems(List<ComandaItem> items) {
        this.items = items;
    }
}

