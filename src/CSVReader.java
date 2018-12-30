import javax.lang.model.element.Name;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static java.nio.charset.StandardCharsets.*;


public class CSVReader {



   public static List<Shift> readShiftsFromCSV(String fileName) {
        List<Shift> shifts = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                US_ASCII)) {


            String line = br.readLine(); // loop until all lines are read

            //if(line!=null){
//
  //          }

            while((line=br.readLine())!= null){

                String[] attributes = line.split(",");


                Shift shift = createShift(attributes);

                shifts.add(shift); // read next line before looping // if end of file reached, line would be null
                line = br.readLine();

            }


        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return shifts;
    }


    private static Shift createShift(String[] metadata) {
        String Area = metadata[0];
        String Employee = metadata[1];
        String EndTime = (metadata[2]);
        String MealBreak = metadata[3];
        String Published = metadata[4];
        String StartTime = metadata[5];
        String Open = metadata[6];
        // create and return book of this metadata
        Shift shift= new Shift(Area, Employee, EndTime, MealBreak, Published, StartTime, Open);
        //return new Shift(Area, Employee, EndTime, MealBreak, Published, StartTime, Open);
       // System.out.println(shift);
        return shift;

    }

}

class Shift {
    private String Area;
    private String Employee;
    private String endTime;
    private String mealbreak;
    private String published;
    private String startTime;
    private String open;


    public Shift(String Area, String Employee, String endTime, String mealbreak, String published, String startTime, String open) {
        this.Area = Area;
        this.Employee = Employee;
        this.endTime = endTime;
        this.mealbreak = mealbreak;
        this.published = published;
        this.startTime = startTime;
        this.open = open;
    }

    public String getArea() {

        if (Area.equals("Agent"))
            Area = "4";
        else if (Area.equals("Lead Agent"))
            Area = "3";
        else if (Area.equals("Lead of Day"))
            Area = "5";
        else if (Area.equals("Customer Care Agent"))
            Area = "6";
        else if (Area.equals("Training"))
            Area = "7";
        else if (Area.equals("Office"))
            Area = "8";
        else if (Area.equals("Community Meeting"))
            Area = "9";
        else if (Area.equals("Trial Agent"))
            Area = "10";
        else if (Area.equals("Onboarding Specialist"))
            Area = "11";
        else if (Area.equals("Conference"))
            Area = "12";
        else if (Area.equals("Shipping and Provisioning"))
            Area = "13";
        else if (Area.equals("Analyst"))
            Area = "14;";
        else if (Area.equals("Tech Support"))
            Area = "15";
        else if (Area.equals("ATIA Agent"))
            Area = "16";
        else if (Area.equals("Engagement-Cancel Pro"))
            Area = "17";
        else if (Area.equals("Lead Care Bear"))
            Area = "18";
        else if (Area.equals("Overnight-Weekday"))
            Area = "20";
        else if (Area.equals("Overnight-Weekend"))
            Area = "21";
        else if (Area.equals("Agent-Weekday"))
            Area = "22";
        else if (Area.equals("Agent-Weekend"))
            Area = "23";
        return Area;

    }



    public String getEmployee() {
        return Employee;
    }

    public String getEndDate() {
        String[] time = endTime.split( "\\s+");
        return time[0];
    }
    public String getEndTime() {
        String[] time = endTime.split( "\\s+");
        //System.out.println("endtime:"+time[1]);
        //time[1].replace(":", "");
        time[1]=time[1].replace(":00", "0");
        return time[1];
    }

    public String getMealbreak() {
        return mealbreak;
    }

    public String getPublished() {
        return published;
    }

    public String getStartDate() {
        String[] time = endTime.split( "\\s+");

        return time[0];
    }


    public String getStartTime() {
        String[] time = startTime.split( "\\s+");
        time[1]=time[1].replace(":00", "0");
        //System.out.println("starttime:"+time[1]);

        return time[1];
    }

    public String getOpen() {
        return open;
    }


    @Override
    public String toString() {
        //return "Shift [Area=" + Area + ", Start=" + startTime + ", End=" + endTime + "]";

        return "Shift [Area=" + Area + ", Start=" + startTime + ", End=" + endTime + ",Open="+open+",Published="+published+ ",Employee="+Employee+",Meal="+mealbreak+"]";

    }


}






