import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Flights {
    static class FlightRequest {
        String city1;
        String city2;
        String mode;

        public FlightRequest(String city1, String city2, String mode) {
            this.city1 = city1;
            this.city2 = city2;
            this.mode = mode;
        }
    }
    static List<List<Connection>> findBestPaths(String source, String destination, List<City> cities, Comparator<List<Connection>> comparator) {
        List<List<Connection>> bestPaths = new ArrayList<>();
        List<Connection> currentPath = new ArrayList<>();
        backtrack(source, destination, cities, currentPath, bestPaths, comparator);
        return bestPaths;
    }

    static void backtrack(String current, String destination, List<City> cities, List<Connection> currentPath, List<List<Connection>> bestPaths, Comparator<List<Connection>> comparator) {
        if (current.equals(destination)) {
            if (bestPaths.size() < 3 || comparator.compare(currentPath, bestPaths.get(2)) < 0) {
                bestPaths.add(new ArrayList<>(currentPath));
                sortBestPaths(bestPaths, comparator);
                if (bestPaths.size() > 3) {
                    bestPaths.remove(3);
                }
            }
            return;
        }

        for (Connection connection : getConnections(current, cities)) {
            if (!containsCity(currentPath, connection.city)) {
                currentPath.add(connection);
                backtrack(connection.city, destination, cities, currentPath, bestPaths, comparator);
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    static List<Connection> getConnections(String cityName, List<City> cities) {
        for (City city : cities) {
            if (city.name.equals(cityName)) {
                return city.connections;
            }
        }
        return new ArrayList<>();
    }

    static boolean containsCity(List<Connection> path, String cityName) {
        for (Connection connection : path) {
            if (connection.city.equals(cityName)) {
                return true;
            }
        }
        return false;
    }

    static int getTotalCost(List<Connection> path) {
        int totalcost = 0;
        for (Connection connection : path) {
            totalcost += connection.cost;
        }
        return totalcost;
    }

    static int getTotalTime(List<Connection> path) {
        int totalTime = 0;
        for (Connection connection : path) {
            totalTime += connection.time;
        }
        return totalTime;
    }

    static void sortBestPaths(List<List<Connection>> bestPaths, Comparator<List<Connection>> comparator) {
        Collections.sort(bestPaths, comparator);
    }

    static void addConnection(List<City> cities, String cityName, String neighborName, int cost, int time) {
        for (City city : cities) {
            if (city.name.equals(cityName)) {
                city.connections.add(new Connection(neighborName, cost, time));
                return;
            }
        }
        City city = new City(cityName);
        city.connections.add(new Connection(neighborName, cost, time));
        cities.add(city);
    }

    static void writePathsToFile(BufferedWriter writer, List<List<Connection>> paths, int flightNumber, String source, String destination) throws IOException {
        for (int i = 0; i < Math.min(2, paths.size()); i++) {
            writer.write("Path " + (i + 1) + ": ");
            List<Connection> path = paths.get(i);
            int totalTime = getTotalTime(path);
            int totalcost = getTotalCost(path);
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(source);
            for (Connection connection : path) {
                if (!connection.city.equals(destination)) {
                    pathBuilder.append(" -> ").append(connection.city);
                }
            }
            pathBuilder.append(" -> ").append(destination);
            writer.write(pathBuilder.toString() + ". Time: " + totalTime + " Cost: " + totalcost);
            writer.newLine();
        }
    }
    static class City {
        String name;
        List<Connection> connections;

        City(String name) {
            this.name = name;
            this.connections = new ArrayList<>();
        }
    }
    static class Connection {
        String city;
        int cost;
        int time;

        Connection(String city, int cost, int time) {
            this.city = city;
            this.cost = cost;
            this.time = time;
        }
    }

    public static void main(String[] args) {
        // Initialize an empty list to store city connections
        List<City> cities = new ArrayList<>();

        // Read the number of connections from the file (assuming it's the first line)
        String filePath = "sample.txt";
        int numConnections = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String firstLine = br.readLine();
            numConnections = Integer.parseInt(firstLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read the information from the file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip the first line
            br.readLine();
            String line;
            // Skip the first line when reading connections
            for (int i = 0; i < numConnections; i++) {
                line = br.readLine();
                String[] parts = line.split("\\|");
                String city1 = parts[0];
                String city2 = parts[1];
                int cost = Integer.parseInt(parts[2]);
                int time = Integer.parseInt(parts[3]);

                // Add city2 to the neighbors of city1 and vice versa
                addConnection(cities, city1, city2, cost, time);
                addConnection(cities, city2, city1, cost, time);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read the requested flights from another file
        String flightsFilePath = "requested.txt";
        List<FlightRequest> requestedFlights = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(flightsFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String city1 = parts[0];
                    String city2 = parts[1];
                    String mode = parts[2];
                    requestedFlights.add(new FlightRequest(city1, city2, mode));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Output file path
        String outputFilePath = "output.txt";

        // Process each requested flight and write the output to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (int i = 0; i < requestedFlights.size(); i++) {
                FlightRequest flightRequest = requestedFlights.get(i);
                String source = flightRequest.city1;
                String destination = flightRequest.city2;
                String mode = flightRequest.mode;

                List<List<Connection>> bestPaths;

                if (mode.equals("C")) {
                    writer.write("Flight " + (i + 1) + ": " + source + ", " + destination + " (Cost)");
                    writer.newLine();
                    bestPaths = findBestPaths(source, destination, cities, Comparator.comparingInt(Flights::getTotalCost));
                } else if (mode.equals("T")) {
                    writer.write("Flight " + (i + 1) + ": " + source + ", " + destination + " (Time)");
                    writer.newLine();
                    bestPaths = findBestPaths(source, destination, cities, Comparator.comparingInt(Flights::getTotalTime));
                } else {
                    writer.write("Invalid mode: " + mode);
                    writer.newLine();
                    continue;
                }

                writePathsToFile(writer, bestPaths, i + 1, source, destination);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    