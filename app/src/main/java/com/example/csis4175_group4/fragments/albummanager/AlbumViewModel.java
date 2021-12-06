package com.example.csis4175_group4.fragments.albummanager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.csis4175_group4.fragments.groupmanager.Group;

public class AlbumViewModel extends ViewModel {

    private MutableLiveData<Album> selectedAlbum = new MutableLiveData<>();
    private MutableLiveData<String> selectedAlbumId = new MutableLiveData<>();

    private MutableLiveData<Photo> selectedPhoto = new MutableLiveData<>();
    private MutableLiveData<String> selectedPhotoId = new MutableLiveData<>();

    public LiveData<Album> getSelectedAlbum() {
        return selectedAlbum;
    }

    public void setSelectedAlbum(Album album) {
        selectedAlbum.setValue(album);
    }

    public LiveData<String> getSelectedAlbumId() {
        return selectedAlbumId;
    }

    public void setSelectedAlbumId(String albumIdId) {
        selectedAlbumId.setValue(albumIdId);
    }

    public MutableLiveData<Photo> getSelectedPhoto() {
        return selectedPhoto;
    }

    public void setSelectedPhoto(Photo photo) {
        selectedPhoto.setValue(photo);
    }

    public MutableLiveData<String> getSelectedPhotoId() {
        return selectedPhotoId;
    }

    public void setSelectedPhotoId(String photoId) {
        selectedPhotoId.setValue(photoId);
    }
}
