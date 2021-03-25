package com.jideguru.epub_viewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** EpubReaderPlugin */
public class EpubViewerPlugin implements MethodCallHandler {

  private Reader reader;
  private ReaderConfig config;

  static private Activity activity;
  static private Context context;
  static BinaryMessenger messenger;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {

    context = registrar.context();
    activity = registrar.activity();
    messenger = registrar.messenger();

    final MethodChannel channel = new MethodChannel(registrar.messenger(), "epub_viewer");
    channel.setMethodCallHandler(new EpubViewerPlugin());
  }

  @SuppressLint("ObsoleteSdkInt")
  public static void setAppLocale(String language) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      Resources resources = context.getResources();
      Configuration configuration = resources.getConfiguration();
      configuration.setLocale(new Locale(language));
      context.getApplicationContext().createConfigurationContext(configuration);
    } else {
      Locale locale = new Locale(language);
      Locale.setDefault(locale);
      Configuration config = context.getResources().getConfiguration();
      config.locale = locale;
      context.getResources().updateConfiguration(config,
              activity.getResources().getDisplayMetrics());
    }

  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {

    //setAppLocale("en");

    switch (call.method) {
      case "setConfig": {
        Map<String, Object> arguments = (Map<String, Object>) call.arguments;
        String identifier = arguments.get("identifier").toString();
        String themeColor = arguments.get("themeColor").toString();
        String scrollDirection = arguments.get("scrollDirection").toString();
        Boolean nightMode = Boolean.parseBoolean(arguments.get("nightMode").toString());
        Boolean allowSharing = Boolean.parseBoolean(arguments.get("allowSharing").toString());
        Boolean enableTts = Boolean.parseBoolean(arguments.get("enableTts").toString());
        config = new ReaderConfig(context, identifier, themeColor,
                scrollDirection, allowSharing, enableTts, nightMode);

        break;
      }
      case "open": {

        Map<String, Object> arguments = (Map<String, Object>) call.arguments;
        String bookPath = arguments.get("bookPath").toString();
        String bookId = arguments.get("bookId").toString();
        String highlights = arguments.get("highlights").toString();
        String lastLocation = arguments.get("lastLocation").toString();

        reader = new Reader(context, messenger, config);
        reader.open(bookPath, bookId, lastLocation, highlights);

        break;
      }
      case "close":
        reader.close();
        break;
      default:
        result.notImplemented();
        break;
    }
  }
}
