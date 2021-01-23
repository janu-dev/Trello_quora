package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonControllerService;
import com.upgrad.quora.service.business.QuestionControllerService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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

        final UserEntity userEntity = commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to post a question");

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

        commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to get all questions");

        List<QuestionEntity> allQuestions = questionControllerService.getAllQuestions();

        List<QuestionDetailsResponse> qResponseList = new ArrayList<>();

        for (QuestionEntity q : allQuestions) {
            qResponseList.add(new QuestionDetailsResponse().id(q.getUuid()).content(q.getContent()));
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(qResponseList, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@RequestHeader("authorization") final String authorization,
                                                                               @PathVariable("userId") final String userUUID) throws AuthorizationFailedException, UserNotFoundException {
        String accessToken = authorization.split("Bearer")[0];

        commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");

        final UserEntity userEntity = commonControllerService.getUserById(userUUID);

        List<QuestionEntity> allQuestions = questionControllerService.getAllQuestionsByUser(userEntity);

        List<QuestionDetailsResponse> qResponseList = new ArrayList<>();

        for (QuestionEntity q : allQuestions) {
            qResponseList.add(new QuestionDetailsResponse().id(q.getUuid()).content(q.getContent()));
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(qResponseList, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@RequestHeader("authorization") final String authorization,
                                                             @PathVariable("questionId") final String questionUUID,
                                                             final QuestionRequest questionRequest) throws AuthorizationFailedException, InvalidQuestionException
    {
        String accessToken = authorization.split("Bearer")[0];

        final UserEntity userEntity = commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to edit the question");

        final QuestionEntity questionEntity = questionControllerService.getQuestionById(questionUUID);

        commonControllerService.canEditOrDelete(questionEntity, userEntity, false);

        QuestionEntity updatedQuestion = new QuestionEntity();
        updatedQuestion.setUser(userEntity);
        updatedQuestion.setContent(questionRequest.getContent());
        updatedQuestion.setUuid(questionEntity.getUuid());
        updatedQuestion.setId(questionEntity.getId());
        updatedQuestion.setDate(questionEntity.getDate());

        questionControllerService.updateQuestion(updatedQuestion);


        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionUUID).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);


    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionUUID  ) throws AuthorizationFailedException, InvalidQuestionException  {

        String accessToken = authorization.split("Bearer")[0];

        final UserEntity userEntity = commonControllerService.getUser(accessToken, "ATHR-002", "User is signed out.Sign in first to edit the question");

        final QuestionEntity questionEntity = questionControllerService.getQuestionById(questionUUID);

        commonControllerService.canEditOrDelete(questionEntity, userEntity, true);

        questionControllerService.deleteQuestion(questionEntity);


        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionUUID).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);


    }





}
