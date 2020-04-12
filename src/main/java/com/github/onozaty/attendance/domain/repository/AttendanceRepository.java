package com.github.onozaty.attendance.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {

    @Query(value = "SELECT * FROM attendances WHERE user_name = :userName AND type = 'COME' ORDER BY date_time DESC LIMIT 1", nativeQuery = true)
    public AttendanceEntity findLastCome(@Param("userName") String userName);
}
