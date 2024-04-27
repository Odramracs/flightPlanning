import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// flight class to read in the sample cities and use them in the adjacency list 
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

    // class for each city being read from the file 
    static class City {
        String name;
        List<Connection> connections;

        City(String name) {
            this.name = name;
            this.connections = new ArrayList<>();
        }
    }
    // class for the edges between the cities and has each of the cost and time 
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

    // function to find the 3 best paths using the backtracking function
    static List<List<Connection>> findBestPaths(String source, String destination, List<City> cities, Comparator<List<Connection>> comparator) {
        List<List<Connection>> bestPaths = new ArrayList<>();
        List<Connection> currentPath = new ArrayList<>();
        backtrack(source, destination, cities, currentPath, bestPaths, comparator);
        return bestPaths;
    }

    // backtracking algorithm using DFS to find the best path, checks to see if the current city is the destination, if it is then add it to the bestpaths array if it is better than the current 3 best paths. 
    // if its not the destination then go to the next connection from the current city 
    // for each connetion if the connected city is not already in the current path then add the edge to the current path and call backtrack recursivly 
    // after exploring all the connection to the current city remove the last connection from the current path to backtrack and explore the other paths 
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

    // function to get the connection between cities 
    static List<Connection> getConnections(String cityName, List<City> cities) {
        for (City city : cities) {
            if (city.name.equals(cityName)) {
                return city.connections;
            }
        }
        return new ArrayList<>();
    }

    // function to check if a given city is inside of a flight path 
    static boolean containsCity(List<Connection> path, String cityName) {
        for (Connection connection : path) {
            if (connection.city.equals(cityName)) {
                return true;
            }
        }
        return false;
    }

    // function to get the total cost of a flight path
    static int getTotalCost(List<Connection> path) {
        int totalcost = 0;
        for (Connection connection : path) {
            totalcost += connection.cost;
        }
        return totalcost;
    }

    // function to get the total time of a flight path
    static int getTotalTime(List<Connection> path) {
        int totalTime = 0;
        for (Connection connection : path) {
            totalTime += connection.time;
        }
        return totalTime;
    }

    //function to find the order between the given paths 
    static void sortBestPaths(List<List<Connection>> bestPaths, Comparator<List<Connection>> comparator) {
        Collections.sort(bestPaths, comparator);
    }

    // adding edges between the cities 
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

    // function to write the output to file instead of the console 
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

    public static void main(String[] args) {
        //empty list to store city connections
        List<City> cities = new ArrayList<>();
        String filePath = "sample.txt";
        int numConnections = 0;
        //getting the first line 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String firstLine = br.readLine();
            numConnections = Integer.parseInt(firstLine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //reading the file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            //skip the first line
            for (int i = 0; i < numConnections; i++) {
                line = br.readLine();
                String[] parts = line.split("\\|");
                String city1 = parts[0];
                String city2 = parts[1];
                int cost = Integer.parseInt(parts[2]);
                int time = Integer.parseInt(parts[3]);
                //adding edges 
                addConnection(cities, city1, city2, cost, time);
                addConnection(cities, city2, city1, cost, time);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        //out
        String outputFilePath = "output.txt";
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

    