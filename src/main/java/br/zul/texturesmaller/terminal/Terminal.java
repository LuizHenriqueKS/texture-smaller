package br.zul.texturesmaller.terminal;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.zul.texturesmaller.service.TextureSmallerService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Terminal implements CommandLineRunner {

    private final TextureSmallerService textureSmallerService ;

    @Override
    public void run(String... args) throws Exception {
        try (var scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Entre com a pasta com as imagens de texture: ");
                var input = scanner.nextLine();
                textureSmallerService.apply(input); 
                System.out.println("Pronto!");
            }
        }
    }
    
}
