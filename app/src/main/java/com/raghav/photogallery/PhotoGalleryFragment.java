package com.raghav.photogallery;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment{
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItemsList = new ArrayList<>();


    private class FetchItemTask extends AsyncTask<Void,Void,List<GalleryItem>>{
        private static final String TAG = "PhotoGalleryFragment";
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            return new Fetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItemsList = galleryItems;
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{


        private ImageView mImageItem;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mImageItem = (ImageView) itemView.findViewById(R.id.item_image_view);

        }
        public void bindDrawable(Drawable drawable){
            mImageItem.setImageDrawable(drawable);
        }
    }

    private class photoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<GalleryItem> mGalleryItemsList;

        public photoAdapter(List<GalleryItem> list){
            mGalleryItemsList = list;
        }
        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
           LayoutInflater inflater = LayoutInflater.from(getActivity());
           View view = inflater.inflate(R.layout.list_item_gallery,viewGroup,false);
           return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int i) {
            GalleryItem galleryItem = mGalleryItemsList.get(i);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_foreground);
            photoHolder.bindDrawable(drawable);
        }

        @Override
        public int getItemCount() {
            return mGalleryItemsList.size();
        }
    }

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery,container,false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    Toast.makeText(getActivity(),"Last",Toast.LENGTH_SHORT).show();
                    Fetcher.page++;
                    List<GalleryItem> newList = new ArrayList<>();
                    new FetchItemTask().execute();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        setupAdapter();
        return v;
    }

    private void setupAdapter() {
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new photoAdapter(mItemsList));
        }
    }
}
