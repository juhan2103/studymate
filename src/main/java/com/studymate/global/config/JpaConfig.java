package com.studymate.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** JPA Auditing 활성화 (BaseTimeEntity의 created/updated 자동 주입). */
@Configuration
@EnableJpaAuditing
public class JpaConfig {}
