package br.zul.texturesmaller.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import br.zul.texturesmaller.model.ImageDim;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FfmpegService {

    private final TerminalService terminalService;

    public ImageDim getImageDim(String ddsFile) {
        var response = terminalService.run("ffprobe", ddsFile);
        var lines = Arrays.asList(response.getError().split("\n"));
        var dimLine = lines
            .stream()
            .filter(l -> l.contains("Stream #0"))
            .findFirst()
            .get();
        var dimStr = dimLine.split(",")[2].trim().split("x");
        var width = Integer.valueOf(dimStr[0]);
        var height = Integer.valueOf(dimStr[1]);
        return new ImageDim(width, height);
    }

    public void resizeImage(String ddsFile, String pngFile, ImageDim newImageDim) {
        var scale = "scale=" + newImageDim.getWidth() + "x" + newImageDim.getHeight();
        terminalService.run("ffmpeg", "-y", "-i", ddsFile, "-vf", scale, pngFile);
    }

}
