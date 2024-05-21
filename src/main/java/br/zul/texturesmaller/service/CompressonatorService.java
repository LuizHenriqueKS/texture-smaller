package br.zul.texturesmaller.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompressonatorService {

    private final TerminalService terminalService;
    private final FileService fileService;

    public void compressFile(String inputFile, String outputFile) {
        fileService.deleteFile(outputFile);
        terminalService.run("compressonatorcli", "-log", "-fd", "dxt5", inputFile, outputFile);
    }

}
