package com.example.mentormentee.service;

import com.example.mentormentee.dto.MenteeSignupDto;
import com.example.mentormentee.dto.MenteeUpdateDto;
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

import java.util.Optional;

/**
 * Service handling Mentee business operations.
 */
@Service
public class MenteeService {

    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MenteeService(MenteeRepository menteeRepository,
                         MentorRepository mentorRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.menteeRepository = menteeRepository;
        this.mentorRepository = mentorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerMentee(MenteeSignupDto dto) {
        Mentor mentor = mentorRepository.findById(dto.getMentorId())
                .orElseThrow(() -> new IllegalArgumentException("Selected mentor does not exist."));

        // 1. Create UserAccount
        UserAccount userAccount = new UserAccount(
                dto.getUsername(),
                passwordEncoder.encode(dto.getPassword()),
                Role.ROLE_MENTEE,
                dto.getEmail()
        );
        userRepository.save(userAccount);

        // 2. Create Mentee Profile
        Mentee mentee = new Mentee(
                dto.getName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getRollNo().toUpperCase(),
                dto.getDepartment(),
                dto.getYear(),
                dto.getUsername(),
                mentor
        );
        menteeRepository.save(mentee);
    }

    public Optional<Mentee> getMenteeByUsername(String username) {
        return menteeRepository.findByUsername(username);
    }

    public Optional<Mentee> getMenteeByRollNo(String rollNo) {
        return menteeRepository.findByRollNo(rollNo.toUpperCase());
    }

    @Transactional
    public void updateMenteeProfile(String username, MenteeUpdateDto dto) {
        Mentee mentee = menteeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Mentee not found."));

        mentee.setLeetcodeUrl(dto.getLeetcodeUrl());
        mentee.setGithubUrl(dto.getGithubUrl());
        mentee.setStrengths(dto.getStrengths());
        mentee.setWeaknesses(dto.getWeaknesses());
        mentee.setAchievements(dto.getAchievements());
        mentee.setIssues(dto.getIssues());

        menteeRepository.save(mentee);
    }
}
