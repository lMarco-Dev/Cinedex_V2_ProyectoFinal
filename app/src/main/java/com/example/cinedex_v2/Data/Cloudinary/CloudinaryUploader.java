package com.example.cinedex_v2.Data.Cloudinary;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudinaryUploader {

    private static final String CLOUD_NAME = "dhfs8sqpe";
    private static final String UPLOAD_PRESET = "android_unsigned";

    private static final String UPLOAD_URL =
            "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";

    // Convierte el URI a File
    public static File getFileFromUri(Context context, Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);

        if (cursor == null) return null;

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return new File(filePath);
    }

    // Subir imagen a Cloudinary
    public static String uploadImage(File file) throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(
                MediaType.parse("image/*"), file
        );

        // 🚀 CORREGIDO: SE USA MultipartBody.Builder
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Error en Cloudinary: " + response.code() + " - " + response.message());
        }

        String responseBody = response.body().string();

        // Si la respuesta NO es JSON válido → dar error claro
        try {
            JSONObject json = new JSONObject(responseBody);
            return json.getString("secure_url");
        } catch (Exception e) {
            throw new IOException("Respuesta inválida de Cloudinary:\n" + responseBody);
        }
    }
}
