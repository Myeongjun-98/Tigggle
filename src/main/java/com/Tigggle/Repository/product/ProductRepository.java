package com.Tigggle.Repository.product;

import com.Tigggle.Entity.product.Bank;
import com.Tigggle.Entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBank(Bank bank);

}
