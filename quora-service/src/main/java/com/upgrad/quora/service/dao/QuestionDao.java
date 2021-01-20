package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext // container managed
    EntityManager entityManager; // provided by dependency spring-boot-starter-data-jpa

    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(){
      return entityManager.createQuery( "SELECT q from QuestionEntity q", QuestionEntity.class).getResultList();
    }

}
