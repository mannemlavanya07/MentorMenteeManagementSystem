package com.example.mentormentee.repository;

import com.example.mentormentee.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Mentor entity.
 */
@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {

    Optional<Mentor> findByUsername(String username);

    Optional<Mentor> findByFacultyId(String facultyId);

    List<Mentor> findAll();

    boolean existsByUsername(String username);

    boolean existsByFacultyId(String facultyId);

    boolean existsByEmail(String email);
}
