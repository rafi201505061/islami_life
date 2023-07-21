package life.islami.app.services;

import java.util.List;

import life.islami.app.dtos.AudioDto;
import life.islami.app.dtos.ListenTimeDto;
import life.islami.app.enums.SongApprovalStatus;
import life.islami.app.models.request.paramModels.AudioQueryModel;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.PaginatedResponse;

public interface AudioService {
  List<String> createAudios(List<AudioDto> audios);

  AudioDto retrieveAudioDetails(String audioId);

  AudioDto updateAudio(AudioDto audioDto);

  PaginatedResponse<AudioDto> retrieveAudios(AudioQueryModel audioQueryModel, SongApprovalStatus songApprovalStatus);

  PaginatedResponse<AudioDto> retrieveArtistAudios(String artistId, Pagination pagination);

  void deleteAudio(String audioId, String requestorUserId);

  void saveListenTime(ListenTimeDto listenTimeDto);
}
