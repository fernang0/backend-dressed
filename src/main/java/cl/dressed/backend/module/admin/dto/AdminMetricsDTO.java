package cl.dressed.backend.module.admin.dto;

import java.util.List;
import java.util.Map;

public record AdminMetricsDTO(
    long totalUsers,
    long newUsersLast7Days,
    long newUsersLast30Days,
    long inactiveUsers7Days,
    long inactiveUsers30Days,
    List<Map<String, Object>> newUsersPerDay
) {}