package life.islami.app.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import life.islami.app.data.entities.SubscriptionEntity;
import life.islami.app.data.repositories.SubscriptionRepository;
import life.islami.app.dtos.SubscriptionDto;
import life.islami.app.enums.Messages;
import life.islami.app.exceptions.CommonException;
import life.islami.app.services.SubscriptionService;
import life.islami.app.utils.CommonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubscriptionServiceImpl implements SubscriptionService {
  private final SubscriptionRepository subscriptionRepository;
  private final ModelMapper modelMapper;
  private final CommonUtils utils;

  @Override
  public SubscriptionDto createSubscription(SubscriptionDto subscriptionDto) {
    SubscriptionEntity subscriptionEntity = modelMapper.map(subscriptionDto, SubscriptionEntity.class);
    subscriptionEntity.setSubscriptionId(utils.generateSubscriptionId());
    subscriptionEntity.setActive(true);
    SubscriptionEntity newSubscriptionEntity = subscriptionRepository.save(subscriptionEntity);
    return modelMapper.map(newSubscriptionEntity, SubscriptionDto.class);
  }

  @Override
  public List<SubscriptionDto> retrieveSubscriptions() {
    List<SubscriptionEntity> subscriptionEntities = subscriptionRepository.findAllByActive(true);
    List<SubscriptionDto> subscriptionDtos = new ArrayList<>();
    for (SubscriptionEntity subscriptionEntity : subscriptionEntities) {
      subscriptionDtos.add(modelMapper.map(subscriptionEntity, SubscriptionDto.class));
    }
    return subscriptionDtos;
  }

  @Override
  public void deleteSubscription(String subscriptionId) {
    SubscriptionEntity subscriptionEntity = subscriptionRepository.findBySubscriptionId(subscriptionId);
    if (subscriptionEntity == null) {
      throw new CommonException(Messages.SUBSCRIPTION_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    subscriptionEntity.setActive(false);
    subscriptionRepository.save(subscriptionEntity);
  }

}
