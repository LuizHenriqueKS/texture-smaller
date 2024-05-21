package br.zul.texturesmaller.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import br.zul.texturesmaller.exception.UnexpectedException;
import br.zul.texturesmaller.model.TerminalResponse;

@Service
public class TerminalService {

    public TerminalResponse run(String... commands) {
        try {
            var processBuilder = new ProcessBuilder(commands);
            processBuilder.redirectOutput();
            processBuilder.redirectInput();
            processBuilder.redirectError();

            var process = processBuilder.start();
            boolean[] outputFinished = { false };
            boolean[] errorFinished = { false };

            var outputBuilder = new StringBuilder();
            var errorBuilder = new StringBuilder();
            redirectOutput(process.getInputStream(), outputBuilder, outputFinished);
            redirectOutput(process.getErrorStream(), errorBuilder, errorFinished);
        
            while (!outputFinished[0] || !errorFinished[0]) {
                Thread.sleep(1000);
            }

            return TerminalResponse
                .builder()
                .output(outputBuilder.toString())
                .error(errorBuilder.toString())
                .build();
        } catch (IOException | InterruptedException ex) {
            throw new UnexpectedException(ex);
        }
    }

    private void redirectOutput(InputStream inputStream, StringBuilder outputBuilder, boolean[] outputFinished) {
        new Thread(() -> {
            try (var isr = new InputStreamReader(inputStream)) {
                var buffer = new char[4096];
                int len;
                while ((len = isr.read(buffer)) != -1) {
                    outputBuilder.append(buffer, 0, len);
                }
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            } finally {
                outputFinished[0] = true;
            }
        }).start();
    }

}
