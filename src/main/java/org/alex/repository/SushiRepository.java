package org.alex.repository;

import org.alex.model.Sushi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SushiRepository extends JpaRepository<Sushi, Integer> {
    Optional<Sushi> findByName(String name);
}
