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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import life.islami.app.dtos.ArtistDto;
import life.islami.app.dtos.AudioDto;
import life.islami.app.dtos.CommentDto;
import life.islami.app.dtos.ListenTimeDto;
import life.islami.app.dtos.ReactionDto;
import life.islami.app.enums.Messages;
import life.islami.app.enums.Reaction;
import life.islami.app.enums.SongApprovalStatus;
import life.islami.app.exceptions.CommonException;
import life.islami.app.models.request.AudioMetadata;
import life.islami.app.models.request.AudioUpdateRequestModel;
import life.islami.app.models.request.CommentCreationRequestModel;
import life.islami.app.models.request.ReactionCreationRequestModel;
import life.islami.app.models.request.paramModels.AudioQueryModel;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.AudioRest;
import life.islami.app.models.response.CommentRest;
import life.islami.app.models.response.PaginatedResponse;
import life.islami.app.models.response.ReactionRest;
import life.islami.app.services.AudioService;
import life.islami.app.services.CommentService;
import life.islami.app.services.ReactionService;
import life.islami.app.utils.CommonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/audios")
public class AudioController {
  private final AudioService audioService;
  private final ReactionService reactionService;
  private final CommentService commentService;
  private final ModelMapper modelMapper;
  private final CommonUtils utils;

  @PostMapping
  public ResponseEntity<List<String>> createAudios(@RequestParam("audios") MultipartFile[] audios,
      @RequestParam("images") MultipartFile[] images,
      @RequestParam("metadata") String metadata, Principal principal)
      throws JsonMappingException, JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonObject metadataMap = JsonParser.parseString(metadata).getAsJsonObject();
    List<AudioDto> audioDtos = new ArrayList<>();
    for (int i = 0; i < audios.length; i++) {
      AudioMetadata audioMetadata = objectMapper.readValue(
          metadataMap.get(audios[i].getOriginalFilename()).getAsJsonObject().toString(),
          AudioMetadata.class);
      AudioDto audioDto = new AudioDto();
      audioDto.setAudio(audios[i]);
      audioDto.setImage(images[i]);
      audioDto.setCreatorId(principal.getName());
      audioDto.setTitle(audioMetadata.getTitle());
      audioDto.setDescription(audioMetadata.getDescription());
      audioDto.setGenre(audioMetadata.getGenre());
      audioDto.setArtist(new ArtistDto().setArtistId(audioMetadata.getArtistId()));
      audioDtos.add(audioDto);
    }

