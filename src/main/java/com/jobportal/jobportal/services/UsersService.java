package com.jobportal.jobportal.services;

import com.jobportal.jobportal.entity.JobSeekerProfile;
import com.jobportal.jobportal.entity.RecruiterProfile;
import com.jobportal.jobportal.entity.Users;
import com.jobportal.jobportal.repository.JobSeekerProfileRepository;
import com.jobportal.jobportal.repository.RecruiterProfileRepository;
import com.jobportal.jobportal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    public final UsersRepository usersRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository, JobSeekerProfileRepository jobSeekerProfileRepository, RecruiterProfileRepository recruiterProfileRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users addNew(Users users)
    {
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        int userTypeId= users.getUserTypeId().getUserTypeId();
        Users savedUser= usersRepository.save(users);
        if(userTypeId==1)
        {
            recruiterProfileRepository.save(new RecruiterProfile(savedUser));
        }
        else
        {
            jobSeekerProfileRepository.save(new JobSeekerProfile(savedUser));
        }

        return savedUser;
    }

    public Optional<Users> getUserByEmail(String email)
    {
        return usersRepository.findByEmail(email);
    }

    public Object getCurrentUserProfile() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken))
        {
            String username= authentication.getName();
            Users users= usersRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Username not found"));
            int userId= users.getUserId();
            if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter")))
            {
                RecruiterProfile recruiterProfile= recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
                return recruiterProfile;
            }
            else
            {
                JobSeekerProfile jobSeekerProfile= jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
                return jobSeekerProfile;
            }
        }
        return null;
    }

    public Users getCurrentUser() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String username= authentication.getName();
            Users users= usersRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Username not found"));
            return users;
        }
        return null;
    }

    public Users findByEmail(String currentUsername) {
        return usersRepository.findByEmail(currentUsername).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    public Optional<Users> getUserByToken(String token) {
        return usersRepository.findByVerificationToken(token);
    }
}
