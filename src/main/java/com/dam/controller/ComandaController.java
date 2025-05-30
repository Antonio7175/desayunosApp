package com.dam.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.entity.Comanda;
import com.dam.entity.ComandaConItemsDTO;
import com.dam.entity.ComandaCreacionDTO;
import com.dam.entity.ComandaItem;
import com.dam.entity.Desayuno;
import com.dam.entity.EstadoComanda;
import com.dam.entity.ItemDTO;
import com.dam.entity.Usuario;
import com.dam.repository.ComandaItemRepository;
import com.dam.repository.ComandaRepository;
import com.dam.repository.DesayunoRepository;
import com.dam.repository.UsuarioRepository;
import com.dam.security.JwtTokenUtil;

@RestController
@RequestMapping("/api/comandas")
public class ComandaController {

    @Autowired
    private ComandaRepository comandaRepo;

    @Autowired
    private ComandaItemRepository itemRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private DesayunoRepository desayunoRepo;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Crear nueva comanda (solo para usuarios autenticados)
    @PostMapping
    public ResponseEntity<?> crearComanda(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody ComandaCreacionDTO dto) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtTokenUtil.extractUsername(token);
        Usuario admin = usuarioRepo.findByEmail(email).orElseThrow();

        Desayuno desayuno = desayunoRepo.findById(dto.getDesayunoId()).orElseThrow();

        if (!desayuno.getBar().getId().equals(dto.getBarId())) {
            return ResponseEntity.badRequest().body("El desayuno no pertenece al bar seleccionado.");
        }

        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Comanda comanda = new Comanda();
        comanda.setAdmin(admin);
        comanda.setBar(desayuno.getBar());
        comanda.setCodigoUnico(codigo);
        comanda.setEstado(EstadoComanda.ABIERTA);
        comandaRepo.save(comanda);

        ComandaItem item = new ComandaItem();
        item.setComanda(comanda);
        item.setDesayuno(desayuno);
        item.setNombreInvitado(admin.getEmail());
        item.setAutorizado(true);
        itemRepo.save(item);

        return ResponseEntity.ok(comanda);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> verComandaCompleta(@PathVariable String codigo) {
        List<ComandaItem> items = itemRepo.findByComandaCodigoUnico(codigo);
        if (items.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comanda comanda = items.get(0).getComanda();

        // 🔒 Comprobamos si está cancelada o enviada
        if (comanda.getEstado() == EstadoComanda.CANCELADA || comanda.getEstado() == EstadoComanda.ENVIADA) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Esta comanda ya no está disponible.");
        }

        // 🔒 Comprobamos si hay usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String emailUsuario = auth.getName();
            
            // ⚠️ Si el email NO es el del admin, no se permite acceder
            if (!comanda.getAdmin().getEmail().equalsIgnoreCase(emailUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver esta comanda.");
            }
        } else {
            // ❌ Si no hay login, también denegamos
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Debes iniciar sesión para acceder.");
        }

        return ResponseEntity.ok(new ComandaConItemsDTO(comanda, items));
    }


    // Agregar item a comanda (público)
    @PostMapping("/{codigo}/agregar-item")
    public ResponseEntity<?> agregarItem(@PathVariable String codigo, @RequestBody ItemDTO dto) {
        Comanda comanda = comandaRepo.findByCodigoUnico(codigo).orElseThrow();
        Desayuno desayuno = desayunoRepo.findById(dto.getDesayunoId()).orElseThrow();

        ComandaItem item = new ComandaItem();
        item.setComanda(comanda);
        item.setDesayuno(desayuno);
        item.setNombreInvitado(dto.getNombreInvitado());
        itemRepo.save(item);

        return ResponseEntity.ok("Pedido agregado");
    }

