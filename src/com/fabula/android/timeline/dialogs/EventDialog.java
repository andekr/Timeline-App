package com.fabula.android.timeline.dialogs;

import java.util.ArrayList;
import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.fabula.android.timeline.NoteActivity;
import com.fabula.android.timeline.R;
import com.fabula.android.timeline.TimelineActivity;
import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.models.Emotion;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.SimpleNote;
import com.fabula.android.timeline.models.Emotion.EmotionColumns;
import com.fabula.android.timeline.sync.GAEHandler;
import com.fabula.android.timeline.utilities.MyLocation;

/**
 * 
 * The {@link Dialog} that shows the {@link Event}, it's items and actions on the {@link Event}.
 * 
 * @author andekr
 *
 */
public class EventDialog extends Dialog {
	
	private final Event mEvent;
	private final Activity mActivity;
	private Context mContext;
	private LinearLayout mainLayout, emotionLayout;
	private QuickAction qa;
	private List<EventItem> items;
	private ImageView dialogIcon;
	private Runnable shareEventThread;
	private String addressString="";
	private Address address;
	private boolean fromMap;

	public EventDialog(Context context, Event event, Activity activity, boolean fromMap){
		super(context);
		this.mContext = context;
		this.mEvent = event;
		this.mActivity = activity;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.popupcontentdialog);
		this.setCancelable(true);
		this.fromMap = fromMap;
		
		 dialogIcon = (ImageView)findViewById(R.id.PopupDialogTypeIconImageView);
         dialogIcon.setImageResource(Utilities.getImageIcon(this.mEvent));
         
         TextView dialogDateTime = (TextView)findViewById(R.id.PopupDialogDateAndTimeTextView);
         TextView dialogLocation = (TextView)findViewById(R.id.PopupDialogLocationTextView);
         
         //Set header (time and location)
         dialogDateTime.setText(DateFormat.format
        		 ("dd MMMM yyyy "+DateFormat.HOUR_OF_DAY+":mm:ss",this.mEvent.getDatetime()));
         if(this.mEvent.getLocation()!=null){
        try {
        	 address = MyLocation.getAddressForLocation(mContext, this.mEvent.getLocation());
	         addressString += address.getAddressLine(0)+", "; //Address
	         addressString += address.getAddressLine(1)+" "; // Zipcode and area
		} catch (Exception e) {
			addressString = "Ukjent sted";
		}
	        
         }
         dialogLocation.setText(addressString);
	
         mainLayout = (LinearLayout)findViewById(R.id.PopupContentLinearLayout);
         emotionLayout = (LinearLayout)findViewById(R.id.PopupMenuDockLinearLayout);
         updateMainview();
         
        ((TimelineActivity)mActivity).setSelectedEvent(this.mEvent);
         
 		setupAddButtonQuickAction();
 		setupEmotionButtonQuickAction();
 		
 		ImageButton shareButton = (ImageButton)findViewById(R.id.PopupShareButton);
 		shareButton.setTag(this.mEvent);
 		shareButton.setOnClickListener(new View.OnClickListener() {
 			
 			public void onClick(View v) {
 				Toast.makeText(mContext, "Sending event to server...", Toast.LENGTH_SHORT).show();
 				Thread shareThread = new Thread(shareEventThread, "shareThread");
 				shareThread.start();
 				}
 		});
 		
