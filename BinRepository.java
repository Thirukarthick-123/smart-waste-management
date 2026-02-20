package com.example.smartwaste.repository;

import com.example.smartwaste.model.Bin;
import com.example.smartwaste.model.BinStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<Bin, Long> {
    Optional<Bin> findByBinId(String binId);

    List<Bin> findByStatus(BinStatus status);
}
