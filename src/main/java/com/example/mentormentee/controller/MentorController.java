package com.example.mentormentee.controller;

import com.example.mentormentee.dto.ChangePasswordDto;
import com.example.mentormentee.entity.Mentee;
import com.example.mentormentee.entity.Mentor;
import com.example.mentormentee.service.MenteeService;
import com.example.mentormentee.service.MentorService;
import com.example.mentormentee.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller handling Mentor dashboard, mentee profiles, and password changes.
 */
@Controller
@RequestMapping("/mentor")
public class MentorController {

    private final MentorService mentorService;
    private final MenteeService menteeService;
    private final UserService userService;

    public MentorController(MentorService mentorService, MenteeService menteeService, UserService userService) {
        this.mentorService = mentorService;
        this.menteeService = menteeService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Mentor mentor = mentorService.getMentorByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Mentor record not found for user: " + username));

        List<Mentee> mentees = mentorService.getMenteesForMentor(mentor);

        model.addAttribute("mentor", mentor);
        model.addAttribute("mentees", mentees);
        return "mentor/dashboard";
    }

    @GetMapping("/mentee/{rollNo}")
    public String viewMenteeProfile(@PathVariable("rollNo") String rollNo, Authentication authentication, Model model) {
        String username = authentication.getName();
        Mentor mentor = mentorService.getMentorByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Mentor record not found for user: " + username));

        Optional<Mentee> menteeOpt = menteeService.getMenteeByRollNo(rollNo);
        if (menteeOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Student not found");
            return "error/404";
        }

        Mentee mentee = menteeOpt.get();

        // Security check: Only allow access if mentee is assigned to logged-in mentor
        if (!mentee.getMentor().getId().equals(mentor.getId())) {
            model.addAttribute("errorMessage", "Access Denied: You are not assigned as mentor for this student.");
            return "error/403";
        }

        model.addAttribute("mentee", mentee);
        return "mentor/mentee-profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        return "mentor/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto dto,
                                 BindingResult bindingResult,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", "New passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "mentor/change-password";
        }

        String username = authentication.getName();
        boolean success = userService.changePassword(username, dto.getOldPassword(), dto.getNewPassword());

        if (!success) {
            bindingResult.rejectValue("oldPassword", "error.oldPassword", "Current password is incorrect");
            return "mentor/change-password";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully");
        return "redirect:/mentor/dashboard";
    }
}
