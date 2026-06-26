package com.foodbook.util;

import com.foodbook.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class ImagemUtil {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    @Value("${application.upload.dir:uploads}")
    private String uploadDir;

    public String salvar(MultipartFile arquivo) {
        validar(arquivo);

        try {
            Path diretorio = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(diretorio);

            String extensao = obterExtensao(arquivo.getOriginalFilename());
            String nomeArquivo = UUID.randomUUID() + extensao;
            Path destino = diretorio.resolve(nomeArquivo);

            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            log.info("Imagem salva: {}", nomeArquivo);
            return nomeArquivo;

        } catch (IOException ex) {
            log.error("Erro ao salvar imagem", ex);
            throw BusinessException.badRequest("Erro ao processar imagem");
        }
    }

    public void excluir(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) return;
        try {
            Path arquivo = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(nomeArquivo);
            Files.deleteIfExists(arquivo);
        } catch (IOException ex) {
            log.warn("Não foi possível excluir imagem: {}", nomeArquivo);
        }
    }

    private void validar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw BusinessException.badRequest("Arquivo de imagem não informado");
        }
        if (!TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw BusinessException.badRequest("Formato de imagem inválido. Use JPEG, PNG, WebP ou GIF");
        }
    }

    private String obterExtensao(String nomeOriginal) {
        if (nomeOriginal == null || !nomeOriginal.contains(".")) return ".jpg";
        return nomeOriginal.substring(nomeOriginal.lastIndexOf(".")).toLowerCase();
    }
}
