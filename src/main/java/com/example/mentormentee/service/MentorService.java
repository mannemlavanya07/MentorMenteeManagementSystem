package com.example.mentormentee.service;

import com.example.mentormentee.dto.MentorSignupDto;
import com.example.mentormentee.entity.Mentee;
import com.example.mentormentee.entity.Mentor;
import com.example.mentormentee.entity.Role;
import com.example.mentormentee.entity.UserAccount;
import com.example.mentormentee.repository.MenteeRepository;
import com.example.mentormentee.repository.MentorRepository;
import com.example.mentormentee.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service handling Mentor business operations.
 */
@Service
public class MentorService {

    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;
    private final PasswordEncoder passwordEncoder;

    public MentorService(MentorRepository mentorRepository,
                         UserRepository userRepository,
                         MenteeRepository menteeRepository,
                         PasswordEncoder passwordEncoder) {
        this.mentorRepository = mentorRepository;
        this.userRepository = userRepository;
        this.menteeRepository = menteeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerMentor(MentorSignupDto dto) {
        // 1. Create UserAccount
        UserAccount userAccount = new UserAccount(
                dto.getUsername(),
                passwordEncoder.encode(dto.getPassword()),
                Role.ROLE_MENTOR,
                dto.getEmail()
        );
        userRepository.save(userAccount);

        // 2. Create Mentor Profile
        Mentor mentor = new Mentor(
                dto.getName(),
                dto.getFacultyId().toUpperCase(),
                dto.getDepartment(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getUsername()
        );
        mentorRepository.save(mentor);
    }

    public Optional<Mentor> getMentorByUsername(String username) {
        return mentorRepository.findByUsername(username);
    }

    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    public List<Mentee> getMenteesForMentor(Mentor mentor) {
        return menteeRepository.findByMentor(mentor);
    }
}
