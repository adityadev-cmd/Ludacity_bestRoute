import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Main {
    private static final double AVERAGE_SPEED_KMH = 20.0; // given

    public static void main(String[] args) {
        Location aman = new Location("Aman", 12.934533, 77.626579);
        Location r1 = new Location("R1", 12.934488, 77.619235);
        Location r2 = new Location("R2", 12.937799, 77.627367);
        Location c1 = new Location("C1", 12.935137, 77.624069);
        Location c2 = new Location("C2", 12.930658, 77.622873);

        Order order1 = new Order(r1, c1, 0.5); // 30 minutes preparation time
        Order order2 = new Order(r2, c2, 0.3); // 18 minutes preparation time

        List<Order> orders = Arrays.asList(order1, order2);

        double totalTime = findOptimalRoute(aman, orders);
        System.out.println("Total time to complete delivery: " + totalTime + " hours");
    }

    private static double findOptimalRoute(Location aman, List<Order> orders) {
        int n = orders.size() * 2; // each order has a restaurant and a consumer
        double[][] graph = new double[n + 1][n + 1];

        List<Location> locations = new ArrayList<>();
        locations.add(aman);
        for (Order order : orders) {
            locations.add(order.getRestaurant());
            locations.add(order.getConsumer());
        }

        // Fill initial distances matrix
        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                if (i != j) {
                    graph[i][j] = haversineDistance(locations.get(i), locations.get(j));
                }
            }
        }

        // Precompute shortest paths using Dijkstra for each location
        double[][] shortestPaths = new double[n + 1][n + 1];
        for (int i = 0; i < locations.size(); i++) {
            shortestPaths[i] = Dijkstra.shortestPaths(graph, i);
        }

        // Initialize dp and parent arrays
        double[][] dp = new double[1 << (n + 1)][n + 1];
        int[][] parent = new int[1 << (n + 1)][n + 1];

        for (double[] row : dp) Arrays.fill(row, Double.MAX_VALUE);
        dp[1][0] = 0;

        for (int mask = 1; mask < (1 << (n + 1)); mask += 2) {
            for (int u = 0; u <= n; u++) {
                if ((mask & (1 << u)) == 0) continue;
                for (int v = 1; v <= n; v++) {
                    if ((mask & (1 << v)) != 0) continue;
                    int nextMask = mask | (1 << v);
                    double newDist = dp[mask][u] + shortestPaths[u][v];
                    if (v % 2 == 1) newDist += orders.get((v - 1) / 2).getPreparationTime(); // add prep time for restaurant

                    if (newDist < dp[nextMask][v]) {
                        dp[nextMask][v] = newDist;
                        parent[nextMask][v] = u;
                    }
                }
            }
        }

        // Find the optimal route
        double bestTime = Double.MAX_VALUE;
        for (int u = 1; u <= n; u++) {
            bestTime = Math.min(bestTime, dp[(1 << (n + 1)) - 1][u]);
        }

        return bestTime;
    }

    private static double haversineDistance(Location loc1, Location loc2) {
        double lat1 = loc1.getLatitude();
        double lon1 = loc1.getLongitude();
        double lat2 = loc2.getLatitude();
        double lon2 = loc2.getLongitude();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (6371.0 * c) / AVERAGE_SPEED_KMH; // Earth radius in km
    }
}