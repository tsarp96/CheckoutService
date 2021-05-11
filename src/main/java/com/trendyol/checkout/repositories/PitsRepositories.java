package com.trendyol.checkout.repositories;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.trendyol.checkout.domain.Pit;
import org.springframework.stereotype.Repository;

@Repository
public class PitsRepositories {
    private final Cluster couchbaseCluster;
    private  final Collection pitsCollection;

    public PitsRepositories(Cluster couchbaseCluster, Collection pitsCollection) {
        this.couchbaseCluster = couchbaseCluster;
        this.pitsCollection = pitsCollection;
    }

    public void insert(Pit pit){
        pitsCollection.insert(pit.getPitId(), pit);
    }

}
