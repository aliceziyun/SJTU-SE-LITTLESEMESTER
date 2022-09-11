package com.example.myapplication.options.json;

import com.google.gson.*;

import java.lang.reflect.Type;

import com.example.myapplication.options.axis.Axis;
import com.example.myapplication.options.axis.CategoryAxis;
import com.example.myapplication.options.axis.TimeAxis;
import com.example.myapplication.options.axis.ValueAxis;
import com.example.myapplication.options.code.AxisType;

public class AxisDeserializer implements JsonDeserializer<Axis> {
    /**
     * 设置json,typeOfT,context值
     *
     * @param json
     * @param typeOfT
     * @param context
     */
    @Override
    public Axis deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        String _type = jsonObject.get("type").getAsString();
        AxisType type = AxisType.valueOf(_type);
        Axis axis = null;
        switch (type) {
            case category:
                axis = context.deserialize(jsonObject, CategoryAxis.class);
                break;
            case value:
                axis = context.deserialize(jsonObject, ValueAxis.class);
                break;
            case time:
                axis = context.deserialize(jsonObject, TimeAxis.class);
                break;
        }
        return axis;
    }
}
