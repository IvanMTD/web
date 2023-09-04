package ru.workwear.server.web.validations;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.workwear.server.web.dto.AddressDTO;

@Component
public class AddressValidation implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(AddressDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AddressDTO addressDTO = (AddressDTO) target;
        if(addressDTO.getIndex() < 100000){
            errors.rejectValue("index","","Индекс не может быть меньше 6 чисел");
        }
    }
}
