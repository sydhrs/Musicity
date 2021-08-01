/*
    Code written by ishaanjav
    github.com/ishaanjav
    App on Google Play Store: https://play.google.com/store/apps/details?id=app.anany.faceanalyzer
*/
package com.students.fyp.emotionrecognition.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.ActivityCameraBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.PictureResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CameraActivity extends AppCompatActivity {

    private FaceServiceClient faceServiceClient;

    ActivityCameraBinding binding;
    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        faceServiceClient = new FaceServiceRestClient("https://appsians.cognitiveservices.azure.com/face/v1.0", "8d5c8550a5754c0198a3664b6b1bf799");

        binding.camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                super.onPictureTaken(result);

                result.toBitmap(new BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        CameraActivity.bitmap = bitmap;
                        detectAndFrame(bitmap);
                    }
                });

            }
        });

        binding.takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.camera.takePicture();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.camera.close();
    }

    private void detectAndFrame(final Bitmap mBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream((outputStream.toByteArray()));

        AsyncTask<InputStream, String, Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>() {
            KProgressHUD pd;



            @Override
            protected Face[] doInBackground(InputStream... inputStreams) {

                publishProgress("Detecting...");
                //This is where you specify the FaceAttributes to detect. You can change this for your own use.
                FaceServiceClient.FaceAttributeType[] faceAttr = new FaceServiceClient.FaceAttributeType[]{
                        FaceServiceClient.FaceAttributeType.HeadPose,
                        FaceServiceClient.FaceAttributeType.Age,
                        FaceServiceClient.FaceAttributeType.Gender,
                        FaceServiceClient.FaceAttributeType.Emotion,
                        FaceServiceClient.FaceAttributeType.FacialHair
                };

                try {
                    Face[] result = faceServiceClient.detect(inputStreams[0],
                            true,
                            false,
                            faceAttr);

                    if (result == null) {
                        publishProgress("Detection failed. Nothing detected.");
                    }

                    publishProgress(String.format("Detection Finished. %d face(s) detected", result.length));
                    return result;
                } catch (Exception e) {
                    publishProgress("Detection Failed: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                pd = KProgressHUD.create(CameraActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setDetailsLabel(getString(R.string.analysing_photo))
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f);
                binding.camera.close();
                pd.show();
            }

            @Override
            protected void onProgressUpdate(String... values) {

            }

            @Override
            protected void onPostExecute(Face[] faces) {
                pd.dismiss();
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                Gson gson = new Gson();
                String data = gson.toJson(faces);
                if (faces == null || faces.length == 0) {
                    showSnack("No faces detected.");
                    binding.camera.open();
                } else {
                    try {
                        JSONArray array = new JSONArray(data);
                        JSONObject object = array.getJSONObject(0);
                        JSONObject emotionObj = object.getJSONObject("faceAttributes")
                                .getJSONObject("emotion");

                        double anger, contempt, disgust, fear, happiness, neutral, sadness, surprise;
                        anger =  emotionObj.getDouble("anger");
                        contempt =  emotionObj.getDouble("contempt");
                        disgust =  emotionObj.getDouble("disgust");
                        fear =  emotionObj.getDouble("fear");
                        happiness =  emotionObj.getDouble("happiness");
                        neutral =  emotionObj.getDouble("neutral");
                        sadness =  emotionObj.getDouble("sadness");
                        surprise =  emotionObj.getDouble("surprise");

                        String feeling = largestFeeling(anger, contempt, disgust,fear,happiness,
                                neutral, sadness, surprise);

                        intent.putExtra("feeling", feeling);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        detectTask.execute(inputStream);
    }

    String largestFeeling(double anger, double contempt, double disgust, double fear,
                          double happiness, double neutral, double sadness, double surprise) {
        if(anger > contempt && anger > disgust && anger > fear && anger > happiness
                && anger > neutral && anger > sadness && anger > surprise) {
            return "Angry";
        } else if(contempt > anger && contempt > disgust && contempt > fear && contempt > happiness
                && contempt > neutral && contempt > sadness && contempt > surprise) {
            return "Contempt";
        } else if(disgust > anger && disgust > contempt && disgust > fear && disgust > happiness
                && disgust > neutral && disgust > sadness && disgust > surprise) {
            return "Disgust";
        } else if(fear > anger && fear > contempt && fear > disgust && fear > happiness
                && fear > neutral && fear > sadness && fear > surprise) {
            return "Fear";
        } else if(happiness > anger && happiness > contempt && happiness > disgust && happiness > fear
                && happiness > neutral && happiness > sadness && happiness > surprise) {
            return "Happiness";
        } else if(neutral > anger && neutral > contempt && neutral > disgust && neutral > fear
                && neutral > happiness && neutral > sadness && neutral > surprise) {
            return "Neutral";
        } else if(sadness > anger && sadness > contempt && sadness > disgust && sadness > fear
                && sadness > happiness && sadness > neutral && sadness > surprise) {
            return "Sadness";
        } else if(surprise > anger && surprise > contempt && surprise > disgust && surprise > fear
                && surprise > happiness && surprise > neutral && surprise > sadness) {
            return "Surprise";
        } else {
            return "Not Detected";
        }
    }



    void showSnack(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

}
