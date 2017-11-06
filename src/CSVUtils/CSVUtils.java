package CSVUtils;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Author Erkki Halinen
 * A class of CSV Parsing utilities for a Salary Program
 */

public class CSVUtils {

    private final double EVENING_COMPENSATION = 1.15;
    private final double HOURLY_WAGE = 3.75;

    private static DecimalFormat decimalFormat = new DecimalFormat("##.##");

    /**
     * Parses the given CSV file into String[] entries
     * where each line's values are in one String[]
     * and then combines them into ArrayList<String[]> containing
     * the combined work shifts into one monthly payment
     * @param csvFile File to be parsed
     * @param hasHeader If the file has a header or not
     * @return ArrayList of parsed values. Each cell contains a String array of that line's values
     * @throws FileNotFoundException thrown if the file cannot be found
     */
    public ArrayList<String[]> parseFile(File csvFile, boolean hasHeader, char separator)  throws FileNotFoundException {

        String valueSeparator = ""+separator;

        ArrayList<String[]> parsedFileValues = new ArrayList<>();

        Scanner scanner = new Scanner(new FileInputStream(csvFile));

        //Skip the header reading
        if(hasHeader && scanner.hasNext()) {
            scanner.nextLine();
        }

        while(scanner.hasNext()) {

            String line = scanner.nextLine();
            String[] shift = line.split(valueSeparator);
            parsedFileValues.add(calculateWorkShift(shift));

        }
        return combineEntries(parsedFileValues);
    }

    /**
     * Combines all workshifts of one person into one total monthly payment
     * @param parsedFile
     * @return
     */
    private ArrayList<String[]> combineEntries(ArrayList<String[]> parsedFile) {

        ArrayList<String> employees = new ArrayList<>();

        ArrayList<String[]> combinedShifts = new ArrayList<>();

        combinedShifts.add(generateHeader());

        for(String[] arr : parsedFile) {

            if(!employees.contains(arr[0] + "," + arr[1])) {
                employees.add(arr[0] + "," + arr[1]);
            }
        }

        for(String employee : employees) {

            combinedShifts.add(new String[] {
                                employee,
                                String.valueOf(sumUpDailyTotals(parsedFile, employee))
                                });

        }

        return combinedShifts;
    }

    /**
     * Sums up the given employees payments from work shifts
     * @param workShifts List of parsed CSV line values
     * @param employee Employees name and ID concatenated and split with ','
     * @return
     */
    private double sumUpDailyTotals(ArrayList<String[]> workShifts, String employee) {

        double monthlyPayment = 0;

        for(String[] shift : workShifts) {
            if(employee.equals(shift[0] + "," + shift[1])) {
                monthlyPayment += Double.parseDouble(shift[6]);
            }
        }

        System.out.println("Total for employee " + employee + ": " + monthlyPayment);

        return monthlyPayment;
    }

    /**
     * Generates a header for a combineEntries ArrayList
     * @return Header
     */
    private String[] generateHeader() {

        return new String[] {

                "Person Name",
                "Person ID",
                "Monthly payment",

        };
    }


    /**
     * Converts given ArrayList into Comma Separated Values (CSV) file
     * @param filePath
     * @param arrayList
     */
    public boolean intoCSVFile(String filePath, ArrayList<String[]> arrayList) throws IOException {

        boolean fileWasCreated = false;

            File monthlyPayments = new File(filePath);
            if (monthlyPayments.createNewFile()) {

                //Create a writer for the file
                FileWriter writer = new FileWriter(monthlyPayments);

                for (String[] arr : arrayList) {
                    //Concatenate values of one line with delimiter between values
                    String oneLine = String.join(",", arr);
                    System.out.println(oneLine);
                    writer.write(oneLine + "\n");
                }

                writer.flush();
                writer.close();

                fileWasCreated = true;

            }
        return fileWasCreated;
    }

