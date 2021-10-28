package com.gostokhov.chat.api;

import com.gostokhov.chat.constant.FileConstant;
import com.gostokhov.chat.domain.HttpResponse;
import com.gostokhov.chat.dto.jwt.token.JwtTokenDto;
import com.gostokhov.chat.dto.user.UserCredentialsDto;
import com.gostokhov.chat.dto.user.UserCurrentDto;
import com.gostokhov.chat.dto.user.UserDto;
import com.gostokhov.chat.dto.user.UserRegisterDto;
import com.gostokhov.chat.entites.User;
import com.gostokhov.chat.exception.ExceptionHandling;
import com.gostokhov.chat.exception.domain.EmailExistException;
import com.gostokhov.chat.exception.domain.EmailNotFoundException;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.exception.domain.UsernameExistException;
import com.gostokhov.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/", "/user"})
public class UserApi extends ExceptionHandling {
    public static final String EMAIL_SENT = "An email with new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtTokenDto> login(@RequestBody UserCredentialsDto userCredentialsDto) throws UserNotFoundException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentialsDto.getUsername(), userCredentialsDto.getPassword()));
        JwtTokenDto jwtTokenDto = modelMapper.map(userService.generateJwtTokenPojo(userCredentialsDto), JwtTokenDto.class);
        return new ResponseEntity<>(jwtTokenDto, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody UserRegisterDto userRegisterDto) throws UserNotFoundException, EmailExistException, UsernameExistException {
        userService.register(userRegisterDto);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserCurrentDto> getCurrentUser() throws UserNotFoundException {
        UserCurrentDto userCurrentDto = modelMapper.map(userService.getCurrentUser(), UserCurrentDto.class);
        return new ResponseEntity<>(userCurrentDto, OK);
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<List<UserDto>> getUsersByUsername(@PathVariable("username") String username) {
        List<UserDto> users = userService.getUsersByUsername(username)
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNotLocked") String isNotLocked,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User newUser = userService.addNewUser(firstName, lastName, username, email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> update(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNotLocked") String isNotLocked,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User updatedUser = userService.updateUser(currentUsername, firstName, lastName, email, username, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) throws UserNotFoundException {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return HttpResponse.response(OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return HttpResponse.response(OK, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<User> updateProfileImage(
            @RequestParam("username") String username,
            @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User updatedUser = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(
            @PathVariable("username") String username,
            @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(FileConstant.USER_FOLDER + username + FileConstant.FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(FileConstant.TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
}
