package life.islami.app.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Messages {
  INTERNAL_SERVER_ERROR("Something went wrong. Please try again later."),
  PASSWORD_MISMATCH("Passwords didn't match."),
  USER_NOT_FOUND("User couldn't be found."),
  COVER_NOT_FOUND("Cover couldn't be found."),

  PLAYLIST_NOT_FOUND("User couldn't be found."),
  AUDIO_NOT_FOUND("Audio couldn't be found."),
  COMMENT_NOT_FOUND("Comment couldn't be found."),
  SUBSCRIPTION_NOT_FOUND("Subscription couldn't be found."),
  AUDIO_REMOVED("Audio removed successfully."),
  ARTIST_NOT_FOUND("Artist couldn't be found."),
  ARTIST_NAME_BLANK("Artist name can not be blank."),
  PLAYLIST_NAME_BLANK("Playlist name can not be blank."),
  PLAYLIST_DESCRIPTION_BLANK("Playlist description can not be blank."),
  PRIVACY_TYPE_INVALID("Privacy type must be PUBLIC or PRIVATE or ''."),
  AUDIO_ADDED_TO_PLAYLIST("Content added to playlist."),

  APPROVAL_ERROR("The audio has already been rejected."),
  REJECTION_ERROR("The audio has already been approved."),
  AUDIO_TITLE_BLANK("Audio title can not be blank."),
  AUDIO_FILE_TYPE("You must provide a valid audio file"),
  IMAGE_FILE_TYPE("You must provide a valid image file"),

  FORBIDDEN("You are not allowed to perform this action."),
  PASSWORD_RESET_TOKEN_TO_EMAIL("Password reset token has been sent to your email address."),
  PASSWORD_RESET_TOKEN_TO_PHONE("Password reset token has been sent to your phone number."),
  PASSWORD_RESET_TOKEN_SENDING_ERROR(
      "Password reset token couldn't be sent cause username is neither a phone number or an email address."),
  FILE_UPLOAD_ERROR("File couldn't be uploaded."),
  DUPLICATE_USER("User already exists."),
  USERNAME_PASSWORD_MISMATCH("Username and password didn't match."),
  INVALID_USERNAME("User name must be a valid phone number or email");

  private String value;
}
