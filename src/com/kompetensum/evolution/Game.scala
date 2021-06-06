package com.kompetensum.evolution

import org.lwjgl._
import opengl.{Display,GL11,DisplayMode}
import GL11._
import input._
import Keyboard._
import scala.util._

// TODO Make collisions callback based and move the collision logic out of GameObject
// TODO Make closeTo callback based and move logic out of GameObject
// TODO One list with all GameObjects
// TODO Create a class for background stars

object Game extends App {
	val GAME_TITLE = "Evolution"
	val FRAMERATE = 60
	val width = 800
	val height = 600

	val audio = new AudioManager
	// Init audio here since it is used in constructor for GameStates
	audio.init
	audio.setupListener
	
	var finished = false
	
	var gameStates = List(
	  new IntroGameState(audio).asInstanceOf[GameState], 
	    new PlayGameState(audio).asInstanceOf[GameState]
	  )
	var currentGameStateIndex = gameStates.length
	  
	init
	run
	cleanup
	
	def init {
		println("init Display")
		Display.setTitle(GAME_TITLE)
		Display.setFullscreen(false)
		Display.setVSyncEnabled(true)
		Display.setDisplayMode(new DisplayMode(width,height))
		Display.create
 
		println("init gl")
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING)
		glEnable(GL_LIGHT0)
		
		println("setup camera")
		val v = Display.getDisplayMode.getWidth.toFloat/Display.getDisplayMode.getHeight.toFloat
		printf("v:%f",v)
		glMatrixMode(GL_PROJECTION)
		glLoadIdentity
		glFrustum(-v,v,-1,1,1,100)
		glMatrixMode(GL_MODELVIEW)
		
	}
	
	def nextGameState = {
	  // If current index is valid, call cleanup
	  // It is only invalid on game start
	  if (currentGameStateIndex < gameStates.length) {
		  gameStates(currentGameStateIndex).cleanup
	  }
	  
	  currentGameStateIndex += 1
	  if (currentGameStateIndex >= gameStates.length) {
	    currentGameStateIndex = 0
	  }
	  
	  // Initialize the new state
	  gameStates(currentGameStateIndex).init
	}
	
	def getCurrentGameState = {
		if (currentGameStateIndex >= gameStates.length) {
			nextGameState
		}

		gameStates(currentGameStateIndex)
	}
	
	def run {
	  while (!finished) {
	    Display.update

		if(isKeyDown(KEY_ESCAPE))
			finished = true
		if(Display.isCloseRequested)
			finished = true

		val currentGameState = getCurrentGameState
		val done = currentGameState.update
		currentGameState.render

		if (done) {
		  nextGameState
		}
	    
	    Display.sync(FRAMERATE)
	  }
	}
	
	def cleanup {
		audio.cleanup
		Display.destroy	  
	}
	
	def updateGameState {
	  
	}
	
	def renderUI {
	}
		
}
