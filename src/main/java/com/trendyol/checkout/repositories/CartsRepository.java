package com.trendyol.checkout.repositories;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.query.QueryResult;
import com.trendyol.checkout.domain.Cart;
import com.trendyol.checkout.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
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

    public void deleteById(String cartId){
        String statement = String.format("Delete from CartDB where id = \"%s\"",cartId);
        QueryResult query = couchbaseCluster.query(statement);
    }

    public List<Cart> getCartByUserID(String id) {
        String statement = String.format("Select id,products,cartPrice,userId,discountRatio from CartDB where userId = '%s'", id);
        QueryResult query = couchbaseCluster.query(statement);
        return query.rowsAs(Cart.class);
    }

    public void addItemToCart(String cartId, Product product) {
        cartsCollection.mutateIn(cartId, Arrays.asList(
                MutateInSpec.arrayAppend("products", Collections.singletonList(product))
        ));
    }
}
