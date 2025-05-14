package study.goorm.domain.model.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.goorm.domain.model.exception.TestException;
import study.goorm.global.common.response.BaseResponse;
import study.goorm.global.error.code.status.ErrorStatus;
import study.goorm.global.error.code.status.SuccessStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    @GetMapping("/execute")
    public BaseResponse<Void> test(@RequestParam String error){

        if(error.equals("yes")){
            throw new TestException(ErrorStatus._BAD_REQUEST);
        }

        return BaseResponse.onSuccess(SuccessStatus.OK,null);
    }
}
