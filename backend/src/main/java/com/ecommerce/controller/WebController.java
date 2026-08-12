package com.ecommerce.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Sirve el HTML del frontend desde la carpeta ../frontend/index.html
 * (relativa al directorio donde se ejecuta el JAR).
 *
 * Si por algun motivo no encuentra el archivo (por ejemplo, al ejecutar
 * dentro de un IDE con un working dir distinto), cae al index.html
 * empaquetado dentro del JAR como fallback.
 *
 * Esto evita tener que copiar manualmente el HTML a src/main/resources/static/.
 */
@Controller
public class WebController {

    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    @GetMapping(value = {"/", "/index.html"})
    @ResponseBody
    public byte[] index() {
        return readFrontendHtml();
    }

    /**
     * Busca ../frontend/index.html subiendo desde el directorio de trabajo
     * actual. Si lo encuentra, lo lee del disco. Si no, cae al JAR.
     */
    private byte[] readFrontendHtml() {
        Path html = locateFrontendHtml();
        if (html != null && Files.exists(html)) {
            try {
                log.info("Sirviendo frontend desde: {}", html.toAbsolutePath());
                return Files.readAllBytes(html);
            } catch (IOException e) {
                log.warn("No se pudo leer {}: {}", html, e.getMessage());
            }
        }
        log.warn("No se encontró ../frontend/index.html; usando el HTML empaquetado en el JAR.");
        return readFromJar();
    }

    /**
     * Busca el archivo del frontend. Comprueba varias ubicaciones candidatas
     * porque el directorio de trabajo varía si se ejecuta con `mvn spring-boot:run`,
     * con `java -jar`, o desde un IDE.
     *
     *   - ../frontend/index.html  (al ejecutar el JAR desde backend/)
     *   - ../../frontend/index.html (si el wd es backend/target/)
     *   - ../frontend/index.html  (mvn spring-boot:run desde backend/)
     */
    private Path locateFrontendHtml() {
        String cwd = System.getProperty("user.dir");
        Path cwdPath = Paths.get(cwd).toAbsolutePath();
        for (Path candidate : new Path[]{
                cwdPath.resolve("../frontend/index.html"),
                cwdPath.resolve("../../frontend/index.html"),
                cwdPath.resolve("frontend/index.html"),
                cwdPath.resolve("../mini-ecommerce-project/frontend/index.html")
        }) {
            if (Files.exists(candidate)) {
                return candidate.toAbsolutePath();
            }
        }
        return null;
    }

    private byte[] readFromJar() {
        try {
            Resource res = new ClassPathResource("static/index.html");
            try (InputStream in = res.getInputStream()) {
                return StreamUtils.copyToByteArray(in);
            }
        } catch (IOException e) {
            log.error("Tampoco se pudo leer el HTML del JAR", e);
            return ("<!DOCTYPE html><html><body><h1>Frontend no encontrado</h1>"
                    + "<p>Coloca el archivo index.html en la carpeta frontend/ "
                    + "o en backend/src/main/resources/static/.</p></body></html>")
                    .getBytes(StandardCharsets.UTF_8);
        }
    }
}