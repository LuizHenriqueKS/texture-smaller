package br.zul.texturesmaller.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.zul.texturesmaller.exception.UnexpectedException;

@Service
public class FileService {

    public void copyFileToDir(String file, String dir) {
        var newFile = new File(dir, getFileName(file));
        try (var out = new FileOutputStream(newFile)) {
            Files.copy(Path.of(file), out);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public List<String> listFiles(String dir) {
        return Arrays.asList(new File(dir).listFiles())
            .stream()
            .map(f -> f.getAbsolutePath())
            .collect(Collectors.toList());
    }

    public String getFileName(String file) {
        return new File(file).getName();
    }

    public String getParentDir(String dir) {
        return new File(dir).getAbsoluteFile().getParentFile().getAbsolutePath();
    }

    public String getChildFile(String dir, String filename) {
        return new File(dir, filename).getAbsolutePath();
    }

    public void mkdir(String dir) {
        new File(dir).mkdir();
    }

    public void deleteFile(String file) {
        new File(file).delete();
    }

}
