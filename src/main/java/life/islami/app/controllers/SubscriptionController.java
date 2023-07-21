package life.islami.app.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import life.islami.app.dtos.SubscriptionDto;
import life.islami.app.models.request.SubscriptionCreationRequestModel;
import life.islami.app.models.response.SubscriptionRest;
import life.islami.app.services.SubscriptionService;
import life.islami.app.utils.CommonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
  private final SubscriptionService subscriptionService;
  private final CommonUtils utils;
  private final ModelMapper modelMapper;

  @PostMapping
  public SubscriptionRest createSubscription(
      @Valid @RequestBody SubscriptionCreationRequestModel subscriptionCreationRequestModel, Errors errors) {
    utils.handleValidationErrors(errors);
    return modelMapper.map(subscriptionService.createSubscription(
        modelMapper.map(subscriptionCreationRequestModel, SubscriptionDto.class)), SubscriptionRest.class);
  }

  @GetMapping
  public List<SubscriptionRest> getActiveSubscriptions() {
    List<SubscriptionRest> subscriptionRests = new ArrayList<>();
    List<SubscriptionDto> subscriptionDtos = subscriptionService.retrieveSubscriptions();
    for (SubscriptionDto subscriptionDto : subscriptionDtos) {
      subscriptionRests.add(modelMapper.map(subscriptionDto, SubscriptionRest.class));
    }
    return subscriptionRests;
  }

  @DeleteMapping("/{subscriptionId}")
  public ResponseEntity<String> deleteSubscription(@PathVariable String subscriptionId) {
    subscriptionService.deleteSubscription(subscriptionId);
    return new ResponseEntity<String>("Subscription deleted successfully.", null, HttpStatus.OK);
  }
}
