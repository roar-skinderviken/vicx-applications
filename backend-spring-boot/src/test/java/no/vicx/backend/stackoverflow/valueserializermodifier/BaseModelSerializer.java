package no.vicx.backend.stackoverflow.valueserializermodifier;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.Base64;

class BaseModelSerializer extends ValueSerializer<BaseModel> {

    @Override
    public void serialize(BaseModel value, JsonGenerator gen, SerializationContext ctxt) {
        gen.writeStartObject();

        // Mask ID
        String maskedId = "usr_" + Base64.getUrlEncoder().encodeToString(Long.toString(value.getId()).getBytes());
        gen.writeName("id");
        gen.writeString(maskedId);

        // Subclass-specific fields
        if (value instanceof BaseModel.User user) {
            gen.writeName("email");
            gen.writeString(user.getEmail());
        } else if (value instanceof BaseModel.Order order) {
            gen.writeName("total");
            gen.writeNumber(order.getTotal());
        }

        gen.writeEndObject();
    }
}