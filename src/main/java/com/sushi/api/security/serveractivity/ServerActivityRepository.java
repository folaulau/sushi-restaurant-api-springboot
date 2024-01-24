package com.sushi.api.security.serveractivity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerActivityRepository extends JpaRepository<ServerActivity, Long> {

    ServerActivity findTopByOrderByIdDesc();
}
