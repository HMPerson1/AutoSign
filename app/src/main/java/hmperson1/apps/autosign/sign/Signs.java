package hmperson1.apps.autosign.sign;

import android.content.Context;
import android.graphics.Color;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import hmperson1.apps.autosign.R;
import nullnull.fontslibrary.TypefaceUtils;

/**
 * Manages {@link Sign}s.
 */
public class Signs {
    private static final String FILE_NAME = "Signs.json";
    private static final Type LIST_TYPE = /*@formatter:off*/ new TypeToken<List<Sign>>(){}.getType(); /*@formatter:on*/
    private static Signs instance;
    private final List<Sign> signs;
    private final Context context;

    private Signs(Context ctx) {
        List<Sign> tmp;
        try {
            Gson gson = new Gson();
            tmp = gson.fromJson(new InputStreamReader(new BufferedInputStream(ctx.openFileInput(FILE_NAME))), LIST_TYPE);
        } catch (FileNotFoundException | JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
            tmp = new ArrayList<>();
        }
        System.out.println(tmp);
        context = ctx;
        signs = tmp;

        if (signs.size() < 1) {
            addSign();
        }
    }

    public static Signs instance(Context ctx) {
        if (instance == null) {
            instance = new Signs(ctx.getApplicationContext());
        }
        return instance;
    }

    public void save() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(signs);
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)))) {
            out.write(json);
        }
    }

    public void addSign() {
        addSign(context.getResources().getString(R.string.default_sign_text), TypefaceUtils.SANS_SERIF_TYPEFACE_NAME, Color.WHITE, Color.BLACK);
    }

    public void addSign(String text, String typeface, int fontColor, int bgColor) {
        signs.add(new Sign(text, typeface, fontColor, bgColor));
    }

    public Signs updateSign(int id, String text, String typeface, int fontColor, int bgColor) {
        signs.set(id, new Sign(text, typeface, fontColor, bgColor));
        return this;
    }

    public Sign removeSign(int id) {
        return signs.remove(id);
    }

    public Sign getSign(int id) {
        return signs.get(id);
    }

    public List<Sign> getSigns() {
        return signs;
    }

    public int size() {
        return signs.size();
    }
}
