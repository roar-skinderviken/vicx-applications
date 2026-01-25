package no.vicx.backend.stackoverflow.valueserializermodifier;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.ValueSerializerModifier;

class MySerializerModifier extends ValueSerializerModifier {

    @Override
    public ValueSerializer<?> modifySerializer(SerializationConfig config,
                                               BeanDescription.Supplier beanDesc,
                                               ValueSerializer<?> serializer) {
        if (BaseModel.class.isAssignableFrom(beanDesc.get().getBeanClass())) {
            return new BaseModelSerializer();
        }
        return serializer;
    }
}