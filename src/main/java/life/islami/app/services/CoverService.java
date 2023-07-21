package life.islami.app.services;

import java.util.List;

import life.islami.app.dtos.CoverDto;

public interface CoverService {
  CoverDto createCover(CoverDto coverDto);

  List<CoverDto> retrieveCovers();

  void deleteCover(String coverId);
}
