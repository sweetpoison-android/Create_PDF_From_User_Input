package com.example.createpdffromuserinput;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.Border3D;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText name_edittext,age_edittext,mobile_edittext,location_edittext;
    Button submit_button;
    String name,age,mobile,location;

    DateTimeFormatter dateFormatter,timeFormatter;
    BarcodeQRCode barcodeQRCode;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name_edittext = findViewById(R.id.main_edittext1);
        age_edittext = findViewById(R.id.main_edittext2);
        mobile_edittext = findViewById(R.id.main_edittext3);
        location_edittext = findViewById(R.id.main_edittext4);
        submit_button = findViewById(R.id.main_button1);



        submit_button.setOnClickListener(v -> {
            name = name_edittext.getText().toString();
            age = age_edittext.getText().toString();
            mobile = mobile_edittext.getText().toString();
            location = location_edittext.getText().toString();

            try {
               createPdf(name, age, mobile, location);
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }

       });


    }


    private void createPdf(String name, String age, String mobile, String location) throws FileNotFoundException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "mypdf.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A6);
        document.setMargins(0, 0, 0, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getDrawable(R.drawable.road);
        }
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);

        Paragraph visitor_ticket = new Paragraph("Visitor Ticket")
                .setBold()
                .setFontSize(25)
                .setFontColor(new DeviceRgb(255,215,0))
                .setBackgroundColor(ColorConstants.BLUE)
                .setMargins(10,20,10,20)
                .setBorder(Border.NO_BORDER)
                .setBorderRadius(new BorderRadius(25))
                .setTextAlignment(TextAlignment.CENTER);
        Paragraph group = new Paragraph("Tourist Department \n "+"Government of Bihar, India")
                .setItalic()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER);
        Paragraph varanasi = new Paragraph("Varanasi")
                .setBold()
                .setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER);

        float[] width = {100f, 100f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addCell(new Cell().add(new Paragraph("Name")));
        table.addCell(new Cell().add(new Paragraph(name)));

        table.addCell(new Cell().add(new Paragraph("Age")));
        table.addCell(new Cell().add(new Paragraph(age)));

        table.addCell(new Cell().add(new Paragraph("Mobile")));
        table.addCell(new Cell().add(new Paragraph(mobile)));

        table.addCell(new Cell().add(new Paragraph("Location")));
        table.addCell(new Cell().add(new Paragraph(location)));

        table.addCell(new Cell().add(new Paragraph("Date")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            table.addCell(new Cell().add(new Paragraph(LocalDate.now().format(dateFormatter))));
        }

            table.addCell(new Cell().add(new Paragraph("Time")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");    //for 24 hour format use HH
            table.addCell(new Cell().add(new Paragraph(LocalTime.now().format(timeFormatter))));
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            barcodeQRCode = new BarcodeQRCode(name+"\n"+age+"\n"+mobile+"\n"+location+"\n"+LocalDate.now().format(dateFormatter)+"\n"+LocalTime.now().format(timeFormatter));
        }

        PdfFormXObject pdfFormXObject = barcodeQRCode.createFormXObject(ColorConstants.BLACK, pdfDocument);
        Image barcodeimage = new Image(pdfFormXObject)
                .setWidth(100)
                .setHeight(100)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(image);
        document.add(visitor_ticket);
        document.add(group);
        document.add(varanasi);
        document.add(table);
        document.add(barcodeimage);

        document.close();
        Toast.makeText(MainActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();

    }
}