    // Autorizar item (solo admin)
    @PutMapping("/{comandaId}/autorizar-item/{itemId}")
    public ResponseEntity<?> autorizarItem(@PathVariable Long comandaId, @PathVariable Long itemId,
                                           @RequestHeader("Authorization") String authHeader) {
    	String token = authHeader.replace("Bearer ", "");
    	String email = jwtTokenUtil.extractUsername(token);
        Comanda comanda = comandaRepo.findById(comandaId).orElseThrow();
        if (!comanda.getAdmin().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ComandaItem item = itemRepo.findById(itemId).orElseThrow();
        item.setAutorizado(true);
        itemRepo.save(item);
        return ResponseEntity.ok("Autorizado");
    }
    
    @GetMapping("/pendientes")
    public List<Comanda> verComandasPendientesParaBar(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtTokenUtil.extractUsername(token);
        Usuario admin = usuarioRepo.findByEmail(email).orElseThrow();
        
        List<Comanda> comandas = comandaRepo.findByBarPropietarioAndEstado(admin, EstadoComanda.ENVIADA);

        comandas.forEach(c -> c.getItems().size());
        
        // Busca solo comandas que están asociadas a bares de este admin
        return comandaRepo.findByBarPropietarioAndEstado(admin, EstadoComanda.ENVIADA);
    }

    
    @PutMapping("/{comandaId}/enviar")
    public ResponseEntity<?> enviarComanda(@PathVariable Long comandaId,
                                           @RequestBody Map<String, String> body,
                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtTokenUtil.extractUsername(token);

        Comanda comanda = comandaRepo.findById(comandaId).orElseThrow();
        if (!comanda.getAdmin().getEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            LocalDateTime hora = LocalDateTime.parse(body.get("horaVisita"));
            comanda.setHoraVisita(hora);
            comanda.setEstado(EstadoComanda.ENVIADA);
            comandaRepo.save(comanda);
            return ResponseEntity.ok("Comanda enviada al bar.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de hora inválido.");
        }
    }
    
    
    @PutMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarComanda(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtTokenUtil.extractUsername(token);

        Comanda comanda = comandaRepo.findById(id).orElseThrow();
        if (!comanda.getBar().getPropietario().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        comanda.setEstado(EstadoComanda.ACEPTADA);
        comandaRepo.save(comanda);
        return ResponseEntity.ok("Comanda aceptada.");
    }



    // Cerrar comanda
    @PostMapping("/{comandaId}/cerrar")
    public ResponseEntity<?> cerrarComanda(@PathVariable Long comandaId,
                                           @RequestHeader("Authorization") String authHeader) {
    	String token = authHeader.replace("Bearer ", "");
    	String email = jwtTokenUtil.extractUsername(token);
        Comanda comanda = comandaRepo.findById(comandaId).orElseThrow();
        if (!comanda.getAdmin().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        comanda.setEstado(EstadoComanda.CERRADA);
        comandaRepo.save(comanda);
        return ResponseEntity.ok("Comanda cerrada");
    }
    
    @PutMapping("/{comandaId}/cancelar")
    public ResponseEntity<?> cancelarComanda(@PathVariable Long comandaId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // viene del token

        Comanda comanda = comandaRepo.findById(comandaId).orElseThrow();

        // ✅ Permitir si el usuario autenticado es el creador de la comanda
        if (!email.equalsIgnoreCase(comanda.getAdmin().getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No estás autorizado para cancelar esta comanda.");
        }

        comanda.setEstado(EstadoComanda.CANCELADA);
        comandaRepo.save(comanda);

        return ResponseEntity.ok("Comanda cancelada correctamente.");
    }



    @DeleteMapping("/{comandaId}/item/{itemId}")
    public ResponseEntity<?> eliminarItem(@PathVariable Long comandaId,
                                          @PathVariable Long itemId,
                                          @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtTokenUtil.extractUsername(token);

        Comanda comanda = comandaRepo.findById(comandaId).orElseThrow();
        if (!comanda.getAdmin().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        itemRepo.deleteById(itemId);
        return ResponseEntity.ok("Item eliminado");
    }


    
}

