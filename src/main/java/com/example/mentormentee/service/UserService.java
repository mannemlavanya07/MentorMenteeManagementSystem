package com.example.mentormentee.service;

import com.example.mentormentee.entity.UserAccount;
import com.example.mentormentee.repository.MenteeRepository;
import com.example.mentormentee.repository.MentorRepository;
import com.example.mentormentee.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for checking account uniqueness and managing user account updates.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       MentorRepository mentorRepository,
                       MenteeRepository menteeRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username) ||
               mentorRepository.existsByUsername(username) ||
               menteeRepository.existsByUsername(username);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email) ||
               mentorRepository.existsByEmail(email) ||
               menteeRepository.existsByEmail(email);
    }

    public boolean isFacultyIdTaken(String facultyId) {
        return mentorRepository.existsByFacultyId(facultyId);
    }

    public boolean isRollNoTaken(String rollNo) {
        return menteeRepository.existsByRollNo(rollNo);
    }

    /**
     * Changes user password if old password matches the current BCrypt hash.
     * @return true if successful, false if old password does not match
     */
    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<UserAccount> accountOpt = userRepository.findByUsername(username);
        if (accountOpt.isEmpty()) {
            return false;
        }

        UserAccount account = accountOpt.get();

        // Verify current password
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            return false;
        }

        // Update with new encoded password
        account.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(account);
        return true;
    }
}
