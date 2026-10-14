package com.example.mentormentee.controller;

import com.example.mentormentee.dto.ChangePasswordDto;
import com.example.mentormentee.dto.MenteeUpdateDto;
import com.example.mentormentee.entity.Mentee;
import com.example.mentormentee.service.MenteeService;
import com.example.mentormentee.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller handling Mentee dashboard, profile editing, and password changes.
 */
@Controller
@RequestMapping("/mentee")
public class MenteeController {

    private final MenteeService menteeService;
    private final UserService userService;

    public MenteeController(MenteeService menteeService, UserService userService) {
        this.menteeService = menteeService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Mentee mentee = menteeService.getMenteeByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Mentee record not found for user: " + username));

        MenteeUpdateDto dto = new MenteeUpdateDto();
        dto.setLeetcodeUrl(mentee.getLeetcodeUrl());
        dto.setGithubUrl(mentee.getGithubUrl());
        dto.setStrengths(mentee.getStrengths());
        dto.setWeaknesses(mentee.getWeaknesses());
        dto.setAchievements(mentee.getAchievements());
        dto.setIssues(mentee.getIssues());

        model.addAttribute("mentee", mentee);
        model.addAttribute("menteeUpdateDto", dto);
        return "mentee/dashboard";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("menteeUpdateDto") MenteeUpdateDto dto,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        menteeService.updateMenteeProfile(username, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        return "redirect:/mentee/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        Mentee mentee = menteeService.getMenteeByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Mentee record not found for user: " + username));

        model.addAttribute("mentee", mentee);
        return "mentee/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        return "mentee/change-password";
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
            return "mentee/change-password";
        }

        String username = authentication.getName();
        boolean success = userService.changePassword(username, dto.getOldPassword(), dto.getNewPassword());

        if (!success) {
            bindingResult.rejectValue("oldPassword", "error.oldPassword", "Current password is incorrect");
            return "mentee/change-password";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully");
        return "redirect:/mentee/dashboard";
    }
}
