package com.semkagtn.trackhelper.util

import java.io.{BufferedReader, BufferedWriter, Closeable, FileWriter}
import java.nio.file._

import com.semkagtn.trackhelper.model.FileDescriptor

import scala.collection.JavaConverters._
import scala.io.Source

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

  /**
    * Returns all non empty lines of the file.
    *
    * @param filePath Path to file to read
    */
  def readNonEmptyLines(filePath: String): Iterator[String] = {
    val absolutePath = Paths.get(filePath).toAbsolutePath.toString
    Source.fromFile(absolutePath)
      .getLines
      .map(_.trim)
      .filter(_.nonEmpty)
  }

  /**
    * Writes lines to the file
    */
  def writeLines(filePath: String, lines: Seq[String]): Unit = {
    val absolutePath = Paths.get(filePath).toAbsolutePath.toString
    using(new BufferedWriter(new FileWriter(absolutePath))) { writer =>
      lines.map(_ + "\n").foreach(writer.write)
    }
  }

  def using[C <: Closeable](closeable: C)
                           (action: C => Unit): Unit =
    try {
      action(closeable)
    } finally {
      closeable.close()
    }
}
