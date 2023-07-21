package life.islami.app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import life.islami.app.dtos.CoverDto;
import life.islami.app.models.response.CoverRest;
import life.islami.app.services.CoverService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("covers")
public class CoverController {
  private final CoverService coverService;
  private final ModelMapper modelMapper;

  @PostMapping
  public CoverRest createCover(@RequestPart("image") MultipartFile image, @RequestPart("url") String url) {

    return modelMapper.map(coverService.createCover(new CoverDto(image, url)), CoverRest.class);
  }

  @GetMapping
  public List<CoverRest> retrieveCovers() {
    List<CoverDto> coverDtos = coverService.retrieveCovers();
    List<CoverRest> coverRests = new ArrayList<>();
    for (CoverDto coverDto : coverDtos) {
      coverRests.add(modelMapper.map(coverDto, CoverRest.class));
    }
    return coverRests;
  }

  @DeleteMapping("{coverId}")
  public ResponseEntity<String> deleteCover(
      @PathVariable String coverId) {
    coverService.deleteCover(coverId);
    return new ResponseEntity<String>("Cover deleted successfully", null, HttpStatus.OK);

  }
}
