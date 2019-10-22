package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe responsável por realizar o controle de ler e escrever no arquivo os
 * dados.
 *
 * @author Bruno Galeazzi Rech, Jeferson Penz
 */
public class ReadWriteControl implements Runnable {

    private File targetFile;
    private BufferedWriter FileWriter;
    private StringBuilder cache;
    private long timestamp;
    private boolean running;

    /**
     * Cria um novo Writer para trabalhar com o arquivo no local informado.
     *
     * @param caminho
     */
    public ReadWriteControl(String caminho) throws IOException {
        this.targetFile = new File(caminho);
        if (!this.targetFile.exists()) {
            this.targetFile.createNewFile();
        }
        this.cache = new StringBuilder();
        this.FileWriter = new BufferedWriter(new FileWriter(this.targetFile, true));
    }

    @Override
    public void run() {
        this.running = true;
        this.readData();
        this.timestamp = this.targetFile.lastModified();
        while (running) {
            try {
                if (this.timestamp != this.targetFile.lastModified()) {
                    this.readData();
                    this.timestamp = this.targetFile.lastModified();
                }
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
        sData = this.cache.toString();
        return sData;
    }

    /**
     * Adiciona dados ao arquivo.
     *
     * @param data
     * @throws IOException
     */
    public void addData(String data) throws IOException {
        this.FileWriter.append(data);
        this.FileWriter.flush();
        this.cache.append(data);
        this.timestamp = this.targetFile.lastModified();
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
