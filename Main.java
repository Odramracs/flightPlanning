import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

class Flight {
    String destination;
    int cost;
    int time;

    public Flight(String destination, int cost, int time) {
        this.destination = destination;
        this.cost = cost;
        this.time = time;
    }
}

public class Main {
    static List<List<Flight>> adjacencyList;

    public static void main(String[] args) {
        // if (args.length < 3) {
        //     System.out.println("Usage: java Main <flight_data_file> <requested_flights_file> <output_file>");
        //     return;
        // }

        String inputFlightDataFile = "sample.txt";
        String inputRequestedFlightsFile = "requested.txt";
        String outputFileName = "out.txt";

        adjacencyList = new ArrayList<>();

        // Read flight data from input file
        readFlightData(inputFlightDataFile);

        // Read requested flight plans from input file
        List<String[]> requestedFlights = readRequestedFlights(inputRequestedFlightsFile);

        // Process requested flight plans
        processRequestedFlights(requestedFlights, outputFileName);
    }

    private static void readFlightData(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            int numFlights = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            while (numFlights-- > 0) {
                String[] flightData = scanner.nextLine().split("\\|");
                String origin = flightData[0];
                String destination = flightData[1];
                int cost = Integer.parseInt(flightData[2]);
                int time = Integer.parseInt(flightData[3]);
                addFlight(origin, destination, cost, time);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static void addFlight(String origin, String destination, int cost, int time) {
        int cityIndex = getCityIndex(origin);
        if (cityIndex == -1) {
            cityIndex = adjacencyList.size();
            adjacencyList.add(new ArrayList<>());
        }
        adjacencyList.get(cityIndex).add(new Flight(destination, cost, time));
    }

    private static int getCityIndex(String cityName) {
        for (int i = 0; i < adjacencyList.size(); i++) {
            if (!adjacencyList.get(i).isEmpty() && adjacencyList.get(i).get(0).destination.equals(cityName)) {
                return i;
            }
        }
        return -1;
    }

    private static List<String[]> readRequestedFlights(String filename) {
        List<String[]> requestedFlights = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            int numRequests = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            while (numRequests-- > 0) {
                requestedFlights.add(scanner.nextLine().split("\\|"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return requestedFlights;
    }

    private static void processRequestedFlights(List<String[]> requestedFlights, String outputFileName) {
        try (PrintWriter writer = new PrintWriter(outputFileName)) {
            for (String[] flightPlan : requestedFlights) {
                String origin = flightPlan[0];
                String destination = flightPlan[1];
                char sortingPreference = flightPlan[2].charAt(0);
                List<List<Flight>> flightPaths = findFlightPaths(origin, destination);
                if (flightPaths.isEmpty()) {
                    writer.println("No viable flight plan from " + origin + " to " + destination + ".");
                } else {
                    for (List<Flight> path : flightPaths) {
                        Collections.sort(path, Comparator.comparingInt(f -> (sortingPreference == 'T') ? f.time : f.cost));
                        writer.println("Flight: " + origin + ", " + destination + " (" + sortingPreference + ")");
                        for (int i = 0; i < Math.min(3, path.size()); i++) {
                            Flight flight = path.get(i);
                            writer.println("Path " + (i + 1) + ": " + origin + " -> " + flight.destination +
                                    ". Time: " + flight.time + " Cost: " + flight.cost);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static List<List<Flight>> findFlightPaths(String origin, String destination) {
        List<List<Flight>> paths = new ArrayList<>();
        Set<Integer> visited = new HashSet<>(); // Use set of city indices instead of city names
        List<Flight> currentPath = new ArrayList<>();
        int originIndex = getCityIndex(origin);
        int destinationIndex = getCityIndex(destination);
        if (originIndex != -1 && destinationIndex != -1) {
            backtracking(originIndex, destinationIndex, visited, currentPath, paths);
        }
        return paths;
    }

    private static void backtracking(int currentCityIndex, int destinationIndex, Set<Integer> visited,
                                 List<Flight> currentPath, List<List<Flight>> paths) {
    visited.add(currentCityIndex); // Mark current city index as visited
    if (currentCityIndex == destinationIndex) {
        paths.add(new ArrayList<>(currentPath)); // Add current path to the result paths
    } else {
        for (Flight flight : adjacencyList.get(currentCityIndex)) {
            int nextCityIndex = getCityIndex(flight.destination);
            if (!visited.contains(nextCityIndex)) {
                currentPath.add(flight); // Add current flight to the path
                backtracking(nextCityIndex, destinationIndex, visited, currentPath, paths);
                currentPath.remove(currentPath.size() - 1); // Backtrack: remove the current flight from the path
            }
        }
    }
    visited.remove(currentCityIndex); // Unmark current city index as visited
}
}