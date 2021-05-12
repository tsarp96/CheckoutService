package com.trendyol.checkout.repositories;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.query.QueryResult;
import com.trendyol.checkout.domain.Pit;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<Pit> getPitsByName(String name){
        String statement = String.format("Select owner, pitId, cars from pits where owner = '%s'", name);
        QueryResult query = couchbaseCluster.query(statement);
        return query.rowsAs(Pit.class);
    }

    public void deletePitById(String id){
        String statement = String.format("Delete from pits where pits.pitId = \"%s\"",id);
        QueryResult query = couchbaseCluster.query(statement);
    }
}
