package com.semkagtn.trackhelper.util

import java.nio.file._

import com.semkagtn.trackhelper.model.FileDescriptor

import scala.collection.JavaConverters._

/**
  * @author semkagtn
  */
object FileUtil {

  def getTmpDir: String =
    System.getProperty("java.io.tmpdir")

  def getHomeDir: String =
    System.getProperty("user.home")

  def move(oldPath: String, newPath: String): Unit =
    try {
      val oldAbsolutePath = Paths.get(oldPath).toAbsolutePath
      val newAbsolutePath = Paths.get(newPath).toAbsolutePath
      Files.move(oldAbsolutePath, newAbsolutePath, StandardCopyOption.REPLACE_EXISTING)
    } catch {
      case e: NoSuchFileException => throw FileNotExistsException(e.getMessage)
    }

  /**
    * Returns absolute paths of the files that directly placed in specified directory
    *
    * @param dirPath path of the directory to scan
    */
  def walkDir(dirPath: String): Iterator[FileDescriptor] =
    try {
      Files.walk(Paths.get(dirPath).toAbsolutePath, 1)
        .iterator
        .asScala
        .map { path: Path => FileDescriptor.fromPath(path.toAbsolutePath.toString) }
    } catch {
      case e: NoSuchFileException => throw FileNotExistsException(e.getMessage)
    }
}
