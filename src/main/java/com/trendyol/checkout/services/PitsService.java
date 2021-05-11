package com.trendyol.checkout.services;

import com.trendyol.checkout.domain.Pit;
import com.trendyol.checkout.repositories.PitsRepositories;
import org.springframework.stereotype.Service;

@Service
public class PitsService {
    private final PitsRepositories pitsRepositorty;

    public PitsService(PitsRepositories pitsRepositories){
        this.pitsRepositorty = pitsRepositories;
    }

    public void createPit(Pit pit){
        pitsRepositorty.insert(pit);
    }
}
