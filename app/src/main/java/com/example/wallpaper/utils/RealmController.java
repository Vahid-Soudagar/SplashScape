package com.example.wallpaper.utils;

import com.example.wallpaper.models.Photo;

import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmController {

    private final Realm realm;

    public RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public void savePhoto(Photo photo) {
        realm.beginTransaction();
        realm.copyToRealm(photo);
        realm.commitTransaction();
    }

    public void deletePhoto(Photo photo) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Photo res = realm.where(Photo.class)
                        .equalTo("id", photo.getId())
                        .findFirst();
                Objects.requireNonNull(res).deleteFromRealm();
            }
        });

    }

    public boolean isPhotoExist(String photoId) {
        Photo res = realm.where(Photo.class)
                .equalTo("id", photoId)
                .findFirst();
        if (res == null){
            return false;
        }
        return true;
    }

    public List<Photo> getPhotos() {
        return realm.where(Photo.class).findAll();
    }

}
