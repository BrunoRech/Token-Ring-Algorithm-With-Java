package server;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jeferson Penz
 */
public class ReadWriteControlTest {

    /**
     * Test of run method, of class ReadWriteControl.
     */
    @Test
    public void testRun() {
        File file = new File("Teste.txt");
        try {
            file.createNewFile();
            ReadWriteControl control = new ReadWriteControl(file.getAbsolutePath());
            (new Thread(control)).start();
            assertEquals(control.getAllData(), "");
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
                control.addData("Teste de escrita no arquivo: " + i + "\n");
                for (int j = 0; j < 100; j++) {
                    control.addData("Teste");
                }
                control.addData("\n");
            }
            assertNotEquals("", control.getAllData());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
