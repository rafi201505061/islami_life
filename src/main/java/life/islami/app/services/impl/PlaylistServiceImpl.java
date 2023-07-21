package life.islami.app.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import life.islami.app.data.entities.AudioEntity;
import life.islami.app.data.entities.PlaylistEntity;
import life.islami.app.data.entities.PlaylistSongEntity;
import life.islami.app.data.entities.UserEntity;
import life.islami.app.data.repositories.AudioRepository;
import life.islami.app.data.repositories.PlaylistRepository;
import life.islami.app.data.repositories.PlaylistSongRepository;
import life.islami.app.data.repositories.UserRepository;
import life.islami.app.dtos.AudioDto;
import life.islami.app.dtos.PlaylistDto;
import life.islami.app.enums.Messages;
import life.islami.app.enums.PrivacyType;
import life.islami.app.exceptions.CommonException;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.PaginatedResponse;
import life.islami.app.services.PlaylistService;
import life.islami.app.utils.CommonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PlaylistServiceImpl implements PlaylistService {
  private final PlaylistRepository playlistRepository;
  private final CommonUtils utils;
  private final ModelMapper modelMapper;
  private final UserRepository userRepository;
  private final AudioRepository audioRepository;
  private final PlaylistSongRepository playlistSongRepository;

  @Override
  public PlaylistDto createPlaylist(PlaylistDto playlistDto) {
    UserEntity userEntity = userRepository.findByUserId(playlistDto.getOwnerId());
    if (userEntity == null) {
      throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    PlaylistEntity playlistEntity = modelMapper.map(playlistDto, PlaylistEntity.class);
    playlistEntity.setPlaylistId(utils.generatePlaylistId());
    playlistEntity.setOwner(userEntity);
    PlaylistEntity newPlaylistEntity = playlistRepository.save(playlistEntity);
    return modelMapper.map(newPlaylistEntity, PlaylistDto.class);
  }

  @Override
  public PlaylistDto retrievePlaylistDetails(String playlistId) {
    PlaylistEntity playlistEntity = playlistRepository.findByPlaylistIdAndIsActive(playlistId, true);
    if (playlistEntity == null)
      throw new CommonException(Messages.PLAYLIST_NOT_FOUND, HttpStatus.NOT_FOUND);
    return modelMapper.map(playlistEntity, PlaylistDto.class);
  }

  @Override
  public PaginatedResponse<PlaylistDto> retrievePlaylists(String userId, PrivacyType privacyType,
      Pagination pagination) {
    Page<PlaylistEntity> playlistEntityPage = null;
    if (privacyType == null) {
      playlistEntityPage = playlistRepository.findByOwnerUserIdAndIsActive(userId, true,
          PageRequest.of(pagination.getPageNo(), pagination.getPageSize(), Sort.by("creationTime").descending()));
    } else {
      System.out.println("hell");
      playlistEntityPage = playlistRepository.findByOwnerUserIdAndPrivacyTypeAndIsActive(userId, privacyType, true,
          PageRequest.of(pagination.getPageNo(), pagination.getPageSize(), Sort.by("creationTime").descending()));
    }
    List<PlaylistDto> playlistDtos = new ArrayList<>();

    for (PlaylistEntity playlistEntity : playlistEntityPage.getContent()) {
      playlistDtos.add(modelMapper.map(playlistEntity, PlaylistDto.class));
    }
    return new PaginatedResponse<>(playlistDtos, playlistEntityPage.getTotalElements(), playlistEntityPage.getNumber(),
        playlistEntityPage.getSize());
  }

  @Override
  public PlaylistDto updatePlaylist(String playlistId, String requestorId, PlaylistDto playlistDto) {
    PlaylistEntity playlistEntity = playlistRepository.findByPlaylistIdAndIsActive(playlistId, true);
    if (playlistEntity == null) {
      throw new CommonException(Messages.PLAYLIST_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    UserEntity userEntity = playlistEntity.getOwner();
    if (!userEntity.getUserId().equals(requestorId)) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    if (playlistDto.getPlaylistName() != null) {
      if (playlistDto.getPlaylistName().isBlank()) {
        throw new CommonException(Messages.PLAYLIST_NAME_BLANK, HttpStatus.BAD_REQUEST);
      } else {
        playlistEntity.setPlaylistName(playlistDto.getPlaylistName());
      }

    }
    if (playlistDto.getDescription() != null) {
      if (playlistDto.getDescription().isBlank()) {
        throw new CommonException(Messages.PLAYLIST_DESCRIPTION_BLANK, HttpStatus.BAD_REQUEST);
      } else {
        playlistEntity.setDescription(playlistDto.getDescription());
      }

    }
    if (playlistDto.getPrivacyType() != null) {
      playlistEntity.setPrivacyType(playlistDto.getPrivacyType());
    }

    PlaylistEntity updatedPlaylist = playlistRepository.save(playlistEntity);
    return modelMapper.map(updatedPlaylist, PlaylistDto.class);
  }

  @Override
  public void deletePlaylist(String playlistId, String requestorId) {
    PlaylistEntity playlistEntity = playlistRepository.findByPlaylistIdAndIsActive(playlistId, true);
    if (playlistEntity == null) {
      throw new CommonException(Messages.PLAYLIST_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    UserEntity owner = playlistEntity.getOwner();
    if (!owner.getUserId().equals(requestorId)) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    playlistEntity.setActive(false);
    playlistRepository.save(playlistEntity);
  }

  @Override
  public void addAudioToPlaylist(String userId, String playlistId, String audioId) {
    PlaylistSongEntity playlistSongEntity1 = playlistSongRepository.findByPlaylistPlaylistIdAndAudioAudioId(playlistId,
        audioId);
    if (playlistSongEntity1 != null) {
      return;
    }
    PlaylistEntity playlistEntity = playlistRepository.findByPlaylistIdAndIsActive(playlistId, true);
    if (playlistEntity == null) {
      throw new CommonException(Messages.PLAYLIST_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    AudioEntity audioEntity = audioRepository.findByAudioId(audioId);
    if (audioEntity == null) {
      throw new CommonException(Messages.AUDIO_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    PlaylistSongEntity playlistSongEntity = new PlaylistSongEntity();
    playlistSongEntity.setAudio(audioEntity);
    playlistSongEntity.setPlaylist(playlistEntity);
    playlistSongRepository.save(playlistSongEntity);
  }

  @Override
  public PaginatedResponse<AudioDto> getPlaylistAudios(String playlistId, Pagination pagination) {
    Page<PlaylistSongEntity> plPage = playlistSongRepository.findByPlaylistPlaylistId(playlistId,
        PageRequest.of(pagination.getPageNo(), pagination.getPageSize(), Sort.by("id").descending()));
    List<AudioDto> audioDtos = new ArrayList<>();
    for (PlaylistSongEntity playlistSongEntity : plPage.getContent()) {
      audioDtos.add(modelMapper.map(playlistSongEntity.getAudio(), AudioDto.class));
    }
    return new PaginatedResponse<>(audioDtos, plPage.getTotalElements(), plPage.getNumber(), plPage.getSize());
  }

  @Override
  public void removeAudioFromPlaylist(String playlistId, String audioId) {
    PlaylistSongEntity playlistSongEntity = playlistSongRepository.findByPlaylistPlaylistIdAndAudioAudioId(playlistId,
        audioId);
    if (playlistSongEntity == null) {
      return;
    }
    playlistSongRepository.delete(playlistSongEntity);
  }

}
