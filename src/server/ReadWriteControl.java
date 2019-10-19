package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável por realizar o controle de ler e escrever no arquivo os dados.
 *
 * @author Jeferson Penz
 */
public class ReadWriteControl implements Runnable {

    private File targetFile;
    private BufferedWriter FileWriter;
    private StringBuilder cache;
    private long timestamp;
    private boolean running;
    private Semaphore semaphore;

    /**
     * Cria um novo Writer para trabalhar com o arquivo no local informado.
     *
     * @param caminho
     */
    public ReadWriteControl(String caminho) {
        this.targetFile = new File(caminho);
        if(!this.targetFile.exists()){
            try {
                this.targetFile.createNewFile();
            } catch (IOException ex) {}
        }
        this.semaphore = new Semaphore(1);
        this.cache = new StringBuilder();
        try {
            this.FileWriter = new BufferedWriter(new FileWriter(this.targetFile, true));
        } catch (IOException ex) {
        }
    }

    @Override
    public void run() {
        this.running = true;
        this.readData();
        this.timestamp = this.targetFile.lastModified();
        while (running) {
            try {
                semaphore.acquire();
                if (this.timestamp != this.targetFile.lastModified()) {
                    this.readData();
                    this.timestamp = this.targetFile.lastModified();
                }
                semaphore.release();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                this.running = false;
            }
        }
    }

    /**
     * Busca os dados do arquivo.
     *
     * @return
     */
    public String getAllData() {
        String sData = "";
        try {
            semaphore.acquire();
            sData = this.cache.toString();
            semaphore.release();
        } catch (InterruptedException ex) {
        }
        return sData;
    }

    /**
     * Adiciona dados ao arquivo.
     *
     * @param data
     * @throws IOException
     */
    public void addData(String data) throws IOException {
        try {
            semaphore.acquire();
            this.FileWriter.append(data);
            this.FileWriter.flush();
            this.cache.append(data);
            this.timestamp = this.targetFile.lastModified();
            semaphore.release();
        } catch (InterruptedException ex) {
        }
    }

    /**
     * Lê os dados do arquivo.
     */
    private void readData() {
        this.cache = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(this.targetFile));
            String line = null;
            do {
                if (line != null) {
                    this.cache.append(line).append("\n");
                }
                try {
                    line = reader.readLine();
                } catch (IOException ex) {
                    line = null;
                }
            } while (line != null);
        } catch (FileNotFoundException ex) {
        }
    }

}
