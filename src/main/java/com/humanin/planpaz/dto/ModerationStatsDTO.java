package com.humanin.planpaz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationStatsDTO {

	private long totalReports;
	private long pendingReports;
	private long acceptedReports;
	private long rejectedReports;
	private long deletedContents;
	private long bannedUsers;
}
