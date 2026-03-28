package com.example.User.Models.Customer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

public  class PointSerializer extends StdSerializer<Point> {
    public PointSerializer() {
        super(Point.class);
    }

    @Override
    public void serialize(Point value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {

        gen.writeStartObject();
        gen.writeNumberField("lat", value.getY());
        gen.writeNumberField("lon", value.getX());
        gen.writeEndObject();
    }
}