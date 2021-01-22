package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.CommonControllerService;
import com.upgrad.quora.service.business.QuestionControllerService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private CommonControllerService commonControllerService;

    @Autowired
    private QuestionControllerService questionControllerService;


    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest)
            throws AuthorizationFailedException {
        String accessToken = authorization.split("Bearer")[0];

        final UserEntity userEntity = commonControllerService.getUser(accessToken);

        //Question Entity object for persistence
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userEntity);
        questionEntity.setDate(LocalDateTime.now());
        questionEntity.setContent(questionRequest.getContent());

        final QuestionEntity createdQuestionEntity = questionControllerService.createQuestion(questionEntity);

        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        String accessToken = authorization.split("Bearer")[0];

        final UserEntity userEntity = commonControllerService.getUser(accessToken);

        List<QuestionEntity> allQuestions = questionControllerService.getAllQuestions();

        List<QuestionDetailsResponse> qResponseList = new ArrayList<>();

        for (QuestionEntity q : allQuestions) {
            qResponseList.add(new QuestionDetailsResponse().id(q.getUuid()).content(q.getContent()));
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(qResponseList, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@RequestHeader("authorization") final String authorization,
                                                                               @PathVariable("userId") final String userUUID   ) throws AuthorizationFailedException
    {
        String accessToken = authorization.split("Bearer")[0];

        final UserEntity userEntity = commonControllerService.getUser(accessToken);

        List<QuestionEntity> allQuestions = questionControllerService.getAllQuestionsByUser(userEntity);

        List<QuestionDetailsResponse> qResponseList = new ArrayList<>();

        for (QuestionEntity q : allQuestions) {
            qResponseList.add(new QuestionDetailsResponse().id(q.getUuid()).content(q.getContent()));
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(qResponseList, HttpStatus.OK);

    }


}
