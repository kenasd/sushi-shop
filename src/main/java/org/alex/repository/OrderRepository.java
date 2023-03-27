package org.alex.repository;

import org.alex.model.SushiOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<SushiOrder, Integer> {
}
