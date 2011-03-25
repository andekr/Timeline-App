package com.fabula.android.timeline.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fabula.android.timeline.R;
import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.TimelineActivity;
import com.fabula.android.timeline.dialogs.EventDialog;
import com.fabula.android.timeline.dialogs.MoodDialog;
import com.fabula.android.timeline.exceptions.MaxZoomedOutException;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.MoodEvent;
import com.fabula.android.timeline.models.Zoom;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

/**
 * 
 * The adapter that renders the event in a grid representing a timeline 
 * 
 * @author andekr
 *
 */
public class TimelineGridAdapter extends ArrayAdapter<BaseEvent> {
	

	private Context mContext;
	private Activity mActivity;
	private EventDialog dialog;
	LinearLayout mainLayout;
	private HashMap<Integer, BaseEvent> displayedEvents;
	int slotNumber;
	Date currentZoomDate, defaultZoomDate;
	Zoom ZOOMTYPE;
	private MoodDialog moodDialog;

	public TimelineGridAdapter(Context context, Activity activity) {
		super(context, 0);
		mContext = context;
		mActivity = activity;
		displayedEvents = new HashMap<Integer, BaseEvent>();
		defaultZoomDate = new Date();
	}
	
	/**
	 * Adds an Event to the timeline based on the time the event was created.
	 * 
	 * 
	 * @param event The Event to be added
	 */
	public void addEvent(BaseEvent event) {
		super.add(event);
		int hour = event.getDatetime().getHours();
		int minute = event.getDatetime().getMinutes();
		int dayofweek = event.getDatetime().getDay();
		int date = event.getDatetime().getDate();
		int firstdayofmonth = Utilities.getFirstDayOfMonth(event.getDatetime()).getDay();
		Log.v("GridAdapter", "Time: "+hour+" "+minute);
		switch (ZOOMTYPE.getType()) {
		case Utilities.DAY_MODE:
			slotNumber = (int)Math.floor(hour)+ZOOMTYPE.getColumns();
			break;
		case Utilities.HOUR_MODE:
			slotNumber = (int)Math.floor(minute/(60/ZOOMTYPE.getColumns()))+ZOOMTYPE.getColumns();
			break;
		case Utilities.WEEK_MODE:
			slotNumber = (int)Math.floor(dayofweek-1)+ZOOMTYPE.getColumns();
			break;
		case Utilities.MONTH_MODE:
			int firstSlotOfTheMonth = ZOOMTYPE.getColumns()+(firstdayofmonth-2);
			slotNumber = firstSlotOfTheMonth+(date);
			break;

		default:
			slotNumber = (int)Math.floor(hour)+ZOOMTYPE.getColumns();
			break;
		}
		if(ZOOMTYPE!=Zoom.MONTH){
		//If the slot is "taken" try in the next slot
		while (displayedEvents.containsKey(slotNumber)) {
			slotNumber = slotNumber+ZOOMTYPE.getColumns();
		}
		}
		Log.i(TimelineGridAdapter.class.toString(), "Inserted event into slot # "+slotNumber);
		displayedEvents.put(slotNumber, event);
		notifyDataSetChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void removeEvent(Event event){
		super.remove(event);
        Set<?> s=displayedEvents.entrySet();

        Iterator<?> it=s.iterator();

        int key=-1;
        while(it.hasNext())
        {
            Map.Entry m =(Map.Entry)it.next();

            key=(Integer)m.getKey();
            System.out.println(event.getId());
            System.out.println(((Event)m.getValue()).getId());

            if(event.getId().equals(((Event)m.getValue()).getId())){
            	break;
            }
        }
        try {
        	displayedEvents.remove(key);
		} catch (Exception e) {
		}
        
        
        notifyDataSetChanged();
        
	}
	
	
	/**
	 * 
	 * Renders the Views(the squares in the Grid).
	 * A bit "hackish" in the sense that we put in time as textview for the first slots to represent the time.
	 * 
	 */
    public View getView(int position, View convertView, ViewGroup parent) {
    	if(position<ZOOMTYPE.getColumns()){
    		Log.i(this.getClass().getSimpleName()+" getView", "Creating headers"+ZOOMTYPE.toString());
    		TextView textView = new TextView(mContext);
    		switch (ZOOMTYPE.getType()) {
			case Utilities.DAY_MODE: //Creates "timeline"
				textView.setText("|"+String.valueOf(position)+":00");
				textView.setTag(position);
				break;
			case Utilities.HOUR_MODE: //Creates "timeline"
				textView.setText("|"+getZoomDate().getHours()+":"+getMinuteOnPosition(position));
				break;
			case Utilities.WEEK_MODE: //Creates "timeline"
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(Utilities.getFirstDayOfWeek(getZoomDate()));
				cal2.add(Calendar.DATE, (position));
				int displayDate = cal2.get(Calendar.DAY_OF_MONTH);
				int displaymonth = cal2.get(Calendar.MONTH);
				textView.setText("|"+displayDate+"."+(displaymonth+1));
				textView.setTag(cal2.getTime());
				break;
			case Utilities.MONTH_MODE: //Creates "calendar"
				textView.setText("|"+Utilities.getDayName(position));
				Calendar monthCal = Calendar.getInstance();
				monthCal.setTime(Utilities.getFirstDayOfWeek(getZoomDate()));
				monthCal.set(Calendar.DAY_OF_MONTH, 1);
				textView.setTag(-1);
				break;

			default:
				break;
			}
    			
    		textView.setGravity(Gravity.CENTER_HORIZONTAL);
    		textView.setTextSize(12);
    		textView.setTextColor(mContext.getResources().getColor(android.R.color.black));
    		textView.setPadding(0, 0, 0, 10);
    		
    		 ((AdapterView<?>)parent).setOnItemClickListener(new OnItemClickListener() {

  				public void onItemClick(AdapterView<?> arg0, View v,
  						int arg2, long arg3) {
  						handleClick(v);		
  				}
    		  
  			});
    		
    		return textView;
    	}else{
    		
    		if(ZOOMTYPE==Zoom.MONTH){
    			 //Reset
    	    	 ((AdapterView<?>)parent).setOnLongClickListener(null);
    	    	  ((AdapterView<?>)parent).setOnCreateContextMenuListener(null);
    	    	  
    			TextView textView = new TextView(mContext);
    			try {
    				textView.setText(String.valueOf(Utilities.convertGridPositionToDate(position, getZoomDate()).getDate()));
				} catch (NullPointerException e) {
					textView.setText("");
				}
    			
    			
    			textView.setGravity(Gravity.CENTER);
        		textView.setTextSize(18);
        		textView.setPadding(10, 6, 10, 6);
        		if(displayedEvents.containsKey(position)){
        			textView.setTextColor(mContext.getResources().getColor(R.color.Green));
        			textView.setTag(Utilities.convertGridPositionToDate(position, getZoomDate()));
        		}else{
        			textView.setTextColor(mContext.getResources().getColor(android.R.color.black));
        			textView.setTag(null);
        		}
        		
        		 ((AdapterView<?>)parent).setOnItemClickListener(new OnItemClickListener() {

     				public void onItemClick(AdapterView<?> arg0, View v,
     						int arg2, long arg3) {
     						handleClick(v);		
     				}
       		  
     			});
        		
    			return textView;
    		}{
    		  ImageView imageView;
//          if (convertView == null) {  // if it's not recycled, initialize some attributes. TODO: Fikse slik at recycling funker. Slik det er nå dukker ikonet opp random plasser pga. recycling.
              imageView = new ImageView(mContext);
              imageView.setLayoutParams(new GridView.LayoutParams(40, 40));
              imageView.setScaleType(ImageView.ScaleType.FIT_XY);
              imageView.setPadding(0, 0, 0, 0);
              
              if(displayedEvents.containsKey(Integer.valueOf(position))){
            	  BaseEvent ex = displayedEvents.get(position); //CASTED FROM BASEEVENT TO EVENT
            	  imageView.setTag(ex);
            	  if(ex instanceof Event) {
            		  imageView.setImageResource(Utilities.getImageIcon((Event)ex));
            	  }
            	  else if(ex instanceof MoodEvent) {
            		  imageView.setImageResource(((MoodEvent) ex).getMood().getIcon());
            	  }

            	  
            	  
            	  imageView.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					handleClick(v);
				}
			});
            	  
            	  imageView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				
				public void onCreateContextMenu(ContextMenu menu, View v,
						ContextMenuInfo menuInfo) {
					menu.add(R.id.MENU_DELETE_EVENT, 0 , 0, R.string.Delete_event_label);
				}
			});
         	
