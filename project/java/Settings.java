// This string is autogenerated by ChangeAppSettings.sh, do not change spaces amount
package net.sourceforge.clonekeenplus;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.util.Log;
import java.io.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.StatFs;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.lang.String;

class Settings
{
	static String SettingsFileName = "libsdl-settings.cfg";

	static boolean settingsLoaded = false;
	static boolean settingsChanged = false;

	static void Save(final MainActivity p)
	{
		try {
			ObjectOutputStream out = new ObjectOutputStream(p.openFileOutput( SettingsFileName, p.MODE_WORLD_READABLE ));
			out.writeBoolean(Globals.DownloadToSdcard);
			out.writeBoolean(Globals.PhoneHasArrowKeys);
			out.writeBoolean(Globals.PhoneHasTrackball);
			out.writeBoolean(Globals.UseAccelerometerAsArrowKeys);
			out.writeBoolean(Globals.UseTouchscreenKeyboard);
			out.writeInt(Globals.TouchscreenKeyboardSize);
			out.writeInt(Globals.AccelerometerSensitivity);
			out.writeInt(Globals.AccelerometerCenterPos);
			out.writeInt(Globals.TrackballDampening);
			out.writeInt(Globals.AudioBufferConfig);
			out.writeInt(Globals.OptionalDataDownload.length);
			for(int i = 0; i < Globals.OptionalDataDownload.length; i++)
				out.writeBoolean(Globals.OptionalDataDownload[i]);
			out.writeInt(Globals.TouchscreenKeyboardTheme);
			out.writeInt(Globals.RightClickMethod);
			out.writeBoolean(Globals.ShowScreenUnderFinger);
			out.writeBoolean(Globals.LeftClickUsesPressure);
			out.writeBoolean(Globals.LeftClickUsesMultitouch);
			out.writeInt(Globals.ClickScreenPressure);
			out.writeInt(Globals.ClickScreenTouchspotSize);
			out.writeBoolean(Globals.KeepAspectRatio);

			out.close();
			settingsLoaded = true;
			
		} catch( FileNotFoundException e ) {
		} catch( SecurityException e ) {
		} catch ( IOException e ) {};
	}

	static void Load( final MainActivity p )
	{
		if(settingsLoaded) // Prevent starting twice
		{
			return;
		}
		System.out.println("libSDL: Settings.Load(): enter");
		try {
			ObjectInputStream settingsFile = new ObjectInputStream(new FileInputStream( p.getFilesDir().getAbsolutePath() + "/" + SettingsFileName ));
			Globals.DownloadToSdcard = settingsFile.readBoolean();
			Globals.PhoneHasArrowKeys = settingsFile.readBoolean();
			Globals.PhoneHasTrackball = settingsFile.readBoolean();
			Globals.UseAccelerometerAsArrowKeys = settingsFile.readBoolean();
			Globals.UseTouchscreenKeyboard = settingsFile.readBoolean();
			Globals.TouchscreenKeyboardSize = settingsFile.readInt();
			Globals.AccelerometerSensitivity = settingsFile.readInt();
			Globals.AccelerometerCenterPos = settingsFile.readInt();
			Globals.TrackballDampening = settingsFile.readInt();
			Globals.AudioBufferConfig = settingsFile.readInt();
			Globals.OptionalDataDownload = new boolean[settingsFile.readInt()];
			for(int i = 0; i < Globals.OptionalDataDownload.length; i++)
				Globals.OptionalDataDownload[i] = settingsFile.readBoolean();
			Globals.TouchscreenKeyboardTheme = settingsFile.readInt();
			Globals.RightClickMethod = settingsFile.readInt();
			Globals.ShowScreenUnderFinger = settingsFile.readBoolean();
			Globals.LeftClickUsesPressure = settingsFile.readBoolean();
			Globals.LeftClickUsesMultitouch = settingsFile.readBoolean();
			Globals.ClickScreenPressure = settingsFile.readInt();
			Globals.ClickScreenTouchspotSize = settingsFile.readInt();
			Globals.KeepAspectRatio = settingsFile.readBoolean();
			
			settingsLoaded = true;

			System.out.println("libSDL: Settings.Load(): loaded settings successfully");
			
			return;
			
		} catch( FileNotFoundException e ) {
		} catch( SecurityException e ) {
		} catch ( IOException e ) {};
		
		// This code fails for both of my phones!
		/*
		Configuration c = new Configuration();
		c.setToDefaults();
		
		if( c.navigation == Configuration.NAVIGATION_TRACKBALL || 
			c.navigation == Configuration.NAVIGATION_DPAD ||
			c.navigation == Configuration.NAVIGATION_WHEEL )
		{
			Globals.AppNeedsArrowKeys = false;
		}
		
		System.out.println( "libSDL: Phone keypad type: " + 
				(
				c.navigation == Configuration.NAVIGATION_TRACKBALL ? "Trackball" :
				c.navigation == Configuration.NAVIGATION_DPAD ? "Dpad" :
				c.navigation == Configuration.NAVIGATION_WHEEL ? "Wheel" :
				c.navigation == Configuration.NAVIGATION_NONAV ? "None" :
				"Unknown" ) );
		*/

		System.out.println("libSDL: Settings.Load(): loading settings failed, running config dialog");
		p.setUpStatusLabel();
		showConfig(p);
	}
	
