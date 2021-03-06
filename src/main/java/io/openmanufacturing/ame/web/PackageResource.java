/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for
 * additional information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package io.openmanufacturing.ame.web;

import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.openmanufacturing.ame.config.ApplicationSettings;
import io.openmanufacturing.ame.exceptions.FileReadException;
import io.openmanufacturing.ame.services.PackageService;
import io.openmanufacturing.ame.services.model.ProcessPackage;
import io.openmanufacturing.ame.web.utils.MediaTypeExtension;

/**
 * Controller class that supports the importing and exporting of the aspect model packages.
 */
@RestController
@RequestMapping( "package" )
public class PackageResource {

   private final PackageService packageService;

   public PackageResource( final PackageService packageService ) {
      this.packageService = packageService;
   }

   /**
    * Method to validate a list of Aspect Models which are saved on local workspace.
    *
    * @param aspectModelFiles - a list of aspect model file names.
    * @return
    */
   @PostMapping( "/validate-models" )
   public ProcessPackage validateAspectModels( @RequestBody final List<String> aspectModelFiles ) {
      return packageService.validateAspectModels( aspectModelFiles, ApplicationSettings.getExportPackageStoragePath() );
   }

   /**
    * Method to validate imported zip
    *
    * @param zipFile zip file as multipart form data
    * @return The information is returned which aspect models have validation errors, are already defined and
    *       which files are not aspect models.
    */
   @PostMapping( "/validate-import-zip" )
   public ResponseEntity<ProcessPackage> validateImportAspectModelPackage(
         @RequestParam( "zipFile" ) final MultipartFile zipFile ) {
      final String extension = FilenameUtils.getExtension( zipFile.getOriginalFilename() );

      if ( !Objects.requireNonNull( extension ).equals( "zip" ) ) {
         throw new FileReadException( "Selected file is not a ZIP file." );
      }

      return ResponseEntity.ok(
            packageService.validateImportAspectModelPackage( zipFile,
                  ApplicationSettings.getImportPackageStoragePath() ) );
   }

   /**
    * Method to import AspectModels into workspace
    *
    * @param aspectModelFiles - a list of aspect model file names.
    * @return
    */
   @PostMapping( "/import" )
   public ResponseEntity<List<String>> importAspectModelPackage( @RequestBody final List<String> aspectModelFiles ) {
      return ResponseEntity.ok( packageService.importAspectModelPackage( aspectModelFiles,
            ApplicationSettings.getImportPackageStoragePath() ) );
   }

   /**
    * Method to export packaged aspect models.
    *
    * @param zipFileName - the name of the zip file to be created.
    * @return the package as zip file as blob.
    */
   @GetMapping( path = "/export-zip/{zipFileName}", produces = MediaTypeExtension.APPLICATION_ZIP_VALUE )
   public ResponseEntity<byte[]> exportAspectModelPackage( @PathVariable final String zipFileName ) {

      return ResponseEntity.ok()
                           .header( HttpHeaders.CONTENT_DISPOSITION,
                                 "attachment; filename=\"" + zipFileName + "\"" )
                           .body( packageService.exportAspectModelPackage( zipFileName,
                                 ApplicationSettings.getExportPackageStoragePath() ) );
   }
}
