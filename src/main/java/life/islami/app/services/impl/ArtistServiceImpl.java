package life.islami.app.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import life.islami.app.data.entities.ArtistEntity;
import life.islami.app.data.repositories.ArtistRepository;
import life.islami.app.dtos.ArtistDto;
import life.islami.app.enums.Messages;
import life.islami.app.exceptions.CommonException;
import life.islami.app.models.request.paramModels.ArtistQueryModel;
import life.islami.app.models.response.PaginatedResponse;
import life.islami.app.services.ArtistService;
import life.islami.app.utils.CommonUtils;
import life.islami.app.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
  private final ModelMapper modelMapper;
  private final FileUploadUtil fileUploadUtil;
  private final CommonUtils utils;
  private final ArtistRepository artistRepository;

  @Value("${artist-images-directory}")
  private String artistImageDirectory;

  @Transactional
  @Override
  public ArtistDto createArtist(ArtistDto artistDto) {
    ArtistEntity artistEntity = modelMapper.map(artistDto, ArtistEntity.class);
    String artistId = utils.generateArtistId();
    artistEntity.setArtistId(artistId);
    if (artistDto.getProfileImage() != null) {
      String imageName = artistId + utils.getFileExtension(artistDto.getProfileImage());
      fileUploadUtil.saveFile(artistImageDirectory, imageName, artistDto.getProfileImage());
      artistEntity.setProfileImageUrl("/artist-images/" + imageName);
    }
    return modelMapper.map(artistRepository.save(artistEntity), ArtistDto.class);

  }

  @Override
  public PaginatedResponse<ArtistDto> retrieveArtists(ArtistQueryModel artistQueryModel) {
    Specification<ArtistEntity> specification = null;
    if (artistQueryModel.getArtistName() != null && !artistQueryModel.getArtistName().isEmpty()) {
      specification = Specification.where((root, query, builder) -> {
        return builder.like(root.get("artistName"), "%" + artistQueryModel.getArtistName() + "%");
      });
    }
    Page<ArtistEntity> artistEntityPage = artistRepository
        .findAll(specification,
            PageRequest.of(artistQueryModel.getPageNo(), artistQueryModel.getPageSize(),
                Sort.by("artistName").descending()));
    PaginatedResponse<ArtistDto> paginatedResponse = new PaginatedResponse<>();
    return paginatedResponse.setContent(entityToDtoMapper(artistEntityPage.getContent()))
        .setPageNo(artistEntityPage.getNumber()).setPageSize(artistEntityPage.getSize())
        .setTotalRecords(artistEntityPage.getTotalElements());
  }

  @Override
  public ArtistDto retrieveArtistByArtistId(String artistId) {
    ArtistEntity artistEntity = findByArtistId(artistId);
    return modelMapper.map(artistEntity, ArtistDto.class);
  }

  private List<ArtistDto> entityToDtoMapper(List<ArtistEntity> artistEntities) {
    List<ArtistDto> artistDtos = new ArrayList<>();
    for (ArtistEntity artistEntity : artistEntities) {
      artistDtos.add(modelMapper.map(artistEntity, ArtistDto.class));

    }
    return artistDtos;
  }

  @Override
  public ArtistDto updateArtist(ArtistDto artistDto) {
    ArtistEntity artistEntity = findByArtistId(artistDto.getArtistId());
    if (artistDto.getArtistName() != null && !artistDto.getArtistName().isBlank()) {
      artistEntity.setArtistName(artistDto.getArtistName());
    }
    if (artistDto.getProfileImage() != null) {
      String imageName = artistDto.getArtistId() + utils.getFileExtension(artistDto.getProfileImage());
      fileUploadUtil.saveFile(artistImageDirectory, imageName, artistDto.getProfileImage());
      artistEntity.setProfileImageUrl("/artist-images/" + imageName);
    }
    ArtistEntity updatedArtistEntity = artistRepository.save(artistEntity);
    return modelMapper.map(updatedArtistEntity, ArtistDto.class);
  }

  private ArtistEntity findByArtistId(String artistId) {
    ArtistEntity artistEntity = artistRepository.findByArtistId(artistId);
    if (artistEntity == null) {
      throw new CommonException(Messages.ARTIST_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }
    return artistEntity;
  }

}
