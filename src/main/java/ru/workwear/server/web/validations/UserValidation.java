package ru.workwear.server.web.validations;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.workwear.server.web.dto.UserDTO;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserValidation implements Validator {

    private WebClient webClient;

    public UserValidation(){
        webClient = WebClient.create();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UserDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;

        checkPassword(userDTO,errors);
        checkAge(userDTO,errors);
    }

    public Mono<BindingResult> checkUsername(UserDTO userDTO, BindingResult errors){
        if (!userDTO.getUsername().equals("")) {
            return webClient.get()
                    .uri("http://localhost:9000/api/auth/user/check/" + userDTO.getUsername())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .map(existValue -> {
                        if (existValue) {
                            errors.rejectValue("username", "", "Имя пользователя занято");
                        }
                        return errors;
                    });
        }else{
            return Mono.just(errors);
        }
    }

    private void checkPassword(UserDTO userDTO, Errors errors){
        if(!userDTO.getPassword().equals(userDTO.getConfirmPassword())){
            errors.rejectValue("confirmPassword", "", "Пароль не совпадает");
        }
    }

    private void checkAge(UserDTO userDTO, Errors errors){
        LocalDate clientDate = userDTO.getBirthdate();
        LocalDate currentDate = LocalDate.now();
        try {
            Period period = Period.between(clientDate, currentDate);
            if(period.getYears() < 18){
                errors.rejectValue("birthdate","","Для регистрации вам должно быть больше 18 лет");
            }
        }catch (NullPointerException e){
            errors.rejectValue("birthdate","","Введите правильную дату");
        }
    }
}
