package no.vicx.backend.stackoverflow.valueserializermodifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stackoverflow")
public class ValueSerializerModifierDemoController {

    @GetMapping("/get-base-models")
    public List<BaseModel> getBaseModels() {
        return List.of(
                new BaseModel.User(1, "a@b.c"),
                new BaseModel.Order(2, 42.0)
        );
    }
}
