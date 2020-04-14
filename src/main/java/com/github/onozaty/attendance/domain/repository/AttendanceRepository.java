package com.github.onozaty.attendance.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.onozaty.attendance.domain.entity.AttendanceEntity;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {

    @Query(value = "SELECT * FROM attendances WHERE user_name = :userName AND date = :date AND type = 'COME' ORDER BY time DESC LIMIT 1", nativeQuery = true)
    public AttendanceEntity findLastComeByUserNameAndDate(
            @Param("userName") String userName, @Param("date") LocalDate date);

    @Query(value = "SELECT * FROM attendances WHERE date BETWEEN :from AND :to", nativeQuery = true)
    public List<AttendanceEntity> findByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
