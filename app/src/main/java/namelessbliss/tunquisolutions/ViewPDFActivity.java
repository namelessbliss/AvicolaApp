package namelessbliss.tunquisolutions;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ViewPDFActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        pdfView =(PDFView) findViewById(R.id.pdfView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            file = new File(bundle.getString("path",""));
        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pdf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.compartirPDF) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");

            Uri uri = Uri.parse("file://" + getNombreBoleta());
            //intent.setPackage("com.whatsapp");
            intent.putExtra(Intent.EXTRA_TEXT, crearMensaje());
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            try {
                startActivity(Intent.createChooser(intent, "Compartir Boleta"));
            } catch (Exception e) {
                Toast.makeText(ViewPDFActivity.this, "Error: El pdf no existe", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String crearMensaje() {
        return "Le envio su boleta de compra, gracias por su preferencia.";
    }

    /**
     * Obtiene y retorna la ubicacion y el nombre del pdf
     * @return
     */
    private String getNombreBoleta(){
        return file.getAbsolutePath();
    }
}
