package com.gostokhov.chat.service.impl;

import com.gostokhov.chat.constant.FileConstant;
import com.gostokhov.chat.domain.JwtToken;
import com.gostokhov.chat.domain.UserPrincipal;
import com.gostokhov.chat.dto.user.UserCredentialsDto;
import com.gostokhov.chat.dto.user.UserRegisterDto;
import com.gostokhov.chat.entites.User;
import com.gostokhov.chat.enumiration.Role;
import com.gostokhov.chat.exception.domain.EmailExistException;
import com.gostokhov.chat.exception.domain.EmailNotFoundException;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.exception.domain.UsernameExistException;
import com.gostokhov.chat.repository.UserRepository;
import com.gostokhov.chat.service.UserService;
import com.gostokhov.chat.utility.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.gostokhov.chat.constant.UserImplConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RequiredArgsConstructor
@Service
@Transactional
@Primary
public class UserServiceImpl implements UserService, UserDetailsService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final JWTTokenProvider jwtTokenProvider;

    @Override
    public JwtToken generateJwtTokenPojo(UserCredentialsDto userCredentialsDto) throws UserNotFoundException {
        User loginUser = findUserByUsername(userCredentialsDto.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        return new JwtToken(jwtTokenProvider.generateJwtToken(userPrincipal));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(RETURNING_FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    @Override
    public void register(UserRegisterDto userRegisterDto) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, userRegisterDto.getUsername(), userRegisterDto.getEmail());
        User user = new User();
        user.setFirstName(userRegisterDto.getFirstName());
        user.setLastName(userRegisterDto.getLastName());
        user.setUsername(userRegisterDto.getUsername());
        user.setEmail(userRegisterDto.getEmail());
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(userRegisterDto.getPassword()));
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRoles(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setImageUrl(getTemporaryProfileImageUrl(userRegisterDto.getUsername()));
        userRepository.save(user);
        LOGGER.info("User user password is: " + userRegisterDto.getPassword());
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setJoinDate(new Date());
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setIsActive(isActive);
        user.setIsNotLocked(isNotLocked);
        user.setRoles(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newEmail, String newUsername, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setIsActive(isActive);
        currentUser.setIsNotLocked(isNotLocked);
        currentUser.setRoles(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(String username) throws IOException {
        User user = userRepository.findUserByUsername(username);
        Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByUsername(String searchString) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        List<User> users = userRepository.findByUsernameContains(searchString);
        return users.stream().filter(user -> !Objects.equals(user.getUsername(), currentUserName)).collect(Collectors.toList());
    }

    @Override
    public User findUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User findUserById(Long id) throws UserNotFoundException {
        return userRepository.findUserById(id);
    }

    @Override
    public User getCurrentUser() throws UserNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return findUserByUsername(currentUserName);
    }

    @Override
    public Set<User> validateUsernameList(Set<String> usernameList) throws UserNotFoundException {
        Set<User> users = new HashSet<>();
        for (String username : usernameList) {
            User user = findUserByUsername(username);
            if (user == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
            } else {
                users.add(user);
            }
        }
        return users;
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        if (isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private void validateLoginAttempt(User user) {
        if (user.getIsNotLocked()) {
            user.setIsNotLocked(!loginAttemptService.hasExceedMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(FileConstant.DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + FileConstant.DOT + FileConstant.JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + FileConstant.DOT + FileConstant.JPG_EXTENSION), REPLACE_EXISTING);
            user.setImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FileConstant.FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.USER_IMAGE_PATH + username + FileConstant.FORWARD_SLASH + username + FileConstant.DOT + FileConstant.JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
