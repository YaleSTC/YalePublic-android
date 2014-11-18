package edu.yalestc.yalepublic.Events;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import edu.yalestc.yalepublic.Videos.JSONReader;

/**
 * Created by Stan Swidwinski on 11/17/14.
 */
public class EventsCategoriesJSONReader extends JSONReader {
    public EventsCategoriesJSONReader(String URL, Context context) {
        super(URL, context);
    }

    public void parseCategoryAddToReader(Bundle extras) {
        String categories = extras.getString("JsonCategories");
        if (categories.split(" ").length > 1) {
            this.addParams(new Pair<String, String>("categories", categories.split(" ")[0]));
            this.addParams(new Pair<String, String>("categories", categories.split(" ")[1]));
        } else {
            if(categories.equals("All"))
                return;
            else
                this.addParams(new Pair<String, String>("categories", categories));
        }
    }

}
