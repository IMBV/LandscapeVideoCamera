package com.jmolsmobile.landscapevideocapture_sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jmolsmobile.landscapevideocapture.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.PredefinedCaptureConfigurations.CaptureQuality;
import com.jmolsmobile.landscapevideocapture.PredefinedCaptureConfigurations.CaptureResolution;
import com.jmolsmobile.landscapevideocapture.VideoCaptureActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class CaptureDemoFragment extends Fragment {

	private final String	KEY_STATUSMESSAGE	= "com.jmolsmobile.statusmessage";
	private final String	KEY_FILENAME		= "com.jmolsmobile.outputfilename";

	private final String[]	RESOLUTION_NAMES	= new String[] { "480p", "720p", "1080p" };
	private final String[]	QUALITY_NAMES		= new String[] { "low", "medium", "high" };

	private String			statusMessage		= null;
	private String			filename			= null;

	private ImageView		thumbnailIv;
	private TextView		statusTv;
	private Spinner			resolutionSp;
	private Spinner			qualitySp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		final Button captureBtn = (Button) rootView.findViewById(R.id.btn_capturevideo);
		captureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startVideoCaptureActivity();
			}
		});

		thumbnailIv = (ImageView) rootView.findViewById(R.id.iv_thumbnail);
		statusTv = (TextView) rootView.findViewById(R.id.tv_status);
		if (savedInstanceState != null) {
			statusMessage = savedInstanceState.getString(KEY_STATUSMESSAGE);
			filename = savedInstanceState.getString(KEY_FILENAME);
		}

		updateStatusAndThumbnail();
		initializeSpinners(rootView);
		return rootView;
	}

	private void initializeSpinners(final View rootView) {
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, RESOLUTION_NAMES);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		resolutionSp = (Spinner) rootView.findViewById(R.id.sp_resolution);
		resolutionSp.setAdapter(adapter);

		final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, QUALITY_NAMES);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		qualitySp = (Spinner) rootView.findViewById(R.id.sp_quality);
		qualitySp.setAdapter(adapter2);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_STATUSMESSAGE, statusMessage);
		outState.putString(KEY_FILENAME, filename);
		super.onSaveInstanceState(outState);
	}

	private void startVideoCaptureActivity() {
		final Intent intent = new Intent(getActivity(), VideoCaptureActivity.class);
		final CaptureResolution resolution = getResolution(resolutionSp.getSelectedItemPosition());
		final CaptureQuality quality = getQuality(qualitySp.getSelectedItemPosition());
		final CaptureConfiguration config = new CaptureConfiguration(resolution, quality);
		intent.putExtra(VideoCaptureActivity.EXTRA_CAPTURE_CONFIGURATION, config);
		startActivityForResult(intent, 101);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			filename = data.getStringExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME);
			statusMessage = String.format(getString(R.string.status_capturesuccess), filename);
		} else if (resultCode == Activity.RESULT_CANCELED) {
			filename = null;
			statusMessage = getString(R.string.status_capturecancelled);
		} else if (resultCode == VideoCaptureActivity.RESULT_ERROR) {
			filename = null;
			statusMessage = getString(R.string.status_capturefailed);
		}
		updateStatusAndThumbnail();

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateStatusAndThumbnail() {
		if (statusMessage == null) {
			statusMessage = getString(R.string.status_nocapture);
		}
		statusTv.setText(statusMessage);

		final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filename, Thumbnails.FULL_SCREEN_KIND);
		if (thumbnail != null) {
			thumbnailIv.setImageBitmap(thumbnail);
		} else {
			thumbnailIv.setImageResource(R.drawable.thumbnail_placeholder);
		}
	}

	private CaptureQuality getQuality(int position) {
		final CaptureQuality[] quality = new CaptureQuality[] { CaptureQuality.LOW, CaptureQuality.MEDIUM,
				CaptureQuality.HIGH };
		return quality[position];
	}

	private CaptureResolution getResolution(int position) {
		final CaptureResolution[] resolution = new CaptureResolution[] { CaptureResolution.RES_480P,
				CaptureResolution.RES_720P };
		return resolution[position];
	}

}