package com.jideguru.epub_viewer;

import com.folioreader.model.Bookmark;
import com.folioreader.model.HighLight;

import java.util.Date;

/**
 * Class contain data structure for highlight data. If user want to
 * provide external highlight data to folio activity. class should implement to
 * {@link HighLight} with contains required members.
 * <p>
 * Created by gautam chibde on 12/10/17.
 */

public class BookmarkData implements Bookmark {

    private String bookId;
    private Date date;
    private String name;
    private String location;
    private String uuid;

    @Override
    public String toString() {
        return "HighlightData{" +
                "bookId='" + bookId + '\'' +
                ", date=" + date +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    @Override
    public String getBookId() {
        return bookId;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getLocation() { return location; }

    @Override
    public String getUUID() {
        return uuid;
    }
}
