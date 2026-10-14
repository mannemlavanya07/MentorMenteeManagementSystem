package com.example.mentormentee.controller;

import com.example.mentormentee.dto.MenteeSignupDto;
import com.example.mentormentee.dto.MentorSignupDto;
import com.example.mentormentee.service.MenteeService;
import com.example.mentormentee.service.MentorService;
import com.example.mentormentee.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller managing registration and login views for Mentors and Mentees.
 */
@Controller
public class AuthController {

    private final MentorService mentorService;
    private final MenteeService menteeService;
    private final UserService userService;

    public AuthController(MentorService mentorService, MenteeService menteeService, UserService userService) {
        this.mentorService = mentorService;
        this.menteeService = menteeService;
        this.userService = userService;
    }

    // --- MENTOR AUTH ---

    @GetMapping("/mentor/signup")
    public String showMentorSignup(Model model) {
        model.addAttribute("mentorSignupDto", new MentorSignupDto());
        return "mentor/signup";
    }

    @PostMapping("/mentor/signup")
    public String registerMentor(@Valid @ModelAttribute("mentorSignupDto") MentorSignupDto dto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (userService.isUsernameTaken(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username already exists");
        }

        if (userService.isEmailTaken(dto.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
        }

        if (dto.getFacultyId() != null && userService.isFacultyIdTaken(dto.getFacultyId())) {
            bindingResult.rejectValue("facultyId", "error.facultyId", "Faculty ID already exists");
        }

        if (bindingResult.hasErrors()) {
            return "mentor/signup";
        }

        mentorService.registerMentor(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Mentor registration successful");
        return "redirect:/mentor/login?success";
    }

    @GetMapping("/mentor/login")
    public String showMentorLogin(@RequestParam(value = "error", required = false) String error,
                                  @RequestParam(value = "success", required = false) String success,
                                  Model model) {
        if (error != null) {
            if ("not_a_mentor".equalsIgnoreCase(error)) {
                model.addAttribute("errorMessage", "This account is not a mentor");
            } else {
                model.addAttribute("errorMessage", "Invalid username or password");
            }
        }
        if (success != null) {
            model.addAttribute("successMessage", "Mentor registration successful. Please log in.");
        }
        return "mentor/login";
    }

    // --- MENTEE AUTH ---

    @GetMapping("/mentee/signup")
    public String showMenteeSignup(Model model) {
        model.addAttribute("menteeSignupDto", new MenteeSignupDto());
        model.addAttribute("mentors", mentorService.getAllMentors());
        return "mentee/signup";
    }

    @PostMapping("/mentee/signup")
    public String registerMentee(@Valid @ModelAttribute("menteeSignupDto") MenteeSignupDto dto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (dto.getMentorId() == null) {
            bindingResult.rejectValue("mentorId", "error.mentorId", "Please select a mentor");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (userService.isUsernameTaken(dto.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username already exists");
        }

        if (userService.isEmailTaken(dto.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
        }

        if (dto.getRollNo() != null && userService.isRollNoTaken(dto.getRollNo().toUpperCase())) {
            bindingResult.rejectValue("rollNo", "error.rollNo", "Roll number already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("mentors", mentorService.getAllMentors());
            return "mentee/signup";
        }

        menteeService.registerMentee(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Mentee registration successful");
        return "redirect:/mentee/login?success";
    }

    @GetMapping("/mentee/login")
    public String showMenteeLogin(@RequestParam(value = "error", required = false) String error,
                                  @RequestParam(value = "success", required = false) String success,
                                  Model model) {
        if (error != null) {
            if ("not_a_mentee".equalsIgnoreCase(error)) {
                model.addAttribute("errorMessage", "This account is not a mentee");
            } else {
                model.addAttribute("errorMessage", "Invalid username or password");
            }
        }
        if (success != null) {
            model.addAttribute("successMessage", "Mentee registration successful. Please log in.");
        }
        return "mentee/login";
    }
}
