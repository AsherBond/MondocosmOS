

package es.igosoftware.globe.weather.aemet.data;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.GStringUtils;


public class AEMETParser {


   private static final ILogger      logger                     = GLogger.instance();


   private static final Charset      CHARSET                    = Charset.forName("ISO-8859-1");


   private static final String       MASTER_FILE_NAME           = "maestro.csv";
   private static final String       OBSERVATION_DIRECTORY_NAME = "datos_observacion";
   private static final GFileName    AEMET_ROOT_DIRECTORY       = GFileName.relative("..", "globe-weather-aemet", "data",
                                                                         "aemet.es", "ftpdatos.aemet.es");


   private static final List<String> NULLS                      = Arrays.asList("///.///", "//////", "/////", "!!!!.!", "!!!.!");


   private static boolean isDataDirectory(final File pathname) {
      return pathname.getName().contains("diezminutales");
   }


   public static AEMETData loadData() throws IOException {

      final long start = System.currentTimeMillis();

      final List<AEMETStation> stations = filterStations(loadStations(AEMET_ROOT_DIRECTORY));
      final AEMETData data = new AEMETData(stations);

      processObservationDirectory(AEMET_ROOT_DIRECTORY, data);

      final long ellapsed = System.currentTimeMillis() - start;
      logger.logInfo("Completed in " + GStringUtils.getTimeMessage(ellapsed));

      return data;
   }


   private static List<AEMETStation> filterStations(final List<AEMETStation> stations) {
      final GSimplePolygon2D spain = PeninsularSpainShape.create();

      return GCollections.select(stations, new GPredicate<AEMETStation>() {
         @Override
         public boolean evaluate(final AEMETStation station) {
            final IVector2 position = station.getPosition().asVector2();

            if (spain.contains(position)) {
               return true;
            }

            final double distance = spain.distance(position);
            if (distance <= 0.003) {
               return true;
            }

            return false;
         }
      });
   }


