package com.example.captureimagesp.TextToPdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.captureimagesp.BuildConfig;
import com.example.captureimagesp.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class TexxtToPdfActivity extends AppCompatActivity {

    EditText editText;
    Button exportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texxt_to_pdf);

        editText = findViewById(R.id.edit_text);
        exportButton = findViewById(R.id.export_button);

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(TexxtToPdfActivity.this, "Please enter some text", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "my_pdf_file.pdf");
                    FileOutputStream outputStream = new FileOutputStream(pdfFile);
                    Document document = new Document();
                    PdfWriter.getInstance(document, outputStream);

                    Uri pdfUri = FileProvider.getUriForFile(TexxtToPdfActivity.this, BuildConfig.APPLICATION_ID + ".provider", pdfFile);


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(pdfUri, "application/pdf");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);

                    document.open();
                    document.add(new Paragraph(text));

                    document.close();

                    Toast.makeText(TexxtToPdfActivity.this, "PDF file exported successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(TexxtToPdfActivity.this, "Error exporting PDF file", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}