package com.ex.smartmm.gallery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.ex.smartmm.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JSJ on 2016-11-21.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private Bitmap placeHolderBitmap;

    public static class AsyncDrawable extends BitmapDrawable{
        final WeakReference<BitmapWorkerTask> taskReference;
        public AsyncDrawable(Resources resources, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
            super(resources, bitmap);
            taskReference = new WeakReference(bitmapWorkerTask);
        }
        public BitmapWorkerTask getBitmapWorkerTask(){
            return taskReference.get();
        }
    }


    private static List<File> filePathList = new ArrayList<File>();
    public ImageAdapter(String path){
        setFileList(path);
    }


    public void setFileList(String path) {

		boolean rtn = false;
		File file = new File(path);
		File[] childFileList = file.listFiles();
		for (File childFile : childFileList) {
			if (childFile.isDirectory()) {
				setFileList(childFile.getAbsolutePath()); // 하위 디렉토리 루프
			} else {
				String fName = childFile.getName();
				String fPath = childFile.getPath();
				if(fName.toLowerCase().contains("png") || fName.toLowerCase().contains("jpg")){
					filePathList.add(new File(fPath));
				}
			}
		}
	}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File imageFile = filePathList.get(position);
//        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//        holder.getImageView().setImageBitmap(imageBitmap);
//        Picasso.with(holder.getImageView().getContext()).load(imageFile).into(holder.getImageView());
//        BitmapWorkerTask workerTask = new BitmapWorkerTask(holder.getImageView());
//        workerTask.execute(imageFile);
        if(checkBitmapWorkerTask(imageFile, holder.getImageView())){
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(holder.getImageView());
            AsyncDrawable asyncDrawable = new AsyncDrawable(holder.getImageView().getResources(), placeHolderBitmap, bitmapWorkerTask);
            holder.getImageView().setImageDrawable(asyncDrawable);
            bitmapWorkerTask.execute(imageFile);
        }
    }

    @Override
    public int getItemCount() { return filePathList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public ViewHolder(View view){
            super(view);
            imageView = (ImageView) view.findViewById(R.id.item_img);
            
        }
        public ImageView getImageView(){return imageView;}
    }


    public static boolean checkBitmapWorkerTask(File imageFile, ImageView imageView){
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if(bitmapWorkerTask != null){
            final File workerFile = bitmapWorkerTask.getmImageFile();
            if(workerFile != null){
                if(workerFile != imageFile){
                    bitmapWorkerTask.cancel(true);
                }else{
                    return false;
                }
            }
        }
        return true;
    }
    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView){
        Drawable drawable = imageView.getDrawable();
        if(drawable instanceof AsyncDrawable){
            AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
            return asyncDrawable.getBitmapWorkerTask();
        }
        return null;
    }

}