 		ImageButton deleteButton = (ImageButton)findViewById(R.id.popupDeleteButton);
 		deleteButton.setTag(event);
 		deleteButton.setOnClickListener(new View.OnClickListener() {
 			
 			public void onClick(View v) {
 				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
 				builder.setMessage(R.string.Delete_event_confirmation)
 				.setPositiveButton(R.string.yes_label, deleteEventListener)
 				.setNegativeButton(R.string.no_label, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							dialog.cancel();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				})
 				.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						try {
							dialog.cancel()	;	
						} catch (Exception e) {
							// TODO: handle exception
						}
										
					}
				});
 				
 				AlertDialog confirmation = builder.create();
 				confirmation.show();
 			}
 		});
 		
 		shareEventThread = new Runnable() {
			
			public void run() {
				GAEHandler.send(mEvent, mActivity);
			}
		};
 		
 		
	}
	
	public OnClickListener deleteEventListener = new OnClickListener() {	
		public void onClick(DialogInterface dialog, int which) {
			deleteEvent(mEvent);
		}
	};

	/**
	 * Update method to run after adding items
	 * 
	 */
	public void updateMainview() {
		items = this.mEvent.getEventItems();
        mainLayout.removeAllViews();
        emotionLayout.removeAllViews();
        dialogIcon.setImageResource(Utilities.getImageIcon(this.mEvent));
        ((TimelineActivity)mActivity).getEventAdapter().notifyDataSetChanged();
        try {
        	 qa.dismiss();
		} catch (NullPointerException e) {
			//Nothing. qa isn't created yet.
		}
       int i = 0;
		for (EventItem exItem : items) {
         	View contentView = exItem.getView(mContext);
             //contentView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
         	Intent openIntent = exItem.getIntent();
			if(openIntent!=null){
				contentView.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						Intent i = ((EventItem)v.getTag()).getIntent();
						mActivity.startActivity(i);
					}
				});
			}
         
         	contentView.setId(i);
         	contentView.setTag(exItem);
         	contentView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				
				public void onCreateContextMenu(ContextMenu menu, View v,
						ContextMenuInfo menuInfo) {
					menu.add(R.id.MENU_EXTRACT_ITEM, v.getId(), 0, R.string.extract_item_label);
					if(v.getTag() instanceof SimpleNote){
						menu.add(R.id.MENU_EDIT_NOTE, v.getId(), 0, R.string.Edit_label);
					}
					menu.add(R.id.MENU_DELETE_ITEM, v.getId(), 0, R.string.Delete_item_label);
				}
			});
         	
         	mainLayout.addView(contentView);
         	
        	i++;
			}
		
		for (Emotion emotion : mEvent.getEmotionList()) {
			ImageView emotionIcon  = getEmotionIcon(mContext.getResources().getDrawable(emotion.getIcon()));
			emotionLayout.addView(emotionIcon);
		}
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getGroupId()) {
		case R.id.MENU_EXTRACT_ITEM:
			Log.v("LONG-CLICK", "Extract: "+items.get(item.getItemId()).getId());
			extractEventItem(items.remove(item.getItemId()));
			return true;
		
		case R.id.MENU_EDIT_NOTE:
			Log.v("LONG-CLICK", "Edit: "+item.getItemId());
			Intent noteIntent = new Intent(mActivity.getBaseContext(), NoteActivity.class);
			noteIntent.putExtra("NOTE_ID", item.getItemId());//TODO: Ikke bra å bruke denne ID'en. Må tenke på bedre løsninger her...
			noteIntent.putExtra(Intent.EXTRA_SUBJECT, ((SimpleNote)items.get(item.getItemId())).getNoteTitle()); 
			noteIntent.putExtra(Intent.EXTRA_TEXT, ((SimpleNote)items.get(item.getItemId())).getNoteText());
			noteIntent.putExtra(Utilities.REQUEST_CODE, Utilities.EDIT_NOTE);
			
			mActivity.startActivityForResult(noteIntent, Utilities.EDIT_NOTE); 
			return true;
			
		case R.id.MENU_DELETE_ITEM:
			Log.v("LONG-CLICK", "Extract: "+items.get(item.getItemId()).getId());
			deleteEventItem(items.remove(item.getItemId()));
			
			return true;
	}
		
		return false;
	}
	
	private ImageView getEmotionIcon(Drawable icon) {
		ImageView img 	= new ImageView(mContext);
		
		if (icon != null) {
			img.setImageDrawable(icon);
			img.setPadding(5, 0, 5, 0);
			LayoutParams lp = new LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
			img.setLayoutParams(lp);
		} else {
			img.setVisibility(View.GONE);
		}
		
		return img;
	}
	
	private void setupAddButtonQuickAction() {
		final ActionItem camera = new ActionItem();
		
		camera.setIcon(mContext.getResources().getDrawable(R.drawable.ic_menu_camera));
		camera.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((TimelineActivity)mActivity).startCamera();						
			}
		});
				
				
		final ActionItem video = new ActionItem();
		
		video.setIcon(mContext.getResources().getDrawable(R.drawable.ic_menu_video));
		video.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((TimelineActivity)mActivity).startVideoCamera();
			}
		});
		
		final ActionItem audio = new ActionItem();
		
		audio.setIcon(mContext.getResources().getDrawable(R.drawable.ic_menu_audio));
		audio.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((TimelineActivity)mActivity).startAudioRecording();
			}
		});
		
		final ActionItem note = new ActionItem();
		
		note.setIcon(mContext.getResources().getDrawable(R.drawable.ic_menu_note));
		note.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((TimelineActivity)mActivity).createNote();
			}

		});
		
		final ActionItem attachment = new ActionItem();
		
		attachment.setIcon(mContext.getResources().getDrawable(R.drawable.ic_menu_attachment));
		attachment.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((TimelineActivity)mActivity).startAttachmentDialog();
			}
		});
        
        
		ImageButton addButton = (ImageButton)findViewById(R.id.PopupAddButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				qa = new QuickAction(v);
				
				qa.addActionItem(camera);
				qa.addActionItem(video);
				qa.addActionItem(audio);
				qa.addActionItem(note);
				qa.addActionItem(attachment);
				qa.setAnimStyle(QuickAction.ANIM_AUTO);
				qa.show();
				
			}
		});
		
	}
	
	private void setupEmotionButtonQuickAction() {
		final ActionItem like = new ActionItem();
		
		like.setIcon(mContext.getResources().getDrawable(Emotion.LIKE.getIcon()));
		like.setTag(mEvent);
		like.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((Event)v.getTag()).addEmotion(Emotion.LIKE);
				addEmotionToDatabase((Event) v.getTag(), Emotion.LIKE);
				Toast.makeText(mContext, R.string.like_toast , Toast.LENGTH_SHORT).show();
				Log.i(this.toString(), "LIKE event set");
				updateMainview();
			}
		});
				
				
		final ActionItem cool = new ActionItem();
		
		cool.setIcon(mContext.getResources().getDrawable(Emotion.COOL.getIcon()));
		cool.setTag(mEvent);
		cool.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((Event)v.getTag()).addEmotion(Emotion.COOL);
				addEmotionToDatabase((Event) v.getTag(), Emotion.COOL);
				Toast.makeText(mContext, R.string.cool_toast , Toast.LENGTH_SHORT).show();
				Log.i(this.toString(), "COOL event set");
				updateMainview();
			}
		});
		
		final ActionItem dislike = new ActionItem();
		
		dislike.setIcon(mContext.getResources().getDrawable(Emotion.DISLIKE.getIcon()));
		dislike.setTag(mEvent);
		dislike.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((Event)v.getTag()).addEmotion(Emotion.DISLIKE);
				addEmotionToDatabase((Event) v.getTag(), Emotion.DISLIKE);
				Toast.makeText(mContext, R.string.dislike_toast , Toast.LENGTH_SHORT).show();
				Log.i(this.toString(), "DISLIKE event set");
				updateMainview();
			}
		});
		
		final ActionItem sad = new ActionItem();
		
		sad.setIcon(mContext.getResources().getDrawable(Emotion.SAD.getIcon()));
		sad.setTag(mEvent);
		sad.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				((Event)v.getTag()).addEmotion(Emotion.SAD);
				addEmotionToDatabase((Event) v.getTag(), Emotion.SAD);
				Toast.makeText(mContext, R.string.sad_toast , Toast.LENGTH_SHORT).show();
				Log.i(this.toString(), "SAD event set");
				updateMainview();
			}

		});
		
        
		ImageButton emotionButton = (ImageButton)findViewById(R.id.PopupEmotionButton);
		emotionButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				qa = new QuickAction(v);
				
				qa.addActionItem(like);
				qa.addActionItem(cool);
				qa.addActionItem(dislike);
				qa.addActionItem(sad);
				qa.setAnimStyle(QuickAction.ANIM_AUTO);
				qa.show();
				
			}
		});
		
	}
	
	public void extractEventItem(EventItem exItem){
		Event newEvent = new Event(mEvent.getExperienceid(), mEvent.getLocation());
		newEvent.addEventItem(exItem);
		((TimelineActivity)mActivity).addEvent(newEvent);
        if(items.size()==0){
        	deleteEvent(mEvent);
        }else{
        	updateMainview();
        }
		
	}
	
	public void deleteEventItem(EventItem exItem){
		((TimelineActivity)mActivity).delete(exItem);
        if(items.size()==0){
        	deleteEvent(mEvent);
        }else{
        	updateMainview();
        }
	}
	
	public void deleteEvent(Event event){
		this.dismiss();
    	((TimelineActivity)mActivity).setSelectedEvent(null);
    	((TimelineActivity)mActivity).removeEvent(event);
	}
	
	private void addEmotionToDatabase(Event tag, Emotion like) {
		
		ContentValues values = new ContentValues();
		
		values.put(EmotionColumns.EVENT_ID, tag.getId());
		values.put(EmotionColumns.EMOTION_TYPE, like.getType());
		
		mContext.getContentResolver().insert(EmotionColumns.CONTENT_URI, values);
	}
	
	@Override
	public void onBackPressed() {
		
		if(fromMap) {
			((TimelineActivity) mActivity).openMapView();
		}
		super.onBackPressed();
	}
}