   private static List<AEMETStation> loadStations(final GFileName rootDirectory) throws IOException {
      final GFileName fileName = GFileName.fromParentAndParts(rootDirectory, OBSERVATION_DIRECTORY_NAME, MASTER_FILE_NAME);

      final File file = fileName.asFile();

      BufferedReader reader = null;
      try {
         if (file.getName().contains(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), CHARSET));
         }
         else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET));
         }

         final List<AEMETStation> stations = new ArrayList<AEMETStation>();

         boolean firstRow = true;
         String line;
         while ((line = reader.readLine()) != null) {
            final String[] row = removeQuotes(line.split(";"));

            if (firstRow) {
               firstRow = false;
               // ignore the titles line
               continue;
            }

            final AEMETStation station = parseStation(row);
            if (station != null) {
               stations.add(station);
            }
         }

         return stations;
      }
      catch (final IOException e) {
         throw new IOException("error loading: " + file, e);
      }
      finally {
         GIOUtils.gentlyClose(reader);
      }


   }


   private static Date parseDateTime(final String string) {
      if (string == null) {
         return null;
      }

      return new Date(Long.parseLong(string));
   }


   private static GAngle parseDMSAngle(final String string) {
      final String[] split = string.split(" ");

      final double degrees = Double.parseDouble(split[0]);
      final double minutes = Double.parseDouble(split[1]);

      final String secondsS = split[2].trim().toLowerCase();

      final double seconds;
      final String signS;
      if (secondsS.endsWith("n") || secondsS.endsWith("s") || secondsS.endsWith("e") || secondsS.endsWith("w")) {
         seconds = Double.parseDouble(secondsS.substring(0, secondsS.length() - 1));
         signS = secondsS.substring(secondsS.length() - 1);
      }
      else {
         seconds = Double.parseDouble(secondsS);
         signS = split[3].trim().toLowerCase();
      }

      final int sign = (signS.equals("s") || signS.equals("w")) ? -1 : 1;

      return GAngle.fromDegrees((degrees + minutes / 60d + seconds / 3600d) * sign);
   }


   private static GPair<String, String> parseNameAndValue(final String string) {
      if (string == null) {
         return null;
      }

      final String workingCopy = string.trim();

      final int equalsPosition = workingCopy.indexOf('=');
      final String name = workingCopy.substring(0, equalsPosition);

      String value = workingCopy.substring(equalsPosition + 1);
      if (NULLS.contains(value)) {
         value = null;
      }

      return new GPair<String, String>(name, value);
   }


   private static AEMETStation parseStation(final String[] row) {
      final String INDCLIM = row[0];
      final String INDSINOP = row[1];
      final String name = row[2];
      //      final String PROVINCIA = row[3];
      final GAngle latitude = parseDMSAngle(row[4]);
      final GAngle longitude = parseDMSAngle(row[5]);
      final double altitude = Double.parseDouble(row[6]);

      return new AEMETStation(INDCLIM, INDSINOP, name, latitude, longitude, altitude);
   }


   private static void processDirectory(final File directory,
                                        final AEMETData data) throws IOException {
      final File[] files = directory.listFiles(new FileFilter() {
         @Override
         public boolean accept(final File pathname) {
            return pathname.getName().contains(".csv");
         }
      });
      Arrays.sort(files);

      final GProgress progress = new GProgress(files.length) {
         @Override
         public void informProgress(final long stepsDone,
                                    final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            logger.logInfo("Reading from " + directory.getName() + " "
                           + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
         }
      };

      for (final File file : files) {
         processFile(file, data);
         progress.stepDone();
      }
   }


   private static void processFile(final File file,
                                   final AEMETData data) throws IOException {
      BufferedReader reader = null;
      try {
         if (file.getName().contains(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), CHARSET));
         }
         else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET));
         }

         String line;
         while ((line = reader.readLine()) != null) {
            final String[] row = removeQuotes(line.split(","));
            processRow(row, data);
         }
      }
      catch (final IOException e) {
         throw new IOException("error loading: " + file, e);
      }
      finally {
         GIOUtils.gentlyClose(reader);
      }
   }


   private static void processObservationDirectory(final GFileName rootDirectory,
                                                   final AEMETData data) throws IOException {
      final GFileName observationDir = GFileName.fromParentAndParts(rootDirectory, OBSERVATION_DIRECTORY_NAME,
               "observaciones_diezminutales");

      if (!observationDir.exists()) {
         throw new IOException("Can't find directory: " + observationDir.buildPath());
      }

      if (!observationDir.isDirectory()) {
         throw new IOException(observationDir.buildPath() + " is not a directory");
      }

      final File[] directories = observationDir.asFile().listFiles(new FileFilter() {
         @Override
         public boolean accept(final File pathname) {
            return pathname.isDirectory() && isDataDirectory(pathname);
         }
      });
      Arrays.sort(directories);


      //      for (final File directory : directories) {
      //         processDirectory(directory, data);
      //      }
      final int TODO_process_all_directories;
      processDirectory(directories[directories.length - 1], data);
      //      processDirectory(directories[0], data);
   }


   @SuppressWarnings("unchecked")
   private static void processRow(final String[] row,
                                  final AEMETData data) {
      if (row == null) {
         return;
      }

      final String stationId = row[0];

      final AEMETStation station = data.getStationById(stationId);
      if (station == null) {
         //         throw new RuntimeException("Can't find the station " + stationId);
         //         System.out.println("Ignoring row for station: " + stationId);
         return;
      }

      final Date observationStart = parseDateTime(row[4]);
      final Date observationEnd = parseDateTime(row[5]);
      //      final GRange<Date> observationLapse = new GRange<Date>(observationStart, observationEnd);
      final Lapse observationLapse = new Lapse(observationStart, observationEnd);

      for (int i = 7; i < row.length; i += 2) {
         final GPair<String, String> variablePair = parseNameAndValue(row[i]);
         final GPair<String, String> qualityPair = parseNameAndValue(row[i + 1]);

         final String variableName = variablePair._first;

         // GAssert.isTrue(qualityPair._first.equals("Q" + variableName), "");

         final String variableValue = variablePair._second;
         final String variableQuality = qualityPair._second;

         final AEMETVariable<?> variable = data.getVariable(variableName);
         if (variable == null) {
            // System.out.println("Ignoring variable: " + variableName);
         }
         else {
            try {
               if (variable.getType() == Double.class) {
                  station.addObservation((AEMETVariable<Double>) variable, observationLapse, variableValue, variableQuality);
               }
               else if (variable.getType() == String.class) {
                  station.addObservation((AEMETVariable<String>) variable, observationLapse, variableValue, variableQuality);
               }
               else if (variable.getType() == GAngle.class) {
                  station.addObservation((AEMETVariable<GAngle>) variable, observationLapse, variableValue, variableQuality);
               }
               else {
                  System.out.println("Ignoring variable " + variableName + ", type=" + variable.getType());
               }
            }
            catch (final Exception e) {
               System.out.println("Exception while parsing: " + variable + ", variableValue=" + variableValue);
               // throw new RuntimeException("Exception while parsing: " + variable + ", variableValue=" + variableValue, e);
            }
         }
      }


   }


   private static String removeQuotes(final String string) {
      if (string == null) {
         return null;
      }

      final boolean isQuoted = string.startsWith("\"") && string.endsWith("\"");
      return isQuoted ? string.substring(1, string.length() - 1) : string;
   }


   private static String[] removeQuotes(final String[] strings) {
      for (int i = 0; i < strings.length; i++) {
         strings[i] = removeQuotes(strings[i]);
      }

      return strings;
   }


}
