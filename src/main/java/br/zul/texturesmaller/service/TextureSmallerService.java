package br.zul.texturesmaller.service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.zul.texturesmaller.model.ImageDim;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TextureSmallerService {

    private final FfmpegService ffmpegService;
    private final FileService fileService;
    private final CompressonatorService compressonatorService;

    public void apply(String dir) {
        var destDir = getDestDir(dir);
        var ddsFiles = listDdsFiles(dir);
        var progress = 0;
        for (var ddsFile : ddsFiles) {
            var ddsName = fileService.getFileName(ddsFile);
            System.out.printf("[%d/%d]%s...", ++progress, ddsFiles.size(), ddsName);
            var imageDim = ffmpegService.getImageDim(ddsFile);
            if (isValid(imageDim)) {
                var newImageDim = divideBy2(imageDim);
                var newDdsFile = fileService.getChildFile(destDir, ddsName);
                var pngFile = newDdsFile + ".png";
                ffmpegService.resizeImage(ddsFile, pngFile, newImageDim);
                compressonatorService.compressFile(pngFile, newDdsFile);
                fileService.deleteFile(pngFile);
                System.out.println("Ok!");
            } else {
                fileService.copyFileToDir(ddsFile, destDir);
                System.out.println("Copiado!");
            }
        }
    }

    private ImageDim divideBy2(ImageDim imageDim) {
        return ImageDim
            .builder()
            .width(imageDim.getWidth() / 2)
            .height(imageDim.getHeight() / 2)
            .build();
    }

    private boolean isValid(ImageDim imageDim) {
        if (imageDim.getWidth() % 2 == 1 || imageDim.getHeight() % 2 == 1) {
            return false;
        }
        return imageDim.getWidth() > 128 && imageDim.getHeight() > 128;
    }

    private List<String> listDdsFiles(String dir) {
        return fileService
            .listFiles(dir)
            .stream()
            .filter(f -> f.toLowerCase().endsWith(".dds"))
            .collect(Collectors.toList());
    }

    private String getDestDir(String dir) {
        var dirName = fileService.getFileName(dir);
        String folder;
        if (Pattern.compile("^v\\d+$").matcher(dirName).matches()) {
            folder = incrementFolderVersion(dir);
        } else {
            folder = createNewVersionFolder(dir);
        }
        fileService.mkdir(folder);
        return folder;
    }

    private String incrementFolderVersion(String dir) {
        var parentDir = fileService.getParentDir(dir);
        var v = fileService.getFileName(dir);
        var newDirName = "v" + (Integer.valueOf(v.substring(1)) + 1);
        return fileService.getChildFile(parentDir, newDirName);
    }

    private String createNewVersionFolder(String dir) {
        var parentDir = fileService.getParentDir(dir);
        var destDir = fileService.getChildFile(parentDir, "v2");
        fileService.mkdir(destDir);
        return destDir;
    }

}
