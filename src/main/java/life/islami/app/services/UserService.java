package life.islami.app.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import life.islami.app.dtos.UserDto;
import life.islami.app.enums.Messages;

public interface UserService extends UserDetailsService {
  UserDto createUser(UserDto user);

  UserDto retrieveUserByUserId(String userId);

  UserDto findUserForAuthorization(String userId);

  UserDto retrieveUserByUserName(String userName);

  Messages sendPasswordResetToken(String userId);

  void resetPassword(UserDto userDto);

  void updatePassword(UserDto userDto);

  UserDto updateProfilePicture(String userId, MultipartFile image);

  UserDto signIn(UserDto userDto);

  void subscribe(String userId, String subscriptionId);

  void deactivateSubscriptions();

}