	public static void showConfig(final MainActivity p) {
		settingsChanged = true;
		showDownloadConfig(p);
	}

	static void showDownloadConfig(final MainActivity p) {

		long freeSdcard = 0;
		long freePhone = 0;
		try {
			StatFs sdcard = new StatFs(Environment.getExternalStorageDirectory().getPath());
			StatFs phone = new StatFs(Environment.getDataDirectory().getPath());
			freeSdcard = (long)sdcard.getAvailableBlocks() * sdcard.getBlockSize() / 1024 / 1024;
			freePhone = (long)phone.getAvailableBlocks() * phone.getBlockSize() / 1024 / 1024;
		}catch(Exception e) {}

		final CharSequence[] items = { p.getResources().getString(R.string.storage_phone, freePhone),
										p.getResources().getString(R.string.storage_sd, freeSdcard) };
		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		String [] downloadFiles = Globals.DataDownloadUrl.split("\\^");
		builder.setTitle(downloadFiles[0].split("[|]")[0]);
		builder.setSingleChoiceItems(items, Globals.DownloadToSdcard ? 1 : 0, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.DownloadToSdcard = (item == 1);

				dialog.dismiss();
				showOptionalDownloadConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	};

	static void showOptionalDownloadConfig(final MainActivity p) {

		String [] downloadFiles = Globals.DataDownloadUrl.split("\\^");
		if(downloadFiles.length <= 1)
		{
			Globals.OptionalDataDownload = new boolean[1];
			Globals.OptionalDataDownload[0] = true;
			showKeyboardConfig(p);
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(p.getResources().getString(R.string.optional_downloads));

		CharSequence[] items = new CharSequence[ downloadFiles.length - 1 ];
		for(int i = 1; i < downloadFiles.length; i++ )
			items[i-1] = new String(downloadFiles[i].split("[|]")[0]);

		if( Globals.OptionalDataDownload == null || Globals.OptionalDataDownload.length != items.length + 1 )
			Globals.OptionalDataDownload = new boolean[downloadFiles.length];
		Globals.OptionalDataDownload[0] = true;
		boolean defaults[] = new boolean[downloadFiles.length-1];
		for(int i=1; i<downloadFiles.length; i++)
			defaults[i-1] = Globals.OptionalDataDownload[i];

		builder.setMultiChoiceItems(items, defaults, new DialogInterface.OnMultiChoiceClickListener() 
		{
			public void onClick(DialogInterface dialog, int item, boolean isChecked) 
			{
				Globals.OptionalDataDownload[item+1] = isChecked;
			}
		});
		builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				dialog.dismiss();
				showKeyboardConfig(p);
			}
		});

		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	};

	static void showKeyboardConfig(final MainActivity p)
	{
		if( ! Globals.AppNeedsArrowKeys )
		{
			Globals.PhoneHasArrowKeys = false;
			Globals.PhoneHasTrackball = false;
			showTrackballConfig(p);
			return;
		}
		
		final CharSequence[] items = { p.getResources().getString(R.string.controls_arrows),
										p.getResources().getString(R.string.controls_trackball),
										p.getResources().getString(R.string.controls_touch) };

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(p.getResources().getString(R.string.controls_question));
		builder.setSingleChoiceItems(items, Globals.PhoneHasArrowKeys ? 0 : ( Globals.PhoneHasTrackball ? 1 : 2 ), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.PhoneHasArrowKeys = (item == 0);
				Globals.PhoneHasTrackball = (item == 1);

				dialog.dismiss();
				showTrackballConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showTrackballConfig(final MainActivity p)
	{
		if( ! Globals.PhoneHasTrackball )
		{
			Globals.TrackballDampening = 0;
			showAdditionalInputConfig(p);
			return;
		}
		
		final CharSequence[] items = { p.getResources().getString(R.string.trackball_no_dampening),
										p.getResources().getString(R.string.trackball_fast),
										p.getResources().getString(R.string.trackball_medium),
										p.getResources().getString(R.string.trackball_slow) };

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(p.getResources().getString(R.string.trackball_question));
		builder.setSingleChoiceItems(items, Globals.TrackballDampening, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.TrackballDampening = item;

				dialog.dismiss();
				showAdditionalInputConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}
	
	
	static void showAdditionalInputConfig(final MainActivity p)
	{

		if( ! ( Globals.AppNeedsArrowKeys || Globals.AppNeedsTextInput || Globals.AppTouchscreenKeyboardKeysAmount > 0 ) && ! Globals.AppUsesJoystick )
		{
			Globals.UseTouchscreenKeyboard = false;
			Globals.UseAccelerometerAsArrowKeys = false;
			showAccelerometerConfig(p);
			return;
		}
		final CharSequence[] items = {
			p.getResources().getString(R.string.controls_screenkb),
			p.getResources().getString(R.string.controls_accelnav),
		};

		final boolean defaults[] = { Globals.UseTouchscreenKeyboard, Globals.UseAccelerometerAsArrowKeys };
		
		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(p.getResources().getString(R.string.controls_additional));
		builder.setMultiChoiceItems(items, defaults, new DialogInterface.OnMultiChoiceClickListener() 
		{
			public void onClick(DialogInterface dialog, int item, boolean isChecked) 
			{
				if( item == 0 )
					Globals.UseTouchscreenKeyboard = isChecked;
				if( item == 1 )
					Globals.UseAccelerometerAsArrowKeys = isChecked;
			}
		});
		builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				dialog.dismiss();
				showAccelerometerConfig(p);
			}
		});

		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showAccelerometerConfig(final MainActivity p)
	{
		if( ! Globals.UseAccelerometerAsArrowKeys || Globals.AppHandlesJoystickSensitivity )
		{
			Globals.AccelerometerSensitivity = 2; // Slow, full range
			showAccelerometerCenterConfig(p);
			return;
		}
		
		final CharSequence[] items = { p.getResources().getString(R.string.accel_fast),
										p.getResources().getString(R.string.accel_medium),
										p.getResources().getString(R.string.accel_slow) };

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(R.string.accel_question);
		builder.setSingleChoiceItems(items, Globals.AccelerometerSensitivity, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.AccelerometerSensitivity = item;

				dialog.dismiss();
				showAccelerometerCenterConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showAccelerometerCenterConfig(final MainActivity p)
	{
		if( ! Globals.UseAccelerometerAsArrowKeys || Globals.AppHandlesJoystickSensitivity )
		{
			Globals.AccelerometerCenterPos = 2; // Fixed horizontal center position
			showScreenKeyboardConfig(p);
			return;
		}
		
		final CharSequence[] items = { p.getResources().getString(R.string.accel_floating),
										p.getResources().getString(R.string.accel_fixed_start),
										p.getResources().getString(R.string.accel_fixed_horiz) };

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(R.string.accel_question_center);
		builder.setSingleChoiceItems(items, Globals.AccelerometerCenterPos, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.AccelerometerCenterPos = item;

				dialog.dismiss();
				showScreenKeyboardConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}


	static void showScreenKeyboardConfig(final MainActivity p)
	{
		if( ! Globals.UseTouchscreenKeyboard )
		{
			Globals.TouchscreenKeyboardSize = 0;
			showScreenKeyboardThemeConfig(p);
			return;
		}
		
		final CharSequence[] items = {	p.getResources().getString(R.string.controls_screenkb_large),
										p.getResources().getString(R.string.controls_screenkb_medium),
										p.getResources().getString(R.string.controls_screenkb_small),
										p.getResources().getString(R.string.controls_screenkb_tiny) };

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(p.getResources().getString(R.string.controls_screenkb_size));
		builder.setSingleChoiceItems(items, Globals.TouchscreenKeyboardSize, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.TouchscreenKeyboardSize = item;

				dialog.dismiss();
				showScreenKeyboardThemeConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showScreenKeyboardThemeConfig(final MainActivity p)
	{
		if( ! Globals.UseTouchscreenKeyboard )
		{
			Globals.TouchscreenKeyboardTheme = 0;
			showAudioConfig(p);
			return;
		}
		
		final CharSequence[] items = {
			p.getResources().getString(R.string.controls_screenkb_by, "Ultimate Droid", "Sean Stieber"),
			p.getResources().getString(R.string.controls_screenkb_by, "Ugly Arrows", "pelya")
			};

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(p.getResources().getString(R.string.controls_screenkb_theme));
		builder.setSingleChoiceItems(items, Globals.TouchscreenKeyboardTheme == 1 ? 0 : 1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				if( item == 0 )
					Globals.TouchscreenKeyboardTheme = 1;
				if( item == 1 )
					Globals.TouchscreenKeyboardTheme = 0;

				dialog.dismiss();
				showAudioConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showAudioConfig(final MainActivity p)
	{
		final CharSequence[] items = {	p.getResources().getString(R.string.audiobuf_verysmall),
										p.getResources().getString(R.string.audiobuf_small),
										p.getResources().getString(R.string.audiobuf_medium),
										p.getResources().getString(R.string.audiobuf_large) };

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(R.string.audiobuf_question);
		builder.setSingleChoiceItems(items, Globals.AudioBufferConfig, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.AudioBufferConfig = item;
				dialog.dismiss();
				showRightClickConfigConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showRightClickConfigConfig(final MainActivity p)
	{
		if( ! Globals.AppNeedsTwoButtonMouse )
		{
			Globals.RightClickMethod = Globals.RIGHT_CLICK_NONE;
			showAdvancedPointAndClickConfigConfig(p);
			return;
		}
		final CharSequence[] items = {	p.getResources().getString(R.string.rightclick_menu),
										p.getResources().getString(R.string.rightclick_multitouch),
										p.getResources().getString(R.string.rightclick_pressure) };

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(R.string.rightclick_question);
		builder.setSingleChoiceItems(items, Globals.RightClickMethod-1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.RightClickMethod = item + 1;
				dialog.dismiss();
				showAdvancedPointAndClickConfigConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showAdvancedPointAndClickConfigConfig(final MainActivity p)
	{

		/*
		if( ! Globals.AppNeedsTwoButtonMouse )
		{
			showTouchPressureMeasurementTool(p);
			return;
		}
		*/
		CharSequence[] items = {		p.getResources().getString(R.string.pointandclick_keepaspectratio),
										p.getResources().getString(R.string.pointandclick_showcreenunderfinger),
										p.getResources().getString(R.string.pointandclick_usepressure),
										p.getResources().getString(R.string.pointandclick_multitouch) };
		boolean defaults[] = { Globals.KeepAspectRatio, Globals.ShowScreenUnderFinger, Globals.LeftClickUsesPressure, Globals.LeftClickUsesMultitouch };
		if( Globals.RightClickMethod == Globals.RIGHT_CLICK_WITH_PRESSURE )
		{
			Globals.LeftClickUsesPressure = false;
			CharSequence[] items2 = {	p.getResources().getString(R.string.pointandclick_keepaspectratio),
										p.getResources().getString(R.string.pointandclick_showcreenunderfinger),
										p.getResources().getString(R.string.pointandclick_multitouch) };
			boolean defaults2[] = { Globals.KeepAspectRatio, Globals.ShowScreenUnderFinger, Globals.LeftClickUsesMultitouch };
			items = items2;
			defaults = defaults2;
		}
		if( Globals.RightClickMethod == Globals.RIGHT_CLICK_WITH_MULTITOUCH )
		{
			Globals.LeftClickUsesMultitouch = false;
			CharSequence[] items2 = {	p.getResources().getString(R.string.pointandclick_keepaspectratio),
										p.getResources().getString(R.string.pointandclick_showcreenunderfinger),
										p.getResources().getString(R.string.pointandclick_usepressure) };
			boolean defaults2[] = { Globals.KeepAspectRatio, Globals.ShowScreenUnderFinger, Globals.LeftClickUsesPressure };
			items = items2;
			defaults = defaults2;
		}
		if( ! Globals.AppNeedsTwoButtonMouse )
		{
			Globals.ShowScreenUnderFinger = false;
			Globals.LeftClickUsesPressure = false;
			Globals.LeftClickUsesMultitouch = false;
			CharSequence[] items2 = {	p.getResources().getString(R.string.pointandclick_keepaspectratio) };
			boolean defaults2[] = { Globals.KeepAspectRatio };
			items = items2;
			defaults = defaults2;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle(p.getResources().getString(R.string.pointandclick_question));
		builder.setMultiChoiceItems(items, defaults, new DialogInterface.OnMultiChoiceClickListener() 
		{
			public void onClick(DialogInterface dialog, int item, boolean isChecked) 
			{
				if( item == 0 )
					Globals.KeepAspectRatio = isChecked;
				if( item == 1 )
					Globals.ShowScreenUnderFinger = isChecked;
				if( item == 2 )
				{
					if( Globals.RightClickMethod == Globals.RIGHT_CLICK_WITH_PRESSURE )
						Globals.LeftClickUsesMultitouch = isChecked;
					else
						Globals.LeftClickUsesPressure = isChecked;
				}
				if( item == 3 )
					Globals.LeftClickUsesMultitouch = isChecked;
			}
		});
		builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				dialog.dismiss();
				showTouchPressureMeasurementTool(p);
			}
		});

		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}
	
	public static class TouchMeasurementTool
	{
		MainActivity p;
		ArrayList<Integer> force = new ArrayList<Integer>();
		ArrayList<Integer> radius = new ArrayList<Integer>();
		static final int maxEventAmount = 100;
		
		public TouchMeasurementTool(MainActivity _p) 
		{
			p = _p;
		}

		public void onTouchEvent(final MotionEvent ev)
		{
			force.add(new Integer((int)(ev.getPressure() * 1000.0)));
			radius.add(new Integer((int)(ev.getSize() * 1000.0)));
			p.setText(p.getResources().getString(R.string.measurepressure_response, force.get(force.size()-1), radius.get(radius.size()-1)));
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) { }
			
			if( force.size() >= maxEventAmount )
			{
				p._touchMeasurementTool = null;
				Globals.ClickScreenPressure = getAverageForce();
				Globals.ClickScreenTouchspotSize = getAverageRadius();
				System.out.println("SDL: measured average force " + Globals.ClickScreenPressure + " radius " + Globals.ClickScreenTouchspotSize);
				Save(p);
				p.startDownloader();
			}
		}

		int getAverageForce()
		{
			int avg = 0;
			for(Integer f: force)
			{
				avg += f;
			}
			return avg / force.size();
		}
		int getAverageRadius()
		{
			int avg = 0;
			for(Integer r: radius)
			{
				avg += r;
			}
			return avg / radius.size();
		}
	}
	
	static void showTouchPressureMeasurementTool(final MainActivity p)
	{
		if( Globals.RightClickMethod == Globals.RIGHT_CLICK_WITH_PRESSURE || Globals.LeftClickUsesPressure )
		{
			p.setText(p.getResources().getString(R.string.measurepressure_touchplease));
			p._touchMeasurementTool = new TouchMeasurementTool(p);
		}
		else
		{
			Save(p);
			p.startDownloader();
		}
	}


	static void Apply(Activity p)
	{
		nativeIsSdcardUsed( Globals.DownloadToSdcard ? 1 : 0 );
		
		if( Globals.PhoneHasTrackball )
			nativeSetTrackballUsed();
		if( Globals.AppUsesMouse )
			nativeSetMouseUsed( Globals.RightClickMethod,
								Globals.ShowScreenUnderFinger ? 1 : 0,
								Globals.LeftClickUsesPressure ? 1 : 0,
								Globals.LeftClickUsesMultitouch ? 1 : 0,
								Globals.ClickScreenPressure,
								Globals.ClickScreenTouchspotSize );
		if( Globals.AppUsesJoystick && (Globals.UseAccelerometerAsArrowKeys || Globals.UseTouchscreenKeyboard) )
			nativeSetJoystickUsed();
		if( Globals.AppUsesMultitouch )
			nativeSetMultitouchUsed();
		nativeSetAccelerometerSettings(Globals.AccelerometerSensitivity, Globals.AccelerometerCenterPos);
		nativeSetTrackballDampening(Globals.TrackballDampening);
		if( Globals.UseTouchscreenKeyboard )
		{
			nativeSetTouchscreenKeyboardUsed();
			nativeSetupScreenKeyboard(	Globals.TouchscreenKeyboardSize,
										Globals.TouchscreenKeyboardTheme,
										Globals.AppTouchscreenKeyboardKeysAmount,
										Globals.AppTouchscreenKeyboardKeysAmountAutoFire,
										Globals.AppNeedsArrowKeys ? 1 : 0,
										Globals.AppNeedsTextInput ? 1 : 0 );
		}
		SetupTouchscreenKeyboardGraphics(p);
		String lang = new String(Locale.getDefault().getLanguage());
		if( Locale.getDefault().getCountry().length() > 0 )
			lang = lang + "_" + Locale.getDefault().getCountry();
		System.out.println( "libSDL: setting envvar LANGUAGE to '" + lang + "'");
		nativeSetEnv( "LANG", lang );
		nativeSetEnv( "LANGUAGE", lang );
		// TODO: get current user name and set envvar USER, the API is not availalbe on Android 1.6 so I don't bother with this
	}

	static byte [] loadRaw(Activity p,int res)
	{
		byte [] buf = new byte[65536 * 2];
		byte [] a = new byte[0];
		try{
			InputStream is = new GZIPInputStream(p.getResources().openRawResource(res));
			int readed = 0;
			while( (readed = is.read(buf)) >= 0 )
			{
				byte [] b = new byte [a.length + readed];
				System.arraycopy(a, 0, b, 0, a.length);
				System.arraycopy(buf, 0, b, a.length, readed);
				a = b;
			}
		} catch(Exception e) {};
		return a;
	}
	
	static void SetupTouchscreenKeyboardGraphics(Activity p)
	{
		if( Globals.UseTouchscreenKeyboard )
		{
			if( Globals.TouchscreenKeyboardTheme == 1 )
			{
				nativeSetupScreenKeyboardButtons(loadRaw(p, R.raw.ultimatedroid));
			}
		}
	}
	
	private static native void nativeIsSdcardUsed(int flag);
	private static native void nativeSetTrackballUsed();
	private static native void nativeSetTrackballDampening(int value);
	private static native void nativeSetAccelerometerSettings(int sensitivity, int centerPos);
	private static native void nativeSetMouseUsed(int RightClickMethod, int ShowScreenUnderFinger, int LeftClickUsesPressure, int LeftClickUsesMultitouch, int MaxForce, int MaxRadius);
	private static native void nativeSetJoystickUsed();
	private static native void nativeSetMultitouchUsed();
	private static native void nativeSetTouchscreenKeyboardUsed();
	private static native void nativeSetupScreenKeyboard(int size, int theme, int nbuttons, int nbuttonsAutoFire, int showArrows, int showTextInput);
	private static native void nativeSetupScreenKeyboardButtons(byte[] img);
	public static native void nativeSetEnv(final String name, final String value);
}

