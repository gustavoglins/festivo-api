package com.festivo.domain.repositories;

import com.festivo.domain.entities.Party;
import com.festivo.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {

//    List<Party> findPartyByCreator(String username);
    List<Party> findAllByCreator(User user);
}
