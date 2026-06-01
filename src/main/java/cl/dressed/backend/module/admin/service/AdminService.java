package cl.dressed.backend.module.admin.service;

import cl.dressed.backend.module.admin.dto.AdminMetricsDTO;
import cl.dressed.backend.module.admin.dto.AdminUserDTO;
import cl.dressed.backend.module.auth.entity.User;
import cl.dressed.backend.module.auth.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public AdminMetricsDTO getMetrics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since7 = now.minusDays(7);
        LocalDateTime since30 = now.minusDays(30);

        long totalUsers = userRepository.countByActiveTrue();
        long newUsersLast7Days = userRepository.countUsersCreatedSince(since7);
        long newUsersLast30Days = userRepository.countUsersCreatedSince(since30);
        long inactiveUsers7Days = userRepository.findInactiveUsersSince(since7).size();
        long inactiveUsers30Days = userRepository.findInactiveUsersSince(since30).size();

        List<Object[]> rawDailyCounts = userRepository.countUsersCreatedPerDaySince(since30);
        List<Map<String, Object>> newUsersPerDay = rawDailyCounts.stream()
            .map(row -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("date", String.valueOf(row[0]));
                entry.put("count", ((Number) row[1]).longValue());
                return entry;
            })
            .toList();

        return new AdminMetricsDTO(
            totalUsers,
            newUsersLast7Days,
            newUsersLast30Days,
            inactiveUsers7Days,
            inactiveUsers30Days,
            newUsersPerDay
        );
    }

    public Page<AdminUserDTO> getUsers(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(this::toAdminUserDTO);
    }

    private AdminUserDTO toAdminUserDTO(User user) {
        return new AdminUserDTO(
            user.getId(),
            user.getEmail(),
            user.getActive(),
            user.getOauthProvider(),
            user.getRoles().stream().map(role -> role.getName()).toList(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}