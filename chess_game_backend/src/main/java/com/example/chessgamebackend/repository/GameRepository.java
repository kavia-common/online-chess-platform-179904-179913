package com.example.chessgamebackend.repository;

import com.example.chessgamebackend.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * PUBLIC_INTERFACE
 * Repository for Game entities.
 */
public interface GameRepository extends JpaRepository<Game, UUID> {
}
