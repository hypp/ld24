package com.kompetensum.evolution

import org.lwjgl._
import org.lwjgl.util._
import openal.{AL, AL10, OpenALException}
import java.nio.FloatBuffer
import java.nio.IntBuffer

class AudioManager {
  
	var loadedFiles : Map[String, Int] = Map.empty
	var sources : List[Int] = Nil
	
	// TODO Position audio in 3D space
	var sourcePos = BufferUtils.createFloatBuffer(3).put(Array(0.0f, 0.0f, 0.0f))
	sourcePos.rewind
	var sourceVel = BufferUtils.createFloatBuffer(3).put(Array(0.0f, 0.0f, 0.0f))
	sourceVel.rewind
	
	// TODO Position listener in 3D space
	var listenerPos = BufferUtils.createFloatBuffer(3).put(Array(0.0f, 0.0f, 0.0f))
	listenerPos.rewind
	var listenerVel = BufferUtils.createFloatBuffer(3).put(Array(0.0f, 0.0f, 0.0f))
	listenerVel.rewind
	var listenerOri = BufferUtils.createFloatBuffer(6).put(Array(0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f))
	listenerOri.rewind
  
	def getALErrorString(err:Int):String = {
		err match {
		  case AL10.AL_NO_ERROR => "AL_NO_ERROR"
		  case AL10.AL_INVALID_NAME =>"AL_INVALID_NAME"
		  case AL10.AL_INVALID_ENUM => "AL_INVALID_ENUM"
		  case AL10.AL_INVALID_VALUE => "AL_INVALID_VALUE"
		  case AL10.AL_INVALID_OPERATION => "AL_INVALID_OPERATION"
		  case AL10.AL_OUT_OF_MEMORY => "AL_OUT_OF_MEMORY";
		  case _ => "No such error code"
		}    
	}
  
/*  def getALCErrorString(err:Int):String = {
    err match {
      case ALC.ALC_NO_ERROR => "AL_NO_ERROR"
      case ALC.ALC_INVALID_DEVICE => "ALC_INVALID_DEVICE"
      case ALC.ALC_INVALID_CONTEXT => "ALC_INVALID_CONTEXT"
      case ALC.ALC_INVALID_ENUM => "ALC_INVALID_ENUM"
      case ALC.ALC_INVALID_VALUE => "ALC_INVALID_VALUE"
      case ALC.ALC_OUT_OF_MEMORY => "ALC_OUT_OF_MEMORY"
      case _ => "no such error code"
    }
  }
*/  
  def loadAudioFile(fileName:String):Int = {
    val buffer = BufferUtils.createIntBuffer(1)
    
    AL10.alGenBuffers(buffer);

    var result = AL10.alGetError()
    if (result != AL10.AL_NO_ERROR) {
      throw new OpenALException(getALErrorString(result));
    }
  
    val waveFile = WaveData.create(fileName);
    if (waveFile != null) {
      AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
      waveFile.dispose();
    } else {
      throw new RuntimeException("No such file: " + fileName);
    }

    result = AL10.alGetError()
    if (result != AL10.AL_NO_ERROR) {
      throw new OpenALException(getALErrorString(result));
    }
    
    buffer.get(0)
  }
  
  def getLoadedALBuffer(fileName:String):Int = {
    val buffer = loadedFiles.get(fileName)
    buffer match {
      case None => {
        var b = loadAudioFile(fileName)
        loadedFiles += fileName -> b
        b
      }
      case b: Option[Int] => b.get
    }
  }
  
  def loadALSample(fileName:String, loop:Boolean):Int = {
    val bufferId = getLoadedALBuffer(fileName)
    
    val source = BufferUtils.createIntBuffer(1)

    AL10.alGenSources(source)
    
    var result = AL10.alGetError()
    if (result != AL10.AL_NO_ERROR) {
      throw new OpenALException(getALErrorString(result));
    }

    val sourceId = source.get(0)
    var looping = loop match {
      case true => AL10.AL_TRUE
      case false => AL10.AL_FALSE
    }
    
    AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId)
    AL10.alSourcef(sourceId, AL10.AL_PITCH, 1.0f)
    AL10.alSourcef(sourceId, AL10.AL_GAIN, 1.0f)
    AL10.alSource(sourceId, AL10.AL_POSITION, sourcePos)
    AL10.alSource(sourceId, AL10.AL_VELOCITY, sourceVel)
    AL10.alSourcei(sourceId, AL10.AL_LOOPING, looping)
      
    sources = sourceId :: sources
    
    sourceId
  }
  
  def init {
    AL.create
  }
  
  def setupListener {
	AL10.alListener(AL10.AL_POSITION, listenerPos)
	AL10.alListener(AL10.AL_VELOCITY, listenerVel)
	AL10.alListener(AL10.AL_ORIENTATION, listenerOri)    
  }
  
  def cleanup {
	var scratch = BufferUtils.createIntBuffer(1)
	loadedFiles.values.foreach(index => {
	  scratch.put(0,index)
	  AL10.alDeleteBuffers(scratch)
	})
	
	sources.foreach(index => {
	  scratch.put(0,index)
	  AL10.alDeleteSources(scratch)	  
	})
	
	
//	loadedFiles = loadedFiles.empty
//	sources = Nil
  }
  
  def play(sourceId:Int) {
	  AL10.alSourcePlay(sourceId)
	  val result = AL10.alGetError()
	  if (result != AL10.AL_NO_ERROR) {
		 throw new OpenALException(getALErrorString(result));
	  }
 }
  
  def stop(sourceId:Int) {
	  AL10.alSourceStop(sourceId)
  }
}