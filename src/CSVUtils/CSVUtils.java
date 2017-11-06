package CSVUtils;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Author Erkki Halinen
 * A class of CSV Parsing utilities for a Salary Program
 */

public class CSVUtils{

    /**
     * Parses the given CSV file into String[] entries
     * where each line's values are in one String[]
     * @param csvFile File to be parsed
     * @param hasHeader If the file has a header or not
     * @return ArrayList of parsed values. Each cell contains a String array of that line's values
     * @throws FileNotFoundException thrown if the file cannot be found
     */
    public ArrayList<String[]> parseFile(File csvFile, boolean hasHeader, char separator)  throws FileNotFoundException {

        String valueSeparator = ""+separator;

        ArrayList<String[]> parsedFileValues = new ArrayList<>();

        Scanner scanner = new Scanner(new FileInputStream(csvFile));

        if(hasHeader) {

            String line = scanner.nextLine();
            String[] header = line.split(valueSeparator);

            parsedFileValues.add(header);

        }

        while(scanner.hasNext()) {

            String line = scanner.nextLine();
            String[] shift = line.split(valueSeparator);
            parsedFileValues.add(shift);

        }
        return parsedFileValues;
    }

    /**
     * Calculates the duration of a workshift
     * @param shiftEntry Shift entry containing one workshift(= one line from the CSV file)
     * @return The duration of the workshift. [0] is hours and [1] is minutes
     */
    public String[] calculateWorkShift(String[] shiftEntry) {

        DateTime[] times = convertToDateTime(shiftEntry);

        int eveningWorkHours = 0;

        for(DateTime count = times[0]; times[1].isAfter(count); count = count.plusHours(1)) {

            if(count.getHourOfDay() >=18 || count.getHourOfDay() <= 6) {
                eveningWorkHours++;
            }
        }

        //Duration temp = new Duration(times[0], times[1]);
        //Period shiftDuration = temp.toPeriod();

        return new String[] {""+eveningWorkHours};
    }

    /**
     * Converts given workshift entry from CSV file into a workshift start and end time
     * so that working hours can be calculated
     * @param values a worksheet entry from CSV file
     * @return LocalDateTime[], [0] has the starting time, [1] has the ending time
     */
    private DateTime[] convertToDateTime(String[] values) {

        DateTime[] shiftTimes = new DateTime[2];

        String startDateAndTime = values[2] + "." + values[3];
        String endDateAndTime = values[2] + "." + values[4];

        //Initialize the formatter for the entries
        org.joda.time.format.DateTimeFormatter format;
        format = DateTimeFormat.forPattern("dd.MM.yyyy.HH:mm");

        DateTime startTime = DateTime.parse(startDateAndTime, format);
        DateTime endTime = DateTime.parse(endDateAndTime, format);

        if(startTime.isAfter(endTime)) {
            endTime = endTime.plusDays(1);
        }

        shiftTimes[0] = startTime;
        shiftTimes[1] = endTime;

        //System.out.println(format.print(shiftTimes[0]) + "|" + format.print(shiftTimes[1]));

        return shiftTimes;

    }

    /*private double calculateTotalDailyPay(int[] regHoursAndMinutes, int eveningHours) {

        double regDailyWage = calculateRegDailyWage(regHoursAndMinutes);

    } */

    public double calculateRegDailyWage(int[] hoursAndMinutes) {

        double fullHoursWage = hoursAndMinutes[0] * 3.75;
        System.out.println(fullHoursWage);

        double remainingMinutesWage = 0;

        if(hoursAndMinutes[1] > 0) {

            //Somehow this stuff didn't work unless put into separate variables
            double temp = hoursAndMinutes[1];
            double temp2 = temp / 60;
            remainingMinutesWage = temp2 * 3.75;
        }

        System.out.println("¤¤¤¤ " + remainingMinutesWage);

        double asd = fullHoursWage + remainingMinutesWage;

        return fullHoursWage + remainingMinutesWage;
    }

}
