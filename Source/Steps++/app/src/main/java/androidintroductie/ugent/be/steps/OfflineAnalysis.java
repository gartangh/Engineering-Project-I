package androidintroductie.ugent.be.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OfflineAnalysis {
    private static final Pattern fileNamePattern = Pattern.compile(".+-([0-9]+)\\.csv");

    private static StepDetector[] createDetectors() {
		/*
		 * Opgave: intialiseer hier de stappendetectors.
		 */
        return new StepDetector[] {
                        // TODO
                };
    }

    public static void main(String [] args) {
        if(args.length != 1) {
            System.err.println("You must provide a file or directory with the relevant data!");
            System.err.println("Usage: OfflineAnalysis" + " <file/dir>");
            System.exit(-1);
        }

        File file = new File(args[0]);

        if(file.isDirectory()) {
            for(File subFile : file.listFiles())
                runDetectors(createDetectors(), subFile);
        }
        else {
            runDetectors(createDetectors(), file);
        }
    }

    private static void runDetectors(StepDetector[] detectors, File csvFile) {
        String fileName = csvFile.getName();

        if(fileName.endsWith(".csv") && !fileName.contains("OfflineAnalysis")) {
			/*
			 * This is somewhat inefficient in that we loop over every csvFile
			 * multiple times, once for each detector, but since we're going to
			 * have a seperate output file for each detector anyway, it's not so
			 * bad.
			 */
            for(StepDetector detector : detectors) {
                DataLogger logger = new DataLogger(new File("."),
                        csvFile + ".OfflineAnalysis." + detector.getName() + ".csv");
                logger.setLogging(true);
                detector.setDataLogger(logger);
                runDetector(detector, logger, csvFile);
            }
        }
    }

    private static void runDetector(StepDetector detector, DataLogger logger, File file) {
        Matcher fileNameMater = fileNamePattern.matcher(file.getName());

        if(fileNameMater.matches()) {
            int expectedSteps = Integer.parseInt(fileNameMater.group(1));

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                for(String line = reader.readLine(); line != null; line = reader.readLine())
                {
					/*
					 * Format is: timestamp,x,y,z\n|comment\n. Comments will
					 * fail to parse and be skipped.
					 */
                    String [] fields = line.split(",");

                    try
                    {
                        String msg = fields[0];
                        long time  = Long.parseLong(fields[1]);
                        double x   = Double.parseDouble(fields[2]);
                        double y   = Double.parseDouble(fields[3]);
                        double z   = Double.parseDouble(fields[4]);

                        logger.setLogPrefix(msg);
                        logger.logSensorData(time, x, y, z);
                        detector.addData(time, x, y, z);
                        logger.flushLine();

                    }
                    catch(Exception e)
                    {
                        System.out.println("Parse error: " + e.getMessage());
                    }
                }

                reader.close();

                printResults(file, detector, expectedSteps);
            }
            catch (Exception outerException) {
                outerException.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private static void printResults(File file, StepDetector detector, int expectedSteps) {
        System.out.println("Steps counted (" + file.getName() + ", " + detector.getName() + "): " +
                detector.getSteps() + " / " + expectedSteps);
    }
}