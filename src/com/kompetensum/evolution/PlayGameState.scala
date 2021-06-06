package com.kompetensum.evolution

import org.lwjgl._
import opengl.{Display,GL11,DisplayMode}
import GL11._
import input._
import Keyboard._
import scala.util._

class PlayGameState(audioManager:AudioManager) extends GameState {

	val audio = audioManager
	
	val sampleEatGoodName = "data/Powerup2.wav"
	val sampleEatGood = audio.loadALSample(sampleEatGoodName, false)
	val sampleEatEvilName = "data/Hit_Hurt2.wav"
	val sampleEatEvil = audio.loadALSample(sampleEatEvilName, false)
	val sampleNewGoodName = "data/Pickup_Coin.wav"
	val sampleNewGood = audio.loadALSample(sampleNewGoodName, false)
	val sampleNewEvilName = "data/Laser_Shoot4.wav"
	val sampleNewEvil = audio.loadALSample(sampleNewEvilName, false)
	val samplePlayerDeadName = "data/Explosion3.wav"
	val samplePlayerDead = audio.loadALSample(samplePlayerDeadName, false)
	val samplePlayerWinName = "data/Jump.wav"
	val samplePlayerWin = audio.loadALSample(samplePlayerWinName, false)
	
	var player = new Player
	
	var food : List[GoodFood] = Nil
	var evilfood : List[EvilFood] = Nil
	var birthTimer = 60 * 5
	
	var minGoodFood = 5
	var minEvilFood = 5
	val maxGoodFood = 100
	val maxEvilFood = 100
	
	var nearstars : List[(Double,Double,Double)] = Nil
	var farstars : List[(Double,Double,Double)] = Nil
	var rotationXSpeed = 0.6
	var rotationX = 0.0
	var rotationYSpeed = 0.7
	var rotationY = 0.0
	var rotationZSpeed = 0.5
	var rotationZ = 0.0
	
	
	def generateStars(numStars:Int, radius:Double) = {
	  
		var stars : List[(Double,Double,Double)] = Nil
		
		for (i <- 0 to numStars) {
		  val inclination = Random.nextDouble * Math.Pi
		  val azimuth = Random.nextDouble * 2 * Math.Pi 
		  val x1 = radius * Math.sin(inclination) * Math.cos(azimuth)
		  val y1 = radius * Math.sin(inclination) * Math.sin(azimuth)
		  val z1 = radius * Math.cos(inclination)
		  
		  stars = (x1,y1,z1) :: stars
		}
		
		stars
	}
	
	
	  
  def init(): Unit = {  
	player = new Player
	
	food = Nil
	evilfood = Nil
	birthTimer = 60 * 5
	minGoodFood = 5
	minEvilFood = 5
		
	nearstars = generateStars(500, 2)
	farstars = generateStars(250,3)
  }

  def update(): Boolean = {
	  if (player.getPoints < 3) {
	    audio.play(samplePlayerDead)
	    println("GAME OVER")
	    return true
	  }
	  
	  if (player.getPoints > 50) {
	    audio.play(samplePlayerWin)
	    println("YOU WON")
	    return true
	  }
	  
	  var moveLeft = isKeyDown(KEY_J)
	  var moveRight = isKeyDown(KEY_L)
	  var moveUp = isKeyDown(KEY_I)
	  var moveDown = isKeyDown(KEY_K)

	  player.move(moveLeft,moveRight,moveUp,moveDown)
	  player.update
	  var (px, py, pz) = player.getCenter
	  rotationX = py * 8;
	  rotationY = px * 8;
	  rotationZ = pz * 8;

	  // Check if it is time for birth!
	  if (birthTimer <= 0) {
		  birthTimer = 60
		  
		  if (food.length < minGoodFood && food.length < maxGoodFood) {
		    // Add new food at random location
		    audio.play(sampleNewGood)
		    var f = new GoodFood
		    var newx = Random.nextDouble * 20 - 10
		    var newy = Random.nextDouble * 20 - 10
		    f.setCenter(newx, newy, -3.0)
		    food = f :: food
		  }
		  
		  if (evilfood.length < minEvilFood && evilfood.length < maxEvilFood) {
		    // Add new food at random location
		    audio.play(sampleNewEvil)
		    var f = new EvilFood
		    var newx = Random.nextDouble * 20 - 10
		    var newy = Random.nextDouble * 20 - 10
		    f.setCenter(newx, newy, -3.0)
		    evilfood = f :: evilfood
		  }	    
	  } else {
	    birthTimer -= 1
	  }
	  
	  
	  // TODO optimize this!
	  food.foreach(obj1 => {
		  evilfood.foreach(obj2 => {
		    obj1.closeTo(obj2)
		    obj2.closeTo(obj1)
		  })
	  })
	  
	  food.foreach(_.closeTo(player)) 
	  food.foreach(_.update)
	  
	  evilfood.foreach(_.closeTo(player)) 
	  evilfood.foreach(_.update)
	  
	  checkCollisions

    false
  }

	def checkCollisions {

	  food = food.filter(s => {
	    if (player.checkCollision(s)) {
	      audio.play(sampleEatGood)
	      player.addPoint
		  birthTimer = 60
		  minEvilFood += 1
	      false
	    } else {
	      true
	    }
	  })

	  evilfood = evilfood.filter(s => {
	    if (player.checkCollision(s)) {
	      audio.play(sampleEatEvil)
	      player.removePoint
		  birthTimer = 30
		  minGoodFood += 1
	      false
	    } else {
	      true
	    }
	  })
	}

  def render(): Unit = {  
	  glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
	  glLoadIdentity
	  
	  glEnable(GL_LIGHTING)

	  glPushMatrix
	  player.render
	  glPopMatrix
	  
	  food.foreach(_.render)
	  
	  evilfood.foreach(_.render)

	  glPushMatrix
	  glColor3f(1,1,1)
	  glDisable(GL_LIGHTING)

	  glRotatef(rotationX.toFloat,1,0,0)
	  glRotatef(rotationY.toFloat,0,1,0)
	  glRotatef(rotationZ.toFloat,0,0,1)
	  glPointSize(2.0f)
	  glBegin(GL_POINTS)
	  nearstars.foreach((pos) => {
	    var (x1, y1, z1) = pos
	    glVertex3f(x1.toFloat,y1.toFloat,z1.toFloat)
	  })
	  glEnd
	  
	  glPopMatrix

	  glPushMatrix
	  glColor3f(1,1,1)
	  glDisable(GL_LIGHTING)

	  glRotatef(rotationX.toFloat / 2,1,0,0)
	  glRotatef(rotationY.toFloat / 2,0,1,0)
	  glRotatef(rotationZ.toFloat / 2,0,0,1)
	  glPointSize(1.0f)
	  glBegin(GL_POINTS)
	  farstars.foreach((pos) => {
	    var (x1, y1, z1) = pos
	    glVertex3f(x1.toFloat,y1.toFloat,z1.toFloat)
	  })
	  glEnd
	  glPopMatrix
  }

  def cleanup(): Unit = {  }

}