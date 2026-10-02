package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humanin.planpaz.model.Report;
import com.humanin.planpaz.model.enums.ReportActionTaken;
import com.humanin.planpaz.model.enums.ReportContentType;
import com.humanin.planpaz.model.enums.ReportStatus;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
	List<Report> findByStatus(ReportStatus status);

	List<Report> findAllByOrderByCreatedAtDesc();

	List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

	List<Report> findByContentTypeOrderByCreatedAtDesc(ReportContentType contentType);

	List<Report> findByStatusAndContentTypeOrderByCreatedAtDesc(ReportStatus status, ReportContentType contentType);

	long countByStatus(ReportStatus status);

	long countByActionTaken(ReportActionTaken actionTaken);
}
