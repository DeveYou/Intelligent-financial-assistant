package com.aitsaid.authservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service pour gérer le stockage des fichiers
 * @author radouane
 */
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads/profile-images}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de créer le répertoire de téléchargement.", ex);
        }
    }

    /**
     * Stocke un fichier et retourne le nom du fichier
     */
    public String storeFile(MultipartFile file) {
        // Normaliser le nom du fichier
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new RuntimeException("Le nom du fichier est invalide");
        }

        // Générer un nom unique
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Vérifier si le fichier contient des caractères invalides
            if (fileName.contains("..")) {
                throw new RuntimeException("Le nom de fichier contient une séquence de chemin invalide " + fileName);
            }

            // Copier le fichier vers l'emplacement cible
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de stocker le fichier " + fileName + ". Veuillez réessayer!", ex);
        }
    }

    /**
     * Charge un fichier en tant que ressource
     */
    public Path loadFile(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

    /**
     * Supprime un fichier
     */
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de supprimer le fichier " + fileName, ex);
        }
    }
}

