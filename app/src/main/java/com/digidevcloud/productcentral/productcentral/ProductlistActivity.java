package com.digidevcloud.productcentral.productcentral;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digidevcloud.productcentral.productcentral.models.ProductModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.digidevcloud.productcentral.productcentral.R.id.ivImage_link;
import static com.digidevcloud.productcentral.productcentral.R.id.tvProductEan;
import static com.digidevcloud.productcentral.productcentral.R.id.tvProductSku;
import static com.digidevcloud.productcentral.productcentral.R.id.tvProductTitle;
import static com.digidevcloud.productcentral.productcentral.R.id.tvRrp;

public class ProductlistActivity extends AppCompatActivity {

    private ListView lvProductlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);

        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        lvProductlist = (ListView)findViewById(R.id.lvProductlist);
        new JSONTask().execute("http://www.productcentral.co.uk/api/launchproducts");
    }

    public class JSONTask extends AsyncTask<String, String, List<ProductModel>> {
        @Override
        protected List<ProductModel> doInBackground(String... params){

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line ="";
                while((line = reader.readLine()) != null)
                {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("products");

                List<ProductModel> productModelList = new ArrayList<ProductModel>();

                for(int i=0; i < 6; i++){

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    JSONObject paramsObject =  new JSONObject(finalObject.getString("product_dims"));
                    ProductModel productModel = new ProductModel();
                    //JSONObject productDims = finalObject.getJSONObject("product_dims");
                    productModel.setProduct_sku(finalObject.getString("product_number" ));
                    productModel.setProduct_description(finalObject.getString("product_designation" ));
                    productModel.setProduct_ean(finalObject.getString("ean"));
                    productModel.setImage_link(paramsObject.getString("image_link"));
                    productModel.setRrp(finalObject.getDouble("RRP"));
                    //Add the final model to the list
                    productModelList.add(productModel);
                }
                return productModelList;

            } catch(MalformedURLException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }
                try{
                    if(reader != null){
                        reader.close();
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ProductModel> result){
            super.onPostExecute(result);

            if(result != null) {
                //TODO need to set the data to the list

                //create an adapter
                ProductAdapter adapter = new ProductAdapter(getApplicationContext(), R.layout.productrow, result);
                lvProductlist.setAdapter(adapter);
            }
            else{
                Toast.makeText(getApplicationContext(), "Wait", Toast.LENGTH_LONG).show();
            }

        }

    }

    public class ProductAdapter extends ArrayAdapter {

        private List<ProductModel> productModelList;
        private int resource;
        private LayoutInflater inflater;

        public ProductAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ProductModel> objects) {
            super(context, resource, objects);
            productModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivImage_link = (ImageView) convertView.findViewById(ivImage_link);
                holder.tvProductSku = (TextView) convertView.findViewById(tvProductSku);
                holder.tvProductTitle = (TextView) convertView.findViewById(tvProductTitle);
                holder.tvProductEan = (TextView) convertView.findViewById(tvProductEan);
                holder.tvRrp = (TextView) convertView.findViewById(tvRrp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvProductSku.setText(productModelList.get(position).getProduct_sku());
            holder.tvProductEan.setText(productModelList.get(position).getProduct_ean());
            holder.tvProductTitle.setText(productModelList.get(position).getProduct_decription());
            holder.tvRrp.setText(String.format("%.2f",productModelList.get(position).getRrp()));
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            final LinearLayout llProductItem = (LinearLayout) convertView.findViewById(R.id.llProductItem);

            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage("http://productcentral.co.uk/" + productModelList.get(position).getImage_link(), holder.ivImage_link, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            }); // Default options will be used

            llProductItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    //intent.putExtra("productID",productModelList.get(position).getDdc_vendor_product_id());
                    //startActivity(intent);
                }
            });

            return convertView;
        }

        class ViewHolder {
            private ImageView ivImage_link;
            private TextView tvProductSku;
            private TextView tvProductTitle;
            private TextView tvProductEan;
            private TextView tvRrp;
            private LinearLayout llProductItem;

        }

    }

}
