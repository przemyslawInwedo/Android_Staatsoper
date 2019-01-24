package app.nunc.com.staatsoperlivestreaming.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NexFileIO {
	private static final String LOG_TAG = "[NexFileIO]";

    /**
     * ���丮 �� 
     * @return dir
     */
    public static File makeDirectory(String dir_path){
        File dir = new File(dir_path);
        if (!dir.exists())
        {
            dir.mkdirs();
            Log.i( LOG_TAG , "!dir.exists" );
        }else{
            Log.i( LOG_TAG , "dir.exists" );
        }
 
        return dir;
    }
 
    /**
     * ���� ��
     * @param dir
     * @return file 
     */
    public static File makeFile(File dir , String fileName){
        File file = null;
        boolean isSuccess = false;
        if(dir.isDirectory()){
            file = new File(dir, fileName);
            if(file!=null&&!file.exists()){
                Log.i( LOG_TAG , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(LOG_TAG, "���ϻ� ���� = " + isSuccess);
                }
            }else{
                Log.i( LOG_TAG , "makeFile file.exists" );
            }
        }
        return file;
    }
 
    /**
     * (dir/file) 
     * @param file
     * @return String
     */
    public static String getAbsolutePath(File file){
        return ""+file.getAbsolutePath();
    }
 
    /**
     * (dir/file) ���� �ϱ�
     * @param file
     */
    public static boolean deleteFile(File file){
        boolean result;
        if(file!=null&&file.exists()){
            file.delete();
            result = true;
        }else{
            result = false;
        }
        return result;
    }

    public static boolean deleteFile(File dir, String fileName){
        boolean result = false;
        File file = null;
        if( dir.isDirectory() ) {
            file = new File(dir, fileName);
            if( file.exists() ) {
                result = file.delete();
            }
        }
        Log.d(LOG_TAG, "deleteFile fileName : " + fileName + " result : " + result);

        return result;
    }
    /**
     * ���Ͽ��� üũ �ϱ�
     * @param file
     * @return
     */
    public static boolean isFile(File file){
        boolean result;
        if(file!=null&&file.exists()&&file.isFile()){
            result=true;
        }else{
            result=false;
        }
        return result;
    }
 
    /**
     * ���丮 ���� üũ �ϱ�
     * @param dir
     * @return
     */
    public static boolean isDirectory(File dir){
        boolean result;
        if(dir!=null&&dir.isDirectory()){
            result=true;
        }else{
            result=false;
        }
        return result;
    }
 
    /**
     * ���� ���� ���� Ȯ�� �ϱ�
     * @param file
     * @return
     */
    public static boolean isFileExist(File file){
        boolean result;
        if(file!=null&&file.exists()){
            result=true;
        }else{
            result=false;
        }
        return result;
    }

    public static boolean isFileExist(File dir, String fileName){
        boolean result = false;
        if(dir.isDirectory()){
            File file = new File(dir, fileName);
            result = file.exists();
        }
        return result;
    }
     
    /**
     * ���� �̸� �ٲٱ�
     * @param file
     */
    public static boolean reNameFile(File file , File new_name){
        boolean result;
        if(file!=null&&file.exists()&&file.renameTo(new_name)){
            result=true;
        }else{
            result=false;
        }
        return result;
    }
     
    /**
     * ���丮�� �ȿ� ������ ���� �ش�.
     * @param file
     * @return
     */
    public static String[] getList(File dir){
        if(dir!=null&&dir.exists())
            return dir.list();
        return null;
    }
 
    /**
     * ���Ͽ� ���� ����
     * @param file
     * @param file_content
     * @return
     */
    public static boolean writeFile(File file , byte[] file_content){
        boolean result;
        FileOutputStream fos;
        if(file!=null&&file.exists()&&file_content!=null){
            try {
                fos = new FileOutputStream(file);
                try {
                    fos.write(file_content);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }
 
    /**
     * ���� �о� ���� 
     * @param file
     */
    public static void readFile(File file){
        int readcount=0;
        if(file!=null&&file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                readcount = (int)file.length();
                byte[] buffer = new byte[readcount];
                fis.read(buffer);
                for(int i=0 ; i<file.length();i++){
                    Log.d(LOG_TAG, ""+buffer[i]);
                }
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
     
    /**
     * ���� ����
     * @param file
     * @param save_file
     * @return
     */
    public static boolean copyFile(File file , String save_file){
        boolean result;
        if(file!=null&&file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(save_file);
                int readcount=0;
                byte[] buffer = new byte[1024];
                while((readcount = fis.read(buffer,0,1024))!= -1){
                    newfos.write(buffer,0,readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }

    public static boolean copyFile(InputStream input, OutputStream output) {
        boolean result = false;
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            input.close();
            output.flush();
            output.close();

            result = true;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }

        return result;
    }

    public static boolean searchFileFromAssets(Context context, String extension) {
        AssetManager manager = context.getAssets();
        try {
            String[] list = manager.list("");
            for (int i = 0; i < list.length; i++) {
                if (list[i].endsWith(extension))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
	@SuppressLint("DefaultLocale")
	public static String subtitlePathFromMediaPath(String path) {
		File subFile = null;
		int nIndex = path.lastIndexOf('.');
		String strSubTitleFileName = null;
		Log.d(LOG_TAG, "subtitleFilename(path='" + path + "')");
		
		if(nIndex != -1) {

			String exts[] = { "smi", "srt", "sub", "dfxp" };
			for( String ext: exts) {
				String strFileName = path.substring(0, nIndex);
				strSubTitleFileName = strFileName + "." + ext;
				subFile = new File(strSubTitleFileName);
				if(subFile.exists()) {
					break;
				}
				
				strSubTitleFileName = strFileName + "." + ext.toUpperCase(Locale.getDefault());
				subFile = new File(strSubTitleFileName);
				if(subFile.exists()) {
					break;
				}
				
				strSubTitleFileName = null;
			}
		}
		
		Log.d(LOG_TAG, "Returning strSubTitleFileName:" + strSubTitleFileName);
		
		return strSubTitleFileName;
	}

	@SuppressLint("DefaultLocale")
	public static String[] subtitlePathArrayFromMediaPath(String path) {
		String[] ret = new String[0];

		File mediaFile = new File(path);
		File parentFile = mediaFile.getParentFile();

        if( parentFile != null ) {
            File[] subtitleFileArray = parentFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    boolean accept = false;
                    final String exts[] = { "smi", "srt", "sub", "dfxp" };

                    File file = new File(dir.getAbsolutePath() + "/" + filename);
                    if( file.isFile() ) {
                        for( String ext : exts ) {
                            if (filename.endsWith(ext)) {
                                accept = true;
                                break;
                            }

                            if (filename.endsWith(ext.toUpperCase(Locale.getDefault()))) {
                                accept = true;
                                break;
                            }
                        }
                    }

                    return accept;
                }
            });

            if(subtitleFileArray != null ) {
                String[] subtitlePathArray = new String[subtitleFileArray.length];
                for(int i = 0; i < subtitleFileArray.length; i++) {
                    subtitlePathArray[i] = subtitleFileArray[i].getAbsolutePath();
                }
                ret = subtitlePathArray;
            }
        }

		return ret;
	}

	public static void deleteFolder(String path) {
		if( path != null ) {
			File file = new File(path);
			File[] childFileList = file.listFiles();
			if( childFileList != null ) {
				for( File childFile : childFileList ) {
					if( childFile.isDirectory() ) {
						deleteFolder(childFile.getAbsolutePath());
					} else {
						childFile.delete();
					}
				}
			}
			file.delete();
		}
	}
	
	public static boolean isCacheExist(String path) {
		boolean result = false;
		if( path != null) {
			File folder = new File(path);
			File[] childFileList = folder.listFiles();
			if( childFileList != null ) {
				if( childFileList.length > 0 ) {
					result = true;
				}
			}
		}

		return result;
	}

	public static String getContentTitle(String url) {
		if( url == null )
			return null;
        
        Uri uri = Uri.parse(url);
        String title = url;
        if( uri != null ) {
            String path = uri.getPath();

            int index = path.lastIndexOf("/");

            if( index > 0 ) {
                title = path.substring(index + 1, path.length());
            }

            index = title.lastIndexOf(".");

            if( index > 0 ) {
                title = title.substring(0, index);
            }
        }

		return title;
	}

	public static String getExternalFileName(String url) {
		String separator = "://";
		url = url.substring(url.indexOf(separator) + separator.length());

		url = url.replace("\\", "_");
		url = url.replace("/", "_");
		url = url.replace(":", "_");
		url = url.replace("*", "_");
		url = url.replace("?", "_");
		url = url.replace("=", "_");
		url = url.replace(".", "_");
		url = url.replace("<", "_");
		url = url.replace(">", "_");
		url = url.replace("|", "_");

		url += ".nxt";
		return url;
	}

	public static String getCacheFolderPath(String path, String title, int bandwidth) {
		Calendar cal = Calendar.getInstance( );

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String today = dateFormat.format(cal.getTime());
		String base = path + today + "_" + title + "_" + (bandwidth / 1000);

        String folderPath = base + "/";
        File file = new File(folderPath);
        int i = 0;
        while (file.exists()) {
            i++;
            folderPath = base + "_" + i + "/";
            file = new File(folderPath);
        }

        return folderPath;
    }

	public static final String STORE_INFO_EXTENSION = ".nex.store";
    public static String makeUniqueStoreInfoFileName(String parentPath, String url, int bandwidth) {
        final String separator = "://";
        String name = "";
        name = url.substring(url.indexOf(separator) + separator.length());

        name = name.replace("\\", "_");
        name = name.replace("/", "_");
        name = name.replace(":", "_");
        name = name.replace("*", "_");
        name = name.replace("?", "_");
        name = name.replace("=", "_");
        name = name.replace(".", "_");
        name = name.replace("<", "_");
        name = name.replace(">", "_");
        name = name.replace("|", "_");
        name = name.replace(".", "_");

        String base = name + "_" + bandwidth;

        int i = 0;
        File file = new File(parentPath + base + STORE_INFO_EXTENSION);
        while( file.exists() ) {
            i++;
            base = name + "_" + bandwidth + "_" + i;
            file = new File(parentPath + base + STORE_INFO_EXTENSION);
        }

        Log.d(LOG_TAG, "makeUniqueStoreInfoFileName base : " + base);

        return base + STORE_INFO_EXTENSION;
    }

}
