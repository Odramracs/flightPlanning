import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CityConnections {

    public static void main(String[] args) {
        // Initialize an empty list to store city connections
        List<City> cities = new ArrayList<>();

        // Read the number of connections from the file (assuming it's the first line)
        String filePath = "sample.txt"; // Replace with the path to your file
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
                int distance = Integer.parseInt(parts[2]);
                int time = Integer.parseInt(parts[3]);

                // Add city2 to the neighbors of city1 and vice versa
                addConnection(cities, city1, city2, distance, time);
                addConnection(cities, city2, city1, distance, time);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Requested flight
        String source = "Dallas";
        String destination = "Houston";

    // Find the three best paths based on distance
    // Find the three best paths based on distance
    System.out.println("Best Paths based on Distance:");
    List<List<Connection>> bestPathsDistance = findBestPaths(source, destination, cities, Comparator.comparingInt(CityConnections::getTotalDistance));
    printPaths(bestPathsDistance, 1, source, destination);

        // Find the three best paths based on time
    System.out.println("\nBest Paths based on Time:");
    List<List<Connection>> bestPathsTime = findBestPaths(source, destination, cities, Comparator.comparingInt(CityConnections::getTotalTime));
    printPaths(bestPathsTime, 2, source, destination);

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

    static int getTotalDistance(List<Connection> path) {
        int totalDistance = 0;
        for (Connection connection : path) {
            totalDistance += connection.distance;
        }
        return totalDistance;
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

    static void addConnection(List<City> cities, String cityName, String neighborName, int distance, int time) {
        for (City city : cities) {
            if (city.name.equals(cityName)) {
                city.connections.add(new Connection(neighborName, distance, time));
                return;
            }
        }
        City city = new City(cityName);
        city.connections.add(new Connection(neighborName, distance, time));
        cities.add(city);
    }

    static void printPaths(List<List<Connection>> paths, int flightNumber, String source, String destination) {
        System.out.println("Flight " + flightNumber + ": " + source + ", " + destination);
        for (int i = 0; i < Math.min(2, paths.size()); i++) {
            System.out.println("Path " + (i + 1) + ":");
            List<Connection> path = paths.get(i);
            int totalTime = getTotalTime(path);
            int totalDistance = getTotalDistance(path);
            System.out.println(source + " -> " + path.get(0).city + " -> " + path.get(path.size() - 1).city + ". Time: " + totalTime + " Distance: " + totalDistance);
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
        int distance;
        int time;

        Connection(String city, int distance, int time) {
            this.city = city;
            this.distance = distance;
            this.time = time;
        }
    }
}
