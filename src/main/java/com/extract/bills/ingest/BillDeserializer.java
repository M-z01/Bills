package com.extract.bills.ingest;

import java.lang.reflect.Type;
import com.extract.bills.bill.Bill;
import com.google.gson.*;

public class BillDeserializer implements JsonDeserializer<Bill> {
    public Bill deserialize(JsonElement json, Type tyoeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        Bill bill = new Bill();
        bill.setType(obj.get("type").getAsString());
        bill.setNumber(Integer.parseInt(obj.get("number").getAsString()));
        bill.setUrl(obj.get("url").getAsString());

        //Normalize the date
        String rawDate = obj.get("updateDateIncludingText").getAsString();
        bill.setUpdateDateIncludingText(rawDate);
        return bill;
    }
}
