package ru.arkhipov.MySecondTestSpringBoot.service;

import org.springframework.stereotype.Service;
import  ru.arkhipov.MySecondTestSpringBoot.model.Response;
@Service

public interface ModifyResponseService {
    Response modify(Response response);
}
