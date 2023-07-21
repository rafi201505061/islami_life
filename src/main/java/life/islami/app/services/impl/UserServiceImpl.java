package life.islami.app.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import life.islami.app.data.entities.SubscriptionEntity;
import life.islami.app.data.entities.UserEntity;
import life.islami.app.data.entities.UserSubscriptionHistory;
import life.islami.app.data.repositories.SubscriptionRepository;
import life.islami.app.data.repositories.UserRepository;
import life.islami.app.data.repositories.UserSubscriptionHistoryRepository;
import life.islami.app.dtos.UserDto;
import life.islami.app.enums.Messages;
import life.islami.app.exceptions.CommonException;
import life.islami.app.services.UserService;
import life.islami.app.utils.CommonUtils;
import life.islami.app.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final CommonUtils utils;
  private final FileUploadUtil fileUploadUtil;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserSubscriptionHistoryRepository userSubscriptionHistoryRepository;
  private final SubscriptionRepository subscriptionRepository;

  @Value("${profile-picture-directory}")
  private String profilePictureDirectoryPath;

  @Override
  public UserDto createUser(UserDto user) {
    UserEntity existingUser = userRepository.findByUserName(user.getUserName());
    if (existingUser != null) {
      throw new CommonException(Messages.DUPLICATE_USER, HttpStatus.CONFLICT);
    }
    UserEntity newUser = modelMapper.map(user, UserEntity.class);
    newUser.setUserId(utils.generateUserId());
    newUser.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    if (utils.validateEmail(user.getUserName())) {
      newUser.setEmail(user.getUserName());
    } else if (utils.validatePhoneNumber("+" + user.getUserName())) {
      newUser.setPhoneNo(user.getUserName());
    } else {
      throw new CommonException(Messages.INVALID_USERNAME, HttpStatus.BAD_REQUEST);
    }

    UserEntity createdUser = userRepository.save(newUser);
    return modelMapper.map(createdUser, UserDto.class);
  }

  @Override
  public UserDto retrieveUserByUserId(String userId) {
    UserEntity foundUser = findByUserId(userId);
    return modelMapper.map(foundUser, UserDto.class);
  }

  @Override
  public UserDto retrieveUserByUserName(String userName) {
    UserEntity foundUser = userRepository.findByUserName(userName);
    if (foundUser == null) {
      throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    return modelMapper.map(foundUser, UserDto.class);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    UserEntity userEntity = userRepository.findByUserName(username);
    if (userEntity == null)
      throw new UsernameNotFoundException(username);
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(userEntity.getRole().name()));
    return new User(userEntity.getUserName(), userEntity.getEncryptedPassword(), authorities);
  }

  @Override
  public Messages sendPasswordResetToken(String userId) {
    UserEntity foundUser = findByUserId(userId);
    Messages msg = Messages.PASSWORD_RESET_TOKEN_SENDING_ERROR;
    if (utils.validatePhoneNumber("+" + foundUser.getUserName())) {
      msg = Messages.PASSWORD_RESET_TOKEN_TO_PHONE;
    } else if (utils.validateEmail(foundUser.getUserName())) {
      msg = Messages.PASSWORD_RESET_TOKEN_TO_EMAIL;
    }
    if (msg != Messages.PASSWORD_RESET_TOKEN_SENDING_ERROR) {
      foundUser.setPasswordResetToken("123456");
      userRepository.save(foundUser);
    }
    return msg;
  }

  @Override
  public void resetPassword(UserDto userDto) {
    UserEntity userEntity = findByUserId(userDto.getUserId());
    if (userDto.getPasswordResetToken().equals(userEntity.getPasswordResetToken())) {
      userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
      userEntity.setPasswordResetToken(null);
      userRepository.save(userEntity);
    } else {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public void updatePassword(UserDto userDto) {
    UserEntity userEntity = findByUserId(userDto.getUserId());
    if (bCryptPasswordEncoder.matches(userDto.getCurrentPassword(), userEntity.getEncryptedPassword())) {
      userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
      userRepository.save(userEntity);
    } else {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public UserDto updateProfilePicture(String userId, MultipartFile image) {
    UserEntity userEntity = findByUserId(userId);
    String imagePath = userId + utils.getFileExtension(image);

    if (fileUploadUtil.saveFile(profilePictureDirectoryPath, imagePath, image)) {
      userEntity.setProfilePictureUrl("/profile-pictures/" + imagePath);
      return modelMapper.map(userRepository.save(userEntity), UserDto.class);
    } else {
      throw new CommonException(Messages.FILE_UPLOAD_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public UserDto findUserForAuthorization(String userId) {
    UserEntity foundUser = userRepository.findByUserId(userId);
    if (foundUser == null)
      return null;
    return modelMapper.map(foundUser, UserDto.class);
  }

  @Override
  public UserDto signIn(UserDto userDto) {
    UserEntity userEntity = userRepository.findByUserName(userDto.getUserName());
    if (userEntity == null) {
      throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (!bCryptPasswordEncoder.matches(userDto.getPassword(), userEntity.getEncryptedPassword())) {
      throw new CommonException(Messages.USERNAME_PASSWORD_MISMATCH, HttpStatus.UNAUTHORIZED);
    }

    return modelMapper.map(userEntity, UserDto.class)
        .setSubscribed(userSubscriptionHistoryRepository.countByActiveAndUserId(true, userEntity.getUserId()) > 0);
  }

  @Override
  public void subscribe(String userId, String subscriptionId) {
    findByUserId(userId);
    SubscriptionEntity subscriptionEntity = subscriptionRepository.findBySubscriptionId(subscriptionId);
    if (subscriptionEntity == null) {
      throw new CommonException(Messages.SUBSCRIPTION_NOT_FOUND, HttpStatus.FORBIDDEN);
    }
    UserSubscriptionHistory userSubscriptionHistory = new UserSubscriptionHistory();
    userSubscriptionHistory.setActive(true);
    userSubscriptionHistory.setSubscriptionEntity(subscriptionEntity);
    userSubscriptionHistory.setUserId(userId);
    userSubscriptionHistoryRepository.save(userSubscriptionHistory);
  }

  @Override
  public void deactivateSubscriptions() {
    List<UserSubscriptionHistory> userSubscriptions = userSubscriptionHistoryRepository.findAllByActive(true);
    for (UserSubscriptionHistory userSubscriptionHistory : userSubscriptions) {
      userSubscriptionHistory.setActive(false);
    }
    userSubscriptionHistoryRepository.saveAll(userSubscriptions);
  }

  private UserEntity findByUserId(String userId) {
    UserEntity foundUser = userRepository.findByUserId(userId);
    if (foundUser == null) {
      throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.FORBIDDEN);
    }
    return foundUser;
  }

}
