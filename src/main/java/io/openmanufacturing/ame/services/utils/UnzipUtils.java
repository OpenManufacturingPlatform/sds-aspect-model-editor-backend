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

package io.openmanufacturing.ame.services.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import io.openmanufacturing.ame.exceptions.CreateFileException;
import io.openmanufacturing.ame.exceptions.FileNotFoundException;
import io.openmanufacturing.ame.exceptions.FileReadException;
import io.openmanufacturing.ame.exceptions.FileWriteException;

public class UnzipUtils {
   private static final Logger LOG = LoggerFactory.getLogger( UnzipUtils.class );

   private UnzipUtils() {

   }

   /**
    * This Method is used to unzip a zip package with aspect models.
    *
    * @param zipFile - The zip file as multipart file
    * @param packagePath - The default package storage folder path
    */
   @SuppressWarnings( { "squid:S135", "squid:S5042" } )
   public static void unzipPackageFile( final MultipartFile zipFile, final Path packagePath ) throws IOException {
      try ( final ZipInputStream zipInputStream = new ZipInputStream( zipFile.getInputStream() ) ) {

         ZipEntry zipEntry = zipInputStream.getNextEntry();

         while ( zipEntry != null ) {

            // Skip Mac entries
            if ( !zipEntry.getName().contains( ".DS_Store" ) && !zipEntry.getName().contains( "__MACOSX" ) ) {

               final File createdFile = UnzipUtils.createNewFile( packagePath.toFile(), zipEntry );

               if ( zipEntry.isDirectory() ) {
                  UnzipUtils.createNewDirectory( createdFile );
               } else {
                  // To create directory for Windows
                  UnzipUtils.createNewDirectory( createdFile.getParentFile() );
                  // create aspect model file content and close output stream
                  UnzipUtils.createNewAspectModelFileWithContent( createdFile, zipInputStream ).close();
               }
            }

            zipEntry = zipInputStream.getNextEntry();
         }
         zipInputStream.closeEntry();

         IOUtils.closeQuietly( zipInputStream );
      } catch ( final IOException e ) {
         LOG.error( "Cannot read file." );
         throw new FileReadException( "Error reading the zip file.", e );
      }
   }

   /**
    * This Method creates a new file.
    *
    * @param destinationDir - destination directory of the file.
    * @param zipEntry - representation of the zip file.
    * @return the new created file.
    */
   private static File createNewFile( final File destinationDir, final ZipEntry zipEntry ) throws IOException {
      final String zipEntryPathName = FilenameUtils.separatorsToSystem( zipEntry.getName() );
      final File destFile = new File( destinationDir, zipEntryPathName );

      final String destDirPath = destinationDir.getCanonicalPath();
      final String destFilePath = destFile.getCanonicalPath();

      if ( !destFilePath.startsWith( destDirPath + File.separator ) ) {
         LOG.error( "Entry is outside of the target directory." );
         throw new FileNotFoundException( "Entry is outside of the target dir: " + zipEntryPathName );
      }

      return destFile;
   }

   /**
    * This Method creates a directory.
    *
    * @param file - to create.
    */
   private static void createNewDirectory( final File file ) throws IOException {
      if ( !file.isDirectory() && !file.mkdirs() ) {
         LOG.error( "Cannot create directory." );
         throw new CreateFileException( "Failed to create directory " + file );
      }
   }

   /**
    * This Method creates a new aspect model file with there specific content.
    *
    * @param file - new created file to fill with there content.
    * @param zis - read files from zip file.
    * @return the output stream of the file.
    */
   private static FileOutputStream createNewAspectModelFileWithContent( final File file, final ZipInputStream zis )
         throws IOException {
      final byte[] buffer = new byte[1024];
      try ( final FileOutputStream fileOutputStream = new FileOutputStream( file ) ) {
         int length;
         while ( (length = zis.read( buffer )) > 0 ) {
            fileOutputStream.write( buffer, 0, length );
         }
         return fileOutputStream;
      } catch ( final IOException e ) {
         LOG.error( "File to write was not found." );
         throw new FileWriteException( "File for writing not found", e );
      }
   }
}
