-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Maintain java native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Maintain enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-dontwarn com.conviva.streamerProxies.**
-dontwarn com.conviva.utils.**
-dontwarn tv.freewheel.utils.**
-dontwarn tv.freewheel.extension.**
-dontwarn tv.freewheel.renderers.**
-dontwarn tv.agama.emp.client.**
-dontwarn com.verimatrix.vdc.**
-dontwarn android.support.v4.app.**
-dontwarn app.nunc.com.staatsoperlivestreaming.nexplayerengine.**

-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexALFactory{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexALFactory$ICodecDownListener{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexALFactory$NexALFactoryErrorCode{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$IListener{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$NexSDKType{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$NexProperty{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$PROGRAM_TIME{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$NexErrorCode{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$NexUniqueIDVer{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$IReleaseListener{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$NexErrorCategory{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$IVideoRendererListener{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$NexRTStreamInformation{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer$IDynamicThumbnailListener{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCodecInformation{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexContentInformation{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagInformation{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagPicture{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexID3TagText{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPictureTimingInfo{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexStreamInformation{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexTrackInformation{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionRenderer{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionRendererForTimedText{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCaptionRendererForTimedText$CaptionData{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$CaptionColor{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexCustomAttribInformation{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTMLRenderingData{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTMLRenderingData$TTMLNodeData{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTML_LengthType{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTML_DisplayAlign{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTML_Fontstyle{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTML_TextAlign{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTML_UnicodeBIDI{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTML_WritingMode{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTML_StyleLength{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TTML_TextOutlineStyleLength{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$WebVTTRenderingData{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$WebVTTRenderingData$WebVTTNodeData{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$WebVTT_TextAlign{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$WebVTT_WritingDirection{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TextStyleEntry{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$TextKaraokeEntry {*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClosedCaption$CaptionMode{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexNetAddrTable{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexNetAddrTable$NetAddrTableInfo{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexClient{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexWVDRM{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexSessionData{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexHLSAES128DRMManager{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexDateRangeData{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexEmsgData{*;}
-keep class app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexVSyncSampler{*;}


# // AmazonFling Sample Start
-keep class com.amazon.whisperlink.**{*;}
-keep class com.amazon.whisperplay.**{*;}
-dontwarn com.amazon.whisperlink.**
-dontwarn com.amazon.whisperplay.**
# // AmazonFling Sample End
