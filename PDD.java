package pdd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author speedex
 */
public class PDD {

    public static final int input_Altitude=50;
    public static final int input_Roll=51;
    public static final int input_Pitch=52;
    public static final int input_Airspeed=55;
    public static final int input_Elevator_position=2;
    public static final int input_Rpm=78;
    public static final int input_Vertical_speed_fps=64;
    public static final int input_Speed_down_fps=58;
    public static final int input_Speed_east_fps=59;
    public static final int input_Speed_north_fps=60;
    public static final int input_v_body_fps=62;
    public static final int input_class=79;
    
    public static final int WINDOW_SIZE=5;

    private static String [] titleOutput(){
        return new String[]{
        "altitude_slope","altitude_var","altitude_min","altitude_max","altitude_size",
        "roll_slope","roll_var","roll_min","roll_max","roll_size",
        "pitch_slope","pitch_var","pitch_min","pitch_max","pitch_size",
        "airspeed_slope","airspeed_var","airspeed_min","airspeed_max","airspeed_size",
        "elevator_slope","elevator_var","elevator_min","elevator_max","elevator_size",
        "rpm_slope","rpm_var","rpm_min","rpm_max","rpm_size",
        "vertspeed_slope","vertspeed_var","vertspeed_min","vertspeed_max","vertspeed_size",
        "speedDown_slope","speedDown_var","speedDown_min","speedDown_max","speedDown_size",
        "speedEast_slope","speedEast_var","speedEast_min","speedEast_max","speedEast_size",
        "speedNorth_slope","speedNorth_var","speedNorth_min","speedNorth_max","speedNorth_size",
        "vbody_slope","vbody_var","vbody_min","vbody_max","vbody_size","class"};
    }
    
    private static String getSlope(ArrayList<String []>data,int attr){
        float [] attrData = new float[data.size()];
        for(int i=0;i<data.size();i++){
            attrData[i]=Float.parseFloat(data.get(i)[attr]);
        }
        float slope=(attrData[0]-attrData[attrData.length-1])/attrData.length;
        return Float.toString(slope);
    }
    private static float sum(float[] a) {
        float sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }
    
    private static float mean(float[] a) {
        float sum = sum(a);
        return sum / a.length;
    }
    
    private static float var(float[] a) {
        float avg = mean(a);
        float sum = 0.0F;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - avg) * (a[i] - avg);
        }
        return sum / (a.length - 1);
    }
    
    private static String getVar(ArrayList<String []>data,int attr){
        float [] attrData = new float[data.size()];
        for(int i=0;i<data.size();i++){
            attrData[i]=Float.parseFloat(data.get(i)[attr]);
        }
        return Float.toString(var(attrData));
    }
    
    private static String getMin(ArrayList<String []>data,int attr){
        float [] attrData = new float[data.size()];
        float min = Float.MAX_VALUE;
        for(int i=1;i<data.size();i++){
            attrData[i]=Float.parseFloat(data.get(i)[attr]);
            if(attrData[i]<min)min=attrData[i];
        }
        return Float.toString(min);
    }
    private static String getMax(ArrayList<String []>data,int attr){
        float [] attrData = new float[data.size()];
        float max=Float.MIN_VALUE;
        for(int i=1;i<data.size();i++){
            attrData[i]=Float.parseFloat(data.get(i)[attr]);
            if(attrData[i]>max)max=attrData[i];
        }
        return Float.toString(max);
    }
    private static String getSize(ArrayList<String []>data,int attr){
        float [] attrData = new float[data.size()];
        float size=0;
        for(int i=0;i<data.size();i++){
            attrData[i]=Float.parseFloat(data.get(i)[attr]);
            size+=Math.pow(attrData[i], 2);
        }        
        return Float.toString((float) Math.sqrt(size));
    }
    
    private static String getClass(ArrayList<String[]> data) {
        int class_cnt =0;
        for(String[] d:data){
            class_cnt+=Integer.parseInt(d[input_class]);
        }
        return (class_cnt>WINDOW_SIZE/2)?Integer.toString(1):Integer.toString(0);
    }
    
    
    public static String [] shrunkData(ArrayList<String []> data){        
        ArrayList <String> point = new ArrayList<>();
        int [] attributes = new int[]{input_Altitude,input_Roll,input_Pitch,
            input_Airspeed,input_Elevator_position,input_Rpm,input_Vertical_speed_fps,
            input_Speed_down_fps,input_Speed_east_fps,input_Speed_north_fps,input_v_body_fps};
        for(int i=0;i<attributes.length;i++){
            point.add(getSlope(data,attributes[i]));
            point.add(getVar(data,attributes[i]));
            point.add(getMin(data,attributes[i]));
            point.add(getMax(data,attributes[i]));
            point.add(getSize(data,attributes[i]));
        }
        point.add(getClass(data));
        String []array= new String[point.size()];
        return point.toArray(array);
    }
    
  
    
    public static String join(String [] array, String delimiter){
        String ret = "";
        ret+=array[0];
        for(int i=1;i<array.length;i++){
            ret+=delimiter;
            ret+=array[i];
        }
        return ret;
    }
  
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        ArrayList<String []> inputlist = new ArrayList<String []>();
        ArrayList<String []> outputlist= new ArrayList<String []>();
        BufferedReader br = new BufferedReader(new FileReader("vyvrtka_class.txt"));
        PrintWriter writer = new PrintWriter("vyvrtka_upraveno.txt", "UTF-8");
        String line;
        while( (line = br.readLine()) != null) {
            inputlist.add(line.split(","));
        }
        for(int i=0;i<inputlist.get(0).length;i++){
            System.out.println(i+"="+inputlist.get(0)[i]);
        }
        outputlist.add(titleOutput());
        for(int i=WINDOW_SIZE+1;i<inputlist.size();i+=WINDOW_SIZE){
            ArrayList<String []>data = new ArrayList<>();
            for(int j=0;j<WINDOW_SIZE;j++){
                data.add(inputlist.get(i-WINDOW_SIZE+j));
            }
            outputlist.add(shrunkData(data));
        }
        
        for(int i=0;i<outputlist.size();i++){
            writer.println(join(outputlist.get(i),","));
        }
        br.close();
        writer.close();
    }


    
}