            	  imageView.setOnLongClickListener(new View.OnLongClickListener() {
				
				public boolean onLongClick(View v) {
					((TimelineActivity)mActivity).setSelectedEvent((Event)v.getTag());
					Log.i(TimelineGridAdapter.class.toString(), "LongClick - selected Event set: "+((Event)v.getTag()).getId());
					return false;
				}
			});
         	
              }
//          } else {
//              imageView = (ImageView) convertView;
//          }
              return imageView;
    	}
    	}
          
          
    }

    private Date getDateOnPosition(int position) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(getZoomDate());
		cal.set(Calendar.HOUR_OF_DAY, position);
		return cal.getTime();
	}

	private void handleClick(View v) {
    try {
    	if(v instanceof ImageView){
    		Boolean showing = false;
    		try {
    			showing = dialog.isShowing();
    		} catch (NullPointerException e) {
    			// TODO: handle exception
    		}
    		if(!showing){
            //set up dialog
    		if((BaseEvent) v.getTag() instanceof Event) {
                dialog = new EventDialog(mContext,(Event)v.getTag(),mActivity, false);
                dialog.setOnCancelListener(new OnCancelListener() {
        			public void onCancel(DialogInterface dialog) {
        				((TimelineActivity)mActivity).setSelectedEvent(null);
        			}
        		});
                dialog.show();
    		}
    		else if((BaseEvent) v.getTag() instanceof MoodEvent) {
    			moodDialog = new MoodDialog(mContext, (MoodEvent)v.getTag());
    			moodDialog.show();
    		}

    		}
    	}else if (v instanceof TextView){
    		switch (ZOOMTYPE.getType()) {
			case Utilities.DAY_MODE:
				Log.i(TimelineGridAdapter.class.toString(), "Sets current zoom time(HOUR): "+getDateOnPosition((Integer)v.getTag()));
				setZoomType(Zoom.HOUR, getDateOnPosition((Integer)v.getTag()));
				break;
			case Utilities.WEEK_MODE:
				Log.i(TimelineGridAdapter.class.toString(), "Sets current zoom time(DAY): "+(Date)v.getTag());
				setZoomType(Zoom.DAY, (Date)v.getTag());
				break;
			case Utilities.MONTH_MODE:
					if(v.getTag()==null)
						Toast.makeText(mContext, R.string.no_events_toast, Toast.LENGTH_SHORT).show();
					else if(v.getTag() instanceof Date){
						Log.i(TimelineGridAdapter.class.toString(), "Sets current zoom time(DAY): "+(Date)v.getTag());
						setZoomType(Zoom.DAY, (Date)v.getTag());
					}
						
				break;

			default:
				break;
			}
    		
    		notifyDataSetChanged();
    	}
	
	} catch (NullPointerException e) {
		Log.e("GridAdapter", "No event on this spot!");
	}
    	
    	
	}

	public int getCount() {
		if(ZOOMTYPE==Zoom.MONTH){
			return 49;
		}else{
			// TODO: Do something more dynamic here
			if(displayedEvents.size()>72)
				return displayedEvents.size();
			else
				return getMaxSlotNumber()+49;
		}
	}
	


	public void updateDialog(){
		dialog.updateMainview();
	}

	public HashMap<Integer, BaseEvent> getEvents() {
		return displayedEvents;
	}
	
	@SuppressWarnings("unchecked")
	private int getMaxSlotNumber(){
		int max = 0;
        Set<?> s=displayedEvents.entrySet();

        Iterator<?> it=s.iterator();

        int key=-1;
        while(it.hasNext())
        {
            Map.Entry m =(Map.Entry)it.next();

            key=(Integer)m.getKey();
            if(key>max)
            	max=key;
        }
        
       return max;
	}
	
	//CHANGED FROM EVENT TO BASEEVENT BENEATH
	private void setEventsInHour(Date hour){
		List<BaseEvent> allEvents = ((TimelineActivity)mActivity).getTimeline().getEvents();
		
		if(hour==null){
			hour = allEvents.get(0).getDatetime();
		}
		
//		this.hour = hour;
		
		displayedEvents.clear();
		
		for (BaseEvent event : allEvents) {
			if(Utilities.isSameHour(event.getDatetime(), hour))
					addEvent(event);
		}
	}
	
	private void setEventsInDay(Date day) {
		List<BaseEvent> allEvents = ((TimelineActivity)mActivity).getTimeline().getEvents();
		
		
		if(day==null){
			day = allEvents.get(0).getDatetime();
		}
		displayedEvents.clear();
		
		for (BaseEvent events : allEvents) {
			Log.i("GRIDADAPTER - GETEXPEVENTSONDAYFROMEXPERIENCE", events.getDatetime().getDate()+" vs "+day);
			if(Utilities.isSameDay(events.getDatetime(), day))
				addEvent(events);
		}
	}
	
	private void setEventsInWeek(Date week){
		List<BaseEvent> allEvents = ((TimelineActivity)mActivity).getTimeline().getEvents();
		if(week==null){
			week = allEvents.get(0).getDatetime();
		}
			
		displayedEvents.clear();

		for (BaseEvent event : allEvents) {
			 Calendar cal1 = Calendar.getInstance();
			  cal1.setTime(event.getDatetime());
			  Log.i("GRIDADAPTER - GETEXPEVENTSONWEEKFROMEXPERIENCE", cal1.get(Calendar.WEEK_OF_YEAR)+" vs "+week);
			if(Utilities.isSameWeek(event.getDatetime(), week))
				addEvent(event);
		}
	}
	
	private void setEventsInMonth(Date month){
		List<BaseEvent> allEvents = ((TimelineActivity)mActivity).getTimeline().getEvents();
		
		if(month==null){
			try {
				month = allEvents.get(0).getDatetime();
			} catch (Exception e) {
				month = new Date();
				defaultZoomDate = new Date();
			}
			
		}
		displayedEvents.clear();

		for (BaseEvent event : allEvents) {
			 Calendar cal1 = Calendar.getInstance();
			 cal1.setTime(event.getDatetime());
			 Log.i("GRIDADAPTER - GETEXPEVENTSONMONTHSFROMEXPERIENCE", cal1.get(Calendar.MONTH)+" mot "+month);
			if(Utilities.isSameMonth(event.getDatetime(), month))
				addEvent(event);
		}
	}

	
	public void setZoomType(Zoom zoomtype, Date date){
		ZOOMTYPE = zoomtype;
		((TimelineActivity)mActivity).getGridView().setNumColumns(ZOOMTYPE.getColumns());
		Log.i(TimelineGridAdapter.class.toString(), "Zoomtype set to "+zoomtype.toString());
//		if(date==null)
//			date = getHourAndDayZoom();
		
		switch (ZOOMTYPE.getType()) {
		case Utilities.HOUR_MODE:
			int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 1000, mContext.getResources().getDisplayMetrics());
			((TimelineActivity)mActivity).getGridView().setLayoutParams(new LinearLayout.LayoutParams(pixels, LayoutParams.FILL_PARENT));
					setEventsInHour(date);
			
				try {
					Log.i(TimelineGridAdapter.class.toString(), "Zoomdate set to "+date);
					setZoomDate(date);
				} catch (NullPointerException e) {
					e.printStackTrace();
					setZoomDate(getHourAndDayZoom());
				}
			
				mActivity.setTitle(DateFormat.format("dd MMMM yyyy "+DateFormat.HOUR_OF_DAY+":00", getZoomDate()));
				((TimelineActivity)mActivity).scrollLeftmost(0);
			break;
		case Utilities.DAY_MODE:
				int day_pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
	                    (float) 1000, mContext.getResources().getDisplayMetrics());
				((TimelineActivity)mActivity).getGridView().setLayoutParams(new LinearLayout.LayoutParams(day_pixels, LayoutParams.FILL_PARENT));
			
				setEventsInDay(date);
				
			
				try {
					Log.i(TimelineGridAdapter.class.toString(), "Zoomdate set to "+date);
					setZoomDate(date);
				} catch (NullPointerException e) {
					setZoomDate(getHourAndDayZoom());
				}
			
				mActivity.setTitle(DateFormat.format("dd MMMM yyyy ",getZoomDate()));
				((TimelineActivity)mActivity).scrollRight(0);
		break;
		case Utilities.WEEK_MODE:
			int week_pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 500, mContext.getResources().getDisplayMetrics());
			((TimelineActivity)mActivity).getGridView().setLayoutParams(new LinearLayout.LayoutParams(week_pixels, LayoutParams.FILL_PARENT));
			
			setEventsInWeek(date);
		
			
			try {
				Log.i(TimelineGridAdapter.class.toString(), "Zoomdate set to "+date);
				setZoomDate(date);
			} catch (NullPointerException e) {
				setZoomDate(getHourAndDayZoom());
			}
		
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat("w yyyy");
			mActivity.setTitle("Week "+formatter.format(getZoomDate()));
		break;
		case Utilities.MONTH_MODE:
			int month_pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 500, mContext.getResources().getDisplayMetrics());
			((TimelineActivity)mActivity).getGridView().setLayoutParams(new LinearLayout.LayoutParams(month_pixels, LayoutParams.FILL_PARENT));
				
			setEventsInMonth(date);
				
			try {
				Log.i(TimelineGridAdapter.class.toString(), "Zoomdate set to "+date);
				setZoomDate(date);
			} catch (NullPointerException e) {
				setZoomDate(getHourAndDayZoom());
			}
		
			mActivity.setTitle(DateFormat.format("MMMM yyyy ",getZoomDate()));
		break;

		default:
			break;
		}
		
		
		notifyDataSetChanged();
	}
	
	public void updateAdapter(){
		displayedEvents.clear();
		switch (ZOOMTYPE.getType()) {
		case Utilities.HOUR_MODE:
			int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 1000, mContext.getResources().getDisplayMetrics());
			((TimelineActivity)mActivity).getGridView().setLayoutParams(new LinearLayout.LayoutParams(pixels, LayoutParams.FILL_PARENT));
				setEventsInHour(getZoomDate());
				mActivity.setTitle(DateFormat.format("dd MMMM yyyy "+DateFormat.HOUR_OF_DAY+":00",getZoomDate()));

//				setZoomDate(getHourAndDayZoom());
				((TimelineActivity)mActivity).scrollRight(0);
			break;
		case Utilities.DAY_MODE:
			int day_pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 1000, mContext.getResources().getDisplayMetrics());
			((TimelineActivity)mActivity).getGridView().setLayoutParams(new LinearLayout.LayoutParams(day_pixels, LayoutParams.FILL_PARENT));
				setEventsInDay(getZoomDate());
				mActivity.setTitle(DateFormat.format("dd MMMM yyyy ",getZoomDate()));
