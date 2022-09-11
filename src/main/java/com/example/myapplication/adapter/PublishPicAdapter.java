package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.List;

/**
 *
 * 发布图片的适配器
 */
public class PublishPicAdapter extends BaseAdapter{

    private Context context;
    private List<String> data;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private Boolean controller = false;

    public PublishPicAdapter(Context context,List<String> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_no_picture)
                .showImageOnFail(R.drawable.default_no_picture)
                .showImageForEmptyUri(R.drawable.default_no_picture)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        View testView = convertView;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.publish_img_item,parent,false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.publish_gridview_img);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Log.i("PicAdapter", String.valueOf(position) + data.size());

        if (position == 0) {
            viewHolder.imageView.setImageResource(R.drawable.add);
        } else {
            Log.i("PicAdapter", String.valueOf(position));
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            String url = ImageDownloader.Scheme.FILE.wrap(data.get(position - 1));
            Log.i("PicAdapter", String.valueOf(url));
            imageLoader.displayImage(url,viewHolder.imageView,options);
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
    }
}
