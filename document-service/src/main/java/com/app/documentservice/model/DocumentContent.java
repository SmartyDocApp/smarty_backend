package com.app.documentservice.model;

import jakarta.persistence.*;
import org.bson.types.Binary;

@Entity
@Table(name = "document_contents")
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Binary car adapté pour stocker des fichiers:
//    Gestion optimisée de grandes quantités de données binaires
//    Préservation de l'intégrité des fichiers (pas de corruption lors du stockage/récupération)
//    Compatibilité avec Spring Data MongoDB
    private Binary content;

    private String contentType;

    // Taille du fichier en octets
    private Long size;
}