//				setZoomDate(getHourAndDayZoom());
				((TimelineActivity)mActivity).scrollRight(0);
			break;
		case Utilities.WEEK_MODE:
			int week_pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 500, mContext.getResources().getDisplayMetrics());
			((TimelineActivity)mActivity).getGridView().setLayoutParams(new LinearLayout.LayoutParams(week_pixels, LayoutParams.FILL_PARENT));
			setEventsInWeek(getZoomDate());
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat("w yyyy");
			mActivity.setTitle("Week "+formatter.format(getZoomDate()));
//			setZoomDate(getHourAndDayZoom());
		break;
		case Utilities.MONTH_MODE:
			int month_pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                    (float) 500, mContext.getResources().getDisplayMetrics());
			((TimelineActivity)mActivity).getGridView().setLayoutParams(new LinearLayout.LayoutParams(month_pixels, LayoutParams.FILL_PARENT));
			setEventsInMonth(getZoomDate());
			mActivity.setTitle(DateFormat.format("MMMM yyyy ",getZoomDate()));
//			setZoomDate(getHourAndDayZoom());
		break;

		default:
			break;
		}
		
		
		notifyDataSetChanged();
	}
	
	private Date getHourAndDayZoom(){
		BaseEvent ex = null;
		 for(Map.Entry<Integer, BaseEvent> entry : displayedEvents.entrySet()){
	        	ex =  entry.getValue(); //CASTED FROM BaseEvent to Event
	        	break;
	        }
		 if(ex==null)
			 return defaultZoomDate;
		 else
			 return ex.getDatetime();
	}
	
	private String getMinuteOnPosition(int position){
		if((position*60/ZOOMTYPE.getColumns())<10)
			return "0"+position*(60/ZOOMTYPE.getColumns());
		else
			return String.valueOf(position*(60/ZOOMTYPE.getColumns()));
	}

	public void zoomOut() throws MaxZoomedOutException {
		if(ZOOMTYPE==Zoom.MONTH){
			throw new MaxZoomedOutException();
		}
//			Toast.makeText(mContext, "You are max zoomed out", Toast.LENGTH_SHORT).show();
		else
			setZoomType(ZOOMTYPE.getPrevious(), getZoomDate());
	}

	public Date getZoomDate() {
		return currentZoomDate;
	}

	public void setZoomDate(Date midDate) throws NullPointerException {
		if(midDate==null)
			throw new NullPointerException("Zoomdate cannot be null!");
		else
			this.currentZoomDate = midDate;
	}

	public void minusOne() {
		Date newdate = Utilities.adjustDate(ZOOMTYPE, getZoomDate(), -1);
		System.out.println("Newdate "+newdate);
		setZoomDate(newdate);
		updateAdapter();
	}
	
	public void plusOne() {
		Date newdate = Utilities.adjustDate(ZOOMTYPE, getZoomDate(), 1);
		System.out.println("Newdate "+newdate);
		setZoomDate(newdate);
		updateAdapter();
	}
	
	
}