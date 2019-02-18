package com.nexstreaming.app.util;

import java.io.File;
import java.util.ArrayList;

public class PlayListUtils {
	public static final int NOT_FOUND = -1;
	private String[] mPlayableExtensionArray = null;
	
	public PlayListUtils(String[] extensionArray) {
		mPlayableExtensionArray = extensionArray;
	}
	
	public int getPreviousContentIndex(ArrayList<File> fileList, int startIndex) {
		int newIndex = findPreviousContentIndex(fileList, startIndex, 0);
		if( newIndex == NOT_FOUND ) {
			newIndex = findPreviousContentIndex(fileList, fileList.size() - 1, startIndex);
		}
		
		return newIndex;
	}
	
	private int findPreviousContentIndex(ArrayList<File> fileList, int startIndex, int endIndex) {
		for( int i = startIndex; i >= endIndex; i-- ) {
			File file = fileList.get(i);
			if( isPlayableFile(file) ) {
				return i;
			}
		}
		
		return NOT_FOUND;
	}
	
	public int getNextContentIndex(ArrayList<File> fileList, int startIndex) {
		int newIndex = findNextContentIndex(fileList, startIndex, fileList.size());
		if( newIndex == NOT_FOUND ) {
			newIndex = findNextContentIndex(fileList, 0, startIndex);
		}
		
		return newIndex;
	}
	
	private int findNextContentIndex(ArrayList<File> fileList, int startIndex, int endIndex) {
		for( int i = startIndex; i < endIndex; i++ ) {
			File file = fileList.get(i);
			if( isPlayableFile(file) ) {
				return i;
			}
		}
		
		return NOT_FOUND;
	}
	
	private boolean isPlayableFile(File file) {
		boolean result = false;
		if( !file.isDirectory() ) {
			String fileName = file.getName().toLowerCase();
			for( int i = 0; i < mPlayableExtensionArray.length; i++ ) {
				if( fileName.endsWith(mPlayableExtensionArray[i]) ) {
					result = true;
				}
			}
		}
		return result;
	}
		
}
