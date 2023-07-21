package life.islami.app.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import life.islami.app.data.entities.CoverEntity;
import life.islami.app.data.repositories.CoverRepository;
import life.islami.app.dtos.CoverDto;
import life.islami.app.enums.Messages;
import life.islami.app.exceptions.CommonException;
import life.islami.app.services.CoverService;
import life.islami.app.utils.CommonUtils;
import life.islami.app.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CoverServiceImpl implements CoverService {
  private final ModelMapper modelMapper;
  private final CoverRepository coverRepository;
  private final CommonUtils utils;
  private final FileUploadUtil fileUploadUtil;

  @Value("${cover-images-directory}")
  private String coverImageDirectory;

  @Override
  public CoverDto createCover(CoverDto coverDto) {
    if (coverDto.getImage() == null || !coverDto.getImage().getContentType().startsWith("image/")) {
      throw new CommonException(Messages.IMAGE_FILE_TYPE, HttpStatus.BAD_REQUEST);
    }
    CoverEntity coverEntity = new CoverEntity();
    String coverId = utils.generateCoverId();
    coverEntity.setCoverId(coverId);
    coverEntity.setUrl(coverDto.getUrl());

    fileUploadUtil.saveFile(coverImageDirectory, coverId, coverDto.getImage());
    coverEntity.setImageUrl("/covers/" + coverId + utils.getFileExtension(coverDto.getImage()));
    CoverEntity newCover = coverRepository.save(coverEntity);
    return modelMapper.map(newCover, CoverDto.class);
  }

  @Override
  public List<CoverDto> retrieveCovers() {
    List<CoverEntity> coverEntities = coverRepository.findAll();
    List<CoverDto> coverDtos = new ArrayList<>();
    for (CoverEntity coverEntity : coverEntities) {
      coverDtos.add(modelMapper.map(coverEntity, CoverDto.class));
    }
    return coverDtos;
  }

  @Override
  public void deleteCover(String coverId) {
    CoverEntity coverEntity = coverRepository.findByCoverId(coverId);
    if (coverEntity == null) {
      throw new CommonException(Messages.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    coverRepository.delete(coverEntity);
  }

}
