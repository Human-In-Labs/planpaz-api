package com.humanin.planpaz.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humanin.planpaz.model.Report;
import com.humanin.planpaz.model.enums.ReportStatus;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
	List<Report> findByStatus(ReportStatus status);
}
