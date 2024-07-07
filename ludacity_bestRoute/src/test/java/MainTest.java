import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

public class MainTest {

    private Location aman;
    private Location r1;
    private Location r2;
    private Location c1;
    private Location c2;

    private Order order1;
    private Order order2;

    @Before
    public void setUp() {
        aman = new Location("Aman", 12.934533, 77.626579);
        r1 = new Location("R1", 12.934488, 77.619235);
        r2 = new Location("R2", 12.937799, 77.627367);
        c1 = new Location("C1", 12.935137, 77.624069);
        c2 = new Location("C2", 12.930658, 77.622873);

        order1 = new Order(r1, c1, 0.5); // 30 minutes preparation time
        order2 = new Order(r2, c2, 0.3); // 18 minutes preparation time
    }

    @Test
    public void testHaversineDistance() {
        double distance = Main.haversineDistance(aman, r1);
        assertEquals(0.351, distance, 0.001); // Expected value should be calculated separately
    }

    @Test
    public void testFindOptimalRoute() {
        List<Order> orders = Arrays.asList(order1, order2);

        // Mocking the haversineDistance method
        Main mainSpy = Mockito.spy(Main.class);
        doReturn(0.351).when(mainSpy).haversineDistance(aman, r1);
        doReturn(0.528).when(mainSpy).haversineDistance(aman, r2);
        doReturn(0.351).when(mainSpy).haversineDistance(r1, c1);
        doReturn(0.351).when(mainSpy).haversineDistance(r2, c2);
        doReturn(0.894).when(mainSpy).haversineDistance(r1, r2);
        doReturn(0.774).when(mainSpy).haversineDistance(r1, c2);
        doReturn(0.528).when(mainSpy).haversineDistance(r2, c1);

        double totalTime = mainSpy.findOptimalRoute(aman, orders);
        assertEquals(1.964, totalTime, 0.001); // Expected value should be calculated separately
    }
}
