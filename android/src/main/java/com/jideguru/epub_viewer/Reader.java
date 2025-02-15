package com.jideguru.epub_viewer;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.Bookmark;
import com.folioreader.model.HighLight;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.ui.base.OnSaveBookmark;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.AppUtil;
import com.folioreader.util.OnBookmarkListener;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadLocatorListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import com.jideguru.epub_viewer.*;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class Reader implements OnHighlightListener, ReadLocatorListener, OnBookmarkListener, FolioReader.OnClosedListener {

    private ReaderConfig readerConfig;
    public FolioReader folioReader;
    private Context context;
    public MethodChannel.Result result;
    private EventChannel.EventSink pageEventSink;
    private EventChannel.EventSink progressEventSink;
    private EventChannel.EventSink highlightsEventSink;
    private EventChannel.EventSink highlightChangeEventSink;
    private EventChannel.EventSink bookmarkChangeEventSink;
    private BinaryMessenger messenger;
    private ReadLocator  read_locator;
    private ArrayList<HighLight> highlightList;
    private ArrayList<Bookmark> bookmarkList;
    private static final String PAGE_CHANNEL = "page";
    private static final String PROGRESS_CHANNEL = "progress";
    private static final String HIGHLIGHTS_CHANNEL = "highlights";
    private static final String HIGHLIGHT_CHANGE_CHANNEL = "highlightsChanges";
    private static final String BOOKMARK_CHANGE_CHANNEL = "bookmarksChanges";

    Reader(Context context, BinaryMessenger messenger,ReaderConfig config){
        this.context = context;
        readerConfig = config;
        this.messenger = messenger;
        getHighlightsAndSave();

        folioReader = FolioReader.get()
                .setOnHighlightListener(this)
                .setOnBookmarkListener(this)
                .setReadLocatorListener(this)
                .setOnClosedListener(this);

     
        setPageHandler(messenger);
        setProgressHandler(messenger);
        setHighlightsHandler(messenger);
        setHighlightChangeHandler(messenger);
        setBookmarkChangeHandler(messenger);
    }

    public void open(String bookPath, String bookId, String lastLocation, final String highlights, final String bookmarks){
        final String path = bookPath;
        final String location = lastLocation;
        final String id = bookId;
        new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                   Log.i("SavedLocation", "-> savedLocation -> " + location);
                   try {
                       readHighlights(highlights);
                   } catch (Exception e) {
                       Log.e("Reader", "Error reading highlights " + e.getMessage());
                       e.printStackTrace();
                   }
                   try {
                       readBookmarks(bookmarks);
                   } catch (Exception e) {
                       Log.e("Reader", "Error reading highlights " + e.getMessage());
                       e.printStackTrace();
                   }
                   if(location != null && !location.isEmpty()){
                       ReadLocator readLocator = ReadLocator.fromJson(location);
                       folioReader.setReadLocator(readLocator);
                   }
                   folioReader.setConfig(readerConfig.config, true)
                           .openBook(path, id);
               } catch (Exception e) {
                   e.printStackTrace();
               }
            }
        }).start();
       
    }

    public void close(){
        folioReader.close();
    }

    private void setPageHandler(BinaryMessenger messenger) {
        new EventChannel(messenger,PAGE_CHANNEL).setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                pageEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }
    private void setProgressHandler(BinaryMessenger messenger) {
        new EventChannel(messenger,PROGRESS_CHANNEL).setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                progressEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private void setHighlightsHandler(BinaryMessenger messenger) {
        new EventChannel(messenger,HIGHLIGHTS_CHANNEL).setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                highlightsEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private void setHighlightChangeHandler(BinaryMessenger messenger) {
        new EventChannel(messenger,HIGHLIGHT_CHANGE_CHANNEL).setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                highlightChangeEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private void setBookmarkChangeHandler(BinaryMessenger messenger) {
        new EventChannel(messenger,BOOKMARK_CHANGE_CHANNEL).setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                bookmarkChangeEventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private void getHighlightsAndSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                readHighlights(null);
            }
        }).start();
    }

    private void readHighlights(String highlights) {
        Reader.this.highlightList = new ArrayList<>(10);
        ArrayList<HighLight> highlightList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = highlights;
            if (json == null) {
                json = loadAssetTextAsString("highlights/highlights_data.json");
            }
            highlightList = objectMapper.readValue(json,
                    new TypeReference<List<HighlightData>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (highlightList != null) {
            final ArrayList<HighLight> finalHighlightList = highlightList;
            folioReader.saveReceivedHighLights(finalHighlightList, new OnSaveHighlight() {
                @Override
                public void onFinished() {
                    Reader.this.highlightList.addAll(finalHighlightList);
                }
            });
        }
    }

    private void readBookmarks(String bookmarks) {
        Reader.this.bookmarkList = new ArrayList<>(10);
        ArrayList<Bookmark> bookmarkList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = bookmarks;
            if (json == null) {
                json = loadAssetTextAsString("bookmarks/bookmarks_data.json");
            }
            bookmarkList = objectMapper.readValue(json,
                    new TypeReference<List<BookmarkData>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bookmarkList != null) {
            final ArrayList<Bookmark> finalBookmarkList = bookmarkList;
            folioReader.saveReceivedBookmarks(finalBookmarkList, new OnSaveBookmark() {
                @Override
                public void onFinished() {
                    Reader.this.bookmarkList.addAll(finalBookmarkList);
                }
            });
        }
    }

    @Override
    public void updateProgression(double progress) {
        if (progressEventSink != null){
            progressEventSink.success(progress);
        }
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("Reader", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("Reader", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    public void onFolioReaderClosed() {
        Log.i("readLocator", "-> saveReadLocator -> " + read_locator.toJson());

        if (pageEventSink != null){
            pageEventSink.success(read_locator.toJson());
        }
        if (highlightsEventSink != null){
            highlightsEventSink.success(highlightsJson());
        }
    }
    
    @Override
    public void onBookmark(final Bookmark bookmark, Bookmark.BookmarkAction type) {
        String action = "";
        switch (type) {
            case NEW:
                bookmarkList.add(bookmark);
                action = "new";
                break;
            case DELETE:
                bookmarkList.remove(bookmark);
                action = "delete";
                break;
            case MODIFY:
                for(int i=0; i < bookmarkList.size();i++) {
                    if (bookmarkList.get(i).getUUID().equals(bookmark.getUUID())){
                        bookmarkList.remove(i);
                        break;
                    }
                }
                bookmarkList.add(bookmark);
                action = "modify";
                break;
        }
        if (bookmarkChangeEventSink != null){
            bookmarkChangeEventSink.success("{\"action\":\""+ action + "\", \"bookmark\":" + bookmarkJson(bookmark) + "}");
        }
    }   
    
    @Override
    public void onHighlight(final HighLight highlight, HighLight.HighLightAction type) {
        String action = "";
        switch (type) {
            case NEW:
                highlightList.add(highlight);
                action = "new";
                break;
            case DELETE:
                highlightList.remove(highlight);
                action = "delete";
                break;
            case MODIFY:
                for(int i=0; i < highlightList.size();i++) {
                    if (highlightList.get(i).getUUID().equals(highlight.getUUID())){
                        highlightList.remove(i);
                        break;
                    }
                }
                highlightList.add(highlight);
                action = "modify";
                break;
        }
        if (highlightsEventSink != null){
            highlightsEventSink.success(highlightsJson());
        }
        if (highlightChangeEventSink != null){
            highlightChangeEventSink.success("{\"action\":\""+ action + "\", \"highlight\":" + highlightJson(highlight) + "}");
        }
    }

    @Override
    public void saveReadLocator(ReadLocator readLocator) {
        read_locator = readLocator;
    }

    private String highlightJson(HighLight highlight) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(highlight);
        } catch (Exception e) {
            return  null;
        }
    }

    private String bookmarkJson(Bookmark bookmark) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(bookmark);
        } catch (Exception e) {
            return  null;
        }
    }

    private String highlightsJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(highlightList);
        } catch (Exception e) {
            return  null;
        }
    }
}
