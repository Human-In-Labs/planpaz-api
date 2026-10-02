package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.humanin.planpaz.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

	List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

	long countByUserIdAndIsReadFalse(UUID userId);

	@Modifying
	@Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.id IN :ids")
	void markAsRead(@Param("userId") UUID userId, @Param("ids") List<UUID> ids);

	@Modifying
	@Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
	void markAllAsRead(@Param("userId") UUID userId);
}
