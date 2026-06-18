package com.studymate.loginhistory.repository;

import com.studymate.loginhistory.domain.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {}