    return new ResponseEntity<>(audioService.createAudios(audioDtos), null, HttpStatus.OK);
  }

  @GetMapping
  private PaginatedResponse<AudioRest> retrieveAudios(@Valid @ModelAttribute AudioQueryModel audioQueryModel,
      Errors errors) {
    return utils.retrieveAudioFiles(errors, audioService.retrieveAudios(audioQueryModel, SongApprovalStatus.APPROVED));
  }

  @GetMapping("/in-review")
  private PaginatedResponse<AudioRest> retrieveNonApprovedAudios(@Valid @ModelAttribute AudioQueryModel audioQueryModel,
      Errors errors) {
    return utils.retrieveAudioFiles(errors, audioService.retrieveAudios(audioQueryModel, SongApprovalStatus.IN_REVIEW));
  }

  @GetMapping("/{audioId}")
  private AudioRest retrieveAudioDetails(@PathVariable String audioId) {
    return modelMapper.map(audioService.retrieveAudioDetails(audioId), AudioRest.class);
  }

  @PutMapping("/{audioId}")
  private AudioRest updateAudio(@PathVariable String audioId,
      @ModelAttribute AudioUpdateRequestModel audioUpdateRequestModel) {
    AudioDto audioDto = modelMapper.map(audioUpdateRequestModel, AudioDto.class)
        .setArtist(new ArtistDto().setArtistId(audioUpdateRequestModel.getArtistId())).setAudioId(audioId);
    return modelMapper.map(audioService.updateAudio(audioDto),
        AudioRest.class);
  }

  @DeleteMapping("/{audioId}")
  private ResponseEntity<Messages> deleteAudio(@PathVariable String audioId, Principal principal) {
    audioService.deleteAudio(audioId, principal.getName());
    return new ResponseEntity<>(Messages.AUDIO_REMOVED, null, HttpStatus.NO_CONTENT);
  }

  @PostMapping("/{audioId}/listen-time")
  private ResponseEntity<String> trackListenTime(@PathVariable String audioId, @RequestBody ListenTimeDto listenTimeDto,
      Principal principal) {
    audioService.saveListenTime(new ListenTimeDto(principal.getName(), audioId, listenTimeDto.getDuration()));
    return new ResponseEntity<String>("success", null, HttpStatus.OK);
  }

  @PutMapping("/{audioId}/approve")
  private AudioRest approveAudio(@PathVariable String audioId,
      @ModelAttribute AudioUpdateRequestModel audioUpdateRequestModel, Principal principal) {
    AudioDto audioDto = modelMapper.map(audioUpdateRequestModel, AudioDto.class)
        .setArtist(new ArtistDto().setArtistId(audioUpdateRequestModel.getArtistId())).setAudioId(audioId)
        .setApprovalStatus(SongApprovalStatus.APPROVED).setCheckedBy(principal.getName());
    return modelMapper.map(audioService.updateAudio(audioDto),
        AudioRest.class);
  }

  @PutMapping("/{audioId}/reject")
  private AudioRest rejectAudio(@PathVariable String audioId,
      @ModelAttribute AudioUpdateRequestModel audioUpdateRequestModel, Principal principal) {
    AudioDto audioDto = modelMapper.map(audioUpdateRequestModel, AudioDto.class)
        .setArtist(new ArtistDto().setArtistId(audioUpdateRequestModel.getArtistId())).setAudioId(audioId)
        .setApprovalStatus(SongApprovalStatus.REJECTED).setCheckedBy(principal.getName());
    return modelMapper.map(audioService.updateAudio(audioDto),
        AudioRest.class);
  }

  @PutMapping("/{audioId}/reactions")
  private ResponseEntity<String> addReaction(@PathVariable String audioId,
      @Valid @RequestBody ReactionCreationRequestModel reactionCreationRequestModel, Principal principal,
      Errors errors) {
    utils.handleValidationErrors(errors);
    if (!reactionCreationRequestModel.getAudioId().equals(audioId)) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.BAD_REQUEST);
    }
    if (!principal.getName().equals(reactionCreationRequestModel.getUserId())) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    reactionService.addReaction(modelMapper.map(reactionCreationRequestModel, ReactionDto.class));
    return new ResponseEntity<String>("Reaction added successfully", null, HttpStatus.OK);
  }

  @GetMapping("/{audioId}/reactions")
  private ResponseEntity<ReactionRest> getReaction(@PathVariable String audioId,
      @RequestParam(name = "userId", required = true) String userId) {
    return new ResponseEntity<ReactionRest>(
        new ReactionRest().setReaction(reactionService.getReaction(audioId, userId)), null, HttpStatus.OK);
  }

  @PutMapping("/{audioId}/comments")
  private CommentRest addComment(@PathVariable String audioId,
      @Valid @RequestBody CommentCreationRequestModel commentCreationRequestModel, Principal principal,
      Errors errors) {
    utils.handleValidationErrors(errors);
    if (!commentCreationRequestModel.getAudioId().equals(audioId)) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.BAD_REQUEST);
    }
    if (!principal.getName().equals(commentCreationRequestModel.getUserId())) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    return modelMapper.map(commentService.addComment(modelMapper.map(commentCreationRequestModel, CommentDto.class)),
        CommentRest.class);
  }

  @GetMapping("/{audioId}/comments")
  private PaginatedResponse<CommentRest> getComments(@PathVariable String audioId,
      @ModelAttribute Pagination pagination) {
    PaginatedResponse<CommentDto> commentDtos = commentService.getComments(audioId, pagination);
    PaginatedResponse<CommentRest> commentRests = new PaginatedResponse<>();
    List<CommentRest> comments = new ArrayList<>();
    for (CommentDto commentDto : commentDtos.getContent()) {
      comments.add(modelMapper.map(commentDto, CommentRest.class));
    }
    commentRests.setContent(comments);
    return commentRests;
  }

}
