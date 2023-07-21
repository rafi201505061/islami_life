package life.islami.app.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import life.islami.app.dtos.AudioDto;
import life.islami.app.dtos.PlaylistDto;
import life.islami.app.enums.Messages;
import life.islami.app.enums.PrivacyType;
import life.islami.app.exceptions.CommonException;
import life.islami.app.models.request.PlaylistCreationRequestModel;
import life.islami.app.models.request.PlaylistUpdateRequestModel;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.AudioRest;
import life.islami.app.models.response.PaginatedResponse;
import life.islami.app.models.response.PlaylistRest;
import life.islami.app.services.PlaylistService;
import life.islami.app.utils.CommonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/playlists")
public class PlaylistController {
  private final CommonUtils utils;
  private final PlaylistService playlistService;
  private final ModelMapper modelMapper;

  @PostMapping
  public ResponseEntity<PlaylistRest> createPlaylist(
      @Valid @RequestBody PlaylistCreationRequestModel playlistCreationRequestModel, Principal principal,
      Errors errors) {
    utils.handleValidationErrors(errors);
    if (!playlistCreationRequestModel.getOwnerId().equals(principal.getName())) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    PlaylistDto newPlaylist = playlistService
        .createPlaylist(modelMapper.map(playlistCreationRequestModel, PlaylistDto.class));
    return new ResponseEntity<PlaylistRest>(modelMapper.map(newPlaylist, PlaylistRest.class), null, HttpStatus.OK);
  }

  @GetMapping
  public PaginatedResponse<PlaylistRest> retrievePlaylists(
      @RequestParam(name = "privacy", required = false, defaultValue = "") String privacyTypeStr,
      @RequestParam(name = "userId", required = true) String userId, @ModelAttribute Pagination pagination) {
    try {
      PrivacyType privacyType = privacyTypeStr.equals("") ? null : PrivacyType.valueOf(privacyTypeStr);
      PaginatedResponse<PlaylistDto> playlistDtos = playlistService.retrievePlaylists(userId, privacyType, pagination);
      List<PlaylistRest> playlistRests = new ArrayList<>();
      for (PlaylistDto playlistDto : playlistDtos.getContent()) {
        playlistRests.add(modelMapper.map(playlistDto, PlaylistRest.class));
      }
      return new PaginatedResponse<PlaylistRest>(playlistRests, playlistDtos.getTotalRecords(),
          playlistDtos.getPageNo(),
          playlistDtos.getPageSize());
    } catch (MethodArgumentTypeMismatchException | IllegalArgumentException e2) {
      throw new CommonException(Messages.PRIVACY_TYPE_INVALID, HttpStatus.BAD_REQUEST);
    }

  }

  @GetMapping("/{playlistId}")
  public PlaylistRest getPlaylistDetails(@PathVariable String playlistId) {
    return modelMapper.map(playlistService.retrievePlaylistDetails(playlistId), PlaylistRest.class);
  }

  @PutMapping("/{playlistId}")
  public PlaylistRest updatePlaylist(@PathVariable String playlistId, Principal principal,
      @RequestBody PlaylistUpdateRequestModel playlistUpdateRequestModel, Errors errors) {
    utils.handleValidationErrors(errors);
    PlaylistDto updatedPlaylistDto = playlistService.updatePlaylist(playlistId, principal.getName(),
        modelMapper.map(playlistUpdateRequestModel, PlaylistDto.class));
    return modelMapper.map(updatedPlaylistDto, PlaylistRest.class);
  }

  @DeleteMapping("/{playlistId}")
  public ResponseEntity<String> deletePlaylist(@PathVariable String playlistId, Principal principal) {
    playlistService.deletePlaylist(playlistId, principal.getName());
    return new ResponseEntity<String>("Playlist deleted successfully", null, HttpStatus.OK);
  }

  @PostMapping("/{playlistId}/audios")
  public ResponseEntity<Messages> addAudioToPlaylist(Principal principal, @PathVariable String playlistId,
      @RequestPart("audioId") String audioId) {
    playlistService.addAudioToPlaylist(principal.getName(), playlistId, audioId);
    return new ResponseEntity<Messages>(Messages.AUDIO_ADDED_TO_PLAYLIST, null, HttpStatus.OK);
  }

  @GetMapping("/{playlistId}/audios")
  public PaginatedResponse<AudioRest> retrieveAudios(@PathVariable String playlistId, Pagination pagination) {
    PaginatedResponse<AudioDto> audioDtos = playlistService.getPlaylistAudios(playlistId, pagination);
    List<AudioRest> audioRests = new ArrayList<>();
    for (AudioDto audioDto : audioDtos.getContent()) {
      audioRests.add(modelMapper.map(audioDto, AudioRest.class));
    }
    return new PaginatedResponse<>(audioRests, audioDtos.getTotalRecords(), audioDtos.getPageNo(),
        audioDtos.getPageSize());
  }

  @DeleteMapping("/{playlistId}/audios/{audioId}")
  public ResponseEntity<String> removeAudioFromPlaylist(@PathVariable String playlistId, @PathVariable String audioId) {
    playlistService.removeAudioFromPlaylist(playlistId, audioId);
    return new ResponseEntity<String>("Audio removed from playlist", null, HttpStatus.OK);
  }

}
