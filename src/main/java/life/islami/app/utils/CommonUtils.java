package life.islami.app.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import life.islami.app.dtos.AudioDto;
import life.islami.app.exceptions.ValidationFailedException;
import life.islami.app.models.response.AudioRest;
import life.islami.app.models.response.PaginatedResponse;

@Component
public class CommonUtils {
  private Random random = new SecureRandom();
  private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private final String DIGITS = "0123456789";
  @Autowired
  private ModelMapper modelMapper;

  public String generateOtpCode(int length) {
    return generateRandomString(length, DIGITS);
  }

  public String generateUserId() {
    return generateRandomString(12, ALPHABET);
  }

  public String generateCoverId() {
    return generateRandomString(12, ALPHABET);
  }

  public String generatePlaylistId() {
    return generateRandomString(12, ALPHABET);
  }

  public String generateAudioId() {
    return generateRandomString(12, ALPHABET);
  }

  public String generateSubscriptionId() {
    return generateRandomString(12, ALPHABET);
  }

  public String generateArtistId() {
    return generateRandomString(12, ALPHABET);
  }

  private String generateRandomString(int length, String alphabet) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      stringBuilder.append(alphabet.charAt(random.nextInt(alphabet.length())));
    }
    return stringBuilder.toString();
  }

  public boolean validatePhoneNumber(String phoneNumber) {
    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    try {
      Phonenumber.PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, null);
      return phoneNumberUtil.isValidNumber(parsedPhoneNumber);
    } catch (NumberParseException e) {
      return false;
    }
  }

  public boolean validateEmail(String email) {
    EmailValidator emailValidator = EmailValidator.getInstance();
    return emailValidator.isValid(email);
  }

  public String getFileExtension(MultipartFile file) {
    if (file == null)
      return "";
    StringTokenizer stringTokenizer = new StringTokenizer(file.getOriginalFilename(), ".");
    stringTokenizer.nextToken();
    return "." + stringTokenizer.nextToken();
  }

  public void handleValidationErrors(Errors errors) {
    if (errors.hasErrors()) {
      List<String> errorMessages = new ArrayList<>();
      for (FieldError error : errors.getFieldErrors()) {
        errorMessages.add(error.getDefaultMessage());
      }
      throw new ValidationFailedException(errorMessages);
    }
  }

  public List<AudioRest> audioDtoToRestMapper(List<AudioDto> audioDtos) {
    List<AudioRest> audioRests = new ArrayList<>();
    for (AudioDto audioDto : audioDtos) {
      audioRests.add(modelMapper.map(audioDto, AudioRest.class));
    }
    return audioRests;
  }

  public PaginatedResponse<AudioRest> retrieveAudioFiles(Errors errors,
      PaginatedResponse<AudioDto> paginatedResponse) {
    handleValidationErrors(errors);
    return new PaginatedResponse<>(audioDtoToRestMapper(paginatedResponse.getContent()),
        paginatedResponse.getTotalRecords(),
        paginatedResponse.getPageNo(), paginatedResponse.getPageSize());
  }
}
