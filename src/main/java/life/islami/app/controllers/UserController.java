package life.islami.app.controllers;

import java.security.Key;
import java.security.Principal;
import java.util.Date;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import life.islami.app.dtos.UserDto;
import life.islami.app.enums.Messages;
import life.islami.app.enums.Role;
import life.islami.app.exceptions.CommonException;
import life.islami.app.models.request.UserCreationRequestModel;
import life.islami.app.models.request.UserLoginRequestModel;
import life.islami.app.models.request.UserPasswordResetRequestModel;
import life.islami.app.models.request.UserPasswordUpdateRequestModel;
import life.islami.app.models.response.UserRest;
import life.islami.app.models.response.UserRest2;
import life.islami.app.security.SecurityConstants;
import life.islami.app.services.UserService;
import life.islami.app.utils.CommonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
  private final ModelMapper modelMapper;
  private final UserService userService;
  private final CommonUtils utils;

  @GetMapping
  public ResponseEntity<UserRest> retrieveUsers(
      @RequestParam(name = "userName", defaultValue = "", required = true) String userName) {
    return new ResponseEntity<UserRest>(modelMapper.map(userService.retrieveUserByUserName(userName), UserRest.class),
        null,
        HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<UserRest> createUser(@Valid @RequestBody UserCreationRequestModel userCreationRequestModel,
      Errors errors) {
    return createUserHelper(userCreationRequestModel, errors, Role.USER);
  }

  @PostMapping("/admin")
  public ResponseEntity<UserRest> createAdmin(@Valid @RequestBody UserCreationRequestModel userCreationRequestModel,
      Errors errors) {
    return createUserHelper(userCreationRequestModel, errors, Role.ADMIN);
  }

  @PostMapping("/artist")
  public ResponseEntity<UserRest> createArtist(@Valid @RequestBody UserCreationRequestModel userCreationRequestModel,
      Errors errors) {
    return createUserHelper(userCreationRequestModel, errors, Role.ARTIST);
  }

  @PostMapping("/sign-in")
  public ResponseEntity<UserRest2> signIn(@Valid @RequestBody UserLoginRequestModel userLoginRequestModel,
      Errors errors) {
    utils.handleValidationErrors(errors);
    UserDto userDto = userService.signIn(modelMapper.map(userLoginRequestModel, UserDto.class));
    Key signingKey = Keys.hmacShaKeyFor(SecurityConstants.getSecurityToken().getBytes());
    String token = Jwts.builder().setSubject(userDto.getUserId())
        .setExpiration(new Date(System.currentTimeMillis() +
            SecurityConstants.EXPIRATION_TIME))
        .signWith(signingKey, SignatureAlgorithm.HS512).compact();
    HttpHeaders headers = new HttpHeaders();
    headers.add(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    headers.add("UserId", userDto.getUserId());
    return new ResponseEntity<>(modelMapper.map(userDto, UserRest2.class), headers, HttpStatus.OK);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserRest> retrieveUser(@PathVariable String userId, Principal principal) {
    checkPermissionAsOwner(principal, userId);
    return new ResponseEntity<UserRest>(modelMapper.map(userService.retrieveUserByUserId(userId), UserRest.class), null,
        HttpStatus.OK);
  }

  @PostMapping("/{userId}/password-reset-token")
  public ResponseEntity<String> createPasswordResetToken(@PathVariable String userId) {
    Messages msg = userService.sendPasswordResetToken(userId);
    return new ResponseEntity<>(msg.getValue(), null,
        HttpStatus.OK);
  }

  @PostMapping("/{userId}/password-reset")
  public ResponseEntity<String> resetPassword(@PathVariable String userId,
      @Valid @RequestBody UserPasswordResetRequestModel userPasswordResetRequestModel, Errors errors) {
    utils.handleValidationErrors(errors);
    validatePasswords(userPasswordResetRequestModel.getPassword(), userPasswordResetRequestModel.getRetypedPassword());
    userService.resetPassword(modelMapper.map(userPasswordResetRequestModel, UserDto.class).setUserId(userId));
    return new ResponseEntity<>("Password has been reset.", null,
        HttpStatus.OK);
  }

  @PutMapping("/{userId}/password")
  public ResponseEntity<String> updatePassword(@PathVariable String userId,
      @Valid @RequestBody UserPasswordUpdateRequestModel userPasswordUpdateRequestModel, Errors errors,
      Principal principal) {
    checkPermissionAsOwner(principal, userId);
    utils.handleValidationErrors(errors);
    validatePasswords(userPasswordUpdateRequestModel.getPassword(),
        userPasswordUpdateRequestModel.getRetypedPassword());
    userService.updatePassword(modelMapper.map(userPasswordUpdateRequestModel, UserDto.class).setUserId(userId));
    return new ResponseEntity<>("Password has been updated.", null,
        HttpStatus.OK);
  }

  @PutMapping("/{userId}/profile-picture")
  public ResponseEntity<UserRest> updateProfilePicture(@PathVariable String userId,
      @RequestPart(name = "image") MultipartFile image, Principal principal) {
    checkPermissionAsOwner(principal, userId);
    return new ResponseEntity<>(modelMapper.map(userService.updateProfilePicture(userId, image), UserRest.class), null,
        HttpStatus.OK);
  }

  @PostMapping("/{userId}/subscription")
  public ResponseEntity<String> subscribe(@PathVariable String userId,
      @RequestParam("subscriptionId") String subscriptionId,
      Principal principal) {
    checkPermissionAsOwner(principal, userId);
    userService.subscribe(userId, subscriptionId);
    return new ResponseEntity<>("Subscribed successfully", null,
        HttpStatus.OK);
  }

  private ResponseEntity<UserRest> createUserHelper(
      @Valid @RequestBody UserCreationRequestModel userCreationRequestModel,
      Errors errors, Role role) {
    utils.handleValidationErrors(errors);
    validatePasswords(userCreationRequestModel.getPassword(), userCreationRequestModel.getRetypedPassword());
    UserDto newUserDto = modelMapper.map(userCreationRequestModel, UserDto.class);
    newUserDto.setRole(role);
    UserDto createdUser = userService.createUser(newUserDto);
    return new ResponseEntity<UserRest>(modelMapper.map(createdUser, UserRest.class), null, HttpStatus.OK);
  }

  private void validatePasswords(String password, String retypedPassword) {
    if (!password.equals(retypedPassword)) {
      throw new CommonException(Messages.PASSWORD_MISMATCH, HttpStatus.BAD_REQUEST);
    }
  }

  private void checkPermissionAsOwner(Principal principal, String userId) {
    if (principal == null || !principal.getName().equals(userId)) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
  }
}
