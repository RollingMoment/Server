package com.RollinMoment.RollinMomentServer.moment.repository;

import com.RollinMoment.RollinMomentServer.moment.entity.Moment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MomentRepository extends JpaRepository<Moment, String> {
}
