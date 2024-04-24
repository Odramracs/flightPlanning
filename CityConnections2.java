// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// public class CityConnections {

//     public static void main(String[] args) {
//         // Initialize an empty list to store city connections
//         List<City> cities = new ArrayList<>();

//         // Read the number of connections from the file (assuming it's the first line)
//         String filePath = "sample.txt"; // Replace with the path to your file
//         int numConnections = 0;
//         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//             String firstLine = br.readLine();
//             numConnections = Integer.parseInt(firstLine);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         // Read the information from the file
//         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//             // Skip the first line
//             br.readLine();

//             String line;
//             // Skip the first line when reading connections
//             for (int i = 0; i < numConnections; i++) {
//                 line = br.readLine();
//                 String[] parts = line.split("\\|");
//                 String city1 = parts[0];
//                 String city2 = parts[1];
//                 int distance = Integer.parseInt(parts[2]);
//                 int time = Integer.parseInt(parts[3]);

//                 // Add city2 to the neighbors of city1 and vice versa
//                 addConnection(cities, city1, city2, distance, time);
//                 addConnection(cities, city2, city1, distance, time);
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         // Print the adjacency list
//         for (City city : cities) {
//             System.out.print(city.name + ": ");
//             for (Connection connection : city.connections) {
//                 System.out.print("(" + connection.city + ", " + connection.distance + ", " + connection.time + ") ");
//             }
//             System.out.println();
//         }
//     }

//     static void addConnection(List<City> cities, String cityName, String neighborName, int distance, int time) {
//         // Check if the city already exists in the list
//         for (City city : cities) {
//             if (city.name.equals(cityName)) {
//                 // Add the connection to the existing city
//                 city.connections.add(new Connection(neighborName, distance, time));
//                 return;
//             }
//         }

//         // If the city does not exist, create a new city and add the connection
//         City city = new City(cityName);
//         city.connections.add(new Connection(neighborName, distance, time));
//         cities.add(city);
//     }

//     static class City {
//         String name;
//         List<Connection> connections;

//         City(String name) {
//             this.name = name;
//             this.connections = new ArrayList<>();
//         }
//     }

//     static class Connection {
//         String city;
//         int distance;
//         int time;

//         Connection(String city, int distance, int time) {
//             this.city = city;
//             this.distance = distance;
//             this.time = time;
//         }
//     }
// }
