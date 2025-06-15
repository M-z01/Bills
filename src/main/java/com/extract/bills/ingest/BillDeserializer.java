package com.extract.bills.ingest;

import java.lang.reflect.Type;
import com.extract.bills.bill.Bill;
import com.google.gson.*;

public class BillDeserializer implements JsonDeserializer<Bill> {
    @Override
    public Bill deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();


        if (obj.has("bill")) {
            obj = obj.getAsJsonObject("bill");
        }

        Bill bill = new Bill();

        JsonElement typeElem = obj.get("type");
        if (typeElem != null && !typeElem.isJsonNull()) bill.setType(typeElem.getAsString());

        JsonElement numberElem = obj.get("number");
        if (numberElem != null && !numberElem.isJsonNull()) bill.setNumber(Integer.parseInt(numberElem.getAsString()));

        JsonElement urlElem = obj.get("url");
        if (urlElem != null && !urlElem.isJsonNull()) bill.setUrl(urlElem.getAsString());

        JsonElement updateDateElem = obj.get("updateDateIncludingText");
        if (updateDateElem != null && !updateDateElem.isJsonNull()) bill.setUpdateDateIncludingText(updateDateElem.getAsString());



        return bill;
    }
}
