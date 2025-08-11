package com.trustai.user_service.user.service;

import com.trustai.user_service.user.entity.UserActivityLog;
import com.trustai.user_service.user.repository.UserActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityLogService {
    private final UserActivityLogRepository repository;

    public void save(UserActivityLog log) {
        repository.save(log);
    }
}
