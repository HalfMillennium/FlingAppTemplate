package com.garrettchestnut.flingfirebase;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import org.apache.commons.io.FileUtils;


public class UploadFile extends AppCompatActivity {

	static String filename;
	static byte[] myByte;
	static boolean fileSuccess = false;
	static String key, resName, bcName;
	static byte[] resByte, bcByte;
	static File resFile, busFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_file);

		if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

			requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

		} else {
			getFile();
		}
	}

	public void getFile() {

		Intent intent = new Intent();
		intent.setType("application/pdf");
		intent.setAction(Intent.ACTION_GET_CONTENT);

		startActivityForResult(intent, 1);

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == 1) {

			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				getFile();

			}


		}

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		filename = null;;
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				try {
					Uri uri = data.getData();

					if (1 > 1) {
						Toast.makeText(this,"The selected file is too large. Select a new file with size less than 2mb",Toast.LENGTH_LONG).show();
					} else {
						String mimeType = getContentResolver().getType(uri);
						if (mimeType == null) {
							String path = getPath(this, uri);
							if (path == null) {
								filename = uri.toString().substring(uri.toString().lastIndexOf("/") + 1);
							} else {
								File file = new File(path);
								filename = file.getName();
							}
						} else {
							Uri returnUri = data.getData();
							Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
							int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
							int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
							returnCursor.moveToFirst();
							filename = returnCursor.getString(nameIndex);
							String size = Long.toString(returnCursor.getLong(sizeIndex));
						}
						File fileSave = getExternalFilesDir(null);
						String sourcePath = getExternalFilesDir(null).toString() + "/" + filename;
						sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf('0') + 2) + "Download/" + filename;
						Log.i("PATH", sourcePath);
						Log.i("NAME", filename);

						// create byte array
						File file = new File(sourcePath);
						int size = (int) file.length();
						byte[] bytes = new byte[size];
						try {
							BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
							buf.read(bytes, 0, bytes.length);
							fileSuccess = true;
							buf.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// byte array created

						myByte = bytes;

						if(getIntent().getExtras().getString("key").equals("resume"))
						{
							key = "resume";
							resByte = bytes;
							resName = filename;
							resFile = file;
						}
						if(getIntent().getExtras().getString("key").equals("bc"))
						{
							key = "bc";
							bcByte = bytes;
							bcName = filename;
							busFile = file;
						}

						finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getPath(Context context, Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else
			if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {split[1]};
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static byte[] getByteArray() { return myByte; }
	public static Intent makeIntent(Context context) { return new Intent(context, UploadFile.class); }

	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}
}