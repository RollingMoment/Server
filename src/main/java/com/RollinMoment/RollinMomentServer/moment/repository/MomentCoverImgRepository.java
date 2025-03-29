package com.RollinMoment.RollinMomentServer.moment.repository;

import com.RollinMoment.RollinMomentServer.moment.entity.Moment;
import com.RollinMoment.RollinMomentServer.moment.entity.MomentCoverImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MomentCoverImgRepository extends JpaRepository<MomentCoverImg, String> {
}
