package com.example.usuario.datosxml.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by usuario on 9/01/18.
 */

public class Noticias implements Parcelable {
    private String title;
    private String link;
    private String description;
    private String pubDate;

    public Noticias(String title, String link, String description, String pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return  title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    protected Noticias(Parcel in) {
        title = in.readString();
        link = in.readString();
        description = in.readString();
        pubDate = in.readString();
    }

    public static final Creator<Noticias> CREATOR = new Creator<Noticias>() {
        @Override
        public Noticias createFromParcel(Parcel in) {
            return new Noticias(in);
        }

        @Override
        public Noticias[] newArray(int size) {
            return new Noticias[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(pubDate);
    }
}
