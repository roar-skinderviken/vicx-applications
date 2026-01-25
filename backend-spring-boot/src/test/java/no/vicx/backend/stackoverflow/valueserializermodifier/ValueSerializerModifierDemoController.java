package no.vicx.backend.stackoverflow.valueserializermodifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stackoverflow")
public class ValueSerializerModifierDemoController {

    @GetMapping("/get-user")
    public BaseModel.User test() {
        return new BaseModel.User(1, "a@b.c");
    }
}
