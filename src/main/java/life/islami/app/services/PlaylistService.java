package life.islami.app.services;

import life.islami.app.dtos.AudioDto;
import life.islami.app.dtos.PlaylistDto;
import life.islami.app.enums.PrivacyType;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.PaginatedResponse;

public interface PlaylistService {
  PlaylistDto createPlaylist(PlaylistDto playlistDto);

  PaginatedResponse<PlaylistDto> retrievePlaylists(String userId, PrivacyType privacyType, Pagination pagination);

  PlaylistDto retrievePlaylistDetails(String playlistId);

  PlaylistDto updatePlaylist(String playlistId, String requestorId, PlaylistDto playlistDto);

  void deletePlaylist(String playlistId, String requestorId);

  void addAudioToPlaylist(String userId, String playlistId, String audioId);

  PaginatedResponse<AudioDto> getPlaylistAudios(String playlistId, Pagination pagination);

  void removeAudioFromPlaylist(String playlistId, String audioId);

}
