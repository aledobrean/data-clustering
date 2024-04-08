package ada.app.service;

import ada.app.model.Box;
import ada.app.model.Point;
import ada.app.service.exception.BoxServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BoxServiceTest {
    static List<Box> input;
    @InjectMocks
    private BoxService boxService;

    @BeforeAll
    static void setup() {
        input = new ArrayList<>();
        input.add(new Box(new Point(1, 2), new Point(2, 3)));
        input.add(new Box(new Point(3, 5), new Point(5, 6)));
        input.add(new Box(new Point(10, 16), new Point(11, 35)));
    }

    @Test
    void groupBoxes_largeDistance() throws Exception {
        List<List<Box>> output = boxService.groupBoxes(input, 100);

        assertEquals(1, output.size());
    }

    @Test
    void groupBoxes_smallDistance() throws Exception {
        List<List<Box>> output = boxService.groupBoxes(input, 2);

        assertEquals(3, output.size());
    }

    @Test
    void groupBoxes_emptyList() throws Exception {
        List<List<Box>> output = boxService.groupBoxes(List.of(), 50);

        assertEquals(0, output.size());
    }

    @Test
    void groupBoxes_negativeThreshold() {
        assertThrows(BoxServiceException.class, () -> boxService.groupBoxes(input, -50));
    }
}
