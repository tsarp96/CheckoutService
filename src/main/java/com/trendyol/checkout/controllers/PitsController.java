package com.trendyol.checkout.controllers;

import com.trendyol.checkout.domain.Car;
import com.trendyol.checkout.domain.Pit;
import com.trendyol.checkout.services.PitsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/Pits")
@CrossOrigin
public class PitsController {

    private final PitsService pitsService;

    public PitsController(PitsService pitsService) {
        this.pitsService = pitsService;
    }

    @PostMapping
    public ResponseEntity<Void> createPit(@RequestParam(name = "owner") String owner){
        Pit pit = new Pit();
        String pitId = UUID.randomUUID().toString();
        pit.setPitId(pitId);
        pit.setOwner(owner);
        pitsService.createPit(pit);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pit.getPitId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<Pit>> getPitsByName(@RequestParam(name = "owner") String owner){
        return ResponseEntity.ok(pitsService.getPitsByName(owner));
    }

    @DeleteMapping("/{pitId}")
    public ResponseEntity<Pit> deletePitById(@PathVariable String pitId){
        pitsService.deletePitById(pitId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{pitId}/cars")
    public ResponseEntity<Void> addCarToPit(@PathVariable String pitId, Car car){
        pitsService.addCarToPit(pitId, car);
        return ResponseEntity.ok().build();
    }
}
