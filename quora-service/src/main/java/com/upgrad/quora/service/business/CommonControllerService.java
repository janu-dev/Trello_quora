package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonControllerService {

    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String uuid,final String authToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity accessToken = userDao.getUserAuthTokenEntity(authToken);
        UserEntity userEntity = userDao.getUser(uuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }else if(accessToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if(accessToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        return userEntity;
    }

    //Check user authorization with access token
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String authToken) throws AuthorizationFailedException {
        UserAuthTokenEntity accessToken = userDao.getUserAuthTokenEntity(authToken);

        if(accessToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if(accessToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        return userDao.getUser(accessToken.getUuid());

    }



}
