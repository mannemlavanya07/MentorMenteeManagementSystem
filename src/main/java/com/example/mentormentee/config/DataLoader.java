package com.example.mentormentee.config;

import com.example.mentormentee.entity.Mentee;
import com.example.mentormentee.entity.Mentor;
import com.example.mentormentee.entity.Role;
import com.example.mentormentee.entity.UserAccount;
import com.example.mentormentee.repository.MenteeRepository;
import com.example.mentormentee.repository.MentorRepository;
import com.example.mentormentee.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Loader initializing sample Mentor and Mentee users on startup if missing.
 * NOTE: For development purposes only. Do not use sample credentials in production.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository,
                      MentorRepository mentorRepository,
                      MenteeRepository menteeRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Create sample mentor 'ravi' if not exists
        if (!userRepository.existsByUsername("ravi")) {
            UserAccount mentorAccount = new UserAccount(
                    "ravi",
                    passwordEncoder.encode("password123"),
                    Role.ROLE_MENTOR,
                    "ravi@example.com"
            );
            userRepository.save(mentorAccount);

            Mentor mentor = new Mentor(
                    "Ravi Kumar",
                    "FAC1001",
                    "CSE",
                    "ravi@example.com",
                    "9876543210",
                    "ravi"
            );
            mentorRepository.save(mentor);

            // 2. Create sample mentee 'anjali' assigned to Ravi Kumar if not exists
            if (!userRepository.existsByUsername("anjali")) {
                UserAccount menteeAccount = new UserAccount(
                        "anjali",
                        passwordEncoder.encode("password123"),
                        Role.ROLE_MENTEE,
                        "anjali@example.com"
                );
                userRepository.save(menteeAccount);

                Mentee mentee = new Mentee(
                        "Anjali",
                        "anjali@example.com",
                        "9876501234",
                        "23A81A0501",
                        "CSE",
                        "3",
                        "anjali",
                        mentor
                );
                mentee.setLeetcodeUrl("https://leetcode.com/anjali_sample");
                mentee.setGithubUrl("https://github.com/anjali_sample");
                mentee.setStrengths("Java, Spring Boot, Data Structures & Algorithms");
                mentee.setWeaknesses("Dynamic Programming optimization, System Design basics");
                mentee.setAchievements("1st Place in College Web Hackathon 2025");
                mentee.setIssues("Need guidance on upcoming campus placement interviews.");

                menteeRepository.save(mentee);
            }
        }
    }
}
