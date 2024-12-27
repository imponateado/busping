package com.example.busping;

import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button editarButton;
    private EditText linhaEditText;
    private RadioGroup rotaGroup;
    private TextView statusMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge content with system bars handling
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main); // Link to your layout XML file

        // Initialize views
        editarButton = findViewById(R.id.editar);
        linhaEditText = findViewById(R.id.linha);
        rotaGroup = findViewById(R.id.rotaGroup);
        statusMessageTextView = findViewById(R.id.statusMessage);

        // Set an OnClickListener on the button to handle clicks
        editarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the line number and the selected route
                String linha = linhaEditText.getText().toString(); // Get text from EditText
                int selectedRota = rotaGroup.getCheckedRadioButtonId(); // Get the selected radio button

                if (selectedRota == R.id.ida) {
                    // Handle "IDA" route
                    statusMessageTextView.setText("Bus is on IDA route: " + linha);
                } else if (selectedRota == R.id.volta) {
                    // Handle "VOLTA" route
                    statusMessageTextView.setText("Bus is on VOLTA route: " + linha);
                } else {
                    statusMessageTextView.setText("Please select a route.");
                    return;
                }

                // Call the API with the line number
                new FetchStatusTask().execute(linha);
            }
        });

        // Handle system window insets (like status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private class FetchStatusTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String linha = params[0];
            String url = "https://192.168.1.7:1235/linha?num=" + linha; // API URL with the linha parameter
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    return jsonObject.optString("msg", "No message found");
                } else {
                    return "Error: " + response.code();
                }
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Update the UI with the result
            statusMessageTextView.setText(result);
        }
    }
}
