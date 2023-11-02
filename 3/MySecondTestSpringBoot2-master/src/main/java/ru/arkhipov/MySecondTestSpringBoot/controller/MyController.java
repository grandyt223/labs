package ru.arkhipov.MySecondTestSpringBoot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.arkhipov.MySecondTestSpringBoot.model.Request;
import ru.arkhipov.MySecondTestSpringBoot.model.Response;
import ru.arkhipov.MySecondTestSpringBoot.model.Codes;
import ru.arkhipov.MySecondTestSpringBoot.model.ErrorMessages;
import ru.arkhipov.MySecondTestSpringBoot.model.ErrorCodes;
import ru.arkhipov.MySecondTestSpringBoot.service.ModifyResponseService;
import ru.arkhipov.MySecondTestSpringBoot.util.DateTimeUtil;
import ru.arkhipov.MySecondTestSpringBoot.service.ValidationService;
import ru.arkhipov.MySecondTestSpringBoot.exception.ValidationFailedException;

import javax.validation.Valid;
import java.util.Date;

@Slf4j
@RestController
public class MyController {
    private final ValidationService validationService;
    private final ModifyResponseService modifyResponseService;

    @Autowired
    public MyController(ValidationService validationService, @Qualifier("ModifySystemTimeResponseService") ModifyResponseService modifyResponseService) {
        this.validationService = validationService;
        this.modifyResponseService = modifyResponseService;
    }

    @PostMapping(value = "/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request, BindingResult bindingResult) {
        log.info("Полученный запрос: {}", request);

        if (bindingResult.hasErrors()) {
            Response response = Response.builder()
                    .uid(request.getUid())
                    .operationUid(request.getOperationUid())
                    .systemTime(DateTimeUtil.getCustomFormat().format(new Date()))
                    .code(Codes.FAILED)
                    .errorCode(ErrorCodes.VALIDATION_EXCEPTION)
                    .errorMessage(ErrorMessages.VALIDATION)
                    .build();

            log.error("Ошибка валидации запроса: {}", request);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Response response = Response.builder()
                .uid(request.getUid())
                .operationUid(request.getOperationUid())
                .systemTime(DateTimeUtil.getCustomFormat().format(new Date()))
                .code(Codes.SUCCESS)
                .errorCode(ErrorCodes.EMPTY)
                .errorMessage(ErrorMessages.EMPTY)
                .build();

        log.info("Сгенерированный ответ: {}", response);

        modifyResponseService.modify(response);
        log.info("Измененный ответ: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
