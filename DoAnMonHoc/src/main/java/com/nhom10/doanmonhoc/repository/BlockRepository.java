package com.nhom10.doanmonhoc.repository;

import com.nhom10.doanmonhoc.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findByIdPost(Long idPost);
    Optional<Block> findByIdPage(Long idPage);

}