    /**
     * Calculates the duration and wages of a workshift
     * @param shiftEntry Shift entry containing one workshift(= one line from the CSV file)
     * @return The duration of the workshift. [0] is hours and [1] is minutes
     * Values returned in array indexes:
     *      0           1               2               3                   4                       5                       6
     * Person Name, Person ID, Workshift Duration, Regular wage, Evening Work Compensation, Overtime Compensation, Total Daily Pay
     */
    private String[] calculateWorkShift(String[] shiftEntry) {

        DateTime[] times = convertToDateTime(shiftEntry);

        double regWage = 0;
        double eveningCompensation = 0;
        double overtimeCompensation = 0;
        double totalDailyWage = 0;

        Duration temp = new Duration(times[0], times[1]);
        Period shiftDuration = temp.toPeriod();

        //Calculate overtime compensation if shiftDuration > 8
        int regularHours = shiftDuration.getHours();
        if(regularHours > 8) {
            regularHours = regularHours - 8;
            overtimeCompensation = calculateOvertimeCompensation(shiftDuration.getHours() - 8, shiftDuration.getMinutes());
            regWage = calculateRegDailyWage(regularHours, 0);
        } else {
            regWage = calculateRegDailyWage(regularHours, shiftDuration.getMinutes());
        }

        //Calculate the evening hours for a workshift
        int eveningWorkHours = 0;
        for(DateTime count = times[0]; times[1].isAfter(count); count = count.plusHours(1)) {

            if(count.getHourOfDay() > 18 || count.getHourOfDay() < 6) {
                eveningWorkHours++;
            }
        }

        //Calculate the evening work compensation if evening work hours > 0
        if(eveningWorkHours > 0) {
           eveningCompensation = calculateEveningCompensation(eveningWorkHours, EVENING_COMPENSATION);
        }

        //Finally calculate the total daily wage
        totalDailyWage = calculateTotalDailyPay(regWage, eveningCompensation, overtimeCompensation);

        //Construct a new entry with calculated wages and additional information about the workshift
        String[] calculatedEntry = {
                shiftEntry[0],
                shiftEntry[1],
                String.valueOf(shiftDuration.getHours() + ":" + shiftDuration.getMinutes()),
                String.valueOf(decimalFormat.format(regWage)),
                String.valueOf(decimalFormat.format(eveningCompensation)),
                String.valueOf(decimalFormat.format(overtimeCompensation)),
                String.valueOf(decimalFormat.format(totalDailyWage))
            };

        return calculatedEntry;
    }

    /**
     * Calculates evening work compensation according to given compensation amount.
     * @param eveningWorkHours Amount of evening work hours
     * @return Evening compensation
     */
    private double calculateEveningCompensation(int eveningWorkHours, double compensation) {

        return eveningWorkHours * compensation;

    }

    /**
     * Calculates the daily total pay.
     * Total daily pay comprises of Regular wage and possible evening and overtime compensation
     * @param regWage
     * @param eveningCompensation
     * @param overtimeCompensation
     * @return
     */
    private double calculateTotalDailyPay(double regWage, double eveningCompensation, double overtimeCompensation) {

        return (regWage + eveningCompensation + overtimeCompensation);
    }

    /**
     * Calculates the regular daily wage of one entry
     * @param hours regular working hours
     * @param minutes minutes remaining
     * @return Regular wage to be paid
     */
    private double calculateRegDailyWage(int hours, int minutes) {

        double fullHoursWage = hours * 3.75;

        double remainingMinutesWage = 0;

        if(minutes > 0) {

            //Somehow this stuff didn't work unless put into separate variables
            double temp = (double)minutes / 60;
            remainingMinutesWage = temp * 3.75;
        }

        return (fullHoursWage + remainingMinutesWage);
    }

    /**
     * Calculates overtime compensation given the hours and minutes of overtime
     * @param hours
     * @param minutes
     * @return Overtime compensation to be paid according to rates
     */
    private double calculateOvertimeCompensation(int hours, int minutes) {

        double totalCompensation = 0;

        for(int i = 0; i <= hours; i++) {
            if(i <= 2) {
                totalCompensation += (HOURLY_WAGE * 1.25);
            }

            if(i <= 4 && i > 2) {
                totalCompensation += (HOURLY_WAGE * 1.50);
            }

            if(i >= 5) {
                totalCompensation += (HOURLY_WAGE * 2);
            }
        }

        return totalCompensation;
    }

    /**
     * Converts given workshift entry from CSV file into a workshift start and end time
     * so that working hours can be calculated
     * @param values a worksheet entry from CSV file
     * @return LocalDateTime[], [0] has the starting time, [1] has the ending time
     */
    private DateTime[] convertToDateTime(String[] values) {

        DateTime[] shiftTimes = new DateTime[2];

        //Concatenate two CSV values to get the work shift start and end
        String startDateAndTime = values[2] + "." + values[3];
        String endDateAndTime = values[2] + "." + values[4];

        //Initialize the formatter for the time strings
        org.joda.time.format.DateTimeFormatter format;
        format = DateTimeFormat.forPattern("dd.MM.yyyy.HH:mm");

        DateTime startTime = DateTime.parse(startDateAndTime, format);
        DateTime endTime = DateTime.parse(endDateAndTime, format);

        if(startTime.isAfter(endTime)) {
            endTime = endTime.plusDays(1);
        }

        shiftTimes[0] = startTime;
        shiftTimes[1] = endTime;

        return shiftTimes;

    }
}
