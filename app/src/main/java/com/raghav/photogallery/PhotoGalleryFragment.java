package com.raghav.photogallery;

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

        private TextView mTextView;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;

        }
        public void bindGalleryItem(GalleryItem item){
            mTextView.setText(item.toString());
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
            TextView mTextView = new TextView(getActivity());

            return new PhotoHolder(mTextView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int i) {
            photoHolder.bindGalleryItem(mGalleryItemsList.get(i));
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
