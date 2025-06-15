package com.extract.bills.ingest;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class InstantTypeAdapter extends TypeAdapter<Instant> {
    @Override
    public void write(JsonWriter out, Instant value) throws IOException {
        out.value(value == null ? null : value.toString());
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        String str = in.nextString();
        if (str == null || str.isEmpty()) return null;
        try {
            return Instant.parse(str); // Try full ISO-8601 first
        } catch (Exception e) {
            // Try parsing as yyyy-MM-dd (date only)
            try {
                LocalDate date = LocalDate.parse(str);
                return date.atStartOfDay().toInstant(ZoneOffset.UTC);
            } catch (Exception ex) {
                throw new IOException("Cannot parse date: " + str, ex);
            }
        }
    }
}