package life.islami.app.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
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
import life.islami.app.data.entities.AudioEntity;
import life.islami.app.data.entities.ListenTimeEntity;
import life.islami.app.data.entities.UserEntity;
import life.islami.app.data.repositories.ArtistRepository;
import life.islami.app.data.repositories.AudioRepository;
import life.islami.app.data.repositories.ListenTimeRepository;
import life.islami.app.data.repositories.UserRepository;
import life.islami.app.dtos.AudioDto;
import life.islami.app.dtos.ListenTimeDto;
import life.islami.app.enums.Messages;
import life.islami.app.enums.Role;
import life.islami.app.enums.SongApprovalStatus;
import life.islami.app.exceptions.CommonException;
import life.islami.app.exceptions.ValidationFailedException;
import life.islami.app.models.request.paramModels.AudioQueryModel;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.PaginatedResponse;
import life.islami.app.services.AudioService;
import life.islami.app.utils.CommonUtils;
import life.islami.app.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AudioServiceImpl implements AudioService {
  private final FileUploadUtil fileUploadUtil;
  private final ModelMapper modelMapper;
  private final ExecutorService threadPoolExecutor;
  private final AudioRepository audioRepository;
  private final UserRepository userRepository;
  private final ArtistRepository artistRepository;
  private final ListenTimeRepository listenTimeRepository;
  @PersistenceContext
  private EntityManager entityManager;

  private final CommonUtils utils;

  @Value("${audio-file-directory}")
  private String audioDirectory;

  @Value("${audio-images-directory}")
  private String audioImageDirectory;

  @Transactional
  @Override
  public List<String> createAudios(List<AudioDto> audios) {
    List<Future<String>> futures = new ArrayList<>();
    List<AudioEntity> audioEntities = new ArrayList<>();
    List<String> status = new ArrayList<>();
    for (AudioDto audio : audios) {
      futures.add(threadPoolExecutor.submit(() -> {
        if (audio.getTitle().isBlank()) {
          return Messages.AUDIO_TITLE_BLANK.getValue();
        }
        ArtistEntity artistEntity = artistRepository.findByArtistId(audio.getArtist().getArtistId());
        if (artistEntity == null) {
          return Messages.ARTIST_NOT_FOUND.getValue();
        }
        if (audio.getAudio() == null || !audio.getAudio().getContentType().startsWith("audio/")) {
          return Messages.AUDIO_FILE_TYPE.getValue();
        }
        if (audio.getImage() == null || !audio.getImage().getContentType().startsWith("image/")) {
          return Messages.IMAGE_FILE_TYPE.getValue();
        }
        AudioEntity audioEntity = modelMapper.map(audio, AudioEntity.class);
        String audioId = utils.generateAudioId();
        audioEntity.setAudioId(audioId);
        String audioFileName = audioId + utils.getFileExtension(audio.getAudio());
        String audioImageName = audioId + utils.getFileExtension(audio.getImage());

        fileUploadUtil.saveFile(audioDirectory, audioFileName, audio.getAudio());
        fileUploadUtil.saveFile(audioImageDirectory, audioImageName, audio.getImage());

        audioEntity.setUrl("/audios/" + audioFileName);
        audioEntity.setImageUrl("/audio-images/" + audioImageName);

        audioEntity.setArtist(artistEntity);
        synchronized (audioEntities) {
          audioEntities.add(audioEntity);
        }
        return "success";

      }));
    }

    for (int i = 0; i < futures.size(); i++) {
      try {
        status.add(i, futures.get(i).get());

      } catch (Exception e) {
        e.printStackTrace();
        status.add(i, "failure");
      }
    }
    audioRepository.saveAll(audioEntities);
    return status;
  }

  @Override
  public AudioDto retrieveAudioDetails(String audioId) {
    AudioEntity audioEntity = findByAudioId(audioId);
    return modelMapper.map(audioEntity, AudioDto.class);
  }

  @Override
  public PaginatedResponse<AudioDto> retrieveAudios(AudioQueryModel audioQueryModel,
      SongApprovalStatus approvalStatus) {

    Specification<AudioEntity> notDeleted = (root, query, builder) -> {
      return builder.equal(root.get("deleted"), false);
    };
    Specification<AudioEntity> approvalStatusSpecification = (root, query, builder) -> {
      return builder.equal(root.get("approvalStatus"), approvalStatus);
    };
    Specification<AudioEntity> deletionStatusSpecification = (root, query, builder) -> {
      return builder.equal(root.get("deleted"), false);
    };
    Specification<AudioEntity> genreSpecification = (root, query, builder) -> {
      if (audioQueryModel.getGenre() == null) {
        return null;
      }
      return builder.equal(root.get("genre"), audioQueryModel.getGenre());
    };
    Specification<AudioEntity> finalSpecification = Specification.where(notDeleted).and(approvalStatusSpecification)
        .and(genreSpecification).and(deletionStatusSpecification);

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
    Root<AudioEntity> countRoot = countQuery.from(AudioEntity.class);
    countQuery.select(builder.count(countRoot));
    countQuery.where(finalSpecification.toPredicate(countRoot, countQuery, builder));
    long count = entityManager.createQuery(countQuery).getSingleResult();

    CriteriaQuery<AudioEntity> dataQuery = builder.createQuery(AudioEntity.class);
    Root<AudioEntity> dataRoot = dataQuery.from(AudioEntity.class);
    dataRoot.fetch("artist", JoinType.INNER);
    dataQuery.select(dataRoot);

    dataQuery.where(finalSpecification.toPredicate(dataRoot, dataQuery, builder));
    dataQuery.orderBy(builder.desc(dataRoot.get("approvalTime")), builder.desc(dataRoot.get("creationTime")));
    TypedQuery<AudioEntity> typedQuery = entityManager.createQuery(dataQuery);
    typedQuery.setFirstResult(audioQueryModel.getPageNo() * audioQueryModel.getPageSize());
    typedQuery.setMaxResults(audioQueryModel.getPageSize());
    List<AudioEntity> audioList = typedQuery.getResultList();
    return new PaginatedResponse<>(entityToDtoMapper(audioList), count,
        audioQueryModel.getPageNo(), audioList.size());
  }

  @Override
  public PaginatedResponse<AudioDto> retrieveArtistAudios(String artistId, Pagination pagination) {
    Page<AudioEntity> audioPage = audioRepository.findAllByArtist_ArtistId(artistId,
        PageRequest.of(pagination.getPageNo(), pagination.getPageSize(), Sort.by("creationTime").descending()));
    return new PaginatedResponse<>(entityToDtoMapper(audioPage.getContent()), audioPage.getTotalElements(),
        audioPage.getNumber(), audioPage.getSize());
  }

  @Transactional
  @Override
  public AudioDto updateAudio(AudioDto audioDto) {
    AudioEntity audioEntity = findByAudioId(audioDto.getAudioId());
    List<String> errorMessages = new ArrayList<>();
    if (audioDto.getTitle() != null) {
      if (audioDto.getTitle().isBlank()) {
        errorMessages.add("Title can not be blank");
      } else {
        audioEntity.setTitle(audioDto.getTitle());
      }
    }
    if (audioDto.getDescription() != null) {
      if (audioDto.getDescription().isBlank()) {
        errorMessages.add("Description can not be blank");
      } else {
        audioEntity.setDescription(audioDto.getDescription());
      }
    }
    if (audioDto.getSummary() != null) {
      if (audioDto.getSummary().isBlank()) {
        errorMessages.add("Summary can not be blank");
      } else {
        audioEntity.setSummary(audioDto.getSummary());
      }
    }
    if (audioDto.getGenre() != null) {
      audioEntity.setGenre(audioDto.getGenre());
    }

    if (audioDto.getArtist().getArtistId() != null && !audioDto.getArtist().getArtistId().isBlank()) {
      ArtistEntity artistEntity = artistRepository.findByArtistId(audioDto.getArtist().getArtistId());
      if (artistEntity == null) {
        throw new CommonException(Messages.ARTIST_NOT_FOUND, HttpStatus.FORBIDDEN);
      }
      audioEntity.setArtist(artistEntity);
    }
    if (audioDto.getImage() != null) {
      String audioImageName = audioDto.getAudioId() + utils.getFileExtension(audioDto.getImage());
      fileUploadUtil.saveFile(audioImageDirectory, audioImageName, audioDto.getImage());
      audioEntity.setImageUrl("/audio-images/" + audioImageName);
    }
    if (audioDto.getApprovalStatus() != null) {
      if (audioEntity.getApprovalStatus() == SongApprovalStatus.APPROVED
          && audioDto.getApprovalStatus() == SongApprovalStatus.REJECTED) {
        throw new CommonException(Messages.REJECTION_ERROR, HttpStatus.FORBIDDEN);
      }
      if (audioEntity.getApprovalStatus() == SongApprovalStatus.REJECTED
          && audioDto.getApprovalStatus() == SongApprovalStatus.APPROVED) {
        throw new CommonException(Messages.APPROVAL_ERROR, HttpStatus.FORBIDDEN);
      }
      audioEntity.setApprovalStatus(audioDto.getApprovalStatus());
    }
    if (audioDto.getCheckedBy() != null) {
      audioEntity.setCheckedBy(audioDto.getCheckedBy());
    }
    if (errorMessages.size() > 0) {
      throw new ValidationFailedException(errorMessages);
    }
    return modelMapper.map(audioRepository.save(audioEntity), AudioDto.class);
  }

  @Transactional
  @Override
  public void deleteAudio(String audioId, String requestorUserId) {
    AudioEntity audioEntity = findByAudioId(audioId);
    UserEntity userEntity = userRepository.findByUserId(requestorUserId);
    if (userEntity == null) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    if (userEntity.getRole() == Role.ADMIN || audioEntity.getCreatorId().equals(requestorUserId)) {
      audioEntity.setDeleted(true);
      audioRepository.save(audioEntity);
    } else {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public void saveListenTime(ListenTimeDto listenTimeDto) {
    ListenTimeEntity listenTimeEntity = modelMapper.map(listenTimeDto, ListenTimeEntity.class);
    listenTimeRepository.save(listenTimeEntity);
  }

  private AudioEntity findByAudioId(String audioId) {
    AudioEntity audioEntity = audioRepository.findByAudioId(audioId);
    if (audioEntity == null) {
      throw new CommonException(Messages.AUDIO_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    return audioEntity;
  }

  private List<AudioDto> entityToDtoMapper(List<AudioEntity> audioEntities) {
    List<AudioDto> audioDtos = new ArrayList<>();
    for (AudioEntity audioEntity : audioEntities) {
      audioDtos.add(modelMapper.map(audioEntity, AudioDto.class));
    }
    return audioDtos;
  }

}
