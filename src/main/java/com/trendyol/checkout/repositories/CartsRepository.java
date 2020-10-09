package com.trendyol.checkout.repositories;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import com.trendyol.checkout.domain.Cart;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CartsRepository {

    private final Cluster couchbaseCluster;
    private final Collection cartsCollection;

    public CartsRepository(Cluster couchbaseCluster, Collection cartsCollection) {
        this.couchbaseCluster = couchbaseCluster;
        this.cartsCollection = cartsCollection;
    }

    public void insert(Cart cart) {
        cartsCollection.insert(cart.getId(), cart);
    }

    public void update(Cart cart) {
        cartsCollection.replace(cart.getId(), cart);
    }

    public Cart findById(String id) {
        GetResult getResult = cartsCollection.get(id);
        Cart cart = getResult.contentAs(Cart.class);
        return cart;
    }

}
