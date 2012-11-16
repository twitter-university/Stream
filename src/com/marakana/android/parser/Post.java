package com.marakana.android.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;
import android.text.TextUtils;

import com.marakana.android.stream.db.StreamContract;


/**
 * Post
 */
public class Post implements Comparable<Post> {
    // !!! US Local???
    private static SimpleDateFormat FORMATTER
        = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    /**
     * @param context d
     * @param id
     * @return
     *
     *  Creates a new post from a given cursor.
     */
    public static Post getPost(long id, Cursor c) {
        Post post = null;

        // Position the cursor to the the first element
        if (c.moveToFirst()) {
            post = new Post(c.getLong(c.getColumnIndex(StreamContract.Feed.Columns.ID)));
            post.setTitle(c.getString(c.getColumnIndex(StreamContract.Feed.Columns.TITLE)));
            post.setDescription(c.getString(c.getColumnIndex(StreamContract.Feed.Columns.DESC)));
            post.setLink(c.getString(c.getColumnIndex(StreamContract.Feed.Columns.LINK)));
            post.setTimestamp(c.getLong(c.getColumnIndex(StreamContract.Feed.Columns.PUB_DATE)));
            post.setLink(c.getString(c.getColumnIndex(StreamContract.Feed.Columns.LINK)));
        }

        return post;
    }


    private final long id;
    private String title;
    private URL link;
    private String description;
    private Date date;

    /** Ctor: noargs */
    public Post() { this(-1); }

    /** @param id */
    public Post(long id) { this.id = id; }

    /** Ctor: copy
     * @param src
     */
    public Post(Post src) {
        this.id = -1;
        this.title = src.title;
        this.link = src.link;
        this.description = src.description;
        this.date = src.date;
    }

    /** @return the id */
    public long getId() { return id; }

    /** @return the title */
    public String getTitle() { return title; }
    /** @param title */
    public void setTitle(String title) { this.title = title.trim(); }

    /** @return the link */
    public URL getLink() { return link; }
    /** @return a string representation of the link */
    public String getLinkString() { return (null == link) ? "" : link.toString(); }
    /** @param link */
    public void setLink(String link) {
        if (!TextUtils.isEmpty(link)) {
            try { this.link = new URL(link); }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** @return the description */
    public String getDescription() { return description; }
    /** @param description */
    public void setDescription(String description) { this.description = description.trim(); }

    /** @return a formatted date string */
    public String getDate() { return FORMATTER.format(this.date); }
    /** @param date */
    public void setDate(String date) {
        // pad the date if necessary
        while ( !date.endsWith("00")) { date += "0"; }
        try { this.date = FORMATTER.parse(date.trim()); }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return the timestamp */
    public long getTimestamp() { return this.date.getTime(); }
    /** @param timestamp */
    public void setTimestamp(long timestamp) { this.date = new Date(timestamp); }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // StringBuilder sb = new StringBuilder();
        // sb.append("Title: ");
        // sb.append(title);
        // sb.append('\n');
        // sb.append("Date: ");
        // sb.append(this.getDate());
        // sb.append('\n');
        // sb.append("Link: ");
        // sb.append(link);
        // sb.append('\n');
        // sb.append("Description: ");
        // sb.append(description);
        // return sb.toString();
        return getTitle();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((date == null) ? 0 : date.hashCode());
        result = (prime * result) + ((description == null) ? 0 : description.hashCode());
        result = (prime * result) + ((link == null) ? 0 : link.hashCode());
        result = (prime * result) + ((title == null) ? 0 : title.hashCode());
        return Math.abs(result);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }

        if (!(obj instanceof Post)) { return false; }
        Post other = (Post) obj;

        if (date == null) {
            if (other.date != null) { return false; }
        }
        else if (!date.equals(other.date)) { return false; }

        if (description == null) {
            if (other.description != null) { return false; }
        }
        else if (!description.equals(other.description)) { return false; }

        if (link == null) {
            if (other.link != null) { return false; }
        }
        else if (!link.equals(other.link)) { return false; }

        if (title == null) {
            if (other.title != null) { return false; }
        }
        else if (!title.equals(other.title)) { return false; }

        return true;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Post another) {
        if (another == null) { return 1; }
        // sort descending, most recent first
        return another.date.compareTo(date);
    }
}
