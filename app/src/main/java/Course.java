import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course {
    private long timestamp;
    private double runtime;
    private String user;
    private double  track_length;
    private List<CourseNode> nodes;
    private double max_speed; //no min cuz that'll probably be 0 when the user stops
    private double avg_speed;
    private int rating;

    Course(String user){
        this.user=user;
        nodes = new ArrayList<>();
    }
    
    public void append_node(CourseNode x){
        //your validation here
        if (x.getVelocity()>max_speed)
            max_speed= x.getVelocity();
        getNodes().add(x);
    }

    public void finalize(){
        //calculate rating and avg_speed here, probably also duration based on nodes*time between node adds
        runtime = nodes.get(nodes.size()-1).getTime_stamp() - nodes.get(0).getTime_stamp();
        track_length=0;
        for(CourseNode x: nodes) track_length+=x.getDistance_from_last();
        timestamp = System.currentTimeMillis();
        avg_speed = track_length/runtime;
        rating = 0; //TODO: IMPLEMENT THIS
    }

    public LatLng centerMapPoint(){return nodes.get(nodes.size()/2).toLatLng();}

    public String getTimestamp() {//requires tweaks maybe
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));
        return formatter.format(date);
    }

    public String getRuntime() {//probably reformat worthy, try the thing above maybe idk
        return runtime/1e+9/3600 + " Hours";
    }

    public String getUser() {
        return user;
    }

    public String getTrack_length() {
        return track_length+" km";
    }

    public List<CourseNode> getNodes() {//need this to redraw course map
        return nodes;
    }

    public String getMax_speed() {
        return max_speed+ " km/h";
    }

    public String getAvg_speed() {
        return avg_speed+ " km/h";
    }

    public int getRating() {
        return rating;
    }
}
