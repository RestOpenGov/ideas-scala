package utils

import java.io.{FileWriter, PrintWriter}

// see http://stackoverflow.com/a/5218279/47633

object FileHelper {

  /**
   * Used for reading/writing to database, files, etc.
   * Code From the book "Beginning Scala"
   * http://www.amazon.com/Beginning-Scala-David-Pollak/dp/1430219890
   */
  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B = {
    try { 
      f(param)
    } finally {
      param.close()
    }
  }

  def writeToFile(fileName: String, data: String) = {
    using (new FileWriter(fileName)) {
      fileWriter => fileWriter.write(data)
    }
  }

  def appendToFile(fileName: String, textData: String) = {
    using (new FileWriter(fileName, true)){ 
      fileWriter => using (new PrintWriter(fileWriter)) {
        printWriter => printWriter.println(textData)
      }
    }
  }

}