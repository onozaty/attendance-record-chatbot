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

    public List<AttendanceEntity> findByUserNameAndDate(String userName, LocalDate date);

    @Query(value = "SELECT * FROM attendances WHERE date BETWEEN :from AND :to", nativeQuery = true)
    public List<AttendanceEntity> findByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
