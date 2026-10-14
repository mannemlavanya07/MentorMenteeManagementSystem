package com.example.mentormentee.repository;

import com.example.mentormentee.entity.Mentee;
import com.example.mentormentee.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Mentee entity.
 */
@Repository
public interface MenteeRepository extends JpaRepository<Mentee, Long> {

    Optional<Mentee> findByUsername(String username);

    Optional<Mentee> findByRollNo(String rollNo);

    List<Mentee> findByMentor(Mentor mentor);

    List<Mentee> findByMentorId(Long mentorId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRollNo(String rollNo);
}
