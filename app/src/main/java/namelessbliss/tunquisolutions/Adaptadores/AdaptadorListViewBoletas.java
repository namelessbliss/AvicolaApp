package namelessbliss.tunquisolutions.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import namelessbliss.tunquisolutions.Modelo.Boleta;
import namelessbliss.tunquisolutions.Modelo.Cliente;
import namelessbliss.tunquisolutions.R;

public class AdaptadorListViewBoletas extends BaseAdapter {

    private Context contexto;
    private int layout;
    private List<Boleta> lista;

    public AdaptadorListViewBoletas(Context context, int layout, List<Boleta> list) {
        this.contexto = context;
        this.layout = layout;
        this.lista = list;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            /* Solo si esta nulo, el decir, por primera vez en ser renderizado, inflamos
             y adjuntamos las referencias del layout en una nueva instancia de nuestro
             ViewHolder, y lo insertamos dentro del convertView, para reciclar su uso */

            convertView = LayoutInflater.from(contexto).inflate(layout, null);
            holder = new ViewHolder();
            holder.nombre = (TextView) convertView.findViewById(R.id.textViewName);
            holder.icono = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            convertView.setTag(holder);

        } else {
            // obtenemos la referencia que posteriomente pusimos dentro del convertView
            // y asi, reciclamos su uso sin necesidad de buscar de nuevo referencias con finviewbyid
            holder = (ViewHolder) convertView.getTag();

        }

        final Boleta currentFruit = (Boleta) getItem(position);
        holder.nombre.setText(currentFruit.getFecha());
        holder.icono.setImageResource(currentFruit.getIcono());

        return convertView;
    }

    class ViewHolder {
        private TextView nombre;
        private ImageView icono;
    }
}
