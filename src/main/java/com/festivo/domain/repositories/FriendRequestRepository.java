package com.festivo.domain.repositories;

import com.festivo.domain.entities.FriendRequest;
import com.festivo.domain.entities.User;
import com.festivo.shared.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {

    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);

    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);

    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequestStatus status);
}
