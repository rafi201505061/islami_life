package life.islami.app.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import life.islami.app.dtos.ArtistDto;
import life.islami.app.enums.Messages;
import life.islami.app.exceptions.CommonException;
import life.islami.app.models.request.paramModels.ArtistQueryModel;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.ArtistRest;
import life.islami.app.models.response.AudioRest;
import life.islami.app.models.response.PaginatedResponse;
import life.islami.app.services.ArtistService;
import life.islami.app.services.AudioService;
import life.islami.app.utils.CommonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/artists")
public class ArtistController {

  private final ArtistService artistService;
  private final AudioService audioService;
  private final ModelMapper modelMapper;
  private final CommonUtils utils;

  @PostMapping
  public ResponseEntity<ArtistRest> createArtist(@RequestParam(name = "artistName", required = true) String artistName,
      @RequestParam(name = "image", required = false) MultipartFile image) {
    if (artistName == null || artistName.isBlank()) {
      throw new CommonException(Messages.ARTIST_NAME_BLANK, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<ArtistRest>(
        modelMapper.map(artistService.createArtist(new ArtistDto().setArtistName(artistName).setProfileImage(image)),
            ArtistRest.class),
        null, HttpStatus.OK);
  }

  @GetMapping
  public PaginatedResponse<ArtistRest> retrieveArtists(
      @Valid @ModelAttribute ArtistQueryModel artistQueryModel, Errors errors) {
    utils.handleValidationErrors(errors);
    PaginatedResponse<ArtistDto> paginatedResponse = artistService.retrieveArtists(artistQueryModel);
    return new PaginatedResponse<ArtistRest>().setPageNo(paginatedResponse.getPageNo())
        .setPageSize(paginatedResponse.getPageSize()).setTotalRecords(paginatedResponse.getTotalRecords())
        .setContent(dtoToRestMapper(paginatedResponse.getContent()));
  }

  @GetMapping("/{artistId}")
  public ArtistRest retrieveArtistDetails(@PathVariable String artistId) {
    return modelMapper.map(artistService.retrieveArtistByArtistId(artistId), ArtistRest.class);
  }

  @PutMapping("/{artistId}")
  public ArtistRest updateArtist(@PathVariable String artistId,
      @RequestParam(name = "artistName", required = false) String artistName,
      @RequestParam(name = "image", required = false) MultipartFile image) {
    if (artistName != null && artistName.isBlank()) {
      throw new CommonException(Messages.ARTIST_NAME_BLANK, HttpStatus.BAD_REQUEST);
    }
    return modelMapper.map(artistService.updateArtist(
        new ArtistDto().setArtistId(artistId).setProfileImage(image).setArtistName(artistName)), ArtistRest.class);
  }

  @GetMapping("/{artistId}/audios")
  public PaginatedResponse<AudioRest> retrieveArtistSongs(@PathVariable String artistId,
      @Valid @ModelAttribute Pagination pagination,
      Errors errors) {
    return utils.retrieveAudioFiles(errors, audioService.retrieveArtistAudios(artistId, pagination));

  }

  private List<ArtistRest> dtoToRestMapper(List<ArtistDto> artistDtos) {
    List<ArtistRest> artistRests = new ArrayList<>();
    for (ArtistDto artistDto : artistDtos) {
      artistRests.add(modelMapper.map(artistDto, ArtistRest.class));
    }
    return artistRests;
  }
}
